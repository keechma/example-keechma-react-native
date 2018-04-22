(ns example-rn.controllers
  (:require [example-rn.controllers.route-transition :as route-transition]
            [example-rn.controllers.initializer :as initializer]))

(def controllers
  {:route-transition route-transition/controller
   :initializer      initializer/controller})
