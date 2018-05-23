(ns example-rn.controllers.checker
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]
            [oops.core :refer [ocall]]
            [promesa.core :as p]
            [keechma.toolbox.animations.core :refer [render-animation-end]]
            [example-rn.animations.rn :as rna]))

(defn measure-component [value]
  (p/promise
   (fn [resolve _]
     (let [c (:cell value)]
       (ocall c "measure"
              (fn [x y width height page-x page-y]
                (resolve (assoc value :cell {:x x
                                             :y y
                                             :width width
                                             :height height
                                             :page-x page-x
                                             :page-y page-y}))))))))

(def controller
  (pp-controller/constructor
   (fn [route]
     (when (= :checker (get-in route [:data :key]))
       true))
   {:open (pipeline! [value app-db]
            (measure-component value)
            (pp/commit! (-> app-db
                            (assoc-in [:kv :open-check] value)
                            (render-animation-end :check/init nil value)))
        )
    :close (pipeline! [value app-db]
             (get-in app-db [:kv :open-check])
          
             (pp/commit! (assoc-in app-db [:kv :open-check] nil)))
    :on-stop (pipeline! [value app-db]
               (pp/commit! (assoc-in app-db [:kv :open-check] nil)))}))
