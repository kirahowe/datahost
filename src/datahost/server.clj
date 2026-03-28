(ns datahost.server
  (:require [integrant.core :as ig]
            [org.httpkit.server :as http-kit]))

(defmethod ig/init-key :datahost/server [_ {:keys [handler port]}]
  (let [port (if (string? port) (Integer/parseInt port) port)]
    (println (str "Starting server on port " port))
    (http-kit/run-server handler {:port port})))

(defmethod ig/halt-key! :datahost/server [_ stop-fn]
  (stop-fn))
