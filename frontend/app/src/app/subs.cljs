(ns app.subs
  (:require
   [re-frame.core :as re-frame]))



(re-frame/reg-sub
  ::current-page
  (fn [db _]
    (:current-page db)))

(re-frame/reg-sub
  ::loading
  (fn [db]
    (:loading db)))


(re-frame/reg-sub
  ::form-username
  (fn [db _]
    (:form-username db)))


(re-frame/reg-sub
  ::form-name
  (fn [db _]
    (:form-name db)))