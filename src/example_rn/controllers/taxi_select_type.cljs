(ns example-rn.controllers.taxi-select-type
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]
            [oops.core :refer [ocall]]
            [promesa.core :as p]
            [keechma.toolbox.animations.core :refer [render-animation-end]]
            [example-rn.animations.rn :as rna]))

(def controller
  (pp-controller/constructor
   (fn [route]
     (when (= :taxi-select-type (get-in route [:data :key]))
       true))
   {:on-start (pipeline! [value app-db]
                (println "----> STARTING")
                (pp/commit! (render-animation-end app-db :taxi-select-type/init nil nil))
                (pp/execute! :start-panresponder nil)
                )
    :start-panresponder (pipeline! [value app-db]
                          (rna/blocking-panresponder-animate-state! app-db :taxi-select-type/panmove nil nil)
                          (pp/execute! :start-panresponder nil))}))
