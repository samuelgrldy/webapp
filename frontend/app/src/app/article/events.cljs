(ns app.article.events
  (:require [ajax.core :as ajax]
            [app.subs :as subs]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [app.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [app.utils :as u]))

(def base-url (db/default-db :base-url))

(re-frame/reg-event-db
  ::select-article
  (fn [db [_ article-id]]
    (assoc db :selected-article-id article-id
              :sections nil))) ;; Reset sections when selecting a new article


(re-frame/reg-event-fx
  ::get-all-articles
  (fn [{:keys [db]}]
    {:http-xhrio {:method          :get
                  :uri             (str base-url "/article/articles")
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::view-articles-success]
                  :on-failure      [::view-articles-failure]}}))

;;testing for showing articles
(re-frame/reg-event-db
  ::view-articles-success
  (fn [db [_ response]]
    (let [articles (:data response)]
      (do (js/alert "successfully getting all articles")
          (println articles)
          (assoc db :articles articles)))))

(re-frame/reg-event-fx
  ::view-articles-failure
  (fn [db [_ response]]
    (let [articles (:data response)]
      (js/alert "failed to get all articles")
      (println articles))))

;; Event to fetch sections for a given article ID
(re-frame/reg-event-fx
  ::get-sections-by-article
  (fn [{:keys [db]} [_ article-id]]
    {:http-xhrio {:method          :post
                  :uri             (str base-url "/article/sections")
                  :params          {:article-id article-id}
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::get-sections-by-article-success]
                  :on-failure      [::get-sections-by-article-failure]}}))

(re-frame/reg-event-db
  ::get-sections-by-article-success
  (fn [db [_ response]]
    (assoc db :sections (get-in response [:data]))))


;;navigate?
(re-frame/reg-event-db
  ::navigate
  (fn [db [_ page]]
    (assoc db :current-page page)))