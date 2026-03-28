(ns datahost.handler
  (:require [datahost.datasets :as datasets]
            [datahost.views :as views]
            [integrant.core :as ig]
            [reitit.ring :as ring]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.util.http-response :as http]))

(defn- html [body]
  (-> (http/ok body)
      (http/content-type "text/html; charset=utf-8")))

(defn- make-routes [datasets-config]
  [["/"
    {:get {:handler (fn [_]
                      (html (views/home-page)))}}]

   ["/datasets"
    {:get {:handler (fn [_]
                      (html (views/datasets-page
                             (datasets/list-datasets datasets-config))))}}]

   ["/datasets/:id"
    {:get {:handler (fn [{:keys [path-params query-params]}]
                      (let [id      (:id path-params)
                            dataset (datasets/get-dataset datasets-config id)]
                        (if dataset
                          (let [active-file (or (get query-params "file")
                                                (first (:files dataset)))
                                csv-data    (when active-file
                                              (datasets/read-csv-file
                                               datasets-config id active-file
                                               :limit 100))]
                            (html (views/dataset-page dataset active-file csv-data)))
                          (-> (http/not-found (views/not-found-page))
                              (http/content-type "text/html; charset=utf-8")))))}}]])

(defmethod ig/init-key :datahost/handler [_ {:keys [datasets]}]
  (ring/ring-handler
   (ring/router
    (make-routes datasets)
    {:data {:middleware [parameters/parameters-middleware]}})
   (ring/create-default-handler
    {:not-found (fn [_]
                  (-> (http/not-found (views/not-found-page))
                      (http/content-type "text/html; charset=utf-8")))})))

(defmethod ig/halt-key! :datahost/handler [_ _] nil)
