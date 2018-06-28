(ns example-rn.components.stories
  (:require [example-rn.rn :refer [view text button flat-list image touchable-opacity animated-view]]
            [keechma.ui-component :as ui]
            [example-rn.util.dimensions :refer [dimensions]]
            [oops.core :refer [oget]]
            [reagent.core :as r]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.toolbox.animations.core :as a]
            [keechma.toolbox.animations.animator :as animator]
            [example-rn.util :refer [with-animation-styles]]))

(def items
  [{:id 1
    :title "Item #1"
    :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fermentum sodales elementum. Nam accumsan eget arcu vitae efficitur. Aliquam euismod hendrerit nisl, eget hendrerit nulla sollicitudin eu. In non felis vel felis elementum suscipit id eu est. Integer rutrum condimentum lacus, et pharetra dolor semper sit amet. Phasellus congue tincidunt est eget convallis. Morbi sagittis nunc eget porttitor euismod. Integer eu leo leo. Proin sodales libero eu dapibus efficitur. Etiam velit felis, imperdiet et erat in, gravida ullamcorper diam. Ut sit amet massa orci. Morbi finibus quam lacus, scelerisque tincidunt sapien consequat nec. Nullam luctus mattis nunc, tristique fringilla nunc volutpat in. "
    :image (js/require "./images/unsplash/1.jpg")}
   {:id 2
    :title "Item #2"
    :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fermentum sodales elementum. Nam accumsan eget arcu vitae efficitur. Aliquam euismod hendrerit nisl, eget hendrerit nulla sollicitudin eu. In non felis vel felis elementum suscipit id eu est. Integer rutrum condimentum lacus, et pharetra dolor semper sit amet. Phasellus congue tincidunt est eget convallis. Morbi sagittis nunc eget porttitor euismod. Integer eu leo leo. Proin sodales libero eu dapibus efficitur. Etiam velit felis, imperdiet et erat in, gravida ullamcorper diam. Ut sit amet massa orci. Morbi finibus quam lacus, scelerisque tincidunt sapien consequat nec. Nullam luctus mattis nunc, tristique fringilla nunc volutpat in. "
    :image (js/require "./images/unsplash/2.jpg")}
   {:id 3
    :title "Item #3"
    :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fermentum sodales elementum. Nam accumsan eget arcu vitae efficitur. Aliquam euismod hendrerit nisl, eget hendrerit nulla sollicitudin eu. In non felis vel felis elementum suscipit id eu est. Integer rutrum condimentum lacus, et pharetra dolor semper sit amet. Phasellus congue tincidunt est eget convallis. Morbi sagittis nunc eget porttitor euismod. Integer eu leo leo. Proin sodales libero eu dapibus efficitur. Etiam velit felis, imperdiet et erat in, gravida ullamcorper diam. Ut sit amet massa orci. Morbi finibus quam lacus, scelerisque tincidunt sapien consequat nec. Nullam luctus mattis nunc, tristique fringilla nunc volutpat in. "
    :image (js/require "./images/unsplash/3.jpg")}
   {:id 4
    :title "Item #4"
    :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fermentum sodales elementum. Nam accumsan eget arcu vitae efficitur. Aliquam euismod hendrerit nisl, eget hendrerit nulla sollicitudin eu. In non felis vel felis elementum suscipit id eu est. Integer rutrum condimentum lacus, et pharetra dolor semper sit amet. Phasellus congue tincidunt est eget convallis. Morbi sagittis nunc eget porttitor euismod. Integer eu leo leo. Proin sodales libero eu dapibus efficitur. Etiam velit felis, imperdiet et erat in, gravida ullamcorper diam. Ut sit amet massa orci. Morbi finibus quam lacus, scelerisque tincidunt sapien consequat nec. Nullam luctus mattis nunc, tristique fringilla nunc volutpat in. "
    :image (js/require "./images/unsplash/4.jpg")}
   {:id 5
    :title "Item #5"
    :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fermentum sodales elementum. Nam accumsan eget arcu vitae efficitur. Aliquam euismod hendrerit nisl, eget hendrerit nulla sollicitudin eu. In non felis vel felis elementum suscipit id eu est. Integer rutrum condimentum lacus, et pharetra dolor semper sit amet. Phasellus congue tincidunt est eget convallis. Morbi sagittis nunc eget porttitor euismod. Integer eu leo leo. Proin sodales libero eu dapibus efficitur. Etiam velit felis, imperdiet et erat in, gravida ullamcorper diam. Ut sit amet massa orci. Morbi finibus quam lacus, scelerisque tincidunt sapien consequat nec. Nullam luctus mattis nunc, tristique fringilla nunc volutpat in. "
    :image (js/require "./images/unsplash/5.jpg")}
   {:id 6
    :title "Item #6"
    :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fermentum sodales elementum. Nam accumsan eget arcu vitae efficitur. Aliquam euismod hendrerit nisl, eget hendrerit nulla sollicitudin eu. In non felis vel felis elementum suscipit id eu est. Integer rutrum condimentum lacus, et pharetra dolor semper sit amet. Phasellus congue tincidunt est eget convallis. Morbi sagittis nunc eget porttitor euismod. Integer eu leo leo. Proin sodales libero eu dapibus efficitur. Etiam velit felis, imperdiet et erat in, gravida ullamcorper diam. Ut sit amet massa orci. Morbi finibus quam lacus, scelerisque tincidunt sapien consequat nec. Nullam luctus mattis nunc, tristique fringilla nunc volutpat in. "
    :image (js/require "./images/unsplash/6.jpg")}
   {:id 7
    :title "Item #7"
    :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fermentum sodales elementum. Nam accumsan eget arcu vitae efficitur. Aliquam euismod hendrerit nisl, eget hendrerit nulla sollicitudin eu. In non felis vel felis elementum suscipit id eu est. Integer rutrum condimentum lacus, et pharetra dolor semper sit amet. Phasellus congue tincidunt est eget convallis. Morbi sagittis nunc eget porttitor euismod. Integer eu leo leo. Proin sodales libero eu dapibus efficitur. Etiam velit felis, imperdiet et erat in, gravida ullamcorper diam. Ut sit amet massa orci. Morbi finibus quam lacus, scelerisque tincidunt sapien consequat nec. Nullam luctus mattis nunc, tristique fringilla nunc volutpat in. "
    :image (js/require "./images/unsplash/7.jpg")}
   {:id 8
    :title "Item #8"
    :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fermentum sodales elementum. Nam accumsan eget arcu vitae efficitur. Aliquam euismod hendrerit nisl, eget hendrerit nulla sollicitudin eu. In non felis vel felis elementum suscipit id eu est. Integer rutrum condimentum lacus, et pharetra dolor semper sit amet. Phasellus congue tincidunt est eget convallis. Morbi sagittis nunc eget porttitor euismod. Integer eu leo leo. Proin sodales libero eu dapibus efficitur. Etiam velit felis, imperdiet et erat in, gravida ullamcorper diam. Ut sit amet massa orci. Morbi finibus quam lacus, scelerisque tincidunt sapien consequat nec. Nullam luctus mattis nunc, tristique fringilla nunc volutpat in. "
    :image (js/require "./images/unsplash/8.jpg")}])

(defn calc-16-9-height [width]
  (let [ratio (/ 16 9)]
    (/ width ratio)))

(def navbar-height 50)

(defmethod a/values :open-story/init [meta _]
  (let [open-item (:args meta)]
    {:container/translate-y 0
     :title/opacity 1
     :background/opacity 0
     :body/opacity 0
     :body/translate-y -20}))

(defmethod a/animator :open-story/init [meta _]
  {:type   :timing
   :config {:duration 400
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/values :open-story/open [meta _]
  (let [open-item (:args meta)]
    {:container/translate-y (- (get-in open-item [:cell :page-y]))
     :title/opacity 0
     :background/opacity 1
     :body/opacity 0
     :body/translate-y 100}))

(defmethod a/animator :open-story/open [meta _]
  {:type   :timing
   :config {:duration 400
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/done? :open-story/open [meta animator]
  (or (animator/done? animator)
      (>= (:position meta) 1)))

(defmethod a/values :open-story/show-body [meta _]
  (let [open-item (:args meta)]
    {:container/translate-y (- (get-in open-item [:cell :page-y]))
     :title/opacity 0
     :background/opacity 1
     :body/opacity 1
     :body/translate-y 0}))

(defmethod a/animator :open-story/show-body [meta _]
  {:type   :timing
   :config {:duration 250
            :easing   {:type   :bezier
                       :values [0.2833 0.99 0.31833 0.99]}}})

(defmethod a/done? :open-story/show-body [meta animator]
  (or (animator/done? animator)
      (>= (:position meta) 1)))

(defn render-open-item [ctx open-item]
  (let [{:keys [cell id]} open-item
        {:keys [width height]} cell
        item (first (filter #(= id (:id %)) items))
        full-height (- (:height (dimensions)) navbar-height)
        animation (:data (sub> ctx :animation :open-story))]
    [animated-view
     {:style (with-animation-styles
               {:position "absolute"
                :width width
                :height full-height
                :top (:page-y cell)}
               animation
               :container)}
     [animated-view
      {:style (with-animation-styles {:background-color "black"
                                      :position "absolute"
                                      :height "100%"
                                      :width "100%"
                                      :opacity 0}
                animation
                :background)}]
     [view {:style {:position "relative"
                    :width width
                    :height height
                    :justify-content "center"
                    :align-items "center"}}
      [image {:source (:image item)
              :style {:resize-mode "cover"
                      :width width
                      :height height
                      :position "absolute"}}]
      [animated-view
       {:style  (with-animation-styles 
                  {:background-color "rgba(0,0,0,0.8)"
                   :padding 20}
                  animation
                  :title)}
       [text
        {:style {:font-size 36
                 :color "white"}}
        (:title item)]]]
     [animated-view
      {:style (with-animation-styles
                {:opacity 0
                 :padding 20}
                animation
                :body)}
      [text {:style {:font-size 26
                     :color "white"
                     :margin-bottom 20}}
       (:title item)]
      [text {:style {:color "white"}
             :margin-bottom 20}
       (:body item)]
      [button {:title "Close" :color "yellow" :on-press #(<cmd ctx [:story-detail :close])}]]]))

(defn render-list-item [ctx item]
  (let [cell-ref (atom nil)]
    (fn [ctx item]
      (let [{:keys [width]} (dimensions)
            height (calc-16-9-height width)]
        [touchable-opacity
         {:on-press #(<cmd ctx [:story-detail :open] {:id (:id item)
                                                      :cell @cell-ref})}
         [view {:style {:position "relative"
                        :width width
                        :height height
                        :justify-content "center"
                        :align-items "center"}
                :ref #(reset! cell-ref %)}
          [image {:source (:image item)
                  :style {:resize-mode "cover"
                          :width width
                          :height height
                          :position "absolute"}}]
          [view {:style {:background-color "rgba(0,0,0,0.8)"
                         :padding 20}}
           [text
            {:style {:font-size 36
                     :color "white"}}
            (:title item)]]]]))))

(defn render [ctx]
  (let [d (dimensions)
        open-story (sub> ctx :open-story)]
    [view {:style {:flex 1
                   :background-color "#fff"
                   :padding-top (:padding-top d)}}
     [flat-list
      {:style {:height "100%"}
       :data (apply array items)
       :key-extractor #(str (:id %))
       :render-item (fn [data]
                      (r/as-element [render-list-item ctx (oget data "item")]))}]
     (when open-story
       [render-open-item ctx open-story])]))

(def component
  (ui/constructor {:renderer render
                   :subscription-deps [:open-story
                                       :animation]}))
