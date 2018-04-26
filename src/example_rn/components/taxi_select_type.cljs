(ns example-rn.components.taxi-select-type
  (:require [example-rn.rn :refer [view text button]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util.routing :refer [navigate-go!]]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [process-transform-styles]]
            [keechma.toolbox.animations.helpers :as helpers :refer [select-keys-by-namespace]]
            [example-rn.animations.rn :as rna]))

(def y-delta 300)

(defmethod a/values :taxi-select-type/init [_ _]
  {:panel/translate-y 0})

#_(defmethod a/values :taxi-select-type/panmove [_ _]
  {:panel/translate-y (- y-delta)})

#_(defmethod a/step :taxi-select-type/panmove [meta start-end]
  (let [gesture (:gesture meta)
        


        move-delta (min y-delta (* -1 (min 0 (:dy gesture))))
        move-value (helpers/map-value-in-range move-delta 0 1 0 y-delta)]

    (println )


    (helpers/get-current-styles move-value start-end)))

(defmethod rna/pan-start-values :taxi-select-type/panmove [_]
  {:panel/translate-y 0})

(defmethod rna/pan-end-values :taxi-select-type/panmove [_]
  {:panel/translate-y (- y-delta)})

(defn clamp [min-value max-value value]
  (max min-value (min max-value value)))

(defmethod rna/pan-init-value :taxi-select-type/panmove [meta]
  (let [prev (:prev meta)
        prev-pan-value (:pan-value prev)
        prev-anim-state (:state prev)]

    (println "PREV PAN VALUE" prev-pan-value)


    (cond
      (and (= :panmove prev-anim-state) prev-pan-value) prev-pan-value
      (= :init prev-anim-state) 0
      :else 1)))

(defmethod rna/pan-value :taxi-select-type/panmove [meta]
  (let [gesture (:gesture meta)
        init-pan-value (:pan-init-value meta)
        init-left-delta (* init-pan-value y-delta)
        init-spent-delta (- y-delta init-left-delta)
        move-y (:moveY gesture)
        y0 (:y0 gesture)]

    (if (= (:dy gesture) 0)
      init-pan-value
      (clamp 0 1 (- 1 (helpers/map-value-in-range move-y 0 1 (- y0 init-spent-delta) (+ y0 init-left-delta)))))))



(defn render [ctx]
  (let [{:keys [width padding-top padding-bottom]} (dimensions)
        animation (sub> ctx :animation :taxi-select-type)
        animation-data (:data animation)]

    (println animation-data)

    [view {:style {:flex 1
                   :background-color "#ecf0f1"
                   :padding-top padding-top
                   :padding-bottom padding-bottom
                   :padding-horizontal 10}}

     [view (merge
            {:style (process-transform-styles
                     (merge {:position "absolute"
                             :bottom 0
                             :background-color "red"
                             :height 400
                             :margin-bottom -300
                             :width width}
                            (select-keys-by-namespace animation-data :panel)))}
            (get-in animation [:meta :pan-handlers]))]]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation]}))
