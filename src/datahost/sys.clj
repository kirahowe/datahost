(ns datahost.sys
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [meta-merge.core :as meta-merge]))

(def config-paths ["system/base.edn" "system/env.edn"])

(defn load-config
  "Load integrant config from a sequence of classpath resource paths,
  meta-merging them in order. Missing resources are silently ignored."
  [paths]
  (->> paths
       (map #(some-> (io/resource %) slurp ig/read-string))
       (remove nil?)
       (apply meta-merge/meta-merge)))

(defonce ^:private system (atom nil))

(defn start!
  ([] (start! (load-config config-paths)))
  ([config]
   (ig/load-namespaces config)
   (reset! system (ig/init config))))

(defn stop! []
  (when-let [sys @system]
    (ig/halt! sys)
    (reset! system nil)))

(defn running? []
  (some? @system))

(defn system-component [key]
  (get @system key))
