(ns app.logic.user
  (:require [app.logic.db :as db]
            [app.utils :refer :all]
            [monger.collection :as mc]))


(defn register-user
  "Registers a use to the db"
  [db-component user]
  (let [db-instance (:db db-component)
        username (get user :username)]
    (info "Checking this username...")
    (pres {:username username})
    (if (empty? (mc/find-maps db-instance "users" {:username username}))
      (db/add-user db-component user)
      (error "Whoops, username already exists"))))

(defn login-user
  "Logs in a user"
  [db-component user]
  (let [db-instance (:db db-component)
        username (get user :username)]
    (if (not (empty? (mc/find-maps db-instance "users" {:username username})))
      (info "Username found. Logging in...")
      (info "Register dulu cuy"))))