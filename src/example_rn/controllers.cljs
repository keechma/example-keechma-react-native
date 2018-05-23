(ns example-rn.controllers
  (:require [example-rn.controllers.route-transition :as route-transition]
            [example-rn.controllers.initializer :as initializer]
            [example-rn.controllers.story-detail :as story-detail]
            [example-rn.controllers.taxi-select-type :as taxi-select-type]
            [example-rn.controllers.sidebar :as sidebar]
            [example-rn.controllers.button :as button]
            [example-rn.controllers.complex-stage :as complex-stage]
            [example-rn.controllers.checker :as checker]
            [example-rn.controllers.kv :as kv]))

(def controllers
  (-> {:route-transition route-transition/controller
       :initializer      initializer/controller
       :story-detail     story-detail/controller
       :taxi-select-type taxi-select-type/controller
       :sidebar          sidebar/controller
       :button           button/controller
       :complex-stage    complex-stage/controller
       :checker          checker/controller}
      (kv/register)))
