(ns example-rn.components.checker
  (:require [example-rn.rn :refer [view text button flat-list image touchable-opacity animated-view hairline-width]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [oops.core :refer [oget ocall]]
            [reagent.core :as r]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles index-of]]
            [example-rn.components.checker.open-item :as open-item]
            [example-rn.components.checker.list-item :as list-item]))

(defn get-detail-items-for-id [items id]
  (let [item (first (filter #(= (:id %) id) items))]
    (get-in item [:details :items])))

(def navbar-height 50)

(def animation-config
  {:type   :timing
   :config {:duration 400
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/values :check-list-item-init/init [meta _]
  (let [item (get-in meta [:args :item])
        items (get-in meta [:args :items])]
    {:translate-y (- (* (index-of items item) 118))
     :scale-y 0.8}))

(defmethod a/values :check-list-item-init/open [_ _]
  {:translate-y 0
   :scale-y 1})
(defmethod a/animator :check-list-item-init/open [_ _]
  (assoc-in animation-config [:config :easing :values] [0.215 0.61 0.355 1]))

(defmethod a/values :check-open-buttons/init [_ _]
  {:translate-y 100})

(defmethod a/animator :check-open-buttons/init [_ _]
  (assoc-in animation-config [:config :easing :values] [0.6 -0.28 0.735 0.045]))

(defmethod a/values :check-open-buttons/open [_ _]
  {:translate-y 0})

(defmethod a/animator :check-open-buttons/open [_ _]
  (assoc-in animation-config [:config :easing :values] [0.175 0.885 0.32 1.275]))

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

(defmethod a/animator :check-open-summary/init [meta _]
  (let [page-y (get-in meta [:args :cell :page-y])]
    (-> animation-config
        (assoc-in [:config :duration] (ocall js/Math "max" 300 page-y))
        (assoc-in [:config :easing :values] [0.23, 1, 0.32, 1]))))

(defmethod a/values :check-open-details/init [_ _]
  {:opacity 0
   :translate-y 0})
(defmethod a/animator :check-open-details/init [_ _]
  animation-config)

(defmethod a/values :check-open-details-items/init [meta _]
  (let [open-item-id (get-in meta [:args :id])
        items (get-in meta [:args :items])
        items-ids (mapv :id (get-detail-items-for-id items open-item-id))
        items-count (count items-ids)]
    (reduce-kv (fn [m k v]
                 (let [style-ns (str "item-" v)
                       order (inc k)]
                   (assoc m
                          (keyword style-ns "opacity") (- 1 (/ order items-count))
                          (keyword style-ns "translate-y") (- (* 100 order))))) {} items-ids)))

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

(defmethod a/animator :check-open-summary/open [meta _]
  (let [page-y (get-in meta [:args :cell :page-y])]
    (assoc-in animation-config [:config :duration] (ocall js/Math "max" 300 (* 1.3 page-y)))))

(defmethod a/values :check-open-details/open [_ _]
  {:opacity 1
   :translate-y 60})
(defmethod a/animator :check-open-details/open [_ _]
  animation-config)

(defmethod a/values :check-open-details-items/open [meta _]
  (let [open-item-id (get-in meta [:args :id])
        items (get-in meta [:args :items])
        items-ids (mapv :id (get-detail-items-for-id items open-item-id))
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
        animation (:data (sub> ctx :animation :check-list-header))
        items (sub> ctx :checks)]
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
                                       :open-check
                                       :checks]}))
