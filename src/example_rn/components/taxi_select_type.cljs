(ns example-rn.components.taxi-select-type
  (:require [example-rn.rn :refer [animated-view view text button image touchable-opacity]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [process-transform-styles]]
            [keechma.toolbox.animations.helpers :as helpers :refer [select-keys-by-namespace]]
            [example-rn.animations.rn :as rna]))

(defn clamp [min-value max-value value]
  (max min-value (min max-value value)))

(def img-map (js/require "./images/osm.png"))

(def y-delta 190)

(defn init-done-animator [meta]
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

(def init-anim-values
  {:panel/translate-y 0
   :background/opacity 0
   :confirm/translate-y 0
   :confirm/opacity 1
   :done/opacity 0
   :done/translate-y -150})

(def done-anim-values
  {:panel/translate-y (- y-delta)
   :background/opacity 0.5
   :confirm/translate-y 400
   :confirm/opacity 0
   :done/opacity 1
   :done/translate-y 0})

(def panmove-values
  (let [all-keys (set (concat (keys init-anim-values) (keys done-anim-values)))]
    (reduce
     (fn [m key]
       (let [init (get init-anim-values key)
             done (get done-anim-values key)]
         (assoc m key {:init (or init done)
                       :done (or done init)})))
     {} all-keys)))


(defmethod a/values :taxi-select-type/init [_ _]
  init-anim-values)

(defmethod a/animator :taxi-select-type/init [meta _]
  (init-done-animator meta))

(defmethod a/values :taxi-select-type/done [_ _]
  done-anim-values)

(defmethod a/animator :taxi-select-type/done [meta _]
  (init-done-animator meta))

(defmethod rna/pan-step :taxi-select-type/panmove [meta [x y]]
  (reduce-kv (fn [m k v]
               (assoc m k (helpers/map-value-in-range y (:init v) (:done v))))
             {} panmove-values))

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
     [image {:source img-map
             :style {:width width
                     :height height
                     :position "absolute"}}]
     [animated-view
      {:style (with-animation-styles
                {:position "absolute"
                 :width width
                 :height height
                 :background-color "#333"
                 :opacity 0}
                animation-data
                :background)}]

     [animated-view
      (merge
       {:style (with-animation-styles
                 {:position "absolute"
                  :bottom 0
                  :background-color "white"
                  :border-top-width 1
                  :border-top-color "#999"
                  :height (+ 330 y-delta)
                  :margin-bottom (- y-delta)
                  :width width
                  :shadow-radius 5
                  :shadow-color "#000"
                  :shadow-opacity 0.1
                  :padding 10}
                 animation-data
                 :panel)}
       (get-in animation [:meta :pan-handlers]))
      [animated-view
       {:style
        (with-animation-styles
          {:position "absolute"
           :border-top-width 1
           :border-top-color "#ccc"
           :padding-top 10
           :left 10
           :bottom (+ 10 y-delta)
           :width "100%"}
          animation-data
          :confirm)}
       [view {:style {:background-color "#222"
                      :padding 15
                      :width "100%"}}
        [text {:style {:color "white"
                       :text-align "center"}} "CONFIRM TAXI"]]]
      [animated-view
       {:style
        (with-animation-styles
          {:position "absolute"
           :border-top-width 1
           :border-top-color "#ccc"
           :left 10
           :bottom 0
           :width "100%"}
          animation-data
          :done)}
       [touchable-opacity
        {:on-press #(<cmd ctx [:taxi-select-type :close] nil)}
        [view {:style {:width "100%"
                       :height "100%"
                       :padding-vertical 20}}
         [text {:style {:color "black"
                        :text-align "center"}}
          "DONE"]]]]]]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation]}))
