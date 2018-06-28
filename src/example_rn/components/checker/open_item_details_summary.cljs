(ns example-rn.components.checker.open-item-details-summary
  (:require [example-rn.rn :refer [view text button flat-list image touchable-opacity animated-view hairline-width]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [oops.core :refer [oget]]
            [reagent.core :as r]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]
            [example-rn.components.checker.summary :as summary]))

(defn render [item]
  (let [details (:details item)]
    [view {:style {:flex-direction "column"
                   :border-bottom-width hairline-width
                   :border-bottom-color "#ccc"
                   :padding-bottom 12}}
     [view {:style {:flex-direction "row"
                    :justify-content "space-between"
                    :padding-bottom 8}}
      [text {:style {:color "#bbb"
                     :font-size 14}} (str "TIN " (:tin details))]
      [text {:style {:color "#bbb"
                     :font-size 14}} (str (:date item) " " (:time details))]]
     [view {:style {:flex-direction "row"
                    :justify-content "space-between"
                    :padding-bottom 4}}
      [text {:style {:color "#333"
                     :font-size 16}} (:title item)]
      [text {:style {:color "#333"
                     :font-size 16}} (:status item)]]
     [view {:style {:flex-direction "row"
                    :justify-content "space-between"}}
      [text {:style {:color "#bbb"
                     :font-size 14}} (:detail details)]
      [text {:style {:color "#bbb"
                     :font-size 14}} (:name details)]]]))
