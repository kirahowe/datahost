(ns datahost.core
  (:require [datahost.datasets]
            [datahost.handler]
            [datahost.server]
            [integrant.core :as ig]
            [clojure.java.io :as io])
  (:gen-class))

(defn read-config []
  (-> (io/resource "config.edn")
      slurp
      (ig/read-string)))

(defn system-config []
  (let [base    (read-config)
        port    (some-> (System/getenv "PORT") (Integer/parseInt))]
    (cond-> base
      port (assoc-in [:datahost/server :port] port))))

(defn -main [& _]
  (let [config (system-config)]
    (ig/load-namespaces config)
    (ig/init config)))
