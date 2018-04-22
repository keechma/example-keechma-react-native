(ns example-rn.controllers.route-transition
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util.routing :as routing]
            [example-rn.domain.routing :refer [decide-animation]]
            [keechma.toolbox.animations.core :refer [render-animation-end]]
            [example-rn.animations.rn :as rna]))

(defn track-route-transition [app-db]
  (let [route-data       (get-in app-db [:route :data])
        route            (routing/current-route route-data)
        route-key        (:key route)
        route-transition (get-in app-db [:kv :route-transition])]
    (if (not= route-key (:current route-transition))
      (assoc-in app-db [:kv :route-transition]
                {:routes        {:prev    (get-in route-transition [:routes :current])
                                 :current route-key}
                 :route-data    {:prev    (get-in route-transition [:route-data :current])
                                 :current route-data}
                 :times-invoked (inc (or (:times-invoked route-transition) -1))})
      app-db)))

(defn transition-routes [app-db]
  (get-in app-db [:kv :router-transition :routes]))

(defn animation-args [app-db]
  {:animation (decide-animation app-db)
   :transition-routes (transition-routes app-db)})

(defn render-animation-init [app-db]
  (render-animation-end app-db :router/init nil (animation-args app-db)))

(def controller
  (pp-controller/constructor
   {:params (constantly true)
    :start (fn [_ _ app-db]
             (track-route-transition app-db))}
   {:on-route-changed (pipeline! [value app-db]
                        (pp/commit! (track-route-transition app-db))
                        (when (get-in app-db [:kv :route-transition :routes :prev])
                          (pipeline! [value app-db]
                            (pp/commit! (render-animation-init app-db))
                            (rna/blocking-animate-state! app-db :router/slide nil (animation-args app-db))
                            (pp/commit! (assoc-in app-db [:kv :router-transition :routes :prev] nil)))))}))
