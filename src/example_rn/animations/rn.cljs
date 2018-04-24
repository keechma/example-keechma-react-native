(ns example-rn.animations.rn
  (:require [example-rn.rn :refer [animated easing]]
            [oops.core :refer [ocall+ ocall oget oapply+]]
            [cljs.core.async :refer [put!]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.helpers :as helpers]
            [keechma.toolbox.tasks :as t]))

(def AnimatedValue (oget animated "Value"))

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
         init-meta (a/make-initial-meta identifier args prev-meta)
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

(def blocking-animate-state! (partial animate-state! t/blocking-task!))
(def non-blocking-animate-state! (partial animate-state! t/non-blocking-task!))
