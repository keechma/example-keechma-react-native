(ns example-rn.controllers.complex-stage
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]
            [oops.core :refer [ocall]]
            [promesa.core :as p]
            [keechma.toolbox.animations.core :as a :refer [render-animation-end]]
            [example-rn.animations.rn :as rna]))

(def controller
  (pp-controller/constructor
   {:params (fn [route]
              (when (= :complex-stage (get-in route [:data :key]))
                true))
    :start (fn [_ _ app-db]
             (println "COMPLEX STAGE")
             (-> app-db
                 (render-animation-end :background/init)
                 (render-animation-end :main/init)))}
   {:on-start (pipeline! [value app-db]
                (pp/execute! :cancel nil)
                (rna/blocking-group-animate-state!
                 app-db
                 {:animation :background/done}
                 {:animation :main/done
                  :delay 5000})
                (println "AFTER GROUP"))
    :cancel (pipeline! [value app-db]
                   ;;(delay-pipeline 1000)
                   ;;(a/cancel-animation! app-db :background)
              )}))
