(ns example-rn.components.avoid-loader
  (:require [example-rn.rn :refer [view text button]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]
            [example-rn.rn :refer [slider]]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [example-rn.controllers.kv :refer [<kv-reset]]))

(defn render [ctx]
  (let [d (dimensions)
        datasource-delay (sub> ctx :datasource-delay)]
    [view {:style {:flex 1
                   :background-color "#fff"
                   :padding-top (:padding-top d)
                   :padding-bottom (:padding-bottom d)
                   :padding-horizontal 10}}
     [text (str "Datasource delay (" datasource-delay "ms)" )]
     [slider {:maximum-value 3000
              :on-value-change #(<kv-reset ctx :datasource-delay %)
              :value datasource-delay}]
     [button {:on-press #(navigate-go! {:key :avoid-loader-target})
              :title "Open page with data deps"}]
     [button {:on-press #(navigate-go! {:key :init})
              :title "Go to Home Page"}]
     ]))
(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:datasource-delay]}))
