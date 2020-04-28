(ns clfx.application
  (:gen-class
    :extends javafx.application.Application)
  (:import (javafx.application Application)
           (javafx.scene Group Scene)
           (javafx.stage Stage)
           (javafx.scene.canvas Canvas)
           (javafx.animation AnimationTimer)
           (javafx.scene.input KeyEvent)
           (javafx.event EventHandler))
  (:require [clfx.resource :refer :all]
            [clfx.main :as main]
            [clfx.sync :as sync])
  )

(defmacro configure
  ([_]
   (throw (IllegalArgumentException.)))
  ([bindings body]
   `(let ~bindings ~@body))
  ([defs bindings & rest]
   `(let ~defs ~@bindings (configure ~@rest))))



(defn -start [this ^Stage stage]
  (let
    [root (Group.)
     scene (Scene. root)
     canvas (Canvas. main/width main/height)
     gc (.getGraphicsContext2D canvas)]
    (.setTitle stage "Hello World")
    (.setScene stage scene)
    (.. root getChildren (add canvas))
    (deliver sync/start-tick (System/nanoTime))
    (deliver sync/gc gc)
    (load-resource)
    (.start (proxy [AnimationTimer] []
              (handle [currentNanoTime]
                (try
                  (main/tick currentNanoTime)
                  (catch Exception e
                    (.printStackTrace e)
                    (Thread/sleep 10000)
                    ))
                ))
            )
    (.setOnKeyPressed scene (proxy [EventHandler] []
                              (handle [^KeyEvent e]
                                (main/key-pressed e)
                                )))
    (.setOnKeyReleased scene (proxy [EventHandler] []
                              (handle [^KeyEvent e]
                                (main/key-released e)))))
  (deliver sync/window stage)
  (.show stage)
  )


(defn launch []
  (if (not (realized? sync/window))
    (.start (Thread. #(Application/launch clfx.application (into-array String []))))
    (println "already launched.")
    ))


