(ns example-rn.util.routing
  (:require [promesa.core :as p]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [route>]]
            [keechma.app-state.react-native-router :refer [navigate! route-atom]]))


(defn ind [x coll]
  (loop [ans [], coll coll, n 0]
    (if-let [[y & ys] (seq coll)]
      (recur (if (= x y) (conj ans n) ans) ys (inc n))
      ans)))

(defn current-route [route]
  (first (filter #(= (:key route) (:key %)) (:routes route))))

(defn current-route> [ctx]
  (let [route (route> ctx)]
    (current-route route)))

(defn navigate-go! [payload]
  (let [route-atom keechma.app-state.react-native-router.route-atom
        current @route-atom
        routes (:routes current)
        route-keys (map :key routes)
        go-index (first (ind (:key payload) route-keys))
        remaining-part (if (nil? go-index) routes (into [] (first (split-at go-index routes))))
        new-routes (conj remaining-part payload)
        new-payload {:key (:key payload)
                     :index (dec (count new-routes))
                     :routes new-routes}]
    (reset! route-atom new-payload)))

(defn navigate-replace! [payload]
  (let [current @route-atom
        clean-routes (into [] (filter #(not= (:key %1) (:key payload)) (:routes current)))
        new-routes (conj clean-routes payload)
        new (-> current
                (assoc :key (:key payload))
                (assoc :index (dec (count new-routes)))
                (assoc :routes new-routes))]
    (reset! route-atom new)))
