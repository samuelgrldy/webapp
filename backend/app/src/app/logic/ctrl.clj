(ns app.logic.ctrl
  (:require [app.logic.article :as article]
            [app.logic.user :as user]
            [app.utils :refer :all]))


(defn gen-article
  "Controller for generating article"
  [db req]
  (let [{:keys [title n-sections prompt]} (get req :body)]
    (article/generate-article db
                              {:title      title
                               :n-sections n-sections
                               :prompt     prompt})))

(defn delete-article
  "Controller for deleting article"
  [db req]
  (let [{:keys [_id]} (get req :body)]
    (article/delete-article db _id)))

(defn reg-user
  "Controller for registering user"
  [db req]
  (let [{:keys [username name]} (get req :body)]
    (user/register-user db
                        {:username username
                         :name     name})))

(defn login-user
  "Controller for logging in user"
  [db req]
  (let [{:keys [username name]} (get req :body)]
    (user/login-user db
                     {:username username
                      :name     name})))