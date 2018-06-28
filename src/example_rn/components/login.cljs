(ns example-rn.components.login
  (:require [example-rn.rn :refer [view text button]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]))

(defn render [ctx]
  (let [d (dimensions)]
    [view {:style {:flex 1
                   :background-color "#3498db"
                   :padding-top (:padding-top d)
                   :padding-bottom (:padding-bottom d)
                   :padding-horizontal 10}}
     [text {:style {:font-size 18}}
      "This is a login page!"]
     [text "It opens like a popup"]
     [button {:on-press #(ui/redirect ctx nil :back)
              :title "Close popup"
              :color "yellow"}]]))

(def component
  (ui/constructor {:renderer render}))
