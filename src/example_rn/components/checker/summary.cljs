(ns example-rn.components.checker.summary
  (:require [example-rn.rn :refer [view text button flat-list image touchable-opacity animated-view hairline-width]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [oops.core :refer [oget]]
            [reagent.core :as r]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]))

(defn price-total [items]
  (reduce (fn [s k]
            (+ s (* (:amount k) (:price k)))) 0 items))

(defn render [item]
  [view {:style {:position "relative"
                 :width "100%"
                 :height 110
                 :justify-content "center"
                 :align-items "center"
                 :border-radius 8
                 :border-width hairline-width
                 :border-color "#dddddd"
                 :margin-bottom 8
                 :background-color "white"
                 :shadow-color "#000000"
                 :shadow-opacity 0.05
                 :shadow-offset {:width 0
                                 :height 4}
                 :shadow-radius 4}}
   [view {:style {:flex-direction "row"
                  :flex 1
                  :padding-horizontal 12
                  :width "100%"
                  :align-items "center"
                  :justify-content "flex-start"}}
    [image {:source (:image item)
            :style {:resize-mode "cover"
                    :width 30
                    :height 30
                    :border-radius 15
                    :overflow "hidden"}}]
    [text {:style {:font-size 14
                   :margin-left 6}} (:title item)]]
   [view {:style {:flex-direction "row"
                  :flex 1
                  :width "100%"
                  :padding-horizontal 12
                  :align-items "center"}}
    [view {:style {:flex-direction "column"
                   :flex 1}}
     [text {:style {:color "#bbb"
                    :font-size 12
                    :margin-bottom 4}} "Amount"]
     [text {:style {:font-size 14
                    :font-weight "700"
                    :color "#333"}} (str "$" (price-total (get-in item [:details :items])) ".00")]]
    [view {:style {:flex-direction "column"
                   :flex 1}}
     [text {:style {:color "#bbb"
                    :font-size 12
                    :margin-bottom 4}} "Date"]
     [text {:style {:font-size 14
                    :font-weight "500"
                    :color "#333"}} (:date item)]]
    [view {:style {:flex-direction "column"
                   :flex 1}}
     [text {:style {:color "#bbb"
                    :font-size 12
                    :margin-bottom 4}} "Status"]
     [text {:style {:font-size 14
                    :font-weight "500"
                    :color "#333"}} (:status item)]]]])
