(ns shapy.components.attrs-editor
  (:require [rum.core :as rum]
            [shapy.utils :refer [event->value]]))

(def container-styles
  {:minWidth 200
   :background "#f8f8f8"
   :borderLeft "1px solid #bfbfbf"
   :display "flex"
   :flex 1
   :flexDirection "column"})

(def attr-container-styles
  {:display "flex"
   :flexDirection "column"
   :borderTop "1px solid #bfbfbf"
   :borderBottom "1px solid #bfbfbf"
   :margin "16px 0 0"})

(def attr-name-styles
  {:fontSize 12
   :fontWeight 600
   :borderBottom "1px solid #ccc"
   :background "#eaeaea"
   :padding "4px 0 4px 8px"})

(def attr-styles
  {:display "flex"
   :alignItems "center"
   :padding 8})

(defn attr [{:keys [name]} & child]
  [:div {:style attr-container-styles}
   [:div {:style attr-name-styles} name]
   [:div {:style attr-styles} child]])

(rum/defc InputColor
  [{:keys [value on-change]}]
  [:input {:type "color"
           :style {:background "transparent"
                   :border "none"
                   :height 26}
           :value value
           :on-change #(-> % event->value on-change)}])

(rum/defc InputNumber
  [{:keys [value
           on-change
           min
           max]}]
  [:input {:type "number"
           :style {:margin 0
                   :padding "0 4px"
                   :width 24
                   :height 16
                   :border "1px solid #777777"}
           :value value
           :min min
           :max max
           :on-change #(-> % event->value js/parseFloat on-change)}])

(rum/defc RadiusAttr
  < rum/static
  [on-change value]
  (attr
   {:name "Radius"}
   (InputNumber {:on-change #(on-change :radius %)
                 :value value
                 :min 0})))

(rum/defc FillAttr
  < rum/static
  [on-change value]
  (attr
   {:name "Fill"}
   (InputColor {:on-change #(on-change :fill %)
                :value value})))

(rum/defc BorderAttr
  < rum/static
  [on-change border-color border-width]
  (attr
   {:name "Border"}
   (InputColor {:on-change #(on-change :border-color %)
                :value border-color})
   (InputNumber {:on-change #(on-change :border-width %)
                 :value border-width
                 :min 0})))

(rum/defc AttrsEditor <
  rum/static
  [on-change
   {:keys [radius
           fill
           border-color
           border-width]}]
  [:div {:style container-styles}
   (RadiusAttr on-change radius)
   (FillAttr on-change fill)
   (BorderAttr on-change border-color border-width)])
