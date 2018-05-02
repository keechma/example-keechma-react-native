(ns example-rn.components.complex-stage
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub>]]
            [example-rn.rn :refer [view
                                   animated-view
                                   touchable-without-feedback
                                   animated-text
                                   text
                                   button
                                   image
                                   switch]]
            [example-rn.util.dimensions :refer [dimensions]]
            [keechma.app-state.react-native-router :refer [navigate!]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles process-transform-styles]]
            [example-rn.util.dimensions :refer [dimensions]]
            [keechma.toolbox.animations.helpers :refer [select-keys-by-namespace]]))

(defmethod a/values :background/init [_]
  {:opacity 0.3
   :height 0})

(defmethod a/values :background/done []
  {:opacity 1
   :height 100})

(defmethod a/animator :background/done [_ _]
  {:type :timing
   :config {:duration 10000
            :easing {:type :linear}
            :useNativeDriver false}})


(defmethod a/values :main/init [_]
  {:opacity 0.3
   :height 0})

(defmethod a/values :main/done []
  {:opacity 1
   :height 100})

(defmethod a/animator :main/done [_ _]
  {:type :timing
   :config {:duration 10000
            :easing {:type :linear}
            :useNativeDriver false}})



(defn render [ctx]
  (let [d (dimensions)
        background-anim (:data (sub> ctx :animation :background))
        main-anim (:data (sub> ctx :animation :main))]
    [view {:style {:flex 1
                   :background-color "#fff"
                   :padding-top (:padding-top d)
                   :padding-bottom (:padding-bottom d)
                   }}
     [text {:style {:font-size 18}}
      "This is a complex stage page!"]
     [view
      [animated-view
       {:style (with-animation-styles
                 {:background-color "red"
                  :width (:width d)
                  :height 100
                  :margin-bottom 1}
                 background-anim)}]
      [animated-view
       {:style (with-animation-styles
                 {:background-color "blue"
                  :width (:width d)
                  :height 100}
                 main-anim)}]]
     [button {:on-press #(navigate! :pop)
              :title "Close popup"
              :color "blue"}]]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:animation]}))