(ns example-rn.components.checker.list-item
  (:require [example-rn.rn :refer [view text button flat-list image touchable-opacity animated-view hairline-width]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [oops.core :refer [oget]]
            [reagent.core :as r]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]
            [example-rn.components.checker.summary :as summary]
            [example-rn.components.checker.open-item-details-summary :as open-item-details-summary]
            [example-rn.components.checker.open-item :as open-item]))

(defn render [ctx item open-item-id]
  (let [cell-ref (atom nil)]
    (fn [ctx item open-item-id]
      (let [{:keys [width height]} (dimensions)
            init-animation (:data (sub> ctx :animation :check-list-item-init (:id item)))
            opacity-animation (:data (sub> ctx :animation :check-list-item-opacity))
            scale-animation (:data (sub> ctx :animation :check-list-item-scale))]
        [view
         {:style {:padding-horizontal 12
                  :opacity (if (= open-item-id (:id item)) 0 1)}
          :ref #(reset! cell-ref %)}
         [animated-view
          {:style (with-animation-styles
                    {}
                    (merge init-animation scale-animation opacity-animation))}
          [touchable-opacity
           {:on-press #(<cmd ctx [:checker :open] {:id (:id item) :cell @cell-ref})}
           [summary/render item]]]]))))
