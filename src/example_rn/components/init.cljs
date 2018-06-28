(ns example-rn.components.init
  (:require [example-rn.rn :refer [view text button]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [keechma.toolbox.ui :refer [<cmd]]))

(defn render [ctx]
  (let [d (dimensions)]
    [view {:style {:flex 1
                   :background-color "#ecf0f1"
                   :padding-top (:padding-top d)
                   :padding-bottom (:padding-bottom d)
                   :padding-horizontal 10}}
     [text {:style {:font-size 18}}
      "Hello From Keechma!"]
     [text "This is an example of the route based transitions with Keechma"]
     [button {:on-press #(ui/redirect ctx {:key :about})
              :title "Go to About Page"}]
     [button {:on-press #(ui/redirect ctx {:key :login})
              :title "Open Login Popup"}]
     [button {:on-press #(ui/redirect ctx {:key :taxi-select-type})
              :title "Open Taxi Select Type Page"}]
     [button {:on-press #(ui/redirect ctx {:key :button})
              :title "Open Button Page"}]
     [button {:on-press #(ui/redirect ctx {:key :complex-stage})
              :title "Open Complex Stage Page"}]
     [button {:on-press #(ui/redirect ctx {:key :avoid-loader})
              :title "Open Avoid Loader"}]
     [button {:on-press #(<cmd ctx [:sidebar :open] :left)
              :title "Open Left Sidebar"}]
     [button {:on-press #(<cmd ctx [:sidebar :open] :right)
              :title "Open Right Sidebar"}]
     [button {:on-press #(ui/redirect ctx {:key :checker})
              :title "Checker"}]]))

(def component
  (ui/constructor {:renderer render}))
