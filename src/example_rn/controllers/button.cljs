(ns example-rn.controllers.button
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]
            [oops.core :refer [ocall]]
            [promesa.core :as p]
            [keechma.toolbox.animations.core
             :as a
             :refer [render-animation-end
                     get-animation-state
                     cancel-animation!
                     stop-animation!]]
            [example-rn.animations.rn :as rna]))

(defn button-pressed-animation! []
  (pipeline! [value app-db]
    (cancel-animation! app-db :button)
    (if (= :init (get-animation-state app-db :button))
      (rna/blocking-animate-state! app-db :button/pressed)
      (rna/blocking-animate-state! app-db :button/fail-pressed))))

(defn button-released-animation! []
  (pipeline! [value app-db]
    (cancel-animation! app-db :button)
    (if (not (get-in app-db [:kv :button :local-fail?]))
      (pipeline! [value app-db]
        (if (= :pressed (get-animation-state app-db :button))
          (rna/blocking-animate-state! app-db :button/init)
          (rna/blocking-animate-state! app-db :button/fail-init))
        (rna/blocking-animate-state! app-db :button/button-loader)
        (pp/commit! (render-animation-end app-db :button/loader-start))
        (rna/non-blocking-animate-state! app-db :button/loader-end)
        (pp/execute! :on-submit nil))
      (rna/blocking-animate-state! app-db :button/fail-init))))

(defn button-animation-success! []
  (pipeline! [value app-db]
    (pp/commit! (render-animation-end app-db :button/button-loader))
    (rna/blocking-animate-state! app-db :button/success-notice)
    (delay-pipeline 1000)
    (rna/blocking-animate-state! app-db :button/init)))

(defn button-animation-error! []
  (pipeline! [value app-db]
    (pp/commit! (render-animation-end app-db :button/button-loader))
    (rna/blocking-animate-state! app-db :button/fail-notice)
    (delay-pipeline 1000)
    (rna/blocking-animate-state! app-db :button/fail-init)))

(def controller
  (pp-controller/constructor
   {:params (fn [route]
              (when (= :button (get-in route [:data :key]))
                true))
    :start (fn [_ _ app-db]
             (-> app-db
                 (assoc-in [:button :local-fail?] false)
                 (assoc-in [:button :server-fail?] false)
                 (render-animation-end :button/init nil nil)))}
   {:on-press-in (pipeline! [value app-db]
                   (button-pressed-animation!))
    :on-press-out (pipeline! [value app-db]
                    (button-released-animation!))
    :on-stop (pipeline! [value app-db]
               (cancel-animation! app-db :button nil))
    :on-submit (pipeline! [value app-db]
                 (delay-pipeline 2000)
                 (if (get-in app-db [:kv :button :server-fail?])
                   (button-animation-error!)
                   (button-animation-success!)))
    :toggle-button-setting (pipeline! [value app-db]
                              (pp/commit! (update-in app-db [:kv :button value] not)))}))








