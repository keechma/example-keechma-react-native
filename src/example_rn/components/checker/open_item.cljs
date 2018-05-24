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
            [example-rn.components.checker.open-item-details-summary :as open-item-details-summary]))

(def items
  [{:id 1
    :title "Item #1"
    :status "Received"
    :date "8/9/20"
    :image (js/require "./images/unsplash/1.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 2
    :title "Item #2" 
    :status "Not Received"
    :date "7/2/20"
    :image (js/require "./images/unsplash/2.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}
                      {:id 4
                       :title "Adidas Yeezy"
                       :amount 2
                       :price 135}]}}
   {:id 3
    :title "Item #3"
    :status "Received"
    :date "6/24/20"
    :image (js/require "./images/unsplash/3.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 4
    :title "Item #4"
    :status "Correct"
    :date "5/21/20"
    :image (js/require "./images/unsplash/4.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 5
    :title "Item #5"
    :status "Complaint"
    :date "5/19/20"
    :image (js/require "./images/unsplash/5.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :details "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 6
    :title "Item #6"
    :status "Not Received"
    :date "5/1/20"
    :image (js/require "./images/unsplash/6.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 7
    :title "Item #7"
    :status "Received"
    :date "4/17/20"
    :image (js/require "./images/unsplash/7.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 8
    :title "Item #8"
    :status "Received"
    :date "4/13/20"
    :image (js/require "./images/unsplash/8.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}])

(defn render-item-detail-items [ctx item-details]
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

(defn render [ctx open-item]
  (let [{:keys [cell id]} open-item
        {:keys [width height]} cell
        item (first (filter #(= id (:id %)) items))
        item-details (:details item)
        full-height (:height (dimensions))
        header-animation (:data (sub> ctx :animation :check-open-header))
        background-animation (:data (sub> ctx :animation :check-open-background))
        summary-animation (:data (sub> ctx :animation :check-open-summary))
        details-animation (:data (sub> ctx :animation :check-open-details))]
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
      [render-item-detail-items ctx item-details]
      [button {:title "< Back" :color "white" :on-press #(<cmd ctx [:checker :close])}]]]))
