(ns datahost.server
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]))

(defmethod ig/init-key :datahost/server [_ {:keys [handler port]}]
  (let [port (if (string? port) (Integer/parseInt port) port)]
    (println (str "Starting server on port " port))
    (jetty/run-jetty handler {:port port :join? false})))

(defmethod ig/halt-key! :datahost/server [_ server]
  (.stop server))
