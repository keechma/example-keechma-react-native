(ns example-rn.components.main
  (:require [keechma.ui-component :as ui]
            [example-rn.rn :refer [view text]]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [current-route>]]))

(defn render [ctx]
  [view {:style {:flex 1
                 :position "relative"
                 :background-color "white"}}
   [(ui/component ctx :router)]])

(def component
  (ui/constructor {:renderer render
                   :component-deps [:router]}))
