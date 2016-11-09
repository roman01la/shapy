(ns shapy.components.attrs-editor
  (:require [rum.core :as rum]))

(def container-styles
  {:width 240
   :background "#eee"
   :borderLeft "1px solid #bfbfbf"
   :display "flex"
   :flexDirection "column"})

(rum/defc AttrsEditor <
  rum/static
  []
  [:div {:style container-styles}])
