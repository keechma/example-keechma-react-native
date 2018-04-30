(ns example-rn.controllers.sidebar
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]
            [oops.core :refer [ocall]]
            [promesa.core :as p]
            [keechma.toolbox.animations.core :as a :refer [render-animation-end]]
            [example-rn.animations.rn :as rna]))

(defn decide-animation [value]
  (if (= :left value)
    :sidebar/left
    :sidebar/right))

(defn should-close? [app-db]
   (let [animation (get-in app-db (a/app-db-animation-path :sidebar nil))
         pan-init-value (get-in animation [:meta :pan-init-value])
         pan-value (get-in animation [:meta :pan-value])]
     (not= pan-init-value pan-value)))

(def controller
  (pp-controller/constructor
   {:params (constantly true)
    :start (fn [_ _ app-db]
             (render-animation-end app-db :sidebar/init nil nil))}
   {:on-route-changed (pipeline! [value app-db]
                        (pp/execute! :close nil))
    :open (pipeline! [value app-db]
            (a/cancel-animation! app-db :sidebar)
            (rna/blocking-animate-state! app-db (decide-animation value) nil nil)
            (pp/execute! :wait-swipe nil))
    :wait-swipe (pipeline! [value app-db]
                  (rna/blocking-panresponder-animate-state! app-db :sidebar/panmove nil nil)
                  (if (should-close? app-db)
                    (rna/blocking-animate-state! app-db :sidebar/init nil nil)
                    (pp/execute! :wait-swipe nil)))
    :close (pipeline! [value app-db]
             (a/cancel-animation! app-db :sidebar)
             (rna/blocking-animate-state! app-db :sidebar/init nil nil))}))
