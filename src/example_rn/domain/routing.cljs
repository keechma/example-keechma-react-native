(ns example-rn.domain.routing)

(def page-transitions
  {:loader              :loader
   :init                :slide
   :about               :slide
   :stories             :slide
   :login               :popup
   :taxi-select-type    :popup
   :button              :popup
   :complex-stage       :popup
   :avoid-loader        :slide
   :avoid-loader-target :slide
   :checker             :slide})

(def pages (keys page-transitions))

(def pages-with-navbar
  [:init :about :stories :checker])

(defn route-processor [route app-db]
  (let [initialized? (get-in app-db [:kv :initialized?])]
    (if initialized?
      route
      {:data {:key :loader}
       :stack [{:key :loader}]})))

(defn decide-animation [app-db]
  (let [route-transition   (get-in app-db [:kv :route-transition])
        route-data         (:route-data route-transition)
        prev               (get-in route-transition [:routes :prev])
        current            (get-in route-transition [:routes :current])
        prev-transition    (get page-transitions prev)
        current-transition (get page-transitions current)
        going-back?        (> (count (get-in route-data [:prev :routes]))
                              (count (get-in route-data [:current :routes])))]
    (cond
      (= :loader prev-transition)      :prev-disappear-up
      (= :popup prev-transition)       :prev-disappear-down
      (= :loader current-transition)   :current-appear-down
      (= :popup current-transition)    :current-appear-up
      going-back?                      :prev-disappear-right
      :else                            :current-appear-left)))

