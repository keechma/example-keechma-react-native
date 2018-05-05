(ns example-rn.controllers.kv
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [keechma.toolbox.ui :refer [<cmd]]))

(defn <kv-swap [ctx path swap-fn]
  (<cmd ctx [::id :swap] {:path path
                          :swap-fn swap-fn}))

(defn <kv-reset [ctx path value]
  (<cmd ctx [::id :reset] {:path path
                           :value value}))

(defn process-path [path]
  (flatten [:kv path]))

(defn swap-value [app-db {:keys [path swap-fn]}]
  (let [kv-path (process-path path)
        current-value (get-in app-db kv-path)]
    (assoc-in app-db kv-path (swap-fn current-value))))

(defn reset-value [app-db {:keys [path value]}]
  (let [kv-path (process-path path)]
    (assoc-in app-db kv-path value)))

(def controller
  (pp-controller/constructor
   (fn [_] true)
   {:swap (pipeline! [value app-db]
            (pp/commit! (swap-value app-db value)))
    :reset (pipeline! [value app-db]
             (pp/commit! (reset-value app-db value)))}))


(defn register
  ([] (register {}))
  ([controllers] (assoc controllers ::id controller)))
