(ns user
  (:require [datahost.datasets]
            [datahost.handler]
            [datahost.server]
            [datahost.sys :as sys]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :refer [system]]))

(ig-repl/set-prep! #(sys/load-config sys/config-paths))

(def go    ig-repl/go)
(def halt  ig-repl/halt)
(def reset ig-repl/reset)

(defn server   [] (get system :datahost/server))
(defn datasets [] (get system :datahost/datasets))
(defn handler  [] (get system :datahost/handler))

(comment
  (go)
  (halt)
  (reset)
  system
  (server)
  (datasets))
