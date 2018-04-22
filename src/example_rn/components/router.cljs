(ns example-rn.components.router
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub>]]
            [example-rn.util.routing :refer [current-route> current-route]]
            [reagent.core :as r]
            [example-rn.util :refer [process-transform-styles]]
            [keechma.toolbox.animations.helpers :refer [select-keys-by-namespace]]
            [example-rn.rn :refer [view animated-view]]
            [example-rn.util.dimensions :refer [dimensions]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.domain.routing :refer [pages]]))

(def panel-style
  {:position "absolute"
   :height   "100%" 
   :width    "100%"
   :overflow "hidden"})

(def panel-offset 150)

(defmethod a/values :router/init [meta _]
  (let [args                   (:args meta)
        animation              (:animation args)
        {:keys [height width]} (dimensions)]
    (case animation
      :prev-disappear-up    {:prev/elevation      1
                             :prev/z-index        100
                             :prev/translate-y    0
                             :current/elevation   0
                             :current/z-index     0
                             :current/translate-y panel-offset}
      :prev-disappear-down  {:prev/elevation      1
                             :prev/z-index        100
                             :prev/translate-y    0
                             :current/elevation   0
                             :current/z-index     0
                             :current/translate-y (- panel-offset)}
      :current-appear-down  {:prev/elevation      0
                             :prev/z-index        0
                             :prev/translate-y    0
                             :current/elevation   1
                             :current/z-index     100
                             :current/translate-y (- height)}
      :current-appear-up    {:prev/elevation      0
                             :prev/z-index        0
                             :prev/translate-y    0
                             :current/elevation   1
                             :current/z-index     100
                             :current/translate-y height}
      :prev-disappear-right {:prev/elevation      1
                             :prev/z-index        100
                             :prev/translate-x    0
                             :current/elevation   0
                             :current/z-index     0
                             :current/translate-x (- (- width panel-offset))}
      :current-appear-left  {:prev/elevation      0
                             :prev/z-index        0
                             :prev/translate-x    0
                             :current/elevation   1
                             :current/z-index     100
                             :current/translate-x width}
      {})))

(defmethod a/values :router/slide [meta _]
  (let [args                   (:args meta)
        animation              (:animation args)
        {:keys [height width]} (dimensions)]
    (case animation
      :prev-disappear-up    {:prev/translate-y    (- height)
                             :current/translate-y 0}
      :prev-disappear-down  {:prev/translate-y    height
                             :current/translate-y 0}
      :current-appear-down  {:prev/translate-y    panel-offset
                             :current/translate-y 0}
      :current-appear-up    {:prev/translate-y    (- panel-offset)
                             :current/translate-y 0}
      :prev-disappear-right {:prev/translate-x    width
                             :current/translate-x 0}
      :current-appear-left  {:prev/translate-x    (- panel-offset)
                             :current/translate-x 0}
      {})))

(defmethod a/animator :router/slide [meta _]
  {:type   :timing
   :config {:duration 500
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/done? :router/slide [meta animator]
  (or (animator/done? animator)
      (>= (:position meta) 1)))

(defmethod a/values :router/slide-end [meta _]
  (let [args                   (:args meta)
        animation              (:animation args)
        {:keys [height width]} (dimensions)]
    (case animation
      :prev-disappear-up    {:prev/translate-y    (- height)
                             :current/translate-y 0}
      :prev-disappear-down  {:prev/translate-y    height
                             :current/translate-y 0}
      :current-appear-down  {:prev/translate-y    height
                             :current/translate-y 0}
      :current-appear-up    {:prev/translate-y    (- height)
                             :current/translate-y 0}
      :prev-disappear-right {:prev/translate-x    width
                             :current/translate-x 0}
      :current-appear-left  {:prev/translate-x    (- width)
                             :current/translate-x 0}
      {})))

(defn renderers->components [components]
  (reduce-kv (fn [acc k v]
               (let [c-meta  (meta v)
                     context (get c-meta :keechma.ui-component/context)]
                 (assoc acc k 
                        (assoc context :components (renderers->components (:components context))))))
             {} (or components {})))

(defn make-internal-ctx [ctx]
  (reduce-kv
   (fn [acc k v]
     (let [c-meta         (meta v)
           renderer       (get c-meta :keechma.ui-component/renderer)
           context        (get c-meta :keechma.ui-component/context)
           components     (or (:components context) {})
           component-deps (vec (or (keys (:components context)) []))
           comp-ctx       (merge context
                                 {:app-db         (:app-db acc)
                                  :components     (renderers->components components)
                                  :component-deps component-deps})]
       (assoc-in acc [:components k] (ui/component->renderer acc comp-ctx))))
   ctx (:components ctx)))

(defn render-panel [ctx page]
  (let [app-db              (:app-db ctx)
        rendering-page-atom (atom page)
        internal-app-db     (r/atom @app-db)
        internal-ctx        (make-internal-ctx (assoc ctx :app-db internal-app-db))
        watch-id            (gensym :transition-watch)]
    (add-watch app-db watch-id
               (fn [key ref _ new-val]
                 (let [current-page   (:key (current-route (get-in new-val [:route :data])))
                       rendering-page @rendering-page-atom]
                   (when (or (nil? rendering-page)
                             (= current-page rendering-page))
                     (reset! internal-app-db new-val)))))

    (r/create-class
     {:reagent-render         (fn [_ page]
                                (reset! rendering-page-atom page)
                                (when page
                                  [(ui/component internal-ctx page)]))
      :component-will-unmount (fn []
                                (remove-watch app-db watch-id))})))

(defn render [ctx]
  (let [route-transition (sub> ctx :route-transition)
        times-invoked     (:times-invoked route-transition)
        prev-page         (get-in route-transition [:routes :prev])
        current-page      (:key (current-route> ctx))
        animation         (sub> ctx :animation :router)
        animation-data    (:data animation)
        prev-animation    (process-transform-styles (select-keys-by-namespace animation-data :prev))
        current-animation (process-transform-styles (select-keys-by-namespace animation-data :current))]
    (when route-transition
      [view {:style {:flex 1}}
       ^{:key :a} [animated-view
                   {:key   :a
                    :style (merge panel-style (if (even? times-invoked) current-animation prev-animation))}
                   (if (even? times-invoked)
                     [render-panel ctx current-page]
                     [render-panel ctx prev-page])]
       ^{:key :b} [animated-view
                   {:key   :b
                    :style (merge panel-style (if (odd? times-invoked) current-animation prev-animation))}
                   (if (odd? times-invoked)
                     [render-panel ctx current-page]
                     [render-panel ctx prev-page])]])))

(def component
  (ui/constructor {:renderer          render
                   :subscription-deps [:animation
                                       :route-transition]
                   :component-deps    pages}))

