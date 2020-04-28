(ns clfx.entity
  (:require [clfx.sync :refer [gc]]
            [clfx.math :refer :all]
            [clfx.graphics :refer :all])
  (:import (javafx.scene.effect BlendMode)))

(defn make-entity
  ([name image-holder pos size]
   {:name name :image-holder image-holder :pos-atom (atom pos) :size-atom (atom size) :vec-atom (atom vec)})
  ([name image-holder pos size blend-mode]
   {:name name :image-holder image-holder :pos-atom (atom pos) :size-atom (atom size) :vec-atom (atom vec) :blend-mode blend-mode}))

(defn render [entity gc]
  (draw-image gc
              (deref (:image-holder entity))
              (sub (deref (:pos-atom entity)) (scale (deref (:size-atom entity)) 0.5))
              (deref (:size-atom entity))))

(defn entity-list [] (atom '()))

(defn add-entity [list e & rest] (swap! list #(apply conj % e rest)))

(defn render-entities [list gc]
  (doseq [e @list]

    (if (contains? e :blend-mode)
      (.setGlobalBlendMode gc (:blend-mode e))
      (.setGlobalBlendMode gc BlendMode/SRC_OVER))
    (render e gc)))

(defn move [e v]
  (swap! (:pos-atom e) sum v))