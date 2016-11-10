(ns shapy.components.app
  (:require [rum.core :as rum]
            [clojure.data :refer [diff]]
            [shapy.components.grid :refer [SnappyGrid]]
            [shapy.components.toolbar :refer [Toolbar]]
            [shapy.components.attrs-editor :refer [AttrsEditor]]
            [shapy.components.shapes :refer [Line Rect Oval InteractiveShape]]))

(def history (atom []))

(defn update-history [history st nst]
  (let [h @history
        st- (dissoc st :mouse)
        nst- (dissoc nst :mouse)
        idx (.indexOf h st-)]
    (if (= idx -1)
      (swap! history conj nst-)
      (reset! history (conj (subvec h 0 (inc idx)) nst-)))))

(defn keyboard-mixin [& handles]
  {:did-mount
   (fn [{st ::state :as state}]
     (.addEventListener
      js/document "keydown"
      (fn [e]
        (let [e-keys {:key-code (.. e -keyCode)
                      :ctrl-key (.. e -ctrlKey)}]
          (doseq [handler handles]
            (handler st e-keys)))))

     state)})

(def logger-mixin
  {:did-mount
   (fn [{st ::state :as state}]
     (add-watch st :log (fn [k r o n]
                          (let [[a] (diff n o)]
                            (console.log a))))
     state)})

(defn handle-undo-redo
  [st
   {:keys [key-code
           ctrl-key]}]
  (cond
    ;; undo
    (and (= key-code 90)
         (true? ctrl-key))
    (let [h @history
          s @st
          idx (dec (.indexOf h (dissoc s :mouse)))]
      (when (>= idx 0)
        (reset! st (assoc (nth h idx) :mouse (:mouse s)))))
    ;; redo
    (and (= key-code 89)
         (true? ctrl-key))
    (let [h @history
          s @st
          idx (inc (.indexOf h (dissoc s :mouse)))]
      (when (< idx (count h))
        (reset! st (assoc (nth h idx) :mouse (:mouse s)))))))

(defn handle-esc [st {:keys [key-code]}]
  (if (= key-code 27)
    (swap! st #(-> %
                (assoc :tool nil)
                (assoc :selected nil)))
    st))

(def container-styles
  {:display "flex"
   :flexDirection "column"})

(def canvas-container-styles
  {:background "#fff"
   :display "flex"
   :overflow "hidden"})

(defn render-line [props]
  (Line props))

(defn render-rect
  [{:keys [start
           end
           radius
           border-color
           border-width
           fill]}]
  (let [{x1 :x y1 :y} start
        {x2 :x y2 :y} end
        inv-x? (> x1 x2)
        inv-y? (> y1 y2)]
    (Rect {:x (if inv-x? x2 x1)
           :y (if inv-y? y2 y1)
           :rx radius
           :ry radius
           :width (if inv-x? (- x1 x2) (- x2 x1))
           :height (if inv-y? (- y1 y2) (- y2 y1))
           :border-color border-color
           :border-width border-width
           :fill fill})))

(defn render-oval
  [{:keys [start
           end
           border-color
           border-width
           fill]}]
  (let [{x1 :x y1 :y} start
        {x2 :x y2 :y} end
        inv-x? (> x1 x2)
        inv-y? (> y1 y2)
        rx (if inv-x? (- x1 x2) (- x2 x1))
        ry (if inv-y? (- y1 y2) (- y2 y1))]
    (Oval {:cx ((if inv-x? - +) x1 (/ rx 2))
           :cy ((if inv-y? - +) y1 (/ ry 2))
           :rx (/ rx 2)
           :ry (/ ry 2)
           :border-color border-color
           :border-width border-width
           :fill fill})))

(defn render-active-shape [props tool]
  (case tool
   :line (render-line props)
   :rect (render-rect props)
   :oval (render-oval props)
   nil))

(defn render-shapes [props]
  (case (get-in props [:props :type])
    :line (rum/with-key (InteractiveShape render-line props) (:idx props))
    :rect (rum/with-key (InteractiveShape render-rect props) (:idx props))
    :oval (rum/with-key (InteractiveShape render-oval props) (:idx props))))

(defn update-state
  [st
   shape
   tool
   pos
   history]
  (let [{:keys [start end]} (:attrs st)]
    (cond
      (and (nil? start) (nil? end))
      (let [nst (assoc-in st [:attrs :start] pos)]
        (update-history history st nst)
        nst)

      (= start pos)
      (assoc-in st [:attrs :start] nil)

      (and start (nil? end))
      (let [nst (-> st
                    (update :shapes conj (merge shape {:end pos
                                                       :type tool}))
                    (assoc-in [:attrs :start] nil)
                    (assoc-in [:attrs :end] nil))]
        (update-history history st nst)
        (console.log nst)
        nst))))

(rum/defcs App <
  logger-mixin
  (keyboard-mixin
   handle-undo-redo
   handle-esc)
  {:did-mount
   (fn [{st ::state :as state}]
     (update-history history @st @st)
     state)}
  (rum/local
   {:mouse nil
    :shapes []
    :selected nil
    :tool nil
    :press? false
    :attrs {:start nil
            :end nil
            :radius 0
            :fill "#f54f4b"
            :border-color "#16909C"
            :border-width 2}}
   ::state)
  [{state ::state}]
  (let [{:keys [mouse
                shapes
                selected
                tool
                press?
                attrs]}
        @state
        {:keys [start end]} attrs]

    [:div {:style container-styles}
     (Toolbar {:on-select #(swap! state assoc :tool %)
               :selected tool})
     [:div {:style canvas-container-styles
            :class (when tool "canvas__tool")}
       (SnappyGrid
        {:on-mouse-move #(swap! state assoc :mouse %)
         :on-mouse-up
         (when tool
           (fn [pos]
            (swap! state #(-> %
                           (update-state attrs tool pos history)
                           (assoc :press? false)))))
         :on-mouse-down
         (when tool
           (fn [pos]
             (swap! state #(-> %
                               (update-state attrs tool pos history)
                               (assoc :press? true)))))}
        [:g
         (map-indexed
          (fn [idx props]
            (render-shapes
             {:on-select
              #(swap! state merge {:attrs (nth shapes idx)
                                   :selected idx})
              :can-interact? (nil? tool)
              :selected? (= idx selected)
              :props props
              :idx idx}))
          shapes)
         (when (and start (or end mouse))
           (render-active-shape
             (assoc attrs :end (or end mouse))
             tool))])
      (AttrsEditor
       (fn [attr val]
        (swap! state #(if selected
                        (-> %
                          (assoc-in [:shapes selected attr] val)
                          (assoc-in [:attrs attr] val))
                        (assoc-in % [:attrs attr] val))))
       (dissoc attrs :start :end))]]))
