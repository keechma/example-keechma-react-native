(ns example-rn.animations.rn
  (:require [example-rn.rn :refer [animated easing PanResponder]]
            [oops.core :refer [ocall+ ocall oget oget+ oapply+]]
            [cljs.core.async :refer [put! chan pipe close! <!]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.helpers :as helpers]
            [keechma.toolbox.tasks :as t]
            [promesa.core :as p])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

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
    (let [values (:values e)
          easing-type (name (:type e))
          easing-fn (if values
                      (oapply+ easing easing-type (clj->js values))
                      (oget+ easing easing-type))]
      (assoc config :easing easing-fn))
    config))

(defn get-animated [animated-value {:keys [type config]}]
  (let [loop? (:loop? config)
        a (ocall+ animated (name type) animated-value
                  (clj->js (-> (merge {:useNativeDriver true :toValue 1} config)
                               setup-easing)))]
    (if loop?
      (ocall animated "loop" a)
      a)))

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

(defn on-move-should-set-pan-responder? [_ gesture]
  (let [dx (.abs js/Math (oget gesture "dx"))
        dy (.abs js/Math (oget gesture "dy"))]
    (or (< 5 dx) (< 5 dy))))

(defn make-panresponder-producer [config]
  (let [panresponder-chan (chan)
        panresponder-atom (atom nil)
        active-data-processor     (make-panresponder-data-processor false false)
        done-data-processor       (make-panresponder-data-processor true false)
        terminated-data-processor (make-panresponder-data-processor true true)

        active-data-handler     #(put! panresponder-chan (active-data-processor %2))
        done-data-handler       #(put! panresponder-chan (done-data-processor %2))
        terminated-data-handler #(put! panresponder-chan (terminated-data-processor %2))

        panresponder (ocall PanResponder "create"
                            #js {:onStartShouldSetPanResponder        (constantly false)
                                 :onStartShouldSetPanResponderCapture (constantly false)
                                 :onMoveShouldSetPanResponder         on-move-should-set-pan-responder?
                                 :onMoveShouldSetPanResponderCapture  (constantly false)
                                 :onPanResponderTerminationRequest    (constantly true)
                                 :onPanResponderGrant                 active-data-handler 
                                 :onPanResponderMove                  active-data-handler 
                                 :onPanResponderRelease               done-data-handler  
                                 :onPanResponderTerminate             terminated-data-handler})]

    (put! panresponder-chan {:gesture nil :done? false :terminated? false})
    {:pan-handlers (js->clj (oget panresponder "panHandlers"))
     :producer     (fn [res-chan _]
                     (go-loop []
                       (let [value (<! panresponder-chan)]
                         (when value
                           (put! res-chan value)
                           (reset! panresponder-atom value)
                           (recur))))

                     (fn [_]
                       (let [last-panresponder @panresponder-atom]
                         (close! panresponder-chan)
                         (when (not (:done? last-panresponder))
                           (assoc last-panresponder :done? true)))))}))

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
  (let [prepared (helpers/prepare-values style)]
    (reduce-kv
     (fn [m k v]
       (let [native-animatable? (contains? animatable-props (keyword (name k)))]
         (if (not native-animatable?)
           (assoc m k (assoc v :animatable false))
           (assoc m k v))))
     {} prepared)))

(defn get-input-range [prop start end]
  [start end])

(defn get-output-range [prop start end]
  [start end])

(defn prepare-output-range [values]
  (map (fn [v]
         (if (= :unit (:animatable v))
           (str (:value v) (name (:unit v)))
           (:value v)))
       values))

(defn start-animation-values [config animated start-end]
  (reduce-kv (fn [m k v]
               (let [{:keys [start end]} v
                     animatable          (:animatable start)]

                 (if animatable
                   (let [get-input-range    (or (:get-input-range config) get-input-range)
                         get-output-range   (or (:get-output-range config) get-output-range)
                         input-range        (apply get-input-range [k (get-from-value config) (get-to-value config)])
                         output-range       (apply get-output-range [k start end])
                         interpolate-config {:inputRange input-range
                                             :outputRange (prepare-output-range output-range)}]
                     (assoc m k (ocall animated "interpolate" (clj->js interpolate-config))))
                   (assoc m k (:value start)))))
             {} start-end))

(defn end-animation-values [value from-value to-value current start-end]
  (reduce-kv
   (fn [m k v]
     (let [{:keys [start end]} v
           animatable (:animatable start)
           start-value (or (:value start) (:value end))
           end-value (or (:value end) (:value start))]

       (if animatable
         (let [new-value
               (cond
                 (= start-value end-value) end-value
                 (= :color animatable) (helpers/interpolate-color value start-value end-value from-value to-value)
                 (or (= :unit animatable) (= :number animatable)) (helpers/map-value-in-range value start-value end-value from-value to-value)
                 :else end-value)]
           (assoc m k (if (= :unit animatable) (str new-value (:unit end)) new-value)))
         (assoc m k (or (:value end) (:value start))))))
   current start-end))

(defn using-native-driver? [config]
  (let [native-driver? (get-in config [:config :useNativeDriver])]
    (if (nil? native-driver?)
      true
      native-driver?)))

(defn get-start-end [prev-values values config]
  (if (using-native-driver? config)
    (helpers/start-end-values (prepare-values prev-values) (prepare-values values))
    (let [prepared (helpers/start-end-values
                    (helpers/prepare-values prev-values)
                    (helpers/prepare-values values))]
      prepared)))

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
         start-end (get-start-end prev-values values config)
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
         last-value (atom init-value)
         animated-values (make-animated-values @last-values)
         {:keys [producer pan-handlers]} (make-panresponder-producer nil)
         task-id (a/animation-id id version)]
     (task-runner!
      producer task-id
      (fn [animation-state app-db]
        (let [{:keys [value state]} animation-state
              {:keys [done? terminated? gesture]} value
              init? (and (not done?) (nil? gesture))
              next-meta (assoc init-meta
                               :pan-handlers pan-handlers
                               :pan-init-value init-value
                               :gesture gesture
                               :data @last-values)
              next-data animated-values
              next-value (if gesture (pan-value next-meta) init-value)
              next-values (if (= next-value @last-value) @last-values (pan-step next-meta next-value))]
          
          (when (and (not= next-value @last-value))
            (reset! last-values next-values)
            (reset! last-value next-value)
            (update-animated-values! animated-values next-values))

          (if done?
            (do
              (let [next-app-db (assoc-in app-db
                                          (a/app-db-animation-path id version)
                                          {:data next-values
                                           :meta (assoc init-meta
                                                        :pan-handlers {}
                                                        :pan-init-value init-value
                                                        :pan-value next-value
                                                        :gesture gesture)})]
                (if (= state :keechma.toolbox.tasks/running)
                  (t/stop-task next-app-db task-id)
                  next-app-db)))
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

(defn run-animation-in-group [app-db animation-config]
  (let [{:keys [animation version args]} animation-config
        a-delay (or (:delay animation-config) 0)
        runner #(blocking-animate-state! app-db animation version args)]
    (if (= 0 a-delay)
      (runner)
      (fn [ctrl app-db-atom value]
        (p/promise (fn [resolve _]
                     (js/setTimeout
                      (fn []
                        (let [r (runner)]
                          (->> (r ctrl app-db-atom value)
                               (p/map resolve)))) 
                      a-delay)))))))

(defn group-animate-state! [blocking? app-db & animations]
  (with-meta
    (fn [ctrl app-db-atom value]
      (let [done-promise
            (p/promise
             (fn [resolve _]
               (let [runners (map #(run-animation-in-group app-db %) animations)]
                 (->> (p/all (map #(% ctrl app-db-atom value) runners))
                      (p/map (fn [results]
                               (let [break? (some #(= % :keechma.toolbox.pipeline.core/break) results)]
                                 (if break?
                                   (resolve :keechma.toolbox.pipeline.core/break)
                                   (resolve)))))))))]
        (if blocking?
          done-promise
          nil)))
    {:pipeline? true}))

(def blocking-group-animate-state! (partial group-animate-state! true))
(def non-blocking-group-animate-state! (partial group-animate-state! false))
