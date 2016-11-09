(ns shapy.components.shapes
  (:require [rum.core :as rum]))

(rum/defc Line
  [{:keys [start
           end
           color]}]
  [:line {:x1 (:x start)
          :y1 (:y start)
          :x2 (:x end)
          :y2 (:y end)
          :stroke color
          :stroke-width 4}])

(rum/defc Rect
  [{:keys [x
           y
           width
           height
           color]}]
  [:rect {:x x
          :y y
          :width width
          :height height
          :stroke color
          :stroke-width 4
          :fill "none"}])
