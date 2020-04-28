(ns clfx.graphics
  )


(defn draw-image
  ([gc image pos]
   (.drawImage gc image (:x pos) (:y pos)))
  ([gc image pos size]
   (.drawImage gc image (:x pos) (:y pos) (:x size) (:y size)))

  )