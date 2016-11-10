(ns shapy.components.grid
  (:require [rum.core :as rum]))

(def canvas-styles
  {:width "100%"
   :height "calc(100vh - 45px)"
   :position "relative"
   :display "flex"})

(def grid-styles
  {:width "calc(100vw - 200px)"
   :height "100vh"
   :flex 1})

(defn normalize-value [w val]
  (-> val
      (/ w)
      js/Math.round
      (* w)))

(defn normalize-pos [cst ref cx cy]
  (let [rect (-> cst (rum/ref-node ref) (.getBoundingClientRect))
        top (.. rect -top)
        left (.. rect -left)]
    [(- cx left)
     (- cy top)]))

(defn pos->handler [cst w handler e]
  (let [[x y] (normalize-pos cst "canvas" (.. e -clientX) (.. e -clientY))]
    (handler {:x (normalize-value w x)
              :y (normalize-value w y)})))

(rum/defcs SnappyGrid <
  rum/static
  (rum/local
   {:w 4
    :h 4}
   ::state)
  [{state ::state :as cst}
   {:keys [on-click
           on-mouse-down
           on-mouse-up
           on-mouse-move]}
   child]
  (let [{:keys [w h]} @state]
   [:svg
    {:style canvas-styles
     :ref "canvas"
     :on-mouse-down
     (when on-mouse-down
      #(pos->handler cst w on-mouse-down %))
     :on-mouse-up
     (when on-mouse-up
      #(pos->handler cst w on-mouse-up %))
     :on-mouse-move
     (when on-mouse-move
      #(pos->handler cst w on-mouse-move %))
     :on-click
     (when on-click
      #(pos->handler cst w on-click %))}
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
    child]))
