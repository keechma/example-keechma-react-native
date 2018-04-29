(ns example-rn.controllers.taxi-select-type
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]
            [oops.core :refer [ocall]]
            [promesa.core :as p]
            [keechma.toolbox.animations.core :as a :refer [render-animation-end]]
            [example-rn.util :refer [delay-pipeline]]
            [example-rn.animations.rn :as rna]))

(defn decide-next-animation [app-db]
  (let [prev-animation (get-in app-db (a/app-db-animation-path :taxi-select-type nil))]
    (let [[_ y-value] (get-in prev-animation [:meta :pan-value])
          velocity (get-in prev-animation [:meta :gesture :vy])]
      (cond
        (< velocity -1.5) :taxi-select-type/done
        (> velocity 1.5) :taxi-select-type/init
        (< y-value 0.5) :taxi-select-type/init
        :else :taxi-select-type/done))))

(def controller
  (pp-controller/constructor
   (fn [route]
     (when (= :taxi-select-type (get-in route [:data :key]))
       true))
   {:on-start (pipeline! [value app-db]
                (pp/commit! 
                 (-> app-db
                     (assoc-in [:kv :selected-vehicle] 1)
                     (render-animation-end :taxi-select-type/init nil nil)))
                (pp/execute! :start-panresponder nil))
    :close (pipeline! [value app-db]
             (a/cancel-animation! app-db :taxi-select-type)
             (rna/blocking-animate-state! app-db :taxi-select-type/init nil nil)
             (pp/execute! :start-panresponder nil))
    :start-panresponder (pipeline! [value app-db]
                          (rna/blocking-panresponder-animate-state! app-db :taxi-select-type/panmove nil nil)
                          (rna/blocking-animate-state! app-db (decide-next-animation app-db) nil nil)
                          (pp/execute! :start-panresponder nil)
                          )}))
