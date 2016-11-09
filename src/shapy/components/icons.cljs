(ns shapy.components.icons
  (:require [rum.core :as rum]))

(defn icon-container-styles [active?]
  {:width 26
   :height 26
   :position "relative"
   :background (if active? "#ccc" "#eee")})

(def line-icon-styles
  {:width 24
   :height 2
   :position "absolute"
   :background "#158eec"
   :transform-origin "50% 50%"
   :transform "rotate(-45deg) translate3d(-9px, 8px, 0)"})

(def rect-icon-styles
  {:width 14
   :height 14
   :margin 4
   :position "absolute"
   :border "2px solid #158eec"})

(rum/defc LineIcon [{:keys [on-click active?]}]
  [:div {:style (icon-container-styles active?)
         :on-click on-click}
   [:div {:style line-icon-styles}]])

(rum/defc RectIcon [{:keys [on-click active?]}]
  [:div {:style (icon-container-styles active?)
         :on-click on-click}
   [:div {:style rect-icon-styles}]])
