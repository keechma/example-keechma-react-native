(ns example-rn.components
  (:require [example-rn.components.main :as main]
            [example-rn.components.router :as router]
            [example-rn.components.init :as init]
            [example-rn.components.loader :as loader]
            [example-rn.components.login :as login]
            [example-rn.components.about :as about]
            [example-rn.components.navbar :as navbar]
            [example-rn.components.stories :as stories]
            [example-rn.components.taxi-select-type :as taxi-select-type]
            [example-rn.components.sidebar :as sidebar]))

(def components
  {:main             main/component
   :router           router/component
   :loader           loader/component
   :init             init/component
   :login            login/component
   :about            about/component
   :navbar           navbar/component
   :stories          stories/component
   :taxi-select-type taxi-select-type/component
   :sidebar          sidebar/component})
