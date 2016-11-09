(ns shapy.components.attrs-editor
  (:require [rum.core :as rum]
            [shapy.components.icons :refer [LineIcon RectIcon]]))

(def editor-styles
  {:box-shadow "0 2px 12px rgba(0, 0, 0, 0.18)"
   :background "#fff"
   :padding "8px 16px"
   :display "flex"
   :justify-content "flex-start"
   :zIndex 1})

(def section-styles
  {:display "flex"
   :flex-direction "column"
   :margin "0 20px 0 0"})

(def icons-panel-styles
  {:display "flex"})

(rum/defc AttrsEditor <
  rum/static
  [{:keys [on-select selected]}]
  [:div {:style editor-styles}
   [:img {:src "/app_icon.png"
          :title "Shapy!"
          :width 24
          :height 24
          :style {:margin "0 32px 0 0"
                  :padding 2}}]
   [:div {:style section-styles}
    [:div {:style icons-panel-styles}
     (LineIcon {:on-click #(on-select :line)
                :active? (= selected :line)})
     [:div {:style {:margin "0 5px"}}]
     (RectIcon {:on-click #(on-select :rect)
                :active? (= selected :rect)})]]])
