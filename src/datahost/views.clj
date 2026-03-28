(ns datahost.views
  (:require [hiccup2.core :as h]))

(def ^:private styles "
  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: system-ui, -apple-system, sans-serif; color: #1a1a1a;
         background: #f8f8f7; line-height: 1.5; }
  a { color: #2563eb; text-decoration: none; }
  a:hover { text-decoration: underline; }
  nav { background: #1a1a1a; color: #fff; padding: 0.75rem 2rem;
        display: flex; align-items: center; gap: 1.5rem; }
  nav a { color: #e5e7eb; font-weight: 500; }
  nav .brand { font-size: 1.1rem; font-weight: 700; color: #fff; }
  .container { max-width: 1100px; margin: 0 auto; padding: 2rem; }
  h1 { font-size: 1.75rem; font-weight: 700; margin-bottom: 0.5rem; }
  h2 { font-size: 1.25rem; font-weight: 600; margin-bottom: 0.75rem; }
  .subtitle { color: #6b7280; margin-bottom: 2rem; }
  .card { background: #fff; border: 1px solid #e5e7eb; border-radius: 8px;
          padding: 1.25rem 1.5rem; margin-bottom: 1rem;
          transition: box-shadow 0.15s; }
  .card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
  .card h2 { margin-bottom: 0.25rem; font-size: 1.1rem; }
  .card p { color: #6b7280; font-size: 0.9rem; }
  .badge { display: inline-block; font-size: 0.75rem; padding: 0.2em 0.6em;
           border-radius: 999px; background: #e0e7ff; color: #3730a3;
           font-weight: 500; margin-right: 0.25rem; }
  .table-wrap { overflow-x: auto; }
  table { width: 100%; border-collapse: collapse; font-size: 0.875rem; }
  th { background: #f3f4f6; text-align: left; padding: 0.5rem 0.75rem;
       border-bottom: 2px solid #d1d5db; white-space: nowrap; }
  td { padding: 0.45rem 0.75rem; border-bottom: 1px solid #e5e7eb;
       max-width: 300px; overflow: hidden; text-overflow: ellipsis;
       white-space: nowrap; }
  tr:hover td { background: #f9fafb; }
  .file-list { list-style: none; display: flex; flex-wrap: wrap; gap: 0.5rem;
               margin-bottom: 1.5rem; }
  .file-list li a { display: inline-block; padding: 0.3rem 0.8rem;
                    border: 1px solid #d1d5db; border-radius: 6px;
                    font-size: 0.875rem; background: #fff; }
  .file-list li a:hover { background: #f3f4f6; text-decoration: none; }
  .file-list li a.active { background: #2563eb; color: #fff; border-color: #2563eb; }
  .meta-dl { display: grid; grid-template-columns: max-content 1fr;
             gap: 0.4rem 1.5rem; margin-bottom: 1.5rem; font-size: 0.9rem; }
  .meta-dl dt { color: #6b7280; font-weight: 500; }
  .empty { color: #9ca3af; font-style: italic; padding: 2rem; text-align: center; }
  .back { display: inline-flex; align-items: center; gap: 0.25rem;
          color: #6b7280; font-size: 0.875rem; margin-bottom: 1rem; }
  .hero { background: #1a1a1a; color: #fff; padding: 4rem 2rem; }
  .hero h1 { color: #fff; font-size: 2.5rem; margin-bottom: 0.75rem; }
  .hero p { color: #9ca3af; font-size: 1.1rem; max-width: 600px; }
  .hero a { display: inline-block; margin-top: 1.5rem; padding: 0.6rem 1.5rem;
            background: #2563eb; color: #fff; border-radius: 6px; font-weight: 600; }
  .hero a:hover { background: #1d4ed8; text-decoration: none; }
")

(defn- page [title & body]
  (str
   "<!DOCTYPE html>"
   (h/html
    [:html {:lang "en"}
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:title (str title " — Datahost")]
      [:style styles]]
     [:body
      [:nav
       [:a.brand {:href "/"} "Datahost"]
       [:a {:href "/datasets"} "Datasets"]]
      body]])))

(defn home-page []
  (page "Home"
    [:div.hero
     [:h1 "Datahost"]
     [:p "A home for public datasets. Explore, download, and understand government spending data."]
     [:a {:href "/datasets"} "Browse datasets"]]
    [:div.container
     [:h2 {:style "margin-top:0"} "Featured: Nova Scotia Public Spending"]
     [:p {:style "color:#6b7280;margin-bottom:1.5rem"}
      "We're starting with public spending data from the Government of Nova Scotia. "
      "More datasets coming soon."]
     [:a {:href "/datasets"}
      [:div.card
       [:h2 "Awarded Public Tenders"]
       [:p "Awarded vendor and amount for government and public sector tenders in Nova Scotia."]]]
     [:a {:href "/datasets"}
      [:div.card
       [:h2 "Government Health Expenditure"]
       [:p "Annual government health expenditure by zone across Nova Scotia."]]]
     [:a {:href "/datasets"}
      [:div.card
       [:h2 "Municipal Fiscal Statistics"]
       [:p "Consolidated revenues and expenses for Nova Scotia municipalities."]]]]))

(defn datasets-page [datasets]
  (page "Datasets"
    [:div.container
     [:h1 "Datasets"]
     [:p.subtitle (str (count datasets) " dataset" (when (not= 1 (count datasets)) "s") " available")]
     (if (seq datasets)
       (for [ds datasets]
         [:a {:href (str "/datasets/" (:id ds))}
          [:div.card
           [:h2 (:name ds)]
           (when (:description ds)
             [:p (:description ds)])
           [:p {:style "margin-top:0.5rem"}
            (for [tag (or (:tags ds) [])]
              [:span.badge tag])
            (when (seq (:files ds))
              [:span {:style "color:#9ca3af;font-size:0.8rem;margin-left:0.5rem"}
               (count (:files ds)) " file(s)"])]]])
       [:p.empty "No datasets found."])]))

(defn dataset-page [dataset active-file csv-data]
  (page (:name dataset)
    [:div.container
     [:a.back {:href "/datasets"} "← Datasets"]
     [:h1 (:name dataset)]
     (when (:description dataset)
       [:p.subtitle (:description dataset)])
     (when (seq (:tags dataset))
       [:p {:style "margin-bottom:1rem"}
        (for [tag (:tags dataset)]
          [:span.badge tag])])
     (when (or (:source dataset) (:updated dataset))
       [:dl.meta-dl
        (when (:source dataset)
          (list [:dt "Source"]
                [:dd [:a {:href (:source dataset) :target "_blank"} (:source dataset)]]))
        (when (:updated dataset)
          (list [:dt "Updated"] [:dd (:updated dataset)]))])
     [:h2 "Files"]
     (if (seq (:files dataset))
       [:ul.file-list
        (for [f (:files dataset)]
          [:li [:a {:href  (str "/datasets/" (:id dataset) "?file=" f)
                    :class (when (= f active-file) "active")}
                f]])]
       [:p.empty "No CSV files found."])
     (when csv-data
       (let [{:keys [headers rows file]} csv-data]
         [:div
          [:h2 (str "Preview: " file)]
          [:p.subtitle {:style "margin-bottom:1rem"} (str (count rows) " rows shown (max 100)")]
          [:div.table-wrap
           [:table
            [:thead [:tr (for [h headers] [:th h])]]
            [:tbody
             (for [row rows]
               [:tr (for [cell row] [:td cell])])]]]]))]));

(defn not-found-page []
  (page "Not Found"
    [:div.container
     [:h1 "404 — Not Found"]
     [:p.subtitle "The page you're looking for doesn't exist."]
     [:a {:href "/"} "← Home"]]))
