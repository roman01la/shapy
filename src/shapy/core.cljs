(ns shapy.core
  (:require [rum.core :as rum]
            [shapy.state :refer [app-state]]
            [shapy.components.app :refer [App]]))

(enable-console-print!)

(rum/defc Wrapper < rum/reactive []
  (App))

(rum/mount (Wrapper)
           (. js/document (getElementById "app")))
