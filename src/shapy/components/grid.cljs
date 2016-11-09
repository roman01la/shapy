(ns shapy.components.grid
  (:require [rum.core :as rum]))

(def container-styles
  {:background "#fff"
   :flex 1
   :overflow "hidden"})

(def canvas-styles
  {:width "100%"
   :height "100vh"
   :position "relative"
   :display "flex"})

(def grid-styles
  {:width "calc(100vw - 200px)"
   :height "100vh"
   :flex 1})

(defn normalize-value [w r val]
  (-> val
      (/ w)
      js/Math.round
      (* w)
      (- r)))

(defn normalize-pos [cst ref cx cy]
  (let [rect (-> cst (rum/ref-node ref) (.getBoundingClientRect))
        top (.. rect -top)
        left (.. rect -left)]
    [(- cx left)
     (- cy top)]))

(rum/defcs SnappyGrid <
  rum/static
  (rum/local
   {:x 0
    :y 0
    :w 10
    :h 10
    :r 6}
   ::state)
  [{state ::state :as cst}
   {:keys [on-click
           on-mouse-move]}
   child]
  (let [{:keys [x y w h r]} @state]
    [:div.canvas {:style container-styles}
     [:svg
      {:style canvas-styles
       :ref "canvas"
       :on-mouse-move
       #(do
         (let [cx (.. % -clientX)
               cy (.. % -clientY)
               [x y] (normalize-pos cst "canvas" cx cy)]
           (swap! state merge {:x (normalize-value w r cx)
                               :y (normalize-value w r cy)})
           (when on-mouse-move (on-mouse-move {:x (normalize-value w 0 x)
                                               :y (normalize-value w 0 y)}))))
       :on-click #(when on-click
                    (let [[x y] (normalize-pos cst "canvas" (.. % -clientX) (.. % -clientY))]
                      (on-click {:x (normalize-value w 0 x)
                                 :y (normalize-value w 0 y)})))}
      [:defs
       [:pattern
        {:id "small-grid"
         :width w
         :height h
         :patternUnits "userSpaceOnUse"
         :fill "none"
         :stroke "#eee"
         :strokeWidth 1}
        [:path
         {:d (str "M " w " 0 L 0 0 0 " h)}]]
       [:pattern
        {:id "grid"
         :width 100
         :height 100
         :patternUnits "userSpaceOnUse"
         :patternTransform "translate(0, 0)"
         :fill "none"
         :stroke "#e4e4e4"
         :strokeWidth 1}
        [:rect
         {:width 100
          :height 100
          :fill "url(#small-grid)"}]
        [:path
         {:d "M 100 0 L 0 0 0 100"}]]]
      [:rect {:style grid-styles
              :fill "url(#grid)"}]
      child]]))
