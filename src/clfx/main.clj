(ns clfx.main
  (:require [clfx.resource :refer :all]
            [clfx.sync :as sync]
            [clfx.graphics :as g]
            [clfx.math :refer :all]
            [clfx.entity :as en])
  (:import (javafx.scene.input KeyEvent)
           (javafx.scene.canvas GraphicsContext)
           (javafx.scene.effect BlendMode)))


(def width 512)
(def height 512)

(def keys-a (atom '()))

(defn code [^KeyEvent e]
  (.toString (.getCode e)))

(defn key-pressed [^KeyEvent e]
  (println (str "press key: " (code e)))
  (when (not (some #{(code e)} @keys-a))
    (swap! keys-a conj (code e))))

(defn key-released [^KeyEvent e]
  (println (str "release key: " (code e)))
  (when (some #{(code e)} @keys-a)
    (swap! keys-a #(remove #{(code e)} %))))





(def e-list (en/entity-list))

(defonce player-e (en/make-entity "player" player (vec2 0 0) (vec2 64 64) ))

(defn cut [x min max]
  (cond
    (< x min) min
    (> x max) max
    :else x))

(defn p-move [v]
  (let [from @(:pos-atom player-e)
        to (sum from v)
        x (cut (:x to) 0 width)
        y (cut (:y to) 0 height)
        ]
    (reset! (:pos-atom player-e) (vec2 x y)))
  )
(def earth-e (en/make-entity "earth" sun (vec2 100 100) (vec2 200 200) ))
(def sun-e (en/make-entity "sun" sun (vec2 100 100) (vec2 40 40) ))

(en/add-entity e-list player-e earth-e sun-e)


(def key-history-a (atom '()))

(def ^:dynamic Keys '())

(defn press?
  "please bind Keys."
  ([a] (some #{a} Keys))
  ([a b] (and (press? a) (press? b))))

(defn now-press?
  "please bind Keys."
  ([a] (and (press? a) (not (some #{a} (first @key-history-a))))))

(defn app [tick]

  (binding [Keys @keys-a]
    (doseq [e @e-list]
      (case (:name e)
        "player" (do
                   (let [step (if (press? "SHIFT") 3 7)]
                    (when (press? "RIGHT") (p-move (vec2    step  0)))
                    (when (press? "LEFT" ) (p-move (vec2 (- step) 0)))
                    (when (press? "UP"   ) (p-move (vec2 0 (- step))))
                    (when (press? "DOWN" ) (p-move (vec2 0    step)))
                    (when (now-press? "X")
                      (en/add-entity e-list (en/make-entity "bomb" bomb @(:pos-atom e) (vec2 64 64) BlendMode/SCREEN)))
                    ))
        "earth" (let
                   [t (/ (- tick @sync/start-tick) 1000000000.0)
                    x (+ 232 (* 228 (Math/cos t)))
                    y (+ 232 (* 228 (Math/sin t)))
                    ]
                  (reset! (:pos-atom e) (vec2 x y)))
        "default" ))
   (swap! key-history-a conj @keys-a)
   (swap! key-history-a #(take 10 %)))
  )

(defn render [tick ^GraphicsContext gc]
  (when (realized? sync/start-tick)
    (g/draw-image gc @space (vec2 0 0))
    (en/render-entities e-list gc)
    ))

(defn tick [tick]
  (app tick)
  (when (realized? sync/gc)
    (render tick @sync/gc)))

