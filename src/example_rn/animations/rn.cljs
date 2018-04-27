(ns example-rn.animations.rn
  (:require [example-rn.rn :refer [animated easing PanResponder]]
            [oops.core :refer [ocall+ ocall oget oapply+]]
            [cljs.core.async :refer [put! chan pipe close!]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.helpers :as helpers]
            [keechma.toolbox.tasks :as t]))

(def AnimatedValue (oget animated "Value"))

(defn make-initial-meta
  ([identifier] (make-initial-meta identifier nil nil))
  ([identifier args] (make-initial-meta identifier args nil))
  ([identifier args prev]
   (let [[id state] (a/identifier->id-state identifier)]
     {:id id
      :state state
      :identifier identifier
      :position 0
      :times-invoked 0
      :prev (dissoc prev :prev)
      :args args})))

(defmulti pan-value a/dispatcher)
(defmulti pan-init-value a/dispatcher)
(defmulti pan-step a/dispatcher)

(defmethod pan-step :default [meta pan-value]
  {})

(defmethod pan-value :default [meta]
  [0 0])

(defmethod pan-init-value :default [meta]
  [0 0])

(defn get-from-value [config]
  (or (:fromValue config) 0))

(defn get-to-value [config]
  (or (:toValue config) 1))

(defn setup-easing [config]
  (if-let [e (:easing config)]
    (assoc config :easing (oapply+ easing (name (:type e)) (clj->js (:values e))))
    config))

(defn get-animated [animated-value {:keys [type config]}]
  (ocall+ animated (name type) animated-value
          (clj->js (-> (merge {:useNativeDriver true :toValue 1} config)
                       setup-easing))))

(defn make-animated-producer [config]
  (fn [res-chan _]
    (let [start-value (get-from-value config)
          value-atom (atom start-value)
          a-value (AnimatedValue. start-value)
          listener-id (ocall a-value "addListener" #(reset! value-atom (oget % "value")))
          a (get-animated a-value (dissoc config :fromValue))
          start (fn []
                  (ocall a "start"
                         (fn [& args]
                           (ocall a-value "removeListener" listener-id)
                           (put! res-chan {:animated a-value :done? true :value @value-atom}))))]
      (put! res-chan {:animated a-value :done? false :start! start :value start-value})
      (fn []
        {:animated a-value :done? true :value @value-atom}))))

(defn make-panresponder-data-processor [done? terminated?]
  (fn [gesture]
    {:gesture (js->clj gesture :keywordize-keys true)
     :done? done?
     :terminated? terminated?}))

(defn make-panresponder-producer [config]
  (let [panresponder-chan (chan)

        active-data-processor     (make-panresponder-data-processor false false)
        done-data-processor       (make-panresponder-data-processor true false)
        terminated-data-processor (make-panresponder-data-processor true true)

        active-data-handler     #(put! panresponder-chan (active-data-processor %2))
        done-data-handler       #(put! panresponder-chan (done-data-processor %2))
        terminated-data-handler #(put! panresponder-chan (terminated-data-processor %2))

        panresponder (ocall PanResponder "create"
                            #js {:onStartShouldSetPanResponder        (constantly true)
                                 :onStartShouldSetPanResponderCapture (constantly true)
                                 :onMoveShouldSetPanResponder         (constantly true)
                                 :onMoveShouldSetPanResponderCapture  (constantly true)
                                 :onPanResponderTerminationRequest    (constantly true)
                                 :onPanResponderGrant                 active-data-handler 
                                 :onPanResponderMove                  active-data-handler 
                                 :onPanResponderRelease               done-data-handler  
                                 :onPanResponderTerminate             terminated-data-handler})]

    (put! panresponder-chan {:gesture nil :done? false :terminated? false})
    {:pan-handlers (js->clj (oget panresponder "panHandlers"))
     :producer     (fn [res-chan _]
                     (pipe panresponder-chan res-chan false)
                     (fn [_] (close! panresponder-chan)))}))

(def animatable-props
  #{:opacity
    :perspective
    :rotate
    :rotate-x
    :rotate-y
    :rotate-z
    :scale
    :scale-x
    :scale-y
    :translate-x
    :translate-y
    :skew-x
    :skew-y})

(defn prepare-values [style]
  (reduce-kv
   (fn [m k v]
     (assoc m k
            (if (contains? animatable-props (keyword (name k)))
              {:value v :animatable true}
              {:value v :animatable false})))
   {} style))

(defn start-animation-values [config animated start-end]
  (reduce-kv (fn [m k v]
               (let [{:keys [start end]} v
                     animatable? (:animatable start)]
                 (if animatable?
                   (assoc m k (ocall animated "interpolate"
                                     (clj->js {:inputRange [(get-from-value config) (get-to-value config)]
                                               :outputRange [(:value start) (:value end)]})))
                   (assoc m k (:value start)))))
             {} start-end))

(defn end-animation-values [value from-value to-value current start-end]
  (reduce-kv (fn [m k v]
               (let [{:keys [start end]} v
                     animatable? (:animatable start)
                     start-value (or (:value start) (:value end))
                     end-value (or (:value end) (:value start))]
                 (if animatable?
                   (assoc m k (helpers/map-value-in-range value start-value end-value from-value to-value))
                   (assoc m k (or (:value end) (:value start))))))
             current start-end))

(defn animate-state!
  ([task-runner! app-db identifier] (animate-state! task-runner! app-db identifier nil nil))
  ([task-runner! app-db identifier version] (animate-state! task-runner! app-db identifier version nil))
  ([task-runner! app-db identifier version args]
   (let [[id state] (a/identifier->id-state identifier)
         prev (a/get-animation app-db id version)
         prev-values (:data prev)
         prev-meta (:meta prev)
         init-meta (make-initial-meta identifier args prev-meta)
         config (a/animator init-meta prev-values)
         values (a/values init-meta)
         start-end (helpers/start-end-values (prepare-values prev-values) (prepare-values values))
         producer (make-animated-producer config)
         task-id (a/animation-id id version)]
     (task-runner!
      producer task-id
      (fn [{:keys [value state]} app-db]
        (let [{:keys [start! done? animated value]} value]
          (if done?
            (let [current-data (:data (a/get-animation app-db id version))
                  next-data (end-animation-values
                             value 
                             (get-from-value config)
                             (get-to-value config)
                             current-data
                             start-end)
                  next-app-db (assoc-in app-db
                                        (a/app-db-animation-path id version)
                                        {:data next-data :meta init-meta})]
              (if (= state :keechma.toolbox.tasks/running)
                (t/stop-task next-app-db task-id)
                next-app-db))
            (let [next-data (start-animation-values config animated start-end)
                  next-app-db (assoc-in app-db
                                        (a/app-db-animation-path id version)
                                        {:data next-data :meta init-meta})]
              (start!)
              next-app-db))))))))

(defn assoc-next-pan-data [next-meta]
  (assoc next-meta :data (pan-step next-meta (:pan-value next-meta))))

(defn make-animated-values [values]
  (reduce-kv (fn [m k v]
               (assoc m k (AnimatedValue. v)))
             {} values))

(defn update-animated-values! [animated-values next-values]
  (reduce-kv (fn [m k v]
               (let [current (or (get m k) (AnimatedValue. v))]
                 (ocall current "setValue" v)
                 (assoc m k current)))
             animated-values next-values))

(defn panresponder-animate-state!
  ([task-runner! app-db identifier] (panresponder-animate-state! task-runner! app-db identifier nil nil))
  ([task-runner! app-db identifier version] (panresponder-animate-state! task-runner! app-db identifier version nil))
  ([task-runner! app-db identifier version args]
   (let [[id state] (a/identifier->id-state identifier)
         prev (a/get-animation app-db id version)
         prev-values (:data prev)
         prev-meta (assoc (:meta prev) :data prev-values)
         init-meta (make-initial-meta identifier args prev-meta)
         init-value (pan-init-value init-meta)
         last-values (atom (pan-step init-meta init-value))
         animated-values (make-animated-values @last-values)
         {:keys [producer pan-handlers]} (make-panresponder-producer nil)
         task-id (a/animation-id id version)]
     (task-runner!
      producer task-id
      (fn [{:keys [value state]} app-db]
        (let [{:keys [done? terminated? gesture]} value
              init? (nil? gesture)
              next-meta (assoc init-meta
                               :pan-handlers pan-handlers
                               :pan-init-value init-value
                               :gesture gesture
                               :data @last-values)
              next-data animated-values
              next-value (pan-value next-meta)
              next-values (pan-step next-meta next-value)]

          (reset! last-values next-values)
          (update-animated-values! animated-values next-values)

          (if done?
            (let [next-app-db (assoc-in app-db
                                    (a/app-db-animation-path id version)
                                    {:data next-values
                                     :meta (assoc init-meta
                                                  :pan-init-value init-value
                                                  :pan-value next-value
                                                  :gesture gesture)})]
              (if (= state :keechma.toolbox.tasks/running)
                (t/stop-task next-app-db task-id)
                next-app-db))
            (if init?
              (assoc-in app-db
                        (a/app-db-animation-path id version)
                        {:data next-data
                         :meta next-meta})
              app-db))))))))



(def blocking-animate-state! (partial animate-state! t/blocking-task!))
(def non-blocking-animate-state! (partial animate-state! t/non-blocking-task!))

(def blocking-panresponder-animate-state! (partial panresponder-animate-state! t/blocking-task!))
(def non-blocking-panresponder-animate-state! (partial panresponder-animate-state! t/blocking-task!))
