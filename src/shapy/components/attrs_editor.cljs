(ns shapy.components.attrs-editor
  (:require [rum.core :as rum]
            [shapy.utils :refer [event->value]]))

(def container-styles
  {:width 200
   :background "#f8f8f8"
   :borderLeft "1px solid #bfbfbf"
   :display "flex"
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
                   :border "none"}
           :value value
           :on-change #(-> % event->value on-change)}])

(rum/defc InputNumber
  [{:keys [value on-change]}]
  [:input {:type "number"
           :style {:margin 0
                   :padding 0
                   :width 32
                   :height 16}
           :value value
           :on-change #(-> % event->value js/parseFloat on-change)}])

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
                 :value border-width})))

(rum/defc AttrsEditor <
  rum/static
  [on-change
   {:keys [fill
           border-color
           border-width]}]
  [:div {:style container-styles}
   (FillAttr on-change fill)
   (BorderAttr on-change border-color border-width)])
