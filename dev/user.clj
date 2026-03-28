(ns user
  (:require [datahost.datasets]
            [datahost.handler]
            [datahost.server]
            [integrant.core :as ig]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :refer [system]]
            [clojure.java.io :as io]))

(defn read-config []
  (-> (io/resource "config.edn")
      slurp
      (ig/read-string)))

(ig-repl/set-prep! read-config)

(def go   ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)

(comment
  (go)
  (halt)
  (reset)
  system)
