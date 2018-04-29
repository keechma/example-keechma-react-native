(ns example-rn.controllers.initializer
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [example-rn.util :refer [delay-pipeline]]))

(def controller
  (pp-controller/constructor
   (constantly true)
   {:on-start (pipeline! [value app-db]
                ;;(delay-pipeline 1000)
                (pp/commit! (assoc-in app-db [:kv :initialized?] true))
                (pp/reroute!))}))
