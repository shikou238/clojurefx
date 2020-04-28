(ns clfx.core
  (:require [application])
  (:import (clfx application)))

(defn -main [& args]
  (application/launch))