(ns shapy.components.attrs-editor
  (:require [rum.core :as rum]
            [shapy.utils :refer [event->value]]))

(def container-styles
  {:width 240
   :background "#f8f8f8"
   :borderLeft "1px solid #bfbfbf"
   :display "flex"
   :flexDirection "column"
   :boxSizing "border-box"
   :padding "8px 0 0"})

(def attr-container-styles
  {:display "flex"
   :flexDirection "column"
   :borderTop "1px solid #bfbfbf"
   :borderBottom "1px solid #bfbfbf"})

(def attr-name-styles
  {:fontSize 12
   :fontWeight 600
   :borderBottom "1px solid #ccc"
   :background "#eaeaea"
   :padding "4px 0 4px 8px"})

(def attr-styles
  {:display "flex"
   :padding 8})

(defn attr [{:keys [name]} child]
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

(rum/defc FillAttr
  < rum/static
  [on-change value]
  (attr
   {:name "Fill"}
   (InputColor {:on-change on-change
                :value value})))

(rum/defc AttrsEditor <
  rum/static
  [on-change
   {:keys [fill]}]
  [:div {:style container-styles}
   (FillAttr #(on-change :fill %) fill)])
