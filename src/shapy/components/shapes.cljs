(ns shapy.components.shapes
  (:require [rum.core :as rum]))

(rum/defc Line
  [{:keys [start
           end
           border-color
           border-width]}]
  [:line {:x1 (:x start)
          :y1 (:y start)
          :x2 (:x end)
          :y2 (:y end)
          :stroke border-color
          :stroke-width border-width}])

(rum/defc Rect
  [{:keys [x
           y
           rx
           ry
           width
           height
           border-color
           fill
           border-width]}]
  [:rect {:x x
          :y y
          :rx rx
          :ry ry
          :width width
          :height height
          :stroke border-color
          :stroke-width border-width
          :fill fill}])

(rum/defc Oval
  [{:keys [cx
           cy
           rx
           ry
           border-color
           fill
           border-width]}]
  [:ellipse {:cx cx
             :cy cy
             :rx rx
             :ry ry
             :stroke border-color
             :stroke-width border-width
             :fill fill}])

(rum/defcs InteractiveShape <
  rum/static
  (rum/local {:hovered? false} ::state)
  [{state ::state}
   render-shape
   {:keys [props
           on-select
           can-interact?
           selected?]}]
  (let [{:keys [hovered?]} @state]
    [:g {:on-mouse-over (when can-interact? #(swap! state assoc :hovered? true))
         :on-mouse-out (when can-interact? #(swap! state assoc :hovered? false))
         :on-click (when (and on-select can-interact?) #(on-select))}
     (render-shape props)
     (when selected?
       (render-shape (merge props {:border-color "#cccccc"
                                   :border-width 0.5
                                   :fill "none"})))
     (when (and hovered? can-interact?)
       (render-shape (merge props {:border-color "#4bc1fc"
                                   :border-width 1.5
                                   :fill "none"})))]))
