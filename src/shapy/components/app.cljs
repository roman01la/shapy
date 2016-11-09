(ns shapy.components.app
  (:require [rum.core :as rum]
            [shapy.components.grid :refer [SnappyGrid]]
            [shapy.components.toolbar :refer [Toolbar]]
            [shapy.components.attrs-editor :refer [AttrsEditor]]
            [shapy.components.shapes :refer [Line Rect Oval]]))

(def history (atom []))

(defn update-history [history st nst]
  (let [h @history
        st- (dissoc st :drag-end)
        nst- (dissoc nst :drag-end)
        idx (.indexOf h st-)]
    (if (= idx -1)
      (swap! history conj nst-)
      (reset! history (conj (subvec h 0 (inc idx)) nst-)))))

(def undo-redo-mixin
  {:did-mount
   (fn [{st ::state :as state}]
     (.addEventListener js/document "keydown"
                        (fn [e]
                          (cond
                            ;; undo
                            (and (= (.. e -keyCode) 90)
                                 (true? (.. e -ctrlKey)))
                            (let [h @history
                                  s @st
                                  idx (dec (.indexOf h (dissoc s :drag-end)))]
                              (when (>= idx 0)
                                (reset! st (assoc (nth h idx) :drag-end (:drag-end s)))))
                            ;; redo
                            (and (= (.. e -keyCode) 89)
                                 (true? (.. e -ctrlKey)))
                            (let [h @history
                                  s @st
                                  idx (inc (.indexOf h (dissoc s :drag-end)))]
                              (when (< idx (count h))
                                (reset! st (assoc (nth h idx) :drag-end (:drag-end s))))))))
     (update-history history @st @st)
     state)})

(def container-styles
  {:display "flex"
   :flexDirection "column"})

(def inner-container-styles
  {:display "flex"})

(rum/defcs App <
  undo-redo-mixin
  (rum/local
   {:start nil
    :end nil
    :drag-end nil
    :fill "#000000"
    :border-color "#158eec"
    :border-width 4
    :shapes []
    :tool nil}
   ::state)
  [{state ::state}]
  (let [{:keys [start
                end
                drag-end
                fill
                border-color
                border-width
                shapes
                tool]}
        @state]
    [:div {:style container-styles}
     (Toolbar {:on-select #(swap! state assoc :tool %)
                   :selected tool})
     [:div {:style inner-container-styles}
       (SnappyGrid
        {:on-mouse-move #(swap! state assoc :drag-end %)
         :on-click #(swap! state (fn [st]
                                   (if tool
                                     (cond
                                       (and (nil? start) (nil? end))
                                       (let [nst (assoc st :start %)]
                                         (update-history history st nst)
                                         nst)

                                       (and start (nil? end))
                                       (let [nst (-> st
                                                     (update :shapes conj {:start start
                                                                           :end %
                                                                           :type tool
                                                                           :fill fill
                                                                           :color border-color
                                                                           :border-width border-width})
                                                     (assoc :start nil)
                                                     (assoc :end nil))]
                                         (update-history history st nst)
                                         nst)

                                       (and start end)
                                       (let [nst (-> st
                                                     (assoc :start nil)
                                                     (assoc :end nil))]
                                         (update-history history st nst)
                                         nst))
                                     st)))}

        [:g
         (map-indexed
          (fn [idx {:keys [start
                           end
                           type
                           fill
                           color
                           border-width]}]
            (case type
              :line
              (rum/with-key
                (Line {:start start
                       :end end
                       :color color
                       :border-width border-width})
                idx)
              :rect
              (let [{x1 :x y1 :y} start
                    {x2 :x y2 :y} end
                    inv-x? (> x1 x2)
                    inv-y? (> y1 y2)]
                (rum/with-key
                  (Rect {:x (if inv-x? x2 x1)
                         :y (if inv-y? y2 y1)
                         :width (if inv-x? (- x1 x2) (- x2 x1))
                         :height (if inv-y? (- y1 y2) (- y2 y1))
                         :color color
                         :border-width border-width
                         :fill fill})
                  idx))
              :oval
              (let [{x1 :x y1 :y} start
                    {x2 :x y2 :y} end
                    inv-x? (> x1 x2)
                    inv-y? (> y1 y2)
                    rx (if inv-x? (- x1 x2) (- x2 x1))
                    ry (if inv-y? (- y1 y2) (- y2 y1))]
                (rum/with-key
                  (Oval {:cx (+ x1 (/ rx 2))
                         :cy (+ y1 (/ ry 2))
                         :rx (/ rx 2)
                         :ry (/ ry 2)
                         :color color
                         :border-width border-width
                         :fill fill})
                  idx))))
          shapes)
         (when (and start (or end drag-end))
           (case tool
            :line
            (Line {:start start
                   :end (or end drag-end)
                   :color border-color
                   :border-width border-width})
            :rect
            (let [{x1 :x y1 :y} start
                  {x2 :x y2 :y} (or end drag-end)
                  inv-x? (> x1 x2)
                  inv-y? (> y1 y2)]
              (Rect {:x (if inv-x? x2 x1)
                     :y (if inv-y? y2 y1)
                     :width (if inv-x? (- x1 x2) (- x2 x1))
                     :height (if inv-y? (- y1 y2) (- y2 y1))
                     :color border-color
                     :border-width border-width
                     :fill fill}))
            :oval
            (let [{x1 :x y1 :y} start
                  {x2 :x y2 :y} (or end drag-end)
                  inv-x? (> x1 x2)
                  inv-y? (> y1 y2)
                  rx (if inv-x? (- x1 x2) (- x2 x1))
                  ry (if inv-y? (- y1 y2) (- y2 y1))]
              (Oval {:cx (+ x1 (/ rx 2))
                     :cy (+ y1 (/ ry 2))
                     :rx (/ rx 2)
                     :ry (/ ry 2)
                     :color border-color
                     :border-width border-width
                     :fill fill}))
            nil))])
      (AttrsEditor
       #(swap! state assoc %1 %2)
       {:fill fill
        :border-color border-color
        :border-width border-width})]]))
