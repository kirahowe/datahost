(ns datahost.datasets
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [integrant.core :as ig])
  (:import [java.io File]))

(defmethod ig/init-key :datahost/datasets [_ {:keys [data-dir]}]
  {:data-dir data-dir})

(defmethod ig/halt-key! :datahost/datasets [_ _] nil)

(defn- dataset-dir [config]
  (io/file (:data-dir config)))

(defn list-datasets
  "Returns a seq of dataset metadata maps, one per subdirectory of data-dir."
  [config]
  (let [dir (dataset-dir config)]
    (when (.exists dir)
      (->> (.listFiles dir)
           (filter #(.isDirectory %))
           (map (fn [^File d]
                  (let [meta-file (io/file d "meta.edn")]
                    (merge {:id   (.getName d)
                            :name (.getName d)
                            :files (->> (.listFiles d)
                                        (filter #(str/ends-with? (.getName %) ".csv"))
                                        (map #(.getName %))
                                        sort
                                        vec)}
                           (when (.exists meta-file)
                             (read-string (slurp meta-file)))))))
           (sort-by :name)))))

(defn get-dataset
  "Returns metadata for a single dataset by id."
  [config id]
  (->> (list-datasets config)
       (filter #(= (:id %) id))
       first))

(defn read-csv-file
  "Reads a CSV file from dataset `id`, returns {:headers [...] :rows [[...]]}.
  Optionally limits to `limit` rows (default 100)."
  [config id filename & {:keys [limit] :or {limit 100}}]
  (let [f (io/file (dataset-dir config) id filename)]
    (when (.exists f)
      (with-open [reader (io/reader f)]
        (let [[headers & rows] (csv/read-csv reader)
              rows (if limit (take limit rows) rows)]
          {:headers headers
           :rows    (vec rows)
           :file    filename})))))
