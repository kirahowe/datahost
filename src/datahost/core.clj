(ns datahost.core
  (:require [datahost.datasets]
            [datahost.handler]
            [datahost.server]
            [datahost.sys :as sys])
  (:gen-class))

(defn -main [& _]
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. ^Runnable sys/stop!))
  (sys/start!))
