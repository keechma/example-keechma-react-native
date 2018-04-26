(ns example-rn.components.init
  (:require [example-rn.rn :refer [view text button]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]))

(defn render [ctx]
  (let [d (dimensions)]
    [view {:style {:flex 1
                   :background-color "#ecf0f1"
                   :padding-top (:padding-top d)
                   :padding-bottom (:padding-bottom d)
                   :padding-horizontal 10}}
     [text {:style {:font-size 18}}
      "Hello From Keechma!"]
     [text "This is an example of the route based transitions with Keechma"]
     [button {:on-press #(navigate-go! {:key :about})
              :title "Go to About Page"}]
     [button {:on-press #(navigate-go! {:key :login})
              :title "Open Login Popup"}]
     [button {:on-press #(navigate-go! {:key :taxi-select-type})
              :title "Open Taxi Select Type Page"}]]))

(def component
  (ui/constructor {:renderer render}))
