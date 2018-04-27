(ns example-rn.controllers.story-detail
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
     (when (= :stories (get-in route [:data :key]))
       true))
   {:open (pipeline! [value app-db]
            (measure-component value)
            (pp/commit! (assoc-in app-db [:kv :open-story] value))
            (pp/commit! (render-animation-end app-db :open-story/init nil value))
            (rna/blocking-animate-state! app-db :open-story/open nil value)
            (rna/blocking-animate-state! app-db :open-story/show-body nil value))
    :close (pipeline! [value app-db]
             (get-in app-db [:kv :open-story])
             (rna/blocking-animate-state! app-db :open-story/open nil value)
             (rna/blocking-animate-state! app-db :open-story/init nil value)
             (pp/commit! (assoc-in app-db [:kv :open-story] nil)))
    :on-stop (pipeline! [value app-db]
               (pp/commit! (assoc-in app-db [:kv :open-story] nil)))}))
