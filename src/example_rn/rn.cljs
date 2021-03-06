(ns example-rn.rn
  (:require [reagent.core :as r]
            [oops.core :refer [oget ocall]]))

(def ReactNative (js/require "react-native"))

(def app-registry (oget ReactNative "AppRegistry"))
(def status-bar (oget ReactNative "StatusBar"))
(def stylesheet (oget ReactNative "StyleSheet"))
(def easing (oget ReactNative "Easing"))
(def animated (oget ReactNative "Animated"))
(def dimensions (oget ReactNative "Dimensions"))
(def platform (oget ReactNative "Platform"))

(def text (r/adapt-react-class (oget ReactNative "Text")))
(def text-input (r/adapt-react-class (oget ReactNative "TextInput")))
(def view (r/adapt-react-class (oget ReactNative "View")))
(def scroll-view (r/adapt-react-class (oget ReactNative "ScrollView")))
(def list-view (r/adapt-react-class (oget ReactNative "ListView")))
(def flat-list (r/adapt-react-class (oget ReactNative "FlatList")))
(def animated-view (r/adapt-react-class (oget animated "View")))
(def animated-text (r/adapt-react-class (oget animated "Text")))
(def animated-image (r/adapt-react-class (oget animated "Image")))

(def animated-scroll-view (r/adapt-react-class (oget animated "ScrollView")))
(def animated-flat-list (r/adapt-react-class (ocall animated "createAnimatedComponent" (oget ReactNative "FlatList"))))
(def image (r/adapt-react-class (oget ReactNative "Image")))
(def image-class (oget ReactNative "Image"))
(def touchable-highlight (r/adapt-react-class (oget ReactNative "TouchableHighlight")))
(def touchable-opacity (r/adapt-react-class (oget ReactNative "TouchableOpacity")))
(def touchable-without-feedback (r/adapt-react-class (oget ReactNative "TouchableWithoutFeedback")))
(def switch (r/adapt-react-class (oget ReactNative "Switch")))
(def activity-indicator (r/adapt-react-class (oget ReactNative "ActivityIndicator")))
(def keyboard-avoiding-view (r/adapt-react-class (oget ReactNative "KeyboardAvoidingView")))
(def ui-manager (oget ReactNative "UIManager"))
(def text-input-state (oget ReactNative "TextInput.State"))
(def alert (oget ReactNative "Alert"))
(def button (r/adapt-react-class (oget ReactNative "Button")))
(def modal (r/adapt-react-class (oget ReactNative "Modal")))
(def keyboard (oget ReactNative "Keyboard"))
(def slider (r/adapt-react-class (oget ReactNative "Slider")))

(def data-source (oget ReactNative "ListView.DataSource"))
(def async-storage (oget ReactNative "AsyncStorage"))
(def linking (oget ReactNative "Linking"))
(def interaction-manager (oget ReactNative "InteractionManager"))

(def AnimatedValue (oget ReactNative "Animated.Value"))
(def animated-event (oget ReactNative "Animated.event"))
(def PanResponder (oget ReactNative "PanResponder"))

(def AppState (oget ReactNative "AppState"))
(def hairline-width (oget stylesheet "hairlineWidth"))
