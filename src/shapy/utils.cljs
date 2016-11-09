(ns shapy.utils)

(defn event->value [event]
  (.. event -target -value))
