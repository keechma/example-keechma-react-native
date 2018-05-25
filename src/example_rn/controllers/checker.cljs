(ns example-rn.controllers.checker
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]
            [oops.core :refer [ocall]]
            [promesa.core :as p]
            [keechma.toolbox.animations.core :refer [render-animation-end clear-animation]]
            [example-rn.animations.rn :as rna]
            [example-rn.edb :as edb]
            [keechma.toolbox.dataloader.controller :refer [wait-dataloader-pipeline!]]))

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

(defn render-list-items-init [app-db items]
  (reduce
   (fn [acc v]
     (render-animation-end acc :check-list-item-init/init (:id v) {:items items :item v}))
   app-db items))

(defn render-list-items-open! [app-db items]
  (let [animations (reduce-kv (fn [acc idx v]
                                (conj acc {:animation :check-list-item-init/open
                                           :version (:id v)
                                           :delay (* idx 10)})) [] (vec items))]
    (apply rna/blocking-group-animate-state! app-db animations)))


(defn clear-list-items-animation [app-db items]
  (reduce
   (fn [acc v]
     (clear-animation acc :check-list-item-init (:id v)))
   app-db items))

(def controller
  (pp-controller/constructor
   (fn [route]
     (when (= :checker (get-in route [:data :key]))
       true))
   {:on-start (pipeline! [value app-db]
                (wait-dataloader-pipeline!)
                (pp/commit! (render-list-items-init app-db (edb/get-collection app-db :check :list)))
                (render-list-items-open! app-db (edb/get-collection app-db :check :list))
                (pp/commit! (clear-list-items-animation app-db (edb/get-collection app-db :check :list))))
    :open (pipeline! [value app-db]
            (measure-component value)
            (assoc value :items (edb/get-collection app-db :check :list))
            (pp/commit! (-> app-db
                            (assoc-in [:kv :open-check] value)
                            (render-animation-end :check-list-header/init nil value)
                            (render-animation-end :check-list-item-opacity/init nil value)
                            (render-animation-end :check-list-item-scale/init nil value)
                            (render-animation-end :check-open-header/init nil value)
                            (render-animation-end :check-open-background/init nil value)
                            (render-animation-end :check-open-summary/init nil value)
                            (render-animation-end :check-open-details/init nil value)
                            (render-animation-end :check-open-details-items/init nil value)
                            (render-animation-end :check-open-buttons/init :blue value)
                            (render-animation-end :check-open-buttons/init :red value)))
            (pp/send-command! [:route-transition :animate-navbar-hide] nil)
            (rna/blocking-group-animate-state!
             app-db
             {:animation :check-list-header/open}
             {:animation :check-list-item-scale/open}
             {:animation :check-open-buttons/open
              :version :blue :delay 200}
             {:animation :check-open-buttons/open
              :version :red :delay 100}
             {:animation :check-list-item-opacity/open
              :delay 100}
             {:animation :check-open-header/open
              :delay 200}
             {:animation :check-open-background/open
              :delay 300}
             {:animation :check-open-summary/open
              :delay 100}
             {:animation :check-open-details/open
              :delay 300}
             {:animation :check-open-details-items/open
              :delay 350
              :args value}))
    :close (pipeline! [value app-db]
             (get-in app-db [:kv :open-check])
             (assoc value :items (edb/get-collection app-db :check :list))
             (pp/send-command! [:route-transition :animate-navbar-show] nil)
             (rna/blocking-group-animate-state!
              app-db 
              {:animation :check-open-buttons/init
               :version :blue :delay 0}
              {:animation :check-open-buttons/init
               :version :red :delay 100}
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
             (pp/commit! (-> app-db
                             (clear-animation :check-list-item-scale)
                             (clear-animation :check-list-item-opacity)))
             
             (pp/commit! (assoc-in app-db [:kv :open-check] nil))
            )
    :on-stop (pipeline! [value app-db]
               (pp/commit! (assoc-in app-db [:kv :open-check] nil)))}))
