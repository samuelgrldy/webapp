(ns app.proset.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::proset
  (fn [db _]
    (:proset db)))

(re-frame/reg-sub
  ::result
  (fn [db _]
    (:result db)))

(re-frame/reg-sub
  ::error
  (fn [db _]
    (:error db)))

(re-frame/reg-sub
  ::completed-articles
  (fn [db _]
    (:completed-articles db)))