(ns example-rn.controllers.taxi-select-type
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]
            [oops.core :refer [ocall]]
            [promesa.core :as p]
            [keechma.toolbox.animations.core :as a :refer [render-animation-end]]
            [example-rn.util :refer [delay-pipeline index-of]]
            [example-rn.animations.rn :as rna]))

(defn decide-next-animation [app-db]
  (let [prev-animation (get-in app-db (a/app-db-animation-path :taxi-select-type nil))]
    (let [[_ y-value] (get-in prev-animation [:meta :pan-value])
          velocity (get-in prev-animation [:meta :gesture :vy])]
      (cond
        (< velocity -1.5) :taxi-select-type/done
        (> velocity 1.5) :taxi-select-type/init
        (< y-value 0.5) :taxi-select-type/init
        :else :taxi-select-type/done))))

(def vehicles
  [{:id 1
    :title "Yugo 45"
    :subtitle "Might get you there"
    :fare "$ 0.00"
    :capacity "1 - 3"}
   {:id 2
    :title "Fiat 500"
    :subtitle "Old one, not new"
    :fare "$ 0.00"
    :capacity "1 - 3"}
   {:id 3
    :title "Zastava 128"
    :subtitle "Getting serious, are we?"
    :fare "$ 0.00"
    :capacity "1 - 4"}])

(defn selected-vehicle-index [app-db]
  (let [selected-vehicle (get-in app-db [:kv :selected-vehicle])
        vehicle-ids (map :id vehicles)]
    (index-of vehicle-ids selected-vehicle)))

(defn animation-args [app-db]
  {:selected-vehicle-index (selected-vehicle-index app-db)})

(defn should-start-vehicle-selector? [app-db]
  (let [animation (get-in app-db (a/app-db-animation-path :taxi-select-type nil))
        state (get-in animation [:meta :state])
        prev-state (get-in animation [:meta :prev :state])]
    (and (= state :panmove)
         (= prev-state :done))))

(def controller
  (pp-controller/constructor
   (fn [route]
     (when (= :taxi-select-type (get-in route [:data :key]))
       true))
   {:on-start
    (pipeline! [value app-db]
      (pp/commit! 
       (-> app-db
           (assoc-in [:kv :selected-vehicle] 1)
           (assoc-in [:kv :vehicles] vehicles)
           (render-animation-end :taxi-select-type/init nil (animation-args app-db))))
      (pp/execute! :start-openclose nil))

    :close 
    (pipeline! [value app-db]
      (a/cancel-animation! app-db :taxi-select-type)
      (rna/blocking-animate-state! app-db :taxi-select-type/init nil (animation-args app-db))
      (pp/execute! :start-openclose nil))

    :select-vehicle 
    (pipeline! [value app-db]
      (pp/commit! (assoc-in app-db [:kv :selected-vehicle] value))
      (pp/execute! :start-openclose nil))

    :start-openclose 
    (pipeline! [value app-db]
      (pp/execute! :start-vehicle-selector nil)
      (a/cancel-animation! app-db :taxi-select-type)
      (rna/blocking-panresponder-animate-state! app-db :taxi-select-type/panmove nil (animation-args app-db))
      (rna/blocking-animate-state! app-db (decide-next-animation app-db) nil (animation-args app-db))
      (pp/execute! :start-openclose nil))

    :start-vehicle-selector 
    (pipeline! [value app-db]
      (println (should-start-vehicle-selector? app-db)))}))
