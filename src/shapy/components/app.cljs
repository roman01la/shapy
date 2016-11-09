(ns shapy.components.app
  (:require [rum.core :as rum]
            [shapy.components.grid :refer [SnappyGrid]]
            [shapy.components.attrs-editor :refer [AttrsEditor]]
            [shapy.components.shapes :refer [Line Rect]]))

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
   :flex-direction "column"})

(def stroke-color "#158eec")

(rum/defcs App <
  undo-redo-mixin
  (rum/local
   {:start nil
    :end nil
    :drag-end nil
    :shapes []
    :tool nil}
   ::state)
  [{state ::state}]
  (let [{:keys [start end drag-end shapes tool]} @state]
    [:div {:style container-styles}
     (AttrsEditor {:on-select #(swap! state assoc :tool %)
                   :selected tool})
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
                                                                         :type tool})
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
        (fn [idx {:keys [start end type]}]
          (case type
            :line
            (rum/with-key
              (Line {:start start
                     :end end
                     :color stroke-color})
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
                       :color stroke-color})
                idx))))
        shapes)
       (when (and start (or end drag-end))
         (case tool
          :line
          (Line {:start start
                 :end (or end drag-end)
                 :color stroke-color})
          :rect
          (let [{x1 :x y1 :y} start
                {x2 :x y2 :y} (or end drag-end)
                inv-x? (> x1 x2)
                inv-y? (> y1 y2)]
            (Rect {:x (if inv-x? x2 x1)
                   :y (if inv-y? y2 y1)
                   :width (if inv-x? (- x1 x2) (- x2 x1))
                   :height (if inv-y? (- y1 y2) (- y2 y1))
                   :color stroke-color}))
          nil))])]))
