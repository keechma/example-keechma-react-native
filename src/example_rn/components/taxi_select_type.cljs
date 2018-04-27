(ns example-rn.components.taxi-select-type
  (:require [example-rn.rn :refer [animated-view view text button]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [process-transform-styles]]
            [keechma.toolbox.animations.helpers :as helpers :refer [select-keys-by-namespace]]
            [example-rn.animations.rn :as rna]))

(def y-delta 500)

(defmethod a/values :taxi-select-type/init [_ _]
  {:panel/translate-y 0
   :background/opacity 0})

(defmethod a/animator :taxi-select-type/init [meta _]
  (if (= :panmove (get-in meta [:prev :state]))
    (let [prev (:prev meta)
          velocity (get-in prev [:gesture :velocity])]
      {:type :spring
       :config {:velocity velocity
                :mass 0.3}})
    {:type   :timing
     :config {:duration 500
              :easing   {:type   :bezier
                         :values [0.2833 0.99 0.31833 0.99]}}}))

(defmethod a/values :taxi-select-type/done [_ _]
  {:panel/translate-y (- y-delta)
   :background/opacity 1})

(defmethod a/animator :taxi-select-type/done [meta _]
  (if (= :panmove (get-in meta [:prev :state]))
    (let [prev (:prev meta)
          velocity (get-in prev [:gesture :velocity])]
      {:type :spring
       :config {:velocity velocity
                :mass 0.3}})
    {:type   :timing
     :config {:duration 500
              :easing   {:type   :bezier
                         :values [0.2833 0.99 0.31833 0.99]}}}))

(defn clamp [min-value max-value value]
  (max min-value (min max-value value)))

(defmethod rna/pan-step :taxi-select-type/panmove [meta [x y]]
  {:panel/translate-y (- (helpers/map-value-in-range y 0 y-delta))
   :background/opacity (clamp 0 1 (helpers/map-value-in-range y 0 1))})

(defmethod rna/pan-init-value :taxi-select-type/panmove [meta]
  (let [prev (:prev meta)
        [_ prev-pan-value] (:pan-value prev)
        prev-anim-state (:state prev)
        init-y (cond
                 (and (= :panmove prev-anim-state) prev-pan-value) prev-pan-value
                 (= :init prev-anim-state) 0
                 :else 1)]
    [0 init-y]))

(defmethod rna/pan-value :taxi-select-type/panmove [meta]
  (let [gesture (:gesture meta)
        [_ init-pan-value] (:pan-init-value meta)
        init-left-delta (* init-pan-value y-delta)
        init-spent-delta (- y-delta init-left-delta)
        move-y (:moveY gesture)
        y0 (:y0 gesture)
        y-value (if (= (:dy gesture) 0)
                  init-pan-value
                  (clamp 0 1 (- 1 (helpers/map-value-in-range move-y 0 1 (- y0 init-spent-delta) (+ y0 init-left-delta)))))]

    [0 y-value]))

(defn with-animation-styles
  ([animation-styles] (with-animation-styles {} animation-styles nil))
  ([static-styles animation-styles] (with-animation-styles static-styles animation-styles nil))
  ([static-styles animation-styles a-namespace]
   (let [a-styles (if (nil? a-namespace) animation-styles (select-keys-by-namespace animation-styles a-namespace))]
     (process-transform-styles (merge static-styles a-styles)))))

(defn render [ctx]
  (let [{:keys [width height padding-top padding-bottom]} (dimensions)
        animation (sub> ctx :animation :taxi-select-type)
        animation-data (:data animation)]

    [view {:style {:flex 1
                   :background-color "#ecf0f1"
                   :padding-top padding-top
                   :padding-bottom padding-bottom
                   :padding-horizontal 10}}
     [animated-view
      {:style (with-animation-styles
                {:position "absolute"
                 :width width
                 :height height
                 :background-color "#333"
                 :opacity 0}
                animation-data
                :background)}]

     [animated-view (merge
                     {:style (with-animation-styles
                              {:position "absolute"
                               :bottom 0
                               :background-color "red"
                               :height (+ y-delta 100)
                               :margin-bottom (- y-delta)
                               :width width}
                              animation-data
                              :panel)}
                     (get-in animation [:meta :pan-handlers]))]]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation]}))
