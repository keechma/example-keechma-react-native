(ns example-rn.components.avoid-loader-target
  (:require [example-rn.rn :refer [view text button activity-indicator]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]
            [keechma.app-state.react-native-router :refer [navigate!]]
            [keechma.toolbox.ui :refer [sub>]]
            ))

(defn render [ctx]
  (let [d (dimensions)]
    [view {:style {:flex 1
                   :background-color "#eaf4f7"
                   :padding-top (:padding-top d)
                   :padding-bottom (:padding-bottom d)
                   :padding-horizontal 10}}
     (if (sub> ctx :show-loader?)
       [view {:style {:align-items "center"
                      :justify-content "center"
                      :height "100%"
                      :width "100%"}}
        [activity-indicator {:size "large"}]]
       [view
        [text (sub> ctx :some-datasource)]
        [button {:on-press #(navigate! :pop)
                 :title "Go Back"}]])
     
     ]))
(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:show-loader?
                                       :some-datasource]}))
