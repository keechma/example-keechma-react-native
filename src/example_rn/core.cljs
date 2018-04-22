(ns example-rn.core
    (:require [reagent.core :as r :refer [atom]]
              [keechma.app-state :as app-state]
              [example-rn.components :refer [components]]
              [example-rn.controllers :refer [controllers]]
              [example-rn.subscriptions :refer [subscriptions]]
              [example-rn.datasources :refer [datasources]]
              [example-rn.edb :refer [edb-schema]]
              [example-rn.forms :as example-rn-forms]
              [keechma.toolbox.forms.app :as forms]
              [keechma.toolbox.dataloader.app :as dataloader]
              [example-rn.domain.routing :refer [route-processor]]))

(defonce running-app (r/atom nil))

(def ReactNative (js/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))

(def app-definition
  (-> {:controllers     controllers
       :components      components
       :subscriptions   subscriptions
       :router          :react-native
       :route-processor route-processor}
      (dataloader/install datasources edb-schema)
      (forms/install example-rn-forms/forms example-rn-forms/forms-automount-fns)))

(defn start-app! [] 
  (reset! running-app (app-state/start! app-definition)))

(defn reload []
  (let [current @running-app]
    (if current
      (app-state/stop! current start-app!)
      (start-app!))))

(defn app-root []
  (let [component (:main-component @running-app)] 
    (when component
      [component])))

(defn init []
  (start-app!)
  (.registerComponent app-registry "ExampleRn" #(r/reactify-component app-root)))

