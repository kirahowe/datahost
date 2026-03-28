(ns datahost.handler
  (:require [datahost.datasets :as datasets]
            [datahost.views :as views]
            [integrant.core :as ig]
            [reitit.ring :as reitit]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as resp]))

(defn- html-response [body]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    body})

(defn- make-routes [datasets-config]
  [["/" {:get (fn [_] (html-response (views/home-page)))}]

   ["/datasets"
    {:get (fn [_]
            (html-response (views/datasets-page
                            (datasets/list-datasets datasets-config))))}]

   ["/datasets/:id"
    {:get (fn [{:keys [path-params query-params]}]
            (let [id      (:id path-params)
                  dataset (datasets/get-dataset datasets-config id)]
              (if dataset
                (let [files      (:files dataset)
                      active-file (or (get query-params "file")
                                      (first files))
                      csv-data   (when active-file
                                   (datasets/read-csv-file datasets-config id active-file
                                                           :limit 100))]
                  (html-response (views/dataset-page dataset active-file csv-data)))
                {:status  404
                 :headers {"Content-Type" "text/html; charset=utf-8"}
                 :body    (views/not-found-page)})))}]])

(defmethod ig/init-key :datahost/handler [_ {:keys [datasets]}]
  (-> (reitit/ring-handler
       (reitit/router (make-routes datasets))
       (reitit/create-default-handler
        {:not-found          (fn [_] {:status  404
                                      :headers {"Content-Type" "text/html; charset=utf-8"}
                                      :body    (views/not-found-page)})
         :method-not-allowed (fn [_] (resp/status (resp/response "Method not allowed") 405))}))
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))

(defmethod ig/halt-key! :datahost/handler [_ _] nil)
