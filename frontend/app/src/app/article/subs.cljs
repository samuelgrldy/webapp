(ns app.article.subs
  (:require [app.utils :as u]
            [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::articles
  (fn [db _]
    (:articles db)))

(re-frame/reg-sub
  ::form-title
  (fn [db _]
    (:form-title db)))

(re-frame/reg-sub
  ::form-prompt
  (fn [db _]
    (:form-prompt db)))

(re-frame/reg-sub
  ::form-n-sections
  (fn [db _]
    (:form-n-sections db)))