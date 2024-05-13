(ns app.events
  (:require [ajax.core :as ajax]
            [app.subs :as subs]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [app.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [app.utils :as u]))

;;=============Initializing DB and the pages================
(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    {:current-page :main}))

(re-frame/reg-event-db
  ::navigate
  (fn [db [_ page]]
    (assoc db :current-page page)))

(def base-url (db/default-db :base-url))

;;=======login and register==========

(re-frame/reg-event-db
  ::update-username
  (fn [db [_ username]]
    (assoc db :form-username username)))

(re-frame/reg-event-db
  ::update-name
  (fn [db [_ name]]
    (assoc db :form-name name)))


;;rewriting this stuff
(re-frame/reg-event-fx
  ::register-user
  (fn [{:keys [db]}]
    (let [username (re-frame/subscribe [::subs/form-username])
          name     (re-frame/subscribe [::subs/form-name])]
      {:db         (assoc db :loading true)
       :http-xhrio {:method          :post
                    :uri             (str base-url "/register")
                    :params          {:username @username :name @name}
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::register-success]
                    :on-failure      [::register-failure]}})))

(re-frame/reg-event-db
  ::register-success
  (fn [db [_ response]]
    (let [username (:username response)
          status   (:status response)
          message  (:message response)]
      (case status
        "ok" (do
               (println "Register success")
               (re-frame/dispatch [::navigate :login])
               (u/set-storage :username username)
               (assoc db :loading false))
        "error" (do
                  (println "Register failed")
                  (js/alert message)
                  (assoc db :loading false))))))

(re-frame/reg-event-db
  ::register-failure
  (fn [db [_ response]]
    (println "Register failed")
    (assoc db :loading false)))

;;login event
(re-frame/reg-event-fx
  ::login-user
  (fn [{:keys [db]}]
    (let [username (re-frame/subscribe [::subs/form-username])
          name     (re-frame/subscribe [::subs/form-name])]
      {:db         (assoc db :loading true)
       :http-xhrio {:method          :post
                    :uri             (str base-url "/login")
                    :params          {:username @username :name @name}
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::login-success]
                    :on-failure      [::login-failure]}})))

(re-frame/reg-event-db
  ::login-success
  (fn [db [_ response]]
    (let [username (:username response)
          status   (:status response)
          message  (:message response)]
      (case  status
        "ok" (do
               (println "Login success")
               (println username)
               (println response)
               (u/set-storage :username username)
               (re-frame/dispatch [::navigate :home])
               (assoc db :loading false))
        "error" (do
                  (println "Login failed")
                  (js/alert message)
                  (assoc db :loading false))))))

(re-frame/reg-event-db
  ::login-failure
  (fn [db [_ response]]
    (println "Login failed")
    (assoc db :loading false)))





