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
                            (render-animation-end :check-list-header/init nil value)
                            (render-animation-end :check-list-item-opacity/init nil value)
                            (render-animation-end :check-list-item-scale/init nil value)
                            (render-animation-end :check-open-header/init nil value)
                            (render-animation-end :check-open-background/init nil value)
                            (render-animation-end :check-open-summary/init nil value)
                            (render-animation-end :check-open-details/init nil value)
                            (render-animation-end :check-open-details-items/init nil value)))
            (rna/blocking-group-animate-state!
             app-db
             {:animation :check-list-header/open}
             {:animation :check-list-item-scale/open}
             {:animation :check-list-item-opacity/open
              :delay 100}
             {:animation :check-open-header/open
              :delay 200}
             {:animation :check-open-background/open
              :delay 300}
             {:animation :check-open-summary/open
              :delay 300}
             {:animation :check-open-details/open
              :delay 300}
             {:animation :check-open-details-items/open
              :delay 350
              :args value}))
    :close (pipeline! [value app-db]
             (get-in app-db [:kv :open-check])

             (rna/blocking-group-animate-state!
              app-db
              {:animation :check-list-header/init
               :delay 300}
              {:animation :check-list-item-scale/init
               :delay 300}
              {:animation :check-list-item-opacity/init
               :delay 300}
              {:animation :check-open-header/init
               :delay 200}
              {:animation :check-open-background/init
               :delay 100}
              {:animation :check-open-summary/init
               :args value
               :delay 100}
              {:animation :check-open-details/init}
              {:animation :check-open-details-items/init
               :args value})
             
             (pp/commit! (assoc-in app-db [:kv :open-check] nil)))
    :on-stop (pipeline! [value app-db]
               (pp/commit! (assoc-in app-db [:kv :open-check] nil)))}))
