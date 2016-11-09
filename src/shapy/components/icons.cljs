(ns shapy.components.icons
  (:require [rum.core :as rum]))

(defn icon-container-styles [active?]
  {:width 28
   :height 28
   :padding 1
   :boxSizing "border-box"
   :background (if active? "#eee" "transparent")
   :border (if active? "1px solid #ccc" "1px solid transparent")})

(def stroke-color "#16909C")

(rum/defc LinearGradient
  [{:keys [id x1 y1 x2 y2 color-stops]}]
  [:linearGradient
   {:id id
    :x1 x1
    :y1 y1
    :x2 x2
    :y2 y2}
   (map-indexed
    (fn [idx {:keys [color offset]}]
      [:stop {:key (str color idx)
              :stop-color color
              :offset offset}])
    color-stops)])

(defn icon-gradient [id]
  (rum/with-key
    (LinearGradient {:id id
                     :x1 "50%"
                     :y1 "0%"
                     :x2 "50%"
                     :y2 "100%"
                     :color-stops [{:color "#31E3F5"
                                    :offset "0%"}
                                   {:color "#2FD3E4"
                                    :offset "100%"}]})
    id))

(rum/defc Icon [{:keys [on-click active?]} shape & defs]
  [:div {:style (icon-container-styles active?)
         :on-click on-click}
    [:svg {:width 24
           :height 24
           :viewBox "0 0 24 24"}
     [:defs defs]
     shape]])

(rum/defc LineIcon [{:keys [on-click active?]}]
  (Icon
    {:on-click on-click
     :active? active?}
    [:rect {:x1 0
            :y1 0
            :width 4
            :height 24
            :stroke stroke-color
            :stroke-width 0.5
            :fill "url(#icon-lg)"
            :transform "translate(19, 2) rotate(-315)"}]
    (icon-gradient "icon-lg")))

(rum/defc RectIcon [{:keys [on-click active?]}]
  (Icon
    {:on-click on-click
     :active? active?}
    [:rect {:x1 0
            :y1 0
            :width 24
            :height 24
            :stroke stroke-color
            :stroke-width 1
            :fill "url(#icon-lg)"}]
    (icon-gradient "icon-lg")))

(rum/defc OvalIcon [{:keys [on-click active?]}]
  (Icon
    {:on-click on-click
     :active? active?}
    [:circle {:cx 12
              :cy 12
              :r 11.5
              :stroke stroke-color
              :stroke-width 0.5
              :fill "url(#icon-lg)"}]
    (icon-gradient "icon-lg")))
