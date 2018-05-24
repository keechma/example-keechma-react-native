(ns example-rn.components.checker.open-item
  (:require [example-rn.rn :refer [view text button flat-list image touchable-opacity animated-view hairline-width]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]
            [oops.core :refer [oget]]
            [reagent.core :as r]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]
            [example-rn.components.checker.summary :as summary]
            [example-rn.components.checker.open-item-details-summary :as open-item-details-summary]
            [example-rn.components.checker.open-item-details :as open-item-details]))



(defn render [ctx open-item]
  (let [{:keys [cell id]} open-item
        {:keys [width height]} cell
        items (sub> ctx :checks)
        item (first (filter #(= id (:id %)) items))
        item-details (:details item)
        full-height (:height (dimensions))
        header-animation (:data (sub> ctx :animation :check-open-header))
        background-animation (:data (sub> ctx :animation :check-open-background))
        summary-animation (:data (sub> ctx :animation :check-open-summary))
        details-animation (:data (sub> ctx :animation :check-open-details))
        blue-button-animation (:data (sub> ctx :animation :check-open-buttons :blue))
        red-button-animation (:data (sub> ctx :animation :check-open-buttons :red))]
    [animated-view {:style {:position "absolute"
                            :width width
                            :height full-height
                            :top 0}} 
     [animated-view
      {:style (with-animation-styles
                {:background-color "white"
                 :position "absolute"
                 :height "100%"
                 :width "100%"
                 :opacity 0}
                background-animation)}]
     [animated-view
      {:style (with-animation-styles
                {:position "absolute"
                 :top 0
                 :height 100
                 :background-color "#0082FE"
                 :width width
                 :overflow "hidden"}
                header-animation)}
      [view {:style {:margin-top 20}}
       [button {:title "< Back" :color "white" :on-press #(<cmd ctx [:checker :close])}]]]
     
     [animated-view
      {:style (with-animation-styles
                {:padding-horizontal 12
                 :z-index 1000
                 :position "relative"}
                summary-animation)}
      [summary/render item]]
     [animated-view
      {:style (with-animation-styles
                {:padding 20}
                details-animation)}
      [open-item-details-summary/render item]
      [open-item-details/render ctx item-details]]
     [view {:style {:position "absolute"
                    :bottom 40
                    :justify-content "center"
                    :align-items "center"
                    :width width}}
      [view {:style {:justify-content "space-between"
                     :flex-direction "row"
                     :width "33%"}}
       [animated-view {:style (with-animation-styles
                                {}
                                red-button-animation)} 
        [view {:style {:width 40
                       :height 40
                       :border-radius 20
                       :overflow "hidden"
                       :justify-content "center"
                       :align-items "center"
                       :background-color "#FF2743"}}
         [text {:style {:color "white"
                        :font-size 16}} "A"]]]
       [animated-view {:style (with-animation-styles
                                {}
                                blue-button-animation)}
        [view {:style {:width 40
                       :height 40
                       :border-radius 20
                       :overflow "hidden"
                       :justify-content "center"
                       :align-items "center"
                       :background-color "#0082FF"}}
         [text {:style {:color "white"
                        :font-size 16}} "B"]]]]]]))
