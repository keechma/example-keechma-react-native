(ns example-rn.components.loader
  (:require [example-rn.rn :refer [view text]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [view {:style {:flex 1
                 :background-color "#2ecc71"
                 :justify-content "center"
                 :align-items "center"}}
   [text {:style {:color "white"
                  :font-size 26}}
    "Loading!"]])

(def component
  (ui/constructor {:renderer render}))
