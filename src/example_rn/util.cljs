(ns example-rn.util
  (:require [camel-snake-kebab.core :refer [->camelCase]]
            [promesa.core :as p]))

(def transform-styles
  [:perspective :rotate :rotate-x :rotate-y :rotate-z :scale :scale-x :scale-y :translate-x :translate-y :skew-x :skew-y])

(defn process-transform-styles [styles]
  (let [transforms (select-keys styles transform-styles)
        transform (or (:transform styles) [])
        styles-without-transforms (apply dissoc styles (conj transform-styles :transform))
        transform-styles (reduce-kv (fn [acc k v] (conj acc {(->camelCase (name k)) v})) transform transforms)]
    (if (empty? transform-styles)
      styles-without-transforms
      (assoc styles-without-transforms :transform transform-styles))))

(defn delay-pipeline [msec]
  (p/promise (fn [resolve reject]
               (js/setTimeout resolve msec))))

(defn index-of [coll item]
 (loop [c coll 
        idx 0]
   (if-let [first-item (first c)]
     (if (= first-item item) 
       idx
       (recur (rest c) (inc idx)))
     nil)))
