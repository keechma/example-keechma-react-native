(ns example-rn.controllers
  (:require [example-rn.controllers.route-transition :as route-transition]
            [example-rn.controllers.initializer :as initializer]
            [example-rn.controllers.story-detail :as story-detail]))

(def controllers
  {:route-transition route-transition/controller
   :initializer      initializer/controller
   :story-detail     story-detail/controller})
