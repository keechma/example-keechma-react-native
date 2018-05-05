(ns example-rn.datasources
  (:require [keechma.toolbox.dataloader.subscriptions :refer [map-loader]]
            [promesa.core :as p]))

(def ignore-datasource
  :keechma.toolbox.dataloader.core/ignore)

(def datasource-delay
  {:target [:kv :datasource-delay]
   :loader (map-loader (fn [{:keys [params]}] params))
   :params (fn [data _ _]
             data)})

(def some-datasource
  {:target [:kv :some-datasource]
   :deps   [:datasource-delay]
   :loader (map-loader 
            (fn [{:keys [params]}]
              (when params
                (p/promise (fn [resolve _] (js/setTimeout #(resolve "SOME DATASOURCE DATA!!!!!1") params)))))) 
   
   :params (fn [_ route {:keys [datasource-delay]}]
             (when (= :avoid-loader-target (:key route))
               (or datasource-delay 0)))})

(def datasources
  {:datasource-delay datasource-delay
   :some-datasource  some-datasource})
