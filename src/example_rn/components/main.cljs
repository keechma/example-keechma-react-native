(ns example-rn.components.main
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub>]]
            [example-rn.rn :refer [view animated-view text]]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [current-route>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]
            [keechma.toolbox.animations.helpers]
            [example-rn.util.dimensions :refer [dimensions]]))

(def navbar-height 50)

(defmethod a/values :navbar/init [meta _]
  (let [animation (:args meta)
        {:keys [height]} (dimensions)]
    (case animation
      :show {:navbar/translate-y navbar-height
             :router/height height}
      :hide {:navbar/translate-y 0
             :router/height height}
      {})))

(defmethod a/values :navbar/slide [meta _]
  (let [animation (:args meta)
        {:keys [height]} (dimensions)]
    (case animation
      :show {:navbar/translate-y 0
             :router/height height}
      :hide {:navbar/translate-y navbar-height
             :router/height height}
      {})))

(defmethod a/animator :navbar/slide [meta _]
  {:type   :timing
   :config {:duration 500
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/done? :navbar/slide [meta animator]
  (or (animator/done? animator)
      (>= (:position meta) 1)))

(defmethod a/values :navbar/slide-end [meta _]
  (let [animation (:args meta)
        {:keys [height]} (dimensions)]
    (case animation
      :show {:router/height (- height navbar-height)
             :navbar/translate-y 0}
      :hide {:router/height height
             :navbar/translate-y navbar-height})))

(defn render [ctx]
  (let [{:keys [width height]} (dimensions)
        navbar-animation (:data (sub> ctx :animation :navbar))]
    [view {:style {:flex 1
                   :position "relative"
                   :background-color "white"}}
     [animated-view
      {:style (with-animation-styles
                {:height height}
                navbar-animation
                :router)}
      [(ui/component ctx :router)]]
     [animated-view
      {:style (with-animation-styles
                {:bottom 0
                 :width width
                 :position "absolute"
                 :translate-y navbar-height}
                navbar-animation
                :navbar)}
      [(ui/component ctx :navbar)]]]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation]
                   :component-deps [:router
                                    :navbar]}))
