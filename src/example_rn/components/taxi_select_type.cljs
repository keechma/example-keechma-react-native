(ns example-rn.components.taxi-select-type
  (:require [example-rn.rn :refer [animated-view view text button image touchable-opacity]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]
            [keechma.toolbox.animations.helpers :as helpers]
            [example-rn.animations.rn :as rna]
            [example-rn.util :refer [clamp]]))

(def img-map (js/require "./images/osm.png"))

(def y-delta 140)

(defn init-done-animator [type meta]
  (if (= :panmove (get-in meta [:prev :state]))
    (let [prev (:prev meta)
          velocity (get-in prev [:gesture :velocity])
          [_ y-value] (:pan-value prev)
          value (or y-value 0)
          from-value (if (= :init type) (- 1 value) value)]
      {:type :spring
       :fromValue from-value
       :config {:velocity velocity
                :mass 0.375}})
    {:type   :timing
     :config {:duration 300
              :easing {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}}))

(defn init-anim-values [args]
  (let [{:keys [width]} (dimensions)]
    {:panel/translate-y 0
     :background/opacity 0
     :confirm/translate-y 0
     :confirm/opacity 1
     :done/opacity 0
     :done/translate-y -150
     :title/opacity 1
     :title/translate-y 0
     :vehicle-selector/scale (/ 1 3)
     :vehicle-selector/translate-x (- width)
     :vehicle-selector/translate-y -90
     :vehicle/scale 1.55
     :vehicle-subtitle/opacity 0
     :vehicle-fare/opacity 1
     :vehicle-info/opacity 0
     :vehicle-info/scale 1}))

(defn done-anim-values [args]
  (let [{:keys [width]} (dimensions)
        selected-vehicle-index (:selected-vehicle-index args)
        vehicle-selector-translate-x (* selected-vehicle-index (- width))]
    {:panel/translate-y (- y-delta)
     :background/opacity 0.5
     :confirm/translate-y 400
     :confirm/opacity 0
     :done/opacity 1
     :done/translate-y 0
     :title/opacity 0
     :title/translate-y -25
     :vehicle-selector/scale 1
     :vehicle-selector/translate-x vehicle-selector-translate-x
     :vehicle-selector/translate-y 0
     :vehicle/scale 1
     :vehicle-subtitle/opacity 1
     :vehicle-fare/opacity 0
     :vehicle-info/opacity 1
     :vehicle-info/scale 1}))

(defn panmove-values [args]
  (let [init-values (init-anim-values args)
        done-values (done-anim-values args)
        all-keys (set (concat (keys init-values) (keys done-values)))]
    (reduce
     (fn [m key]
       (let [init (get init-values key)
             done (get done-values key)]
         (assoc m key {:start (or init done)
                       :end (or done init)})))
     {} all-keys)))


(defmethod a/values :taxi-select-type/init [meta _]
  (init-anim-values (:args meta)))

(defmethod a/animator :taxi-select-type/init [meta _]
  (init-done-animator :init meta))

(defmethod a/values :taxi-select-type/done [meta _]
  (done-anim-values (:args meta)))

(defmethod a/animator :taxi-select-type/done [meta _]
  (init-done-animator :done meta))

(defmethod a/animator :taxi-select-type/commit-vehicle [_ _]
  {:type   :timing
   :config {:duration 300
            :easing {:type   :bezier
                     :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/values :taxi-select-type/commit-vehicle [meta _]
  (done-anim-values (:args meta)))

(defmethod rna/pan-step :taxi-select-type/panmove [meta [x y]]
  (let [args (:args meta)
        selected-vehicle-index (:selected-vehicle-index args)
        args-with-x-move (assoc args :selected-vehicle-index (+ x selected-vehicle-index))]
    (reduce-kv (fn [m k v]
                 (assoc m k (helpers/map-value-in-range y (:start v) (:end v))))
               {} (panmove-values args-with-x-move))))

(defmethod rna/pan-init-value :taxi-select-type/panmove [meta]
  (let [args (:args meta)
        prev (:prev meta)
        [_ prev-pan-value] (:pan-value prev)
        prev-anim-state (:state prev) 
        init-y (cond
                 (and (= :panmove prev-anim-state) prev-pan-value) prev-pan-value
                 (= :init prev-anim-state) 0
                 :else 1)]
    [0 init-y]))

(defn calculate-panmove-y [meta]
  (let [gesture (:gesture meta)
        [init-x init-y] (:pan-init-value meta)
        init-y-left-delta (* init-y y-delta)
        init-y-spent-delta (- y-delta init-y-left-delta)
        move-y (:moveY gesture)
        y0 (:y0 gesture)]
    (if (= (:dy gesture) 0)
      init-y
      (clamp 0 1 (- 1 (helpers/map-value-in-range move-y 0 1 (- y0 init-y-spent-delta) (+ y0 init-y-left-delta)))))))

(defn calculate-panmove-x [meta]
  (let [prev (:prev meta)
        gesture (:gesture meta)
        dx (:dx gesture)
        prev-state (:state prev)
        selected-vehicle-index (get-in meta [:args :selected-vehicle-index])
        clamp-min (if (= 0 selected-vehicle-index) -0.25 -1)
        clamp-max (if (= 2 selected-vehicle-index) 0.25 1)]
    (when (and (or (= :done prev-state)
                   (= :commit-vehicle prev-state))
               (< 257 (:y0 gesture) 520)  ;; Gesture started in the "card" area, should be generalized to take phone dimensions in to account
               (< 10 (.abs js/Math dx))) ;; Moved more than 10 pixels left or right
      (let [args (:args meta)
            selected-vehicle-index (:selected-vehicle-index meta)
            {:keys (width)} (dimensions)]
        (clamp clamp-min clamp-max (* -1 (/ dx width)))))))

(defmethod rna/pan-value :taxi-select-type/panmove [meta]
  (let [args (:args meta)
        [_ init-y] (:pan-init-value meta)
        x-value (calculate-panmove-x meta)] 

    (if x-value
      [x-value init-y]
      [0 (calculate-panmove-y meta)])))


(defn render-vehicle-selector [ctx]
  (let [{:keys [width]} (dimensions)
        animation (sub> ctx :animation :taxi-select-type)
        animation-data (:data animation)
        selected-vehicle (sub> ctx :selected-vehicle)
        vehicles (sub> ctx :vehicles)]
    [view
     {:style {:width (* 3 width)
              :flex-direction "row"}}
     (map-indexed
      (fn [idx v]
        ^{:key (:id v)}
        [touchable-opacity
         {:on-press (fn []
                      (when (= :init (get-in animation [:meta :prev :state]))
                        (<cmd ctx [:taxi-select-type :select-vehicle] (:id v))))}
         [animated-view
          {:style 
           (with-animation-styles
             {:width width
              :align-items "center"}
             animation-data
             :vehicle)}
          
          [view {:style {:width 120
                         :height 120
                         :border-radius 60
                         :margin-bottom 20
                         :background-color (if (= selected-vehicle (:id v)) "yellow" "gray")}}]
          [text {:style {:font-size 36
                         :text-align "center"}}
           (:title v)]
          [view
           {:style {:position "relative"
                    :width width}}
           [animated-view
            {:style (with-animation-styles
                      {:position "absolute"
                       :width width}
                      animation-data
                      :vehicle-subtitle)}
            [text {:style {:font-size 30
                           :text-align "center"}}
             (:subtitle v)]]
           [animated-view
            {:style (with-animation-styles
                      {:position "absolute"
                       :width width}
                      animation-data
                      :vehicle-fare)} 
            [text {:style {:font-size 30
                           :text-align "center"}}
             (:fare v)]]]
          [animated-view
           {:style
            (with-animation-styles
              {:margin-top 50}
              animation-data
              :vehicle-info)}
           [view {:style {:justify-content "space-between"
                          :flex-direction "row"
                          :width (* .7 width)}}
            [text "Fare"]
            [text (:fare v)]]
           [view {:style {:justify-content "space-between"
                          :flex-direction "row"
                          :width (* .7 width)}}
            [text "Capacity"]
            [text (:capacity v)]]]]]) vehicles)]))

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
          {:opacity 1
           :padding-top 10}
          animation-data
          :title)}
       [text {:style {:font-size 18
                      :text-align "center"}}
        "Popular"]
       [text {:style {:font-size 14
                      :text-align "center"
                      :color "gray"}}
        "Something about the selection"]]
      [animated-view
       {:style
        (with-animation-styles
          {:margin-left -10}
          animation-data
          :vehicle-selector)}
       [render-vehicle-selector ctx]]
      [animated-view
       {:style
        (with-animation-styles
          {:position "absolute"
           :border-top-width 1
           :border-top-color "#ccc"
           :padding-top 10
           :left 10
           :bottom (+ 10 y-delta)
           :z-index 1
           :elevation 1
           :width "100%"}
          animation-data
          :confirm)}
       [touchable-opacity
        {:on-press #(navigate-go! {:key :init})}
        [view {:style {:background-color "#222"
                       :padding 15
                       :width "100%"}}
         [text {:style {:color "white"
                        :text-align "center"}} "GO BACK"]]]]
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
                   :subscription-deps [:animation
                                       :selected-vehicle
                                       :vehicles]}))
