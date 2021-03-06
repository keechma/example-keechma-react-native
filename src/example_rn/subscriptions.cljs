(ns example-rn.subscriptions
  (:require [keechma.toolbox.animations.core :as animation])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn get-kv [key]
  (fn [app-db-atom]
    (reaction
     (get-in @app-db-atom (flatten [:kv key])))))

(defn get-animation
  ([app-db-atom id] (get-animation app-db-atom id nil))
  ([app-db-atom id version]
   (reaction
    (animation/get-animation @app-db-atom id version))))

(defn route-transition? [app-db-atom]
  (reaction
   (let [app-db @app-db-atom
         {:keys [prev current]} (get-in app-db [:kv :route-transition :routes])] 
     (not (some nil? [prev current])))))

(defn datasource-delay [app-db-atom]
  (reaction
   (or (get-in @app-db-atom [:kv :datasource-delay]) 0)))

(def subscriptions
  {:route-transition  (get-kv :route-transition)
   :route-transition? route-transition?
   :animation         get-animation
   :open-story        (get-kv :open-story)
   :open-check        (get-kv :open-check)
   :vehicles          (get-kv :vehicles)
   :selected-vehicle  (get-kv :selected-vehicle)
   :button            (get-kv :button)
   :datasource-delay  datasource-delay
   :show-loader?      (get-kv :show-loader?)})
