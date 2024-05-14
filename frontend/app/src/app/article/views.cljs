(ns app.article.views
  (:require [app.utils :as u]
            [re-frame.core :as re-frame]
            [app.article.events :as events]
            [app.article.subs :as subs]))

(defn generate-page []
  [:div
   [:h1 "Placeholder for generate page"]])



(defn display-articles
  [articles]
  (if (empty? articles)
    [:p "No articles to display right now"]
    [:div.row
     (for [article articles]
       [:div.col-md-4 {:class "article"}
        [:div.card
         [:div.card-body
          [:h2.card-title
           [:a {:href "#" :on-click #(do
                                       (re-frame/dispatch [::events/select-article (:_id article)])
                                       (re-frame/dispatch [::events/navigate :view]))}
            (:title article)]]]]])]))

(defn view-article-page []
  (let [articles (re-frame/subscribe [::subs/articles])]
    (re-frame/dispatch [::events/get-all-articles])
    (fn []
      [:div.container
       [:h1 "List of Articles"]
       (display-articles @articles)])))


(defn display-sections
  []
  (let [sections (re-frame/subscribe [::subs/sections])]
    (fn []
      (if (empty? @sections)
        [:p "No sections to display right now"]
        [:div
         (for [section @sections]
           [:div
            [:h2 (:title section)]
            [:p (:content section)]
            [:button.btn.btn-primary {:on-click #(re-frame/dispatch [::events/start-proset (:id section)])} "Do Proset"]])]))))


(defn article-page [article-id]
  (let [sections (re-frame/subscribe [::subs/sections])]
    (when (nil? @sections)
      (re-frame/dispatch [::events/get-sections-by-article article-id]))
    (fn []
      [:div.container
       [:h1 "Article Details"]
       [display-sections]]))) ;; Use the display-sections component
