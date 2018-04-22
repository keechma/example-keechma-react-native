(ns example-rn.util.dimensions
  (:require [example-rn.rn :refer [status-bar dimensions platform] :rename {dimensions req-dimensions}]
            [oops.core :refer [oget ocall]]))

(defn adjust-height [dim height]
  (assoc dim :height (- (:height dim) height)))

(defn iphone-x? []
  (let [dimensions (js->clj (ocall req-dimensions "get" "window") :keywordize-keys true)]
    (and (= "ios" (oget platform "OS"))
         (not (oget platform "isPad"))
         (not (oget platform "isTVOS"))
         (or (= 812 (:height dimensions))
             (= 812 (:width dimensions))))))

(defn adjust-iphone-x-top-padding [top-padding]
  (if (iphone-x?)
    (+ 24 top-padding)
    top-padding))

(defn adjust-iphone-x-bottom-padding [bottom-padding]
  (if (iphone-x?)
    (+ 34 bottom-padding)
    bottom-padding))

(defn dimensions []
  (let [status-height (or (oget status-bar "currentHeight") 0)
        dimensions (adjust-height (js->clj (ocall req-dimensions "get" "window") :keywordize-keys true) 0)]
    (merge {:padding-top (adjust-iphone-x-top-padding (if (= 0 status-height) 20 0))
            :padding-bottom (adjust-iphone-x-bottom-padding 0)}
           dimensions)))

