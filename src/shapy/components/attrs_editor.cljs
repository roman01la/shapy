(ns shapy.components.attrs-editor
  (:require [rum.core :as rum]))

(def container-styles
  {:width 240
   :background "#eee"
   :border-left "1px solid #bfbfbf"})

(rum/defc AttrsEditor <
  rum/static
  []
  [:div {:style container-styles}])
