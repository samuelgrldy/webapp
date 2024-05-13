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
    [:p "No articles to display rn"]
    [:div
     (for [article articles]
       [:div
        [:h2 (:title article)]
        [:p (:prompt article)]
        [:p "Sections: " (:n-sections article)]])]))

(defn view-article-page []
  (let [articles (re-frame/subscribe [::subs/articles])]
    [:div
     [:h1 "Placeholder for view article page"]
     [:button {:on-click #(re-frame/dispatch [::events/get-all-articles])} "View Articles"]
     (display-articles @articles)]))