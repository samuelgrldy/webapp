(ns app.article.events
  (:require [ajax.core :as ajax]
            [app.subs :as subs]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [app.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [app.utils :as u]))

(def base-url (db/default-db :base-url))



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

(re-frame/reg-event-db
  ::navigate
  (fn [db [_ page]]
    (assoc db :current-page page)))