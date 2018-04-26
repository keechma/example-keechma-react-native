(ns example-rn.controllers
  (:require [example-rn.controllers.route-transition :as route-transition]
            [example-rn.controllers.initializer :as initializer]
            [example-rn.controllers.story-detail :as story-detail]
            [example-rn.controllers.taxi-select-type :as taxi-select-type]))

(def controllers
  {:route-transition route-transition/controller
   :initializer      initializer/controller
   :story-detail     story-detail/controller
   :taxi-select-type taxi-select-type/controller})
