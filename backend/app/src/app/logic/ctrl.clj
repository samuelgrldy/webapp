(ns app.logic.ctrl
  (:require [app.logic.article :as article]
            [app.logic.user :as user]
            [app.utils :refer :all]))


(defn gen-article
  "Controller for generating article"
  [db req]
  (info "Initiating generate-article controller...")
  (let [{:keys [title n-sections prompt]} (get req :body)]
    (info "received body: ")
    (pres {:title title
           :n-sections n-sections
           :prompt prompt})
    (article/generate-article db
                              {:title      title
                               :n-sections n-sections
                               :prompt     prompt})))

(defn get-articles
  "Controller for getting all articles"
  [db]
  (info "Initiating get articles...")
  (article/get-all-articles db))

(defn delete-article
  "Controller for deleting article"
  [db req]
  (let [{:keys [_id]} (get req :body)]
    (article/delete-article db _id)))

;;===============user related ctrl================

(defn reg-user
  "Controller for registering user"
  [db req]
  (info "Initiating register user...")
  (let [{:keys [username name]} (get req :body)]
    (println "Req received in controller: ")
    (pres req)
    (info "received body: " username name)
    (user/register-user db
                        {:username username
                         :name     name})))

(defn login-user
  "Controller for logging in user"
  [db req]
  (info "Initiating login user...")
  (let [{:keys [username name]} (get req :body)]
    (info "recevied body: " username name)
    (user/login-user db
                     {:username username
                      :name     name})))