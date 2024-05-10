(ns app.events
  (:require [ajax.core :as ajax]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [app.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
   ))

;; Initializing DB
(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    {:current-page :main}))

(re-frame/reg-event-db
  :navigate
  (fn [db [_ page]]
    (assoc db :current-page page)))

;;=======login and register==========

;;login event
(re-frame/reg-event-fx
  :login-user
  (fn [_ [username name]]
    {:http-xhrio {:method          :post
                  :uri             "/api/login"
                  :params          {:username username :name name}
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:login-success]
                  :on-failure      [:login-failure]}}))

;;register event
(re-frame/reg-event-fx
  :register-user
  (fn [_ [username name]]
    {:http-xhrio {:method          :post
                  :uri             "/api/register"
                  :params          {:username username :password name}
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:register-success]
                  :on-failure      [:register-failure]}}))

;;login and register success and failure events
(re-frame/reg-event-fx
  :login-success
  (fn [_ response]
    (let [username (get-in response [:body :username])]
      {:local-storage (js/JSON.stringify {:username username})
       :dispatch      [:navigate :main-page]})))

(re-frame/reg-event-fx
  :login-failure
  (fn [_ response]
    {:console-log "Login failed"}))


(re-frame/reg-event-fx
  :register-success
  (fn [_ response]
    {:dispatch [:navigate :login]}))

(re-frame/reg-event-fx
  :register-failure
  (fn [_ response]
    {:console-log "Registration failed"}))