(ns example-rn.components.checker
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

(def navbar-height 50)

(defn price-total [items]
  (reduce (fn [s k]
            (+ s (* (:amount k) (:price k)))) 0 items))

(defmethod a/values :check/init [meta _]
  (let [open-item (:args meta)]
    (println "AAAAA->>>" open-item)
    {:list-item/scale 1
     :list-item/opacity 1
     :header/height 0
     :panel-background/opacity 0
     :open-summary/translate-y (get-in open-item [:cell :page-y])
     :open-details/opacity 0
     :open-details/translate-y 100}))

(defmethod a/animator :open-check/init [meta _]
  {:type   :timing
   :config {:duration 400
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})


(defn render-item-summary [item]
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

(defn render-item-detail-summary [item]
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

(defn render-item-detail-items [item-details]
  [view
   (map (fn [item]
          [view {:key (:id item)
                 :style {:width "100%"
                         :padding-vertical 8
                         :flex-direction "row"
                         :justify-content "space-between"}}
           [view {:style {:flex-direction "column"}}
            [text {:style {:color "#333"
                           :font-size 14
                           :margin-bottom 2}} (:title item)]
            [text {:style {:color "#bbb"
                           :font-size 12}} (str "$" (:price item) ".00 x" (:amount item) " (Including VAT 10%)")]]
           [view
            [text {:style {:color "#333"
                           :font-size 16}} (str "$" (* (:amount item) (:price item)) ".00")
             ]]]) (:items item-details))])

(defn render-open-item [ctx open-item]
  (let [{:keys [cell id]} open-item
        {:keys [width height]} cell
        item (first (filter #(= id (:id %)) items))
        item-details (:details item)
        full-height (- (:height (dimensions)) navbar-height)
        animation (:data (sub> ctx :animation :open-check))] 
    [animated-view
     {:style (with-animation-styles
               {:position "absolute"
                :width width
                :height full-height
                :top 0}
               animation
               :container)} 
     [animated-view
      {:style (with-animation-styles {:background-color "white"
                                      :position "absolute"
                                      :height "100%"
                                      :width "100%"
                                      :opacity 0}
                animation
                :background)}]
     [view {:style {:position "absolute"
                    :top 0
                    :height 100
                    :background-color "#0082FE"
                    :width width}}
      [button {:title "< Back" :color "white" :on-press #(<cmd ctx [:checker :close])}]]
     [view {:padding-horizontal 12}
      [render-item-summary item]]
     [animated-view
      {:style (with-animation-styles
                {:opacity 0
                 :padding 20}
                animation
                :body)}
      [render-item-detail-summary item]
      [render-item-detail-items item-details]
      [button {:title "< Back" :color "white" :on-press #(<cmd ctx [:checker :close])}]]]))


(defn render-list-item [ctx item open-item-id]
  (let [cell-ref (atom nil)]
    (fn [ctx item open-item-id]
      (let [{:keys [width height]} (dimensions)
            animation (:data (sub> ctx :animation :check))]
        [view
         {:style {:padding-horizontal 12
                         :opacity (if (= open-item-id (:id item)) 0 1)}
               :ref #(reset! cell-ref %)}
         [animated-view
          {:style (with-animation-styles
                    {}
                    animation
                    :list-item)}
          [touchable-opacity
           {:on-press #(<cmd ctx [:checker :open] {:id (:id item) :cell @cell-ref})}
           [render-item-summary item]]]]))))


(defn render [ctx]
  (let [d (dimensions)
        open-check (sub> ctx :open-check)]
    [view {:style {:flex 1
                   :background-color "#fff"
                   :padding-top (:padding-top d)}}
     [view {:style {:padding 12}}
      [text {:style {:font-weight "700"
                     :font-size 28}} "My Checks"]]
     [flat-list
      {:style {:height "100%"}
       :data (apply array items)
       :key-extractor #(str (:id %))
       :render-item (fn [data]
                      (r/as-element [render-list-item ctx (oget data "item") (:id open-check)]))}]
     (when open-check
       [render-open-item ctx open-check])]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation
                                       :open-check]}))
