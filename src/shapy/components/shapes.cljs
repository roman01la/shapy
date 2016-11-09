(ns shapy.components.shapes
  (:require [rum.core :as rum]))

(rum/defc Line
  [{:keys [start
           end
           color
           border-width]}]
  [:line {:x1 (:x start)
          :y1 (:y start)
          :x2 (:x end)
          :y2 (:y end)
          :stroke color
          :stroke-width border-width}])

(rum/defc Rect
  [{:keys [x
           y
           rx
           ry
           width
           height
           color
           fill
           border-width]}]
  [:rect {:x x
          :y y
          :rx rx
          :ry ry
          :width width
          :height height
          :stroke color
          :stroke-width border-width
          :fill fill}])

(rum/defc Oval
  [{:keys [cx
           cy
           rx
           ry
           color
           fill
           border-width]}]
  [:ellipse {:cx cx
             :cy cy
             :rx rx
             :ry ry
             :stroke color
             :stroke-width border-width
             :fill fill}])
