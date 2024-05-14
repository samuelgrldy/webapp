(ns app.logic.user
  (:require [app.logic.db :as db]
            [app.utils :refer :all]
            [monger.collection :as mc]))


(defn register-user
  "Registers a user to the db"
  [db-component user]
  (let [username (get user :username)]
    (info "Checking this username...")
    (pres {:username username})
    (if (empty? (db/find-user db-component username))
      (do (db/add-user db-component user)
          (info "User added successfully")
          {:body {:status "ok"
                  :message "User added successfully. Go login"}})
      (do (info "Whoops, username already exists")
          {:body {:status "error"
                  :message "Username udah ada, cuy. Coba yg laen ye"}}))))

(defn login-user
  "Logs in a user"
  [db-component user-data]
  (let [username (get user-data :username)
        name (get user-data :name)
        find-user (db/find-user db-component username)
        user-id (:_id (first find-user))]
    (if (not (empty? find-user))
      {:body {:status "ok"
              :message "Login successful"
              :user-id user-id
              :username username
              :name name}}
      {:body {:status "error"
              :message "Username gaada cuy. Register dulu sono"}})))