(ns clfx.resource
  (:import (javafx.scene.image Image))
  (:require [clfx.sync]))


(def image-paths-a (atom '()))

(defmacro def-image [name path]
  `(do
     (def ~name (promise))
     (swap! image-paths-a conj (fn [] (deliver ~name (Image. ~path))))))

(def-image earth "earth.png")
(def-image sun "sun.png")
(def-image space "space.png")
(def-image player "player.png" )
(def-image bomb "bomb.png")

(defn load-resource []
  (doseq [f @image-paths-a] (f))
  )

(when (realized? clfx.sync/window) (load-resource))