(ns clfx.math)

(defn vec2 [x y] {:x x :y y})

(defn sum [a b] (merge-with + a b))

(defn sub [a b] (merge-with - a b))

(defn scale [a n]
  (into {} (map #(update % 1 * n)) a))