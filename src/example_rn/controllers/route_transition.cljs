(ns example-rn.controllers.route-transition
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.domain.routing :refer [decide-animation pages-with-navbar]]
            [keechma.toolbox.animations.core :refer [render-animation-end]]
            [example-rn.animations.rn :as rna]
            [keechma.toolbox.tasks :refer [blocking-raf! stop-task stop-task! cancel-task!]]
            [oops.core :refer [ocall]]
            [keechma.toolbox.dataloader.controller :refer [dataloader-status-key]]))

(defn track-route-transition [app-db]
  (let [route            (get-in app-db [:route :data])
        route-key        (:key route)
        route-transition (get-in app-db [:kv :route-transition])]
    (if (not= route-key (:current route-transition))
      (assoc-in app-db [:kv :route-transition]
                {:routes        {:prev    (get-in route-transition [:routes :current])
                                 :current route-key}
                 :route-data    {:prev    (get-in route-transition [:route-data :current])
                                 :current route}
                 :times-invoked (inc (or (:times-invoked route-transition) -1))})
      app-db)))

(defn transition-routes [app-db]
  (get-in app-db [:kv :route-transition :routes]))

(defn animation-args [app-db]
  {:animation (decide-animation app-db)
   :transition-routes (transition-routes app-db)})

(defn render-route-transition-init [app-db]
  (render-animation-end app-db :router/init nil (animation-args app-db)))

(defn determine-navbar-state [app-db]
  (let [route-transition (get-in app-db [:kv :route-transition])
        prev-page        (get-in route-transition [:routes :prev])
        current-page     (get-in route-transition [:routes :current])
        with-navbar      (set pages-with-navbar)
        prev-navbar?     (contains? with-navbar prev-page)
        current-navbar?  (contains? with-navbar current-page)]
    (cond
      (and (nil? prev-page) current-navbar?)         :keep-shown
      (and prev-navbar? current-navbar?)             :keep-shown
      (and (not prev-navbar?) current-navbar?)       :show
      (and prev-navbar? (not current-navbar?))       :hide
      (and (not prev-navbar?) (not current-navbar?)) :keep-hidden
      :else                                          nil)))

(def block-thresholds
  {:route 800
   :loader 1000})

(defn block-route-transition! []
  (blocking-raf!
   :block-route-transition
   (fn [{:keys [started-at id]} app-db]
     (let [now (ocall js/Date "now")
           ms-diff (- now started-at)
           spent-threshold? (< (:route block-thresholds) ms-diff)
           dataloader-done? (= :loaded (get-in app-db dataloader-status-key))]
       (if (or dataloader-done? spent-threshold?)
         (let [new-app-db (if dataloader-done? app-db (assoc-in app-db [:kv :show-loader?] true))]
           (stop-task new-app-db id))
         app-db)))))

(defn block-loader-hide! []
  (blocking-raf!
   :block-route-transition
   (fn [{:keys [started-at id]} app-db]
     (let [now (ocall js/Date "now")
           ms-diff (- now started-at)
           spent-threshold? (< (:loader block-thresholds) ms-diff)
           dataloader-done? (= :loaded (get-in app-db dataloader-status-key))]
       (if (and dataloader-done? spent-threshold?)
         (stop-task app-db id)
         app-db)))))

(def controller
  (pp-controller/constructor
   {:params (fn [params]
              params)
    :start (fn [_ _ app-db]
             (-> app-db
                 (assoc-in [:kv :show-loader?] false)
                 (track-route-transition)
                 (render-route-transition-init)))}
   {:on-start (pipeline! [value app-db]
                (block-route-transition!)
                (when (get-in app-db [:kv :show-loader?])
                  (pp/execute! :block-loader-hide nil))
                (pp/execute! :animate-route-transition value)
                (pp/execute! :animate-navbar-transition value)
                (pp/execute! :animate-navbar-marker-transition value))
    :animate-route-transition (pipeline! [value app-db]
                                (when (get-in app-db [:kv :route-transition :routes :prev])
                                  (pipeline! [value app-db]
                                    (rna/blocking-animate-state! app-db :router/slide nil (animation-args app-db))
                                    (pp/commit! (assoc-in app-db [:kv :route-transition :routes :prev] nil)))))
    :animate-navbar-transition (pipeline! [value app-db]
                                 (determine-navbar-state app-db)
                                 (case value
                                   :keep-shown (pp/commit! (render-animation-end app-db :navbar/slide-end nil :show))
                                   :keep-hidden (pp/commit! (render-animation-end app-db :navbar/slide-end nil :hide))
                                   :show (pp/execute! :animate-navbar-show value)
                                   :hide (pp/execute! :animate-navbar-hide value)
                                   nil))
    :animate-navbar-hide (pipeline! [value app-db]
                           (pp/commit! (render-animation-end app-db :navbar/init nil :hide))
                           (rna/blocking-animate-state! app-db :navbar/slide nil :hide)
                           (pp/commit! (render-animation-end app-db :navbar/slide-end nil :hide)))
    :animate-navbar-show (pipeline! [value app-db]
                           (pp/commit! (render-animation-end app-db :navbar/init nil :show))
                           (rna/blocking-animate-state! app-db :navbar/slide nil :show)
                           (pp/commit! (render-animation-end app-db :navbar/slide-end nil :show)))
    :animate-navbar-marker-transition (pipeline! [value app-db]
                                        (get-in app-db [:kv :route-transition :routes :prev])
                                        (pp/commit! (render-animation-end app-db :navbar-marker/position nil value))
                                        (get-in app-db [:kv :route-transition :routes :current])
                                        (rna/blocking-animate-state! app-db :navbar-marker/position nil value))
    :block-loader-hide (pipeline! [value app-db]
                         (block-loader-hide!)
                         (pp/commit! (assoc-in app-db [:kv :show-loader?] false)))
    :on-stop (pipeline! [value app-db]
               (cancel-task! app-db :block-route-transition))}))
