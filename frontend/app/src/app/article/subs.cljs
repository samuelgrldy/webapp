(ns app.article.subs
  (:require [app.utils :as u]
            [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::articles
  (fn [db _]
    (:articles db)))

(re-frame/reg-sub
  ::sections
  (fn [db _]
    (:sections db)))

(re-frame/reg-sub
  ::selected-article-id
  (fn [db _]
    (:selected-article-id db)))

(re-frame/reg-sub
  ::form-state
  (fn [db _]
    (:form-state db)))




