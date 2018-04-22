(ns example-rn.components.about
  (:require [example-rn.rn :refer [view text button]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]))

(defn render [ctx]
  (let [d (dimensions)]
    [view {:style {:flex 1
                   :background-color "#f1c40f"
                   :padding-top (:padding-top d)
                   :padding-bottom (:padding-bottom d)
                   :padding-horizontal 10}}
     [text {:style {:font-size 18}}
      "This is about page!"]
     [text "This is an example of the route based transitions with Keechma"]
     [button {:on-press #(navigate-go! {:key :init})
              :title "Go to Home Page"}]
     [button {:on-press #(navigate-go! {:key :login})
              :title "Open Login Popup"}]]))

(def component
  (ui/constructor {:renderer render}))
