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
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    {:db   (assoc db :loading true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :post
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
  ::login-user
  (fn [{:keys [db]}]
    (let [username (re-frame/subscribe [::subs/form-username])
          name    (re-frame/subscribe [::subs/form-name])]
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
    (let [username (:username response)]
      (println username)
      (println response)
      (u/set-storage :username username)
      (assoc db :loading false))
    (re-frame/dispatch [::navigate :home])))

;;login and register success and failure events
(re-frame/reg-event-fx
  ::login-failure
  (fn [db [_ response]]
    {:console-log "Login failed"}))

(re-frame/reg-event-fx
  ::show-articles                                           ;;ini impostor, buat test doang
  (fn [{:keys [db]} _]
    {:db         (assoc db :loading true)
     :http-xhrio {:method          :get
                  :uri             (str base-url "/article/articles")
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::show-articles]
                  :on-failure      [:login-failure]}}))

;;testing for showing articles
(re-frame/reg-event-fx
  ::show-articles
  (fn [db [_ response]]
    (let [articles (:data response)]
      (println articles))))




