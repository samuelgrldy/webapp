(ns app.logic.user
  (:require [app.logic.db :as db]
            [app.utils :refer :all]
            [monger.collection :as mc]))


(defn register-user
  "Registers a use to the db"
  [db-component user]
  (let [db-instance (:db db-component)
        username (get user :username)]
    (if (mc/find-maps db-instance "users" {:username username})
      (error "Username already exists.")
      (db/add-user db-instance user))))

(defn login-user
  "Logs in a user"
  [db-component user]
  (let [db-instance (:db db-component)
        username (get user :username)]
    (if (mc/find-maps db-instance "users" {:username username})
      (info "Login successful")
      (error "Username does not exist."))))