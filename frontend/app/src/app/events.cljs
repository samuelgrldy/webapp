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

(def base-url (db/default-db :base-url))

;;=======login and register==========

(re-frame/reg-event-db
  :update-username
  (fn [db [_ username]]
    (assoc db :form-username username)))

(re-frame/reg-event-db
  :update-name
  (fn [db [_ name]]
    (assoc db :form-name name)))


;;rewriting this stuff
(re-frame/reg-event-fx
  ::register-user
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    {:db   (assoc db :loading true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             (str base-url "/register")
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [::register-success]
                  :on-failure      [::register-failure]}}))

(re-frame/reg-event-fx
  ::register-success
  (fn [db [_ response]]
    (js/console.log "Register Success:" response)
    (println "Register Success with this response: " response)
    (-> db
        (assoc :loading false))))

(re-frame/reg-event-fx
  ::register-failure
  (fn [_ response]
    (js/console.log "Register Failed:" response)
    {:console-log "Registration failed"}))

;;login event
(re-frame/reg-event-fx
  :login-user
  (fn [coeffects [_ username name]]
    {:http-xhrio {:method          :post
                  :uri             (str base-url "/login")
                  :params          {:username username :name name}
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:login-success]
                  :on-failure      [:login-failure]}}))


;;login and register success and failure events
(re-frame/reg-event-fx
  :login-success
  (fn [_ response]
    (let [username (get-in response [:body :username])]
      {:local-storage (js/JSON.stringify {:username username})
       :dispatch      [:navigate :home-page]})))

(re-frame/reg-event-fx
  :login-failure
  (fn [_ response]
    {:console-log "Login failed"}))


