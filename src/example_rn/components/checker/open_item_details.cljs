(ns example-rn.components.checker.open-item-details
  (:require [example-rn.rn :refer [view text button flat-list image touchable-opacity animated-view hairline-width]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]
            [oops.core :refer [oget]]
            [reagent.core :as r]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]))

(defn render [ctx item-details]
  (let [animation (:data (sub> ctx :animation :check-open-details-items))]
    [view
     (map
      (fn [item]
        (let [style-ns (keyword (str "item-" (:id item)))]
          [animated-view
           {:key (:id item)
            :style (with-animation-styles
                     {:width "100%"
                      :padding-vertical 8
                      :flex-direction "row"
                      :justify-content "space-between"}
                     animation
                     style-ns)}
           [view {:style {:flex-direction "column"}}
            [text {:style {:color "#333"
                           :font-size 14
                           :margin-bottom 2}} (:title item)]
            [text {:style {:color "#bbb"
                           :font-size 12}} (str "$" (:price item) ".00 x" (:amount item) " (Including VAT 10%)")]]
           [view
            [text {:style {:color "#333"
                           :font-size 16}} (str "$" (* (:amount item) (:price item)) ".00")]]])) (:items item-details))]))
