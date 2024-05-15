(ns app.article.views
  (:require [app.utils :as u]
            [re-frame.core :as re-frame]
            [app.events :as main-events]
            [app.article.events :as events]
            [app.proset.events :as proset-events]
            [app.article.subs :as subs]
            [app.article.components :as comp]))



(defn generate-page []
  [:div
   [:h1 "Placeholder for generate page"]
   (comp/generate-article-form)])

(defn display-articles
  [articles]
  (println "triggering display articles...")
  (if (empty? articles)
    [:p "No articles to display right now"]
    [:div.row
     (for [article articles]
       ^{:key (:_id article)}
       [:div.col-md-4 {:class "article"}
        [:div.card
         [:div.card-body
          [:h2.card-title
           [:a {:href "#" :on-click #(do
                                       (re-frame/dispatch [::events/select-article (:_id article)])
                                       (re-frame/dispatch [::main-events/navigate :view]))}
            (:title article)]]]]])]))


(defn view-all-article-page []
  (let [articles (re-frame/subscribe [::subs/articles])]
    (println "triggering view all article page...")
    (re-frame/dispatch [::events/get-all-articles])
    [:div.container
     [:h1 "List of Articles:"]
     (display-articles @articles)]))


(defn display-sections
  []
  (let [sections (re-frame/subscribe [::subs/sections])]
    (if (empty? @sections)
      [:p "No sections to display right now. Still generating?"]
      [:div
       [:button {:on-click #(re-frame/dispatch [::events/deselect-article])} "View another article"]
       (for [section @sections]
         [:div
          [:h2 (:title section)]
          (u/html->hiccup (:content section))
          [:button.btn.btn-primary {:on-click #(re-frame/dispatch [::proset-events/start-proset (:_id section)])} "Practice"]])])))


(defn article-page [article-id]
  (let [sections (re-frame/subscribe [::subs/sections])]
    (when (nil? @sections)
      (re-frame/dispatch [::events/get-sections-by-article article-id]))
    [:div.container
     [:h1 "Article Details"]
     [display-sections]])) ;; Use the display-sections component

;;================sections after proset views===============

(defn section-page []
  (let [sections (re-frame/subscribe [::subs/sections])
        completed-sections (re-frame/subscribe [::subs/completed-sections])]
    (fn []
      [:div.container
       (for [section @sections]
         ^{:key (:id section)}
         [:div.section
          [:h3 (:title section)]
          [:p (:content section)]
          (if (contains? @completed-sections (:id section))
            [:button.btn.btn-secondary
             {:on-click #(re-frame/dispatch [::proset-events/redo-practice (:id section)])}
             "Redo Practice"]
            [:button.btn.btn-primary
             {:on-click #(re-frame/dispatch [::proset-events/start-practice (:id section)])}
             "Practice"])])])))

