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
            [example-rn.util :refer [with-animation-styles]]
            [example-rn.components.checker.open-item :as open-item]
            [example-rn.components.checker.list-item :as list-item]))

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

(defn get-detail-items-for-id [id]
  (let [item (first (filter #(= (:id %) id) items))]
    (get-in item [:details :items])))

(def navbar-height 50)

(def animation-config
  {:type   :timing
   :config {:duration 400
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/values :check-list-item-opacity/init [_ _]
  {:opacity 1})
(defmethod a/animator :check-list-item-opacity/init [_ _]
  animation-config)

(defmethod a/values :check-list-item-scale/init [_ _]
  {:scale 1
   :translate-y 0})
(defmethod a/animator :check-list-item-scale/init [_ _]
  animation-config)

(defmethod a/values :check-list-header/init [_ _]
  {:opacity 1
   :translate-y 0})
(defmethod a/animator :check-list-header/init [_ _]
  animation-config)

(defmethod a/values :check-open-header/init [_ _]
  {:height 0})
(defmethod a/animator :check-open-header/init [_ _]
  (assoc-in animation-config [:config :useNativeDriver] false))

(defmethod a/values :check-open-background/init [_ _]
  {:opacity 0})
(defmethod a/animator :check-open-background/init [_ _]
  animation-config)

(defmethod a/values :check-open-summary/init [meta _]
  {:translate-y (get-in meta [:args :cell :page-y])})
(defmethod a/animator :check-open-summary/init [_ _]
  animation-config)

(defmethod a/values :check-open-details/init [_ _]
  {:opacity 0
   :translate-y 0})
(defmethod a/animator :check-open-details/init [_ _]
  animation-config)

(defmethod a/values :check-open-details-items/init [meta _]
  (let [open-item-id (get-in meta [:args :id])
        items-ids (mapv :id (get-detail-items-for-id open-item-id))
        items-count (count items-ids)]
    (reduce-kv (fn [m k v]
                 (let [style-ns (str "item-" v)
                       order (inc k)]
                   (assoc m
                          (keyword style-ns "opacity") (- 1 (/ order items-count))
                          (keyword style-ns "translate-y") (- (* 20 order))))) {} items-ids)))

(defmethod a/animator :check-open-details-items/init [_ _]
  animation-config)




(defmethod a/values :check-list-item-scale/open [_ _]
  {:scale 0.95
   :translate-y 10})
(defmethod a/animator :check-list-item-scale/open [_ _]
  animation-config)

(defmethod a/values :check-list-item-opacity/open [_ _]
  {:opacity 0})
(defmethod a/animator :check-list-item-opacity/open [_ _]
  (assoc-in animation-config [:config :duration] 500))

(defmethod a/values :check-list-header/open [_ _]
  {:opacity 0
   :translate-y -10})
(defmethod a/animator :check-list-header/open [_ _]
  animation-config)

(defmethod a/values :check-open-header/open [_ _]
  {:height 120})
(defmethod a/animator :check-open-header/open [_ _]
  (assoc-in animation-config [:config :useNativeDriver] false))

(defmethod a/values :check-open-background/open [_ _]
  {:opacity 0})
(defmethod a/animator :check-open-background/open [_ _]
  animation-config)

(defmethod a/values :check-open-summary/open [_ _]
  {:translate-y 65})
(defmethod a/animator :check-open-summary/open [_ _]
  animation-config)

(defmethod a/values :check-open-details/open [_ _]
  {:opacity 1
   :translate-y 60})
(defmethod a/animator :check-open-details/open [_ _]
  animation-config)

(defmethod a/values :check-open-details-items/open [meta _]
  (let [open-item-id (get-in meta [:args :id])
        items-ids (mapv :id (get-detail-items-for-id open-item-id))
        items-count (count items-ids)]
    (reduce-kv (fn [m k v]
                 (let [style-ns (str "item-" v)
                       order (inc k)]
                   (assoc m
                          (keyword style-ns "opacity") 1
                          (keyword style-ns "translate-y") 0))) {} items-ids)))
(defmethod a/animator :check-open-details-items/open [_ _]
  animation-config)


(defn render [ctx]
  (let [d (dimensions)
        open-check (sub> ctx :open-check)
        animation (:data (sub> ctx :animation :check-list-header))]
    [view {:style {:flex 1
                   :background-color "#fff"
                   :padding-top (:padding-top d)}}
     [animated-view
      {:style (with-animation-styles
                {:padding 12}
                animation)}
      [text {:style {:font-weight "700"
                     :font-size 28}} "My Checks"]]
     [flat-list
      {:style {:height "100%"}
       :data (apply array items)
       :key-extractor #(str (:id %))
       :render-item (fn [data]
                      (r/as-element [list-item/render ctx (oget data "item") (:id open-check)]))}]
     (when open-check
       [open-item/render ctx open-check])]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation
                                       :open-check]}))
