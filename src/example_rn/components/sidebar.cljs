(ns example-rn.components.sidebar
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [example-rn.rn :refer [view animated-view button text]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]
            [example-rn.util.dimensions :refer [dimensions]]
            [example-rn.util :refer [index-of]]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [example-rn.animations.rn :as rna]
            [keechma.toolbox.animations.helpers :as helpers]
            [example-rn.util :refer [clamp]]))

(defn get-sidebar-width []
  (let [{:keys [width]} (dimensions)]
    (* width .67)))

(def animator
  {:type   :timing
   :config {:duration 500
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/values :sidebar/init [_ _]
  {:sidebar/translate-x 0
   :overlay/opacity 0})

(defmethod a/animator :sidebar/init [meta _]
  (let [velocity (get-in meta [:prev :gesture :vx])
        [x-value _] (get-in meta [:prev :pan-value])]
    (if (and velocity x-value)
      {:type :spring
       :fromValue (.abs js/Math x-value)
       :config {:mass 0.375}}
      animator)))

(defmethod a/values :sidebar/left [_ _]
  (let [{:keys [width]} (dimensions)]
    {:sidebar/translate-x (get-sidebar-width)
     :overlay/opacity 1}))

(defmethod a/animator :sidebar/left [_ _]
  animator)

(defmethod a/values :sidebar/right [_ _]
  (let [{:keys [width]} (dimensions)]
    {:sidebar/translate-x (- (get-sidebar-width))
     :overlay/opacity 1}))

(defmethod a/animator :sidebar/right [_ _]
  animator)

(defmethod rna/pan-init-value :sidebar/panmove [meta]
  (let [prev-state (get-in meta [:prev :state])
        prev-x (case prev-state
                 :left 1
                 :right -1
                 :panmove (first (get-in meta [:prev :pan-value]))
                 1)]
    [prev-x 0]))

(defmethod rna/pan-value :sidebar/panmove [meta]
  (let [prev-state (get-in meta [:prev :state])
        [init-x _] (:pan-init-value meta)
        gesture (:gesture meta)
        dx (:dx gesture)
        sidebar-width (get-sidebar-width)] 

    (cond
      (and (= 1 init-x) (neg? dx)) [(+ 1 (/ dx sidebar-width)) 0]
      (and (= -1 init-x) (pos? dx)) [(+ -1 (/ dx sidebar-width)) 0]
      :else [init-x 0])))

(defmethod rna/pan-step :sidebar/panmove [meta [x _]]
  (let [prev-state (get-in meta [:prev :meta :state])
        init-x (get-in meta [:pan-init-value :x])
        sidebar-width (get-sidebar-width)
        start-translate-x (if (= 1 init-x) sidebar-width (- sidebar-width))] 

    {:sidebar/translate-x (* x sidebar-width)
     :overlay/opacity (.abs js/Math x)}))

(defn render [ctx & children]
  (let [animation (sub> ctx :animation :sidebar)
        animation-data (:data animation)
        {:keys [width height]} (dimensions)
        sidebar-width (get-sidebar-width)]
    [animated-view
     (merge
      {:style (with-animation-styles
                {:flex 1
                 :width (+ width (* 2 sidebar-width))
                 :flex-direction "row"
                 :position "relative"
                 :margin-left (- (get-sidebar-width))}
                animation-data
                :sidebar)}
      (get-in animation [:meta :pan-handlers]))
     [view {:style {:background-color "#eee"
                    :height "100%"
                    :justify-content "center"
                    :width sidebar-width}}
      [button {:on-press #(<cmd ctx [:sidebar :close])
               :title "Close Sidebar"}]
      [button {:on-press #(ui/redirect ctx {:key :about})
               :title "Go To About Page"}]]
     (into [view {:style {:height "100%"
                          :width width}}] children)
     [animated-view
      {:style (with-animation-styles
                {:position "absolute"
                 :left sidebar-width
                 :width (if (= 0 (:overlay/opacity animation-data)) 0 width)
                 :height "100%"
                 :background-color "rgba(0,0,0,0.5)"}
                animation-data
                :overlay)}]
     [view {:style {:background-color "#eee"
                    :height "100%"
                    :justify-content "center"
                    :width sidebar-width}}
       [button {:on-press #(<cmd ctx [:sidebar :close])
               :title "Close Sidebar"}]]]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation]}))
