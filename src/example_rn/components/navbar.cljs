(ns example-rn.components.navbar
  (:require [keechma.ui-component :as ui]
            [example-rn.rn :refer [view animated-view hairline-width touchable-opacity text]]
            [example-rn.util.routing :refer [navigate-go!]]
            [example-rn.domain.routing :refer [pages-with-navbar]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [process-transform-styles]]
            [keechma.toolbox.animations.helpers :refer [select-keys-by-namespace]]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util :refer [index-of]]
            [keechma.toolbox.ui :refer [sub>]]))

(def page-titles
  {:init    "Home"
   :about   "About"
   :stories "Stories"})

(defn calc-marker-width []
  (let [{:keys [width]} (dimensions)]
    (/ width (count pages-with-navbar))))

(defmethod a/values :navbar-marker/position [meta _]
  (let [page (:args meta)
        page-idx (index-of pages-with-navbar page)
        marker-width (calc-marker-width)]

    (if (nil? page-idx)
      {:translate-x (- marker-width)}
      {:translate-x (* page-idx marker-width)})))

(defmethod a/animator :navbar-marker/position [meta _]
  {:type   :timing
   :config {:duration 500
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defn render [ctx]
  (let [animation-data (:data (sub> ctx :animation :navbar-marker))]
    [view {:style {:height 50
                   :border-top-color "#333"
                   :border-top-width hairline-width
                   :background-color "#f0f0f0"
                   :flex-direction "row"
                   :position "relative"}}
     (map (fn [p]
            ^{:key p}
            [touchable-opacity
             {:on-press #(navigate-go! {:key p})
              :style {:height 47
                      :width (str (/ 100 (count pages-with-navbar)) "%")
                      :margin-top 3}}
             [view {:style {:flex 1
                            :align-items "center"
                            :justify-content "center"}}
              [text {:style {:color "red"}} (get page-titles p)]]])
          pages-with-navbar)
     [animated-view {:style 
            (process-transform-styles
             (merge {:height 3
                     :width (calc-marker-width)
                     :translate-x (- (calc-marker-width))
                     :background-color "#333"
                     :position "absolute"}
                    animation-data))}]]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation]}))
