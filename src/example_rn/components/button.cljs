(ns example-rn.components.button
  (:require [example-rn.rn :refer [view
                                   animated-view
                                   touchable-without-feedback
                                   animated-text
                                   text
                                   button
                                   image
                                   switch]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [keechma.app-state.react-native-router :refer [navigate!]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles process-transform-styles]]
            [example-rn.util.dimensions :refer [dimensions]]
            [keechma.toolbox.animations.helpers :refer [select-keys-by-namespace]]
            [keechma.toolbox.ui :refer [<cmd sub>]]))

(def checkmark (js/require "./images/checkmark.png"))
(def close (js/require "./images/close.png"))

(defn make-spring-animator
  ([] (make-spring-animator {}))
  ([config]
   {:type :spring
    :config (merge
             {:duration 500
              :useNativeDriver false
              :overshootClamping true}
             config)}))

(defmethod a/values :button/init [_]
  {:button/border-radius    5
   :button/height           50
   :button/width            200
   :button/border-width     2
   :button/border-color     "#0061ff"
   :button/background-color "#222"
   :button/scale            1
   :label/color             "#3883ff"
   :label/font-size         16})

(defmethod a/animator :button/init [meta data]
  (let [prev (:prev meta)
        prev-value (:value prev)]
    (make-spring-animator {:tension 150})))


(defmethod a/values :button/pressed [_]
  {:button/border-radius    5
   :button/height           50
   :button/width            200
   :button/border-width     2
   :button/border-color     "#0061ff"
   :button/background-color "#0061ff"
   :button/scale            .9
   :label/color             "#fff"
   :label/font-size         16})

(defmethod a/animator :button/pressed [meta data]
  (make-spring-animator {:tension 150}))


(defmethod a/values :button/button-loader [_]
  {:button/border-radius    25
   :button/height           50
   :button/width            50
   :button/border-width     4
   :button/border-color     "#666"
   :button/background-color "#222"
   :button/scale            1
   :label/color             "#fff"
   :label/font-size         14})

(defmethod a/animator :button/button-loader [meta]
  (make-spring-animator {:tension 150}))

(defmethod a/values :button/loader-start [_ _]
  {:rotate "0deg"})

(defmethod a/values :button/loader-end [_ _]
  {:rotate "360deg"})

(defmethod a/animator :button/loader-end [_ _]
  {:type :timing
   :config {:duration 750
            :loop? true
            :easing {:type :linear}}})

(defmethod a/values :button/success-notice [_]
  {:button/border-radius    5
   :button/height           50
   :button/width            200
   :button/border-width     2
   :button/border-color     "#0061ff"
   :button/background-color "#0061ff"
   :button/scale            1
   :label/color             "#fff"
   :label/font-size         14})

(defmethod a/animator :button/success-notice [_]
  (make-spring-animator {:tension 80
                         :overshootClamping false}))

(defmethod a/values :button/fail-notice [_]
  {:button/border-radius    5
   :button/height           50
   :button/width            260
   :button/border-width     4
   :button/border-color     "#ff3300"
   :button/background-color "#ff3300"
   :button/scale            1
   :label/color             "#fff"
   :label/font-size         14})

(defmethod a/animator :button/fail-notice [_]
  (make-spring-animator {:tension 80
                         :overshootClamping false}))

(defmethod a/values :button/fail-init [_]
  {:button/border-radius    5
   :button/height           50
   :button/width            260
   :button/border-width     2
   :button/border-color     "#ff3300"
   :button/background-color "#222"
   :button/scale            1
   :label/color             "#ff3300"
   :label/font-size         16})

(defmethod a/animator :button/fail-init [meta data]
  (let [prev (:prev meta)]
    (make-spring-animator {:tension 150})))


(defmethod a/values :button/fail-pressed [_]
  {:button/border-radius    5
   :button/height           50
   :button/width            260
   :button/border-width     2
   :button/border-style     "solid"
   :button/border-color     "#ff3300"
   :button/background-color "#ff3300"
   :button/scale            .9
   :label/color             "#fff"
   :label/font-size         16})

(def default-button-styles
  {:align-items "center"
   :justify-content "center"})

(defmethod a/animator :button/fail-pressed [meta data]
  (make-spring-animator {:tension 150}))

(defn render-notice-icon [success?]
  (let [source (if success? checkmark close)]
    [view
     [image {:source source
             :style {:width 16
                     :height 16}}]]))

(defn render-button [animation]
  (let [state (get-in animation [:meta :state])
        styles (:data animation)
        button-styles (with-animation-styles default-button-styles styles :button)
        label-styles (with-animation-styles default-button-styles styles :label)]
    [animated-view {:style (merge default-button-styles button-styles)}
     (cond
       (contains? #{:init :pressed} state) [animated-text {:style label-styles} "Submit"]
       (contains? #{:fail-init :fail-pressed} state) [animated-text {:style label-styles} "Submit Failed. Try Again"]
       (= :success-notice state) [render-notice-icon true]
       (= :fail-notice state) [render-notice-icon false]
       :else nil)]))

(defn render-loader [animation]
  (let [animation-data (:data animation)] 
    [animated-view 
     {:style (with-animation-styles
               {:border-radius 25
                :height 50
                :width 50
                :background-color "#555"
                :overflow "hidden"
                :justify-content "center"
                :align-items "center"
                :position "relative"}
               animation-data)}
     [view {:style (process-transform-styles
                    {:background-color "#3883ff"
                     :width 50
                     :height 25
                     :margin-left -25
                     :position "absolute"
                     :translate-x 25
                     :translate-y 25})}]
     [view {:style {:border-radius 25
                    :background-color "#222"
                    :width 42
                    :height 42
                    :position "relative"}}]]))

(defn render [ctx props]
  (let [animation (sub> ctx :animation :button (:id props))
        animation-state (get-in animation [:meta :state])
        pressable? (or (= :init animation-state) (= :fail-init animation-state))
        releasable? (or (= :pressed animation-state) (= :fail-pressed animation-state))
        on-press-in (or (:on-press-in props) identity)
        on-press-out (or (:on-press-out props) identity)
        button-settings (sub> ctx :button)
        d (dimensions)]
    [view {:style {:flex 1
                   :background-color "#222"
                   :padding-top (:padding-top d)
                   :padding-bottom (:padding-bottom d)
                   :padding-horizontal 10
                   :align-items "center"
                   :justify-content "center"}}
     [view {:style {:flex-direction "row"
                    :align-items "center"
                    :justify-content "space-between"
                    :width 200
                    :margin-bottom 10}}
      [switch {:value (:local-fail? button-settings)
               :on-value-change #(<cmd ctx [:button :toggle-button-setting] :local-fail?)}]
      [text {:style {:color "white"}} "Emulate local error"]]
     [view {:style {:flex-direction "row"
                    :align-items "center"
                    :justify-content "space-between"
                    :width 200
                    :margin-bottom 50}}
      [switch {:value (:server-fail? button-settings)
               :on-value-change #(<cmd ctx [:button :toggle-button-setting] :server-fail?)}]
      [text {:style {:color "white"}} "Emulate server error"]]
     [touchable-without-feedback
      {:on-press-in #(when pressable? (<cmd ctx [:button :on-press-in] nil))
       :on-press-out #(when releasable? (<cmd ctx [:button :on-press-out] nil))}
      [view {:margin-bottom 50}
       (if (or (= :loader-start animation-state)
               (= :loader-end animation-state))
         [render-loader animation]
         [render-button animation])]]
     [button {:on-press #(navigate! :pop)
              :title "Close popup"
              :color "yellow"}]]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation
                                       :button]}))
