(ns app.logic.ctrl
  (:require [app.logic.article :as article]
            [app.logic.user :as user]
            [app.logic.proset :as proset]
            [app.utils :refer :all]))


(defn gen-article
  "Controller for generating article"
  [db req]
  (info "Initiating generate-article controller...")
  (let [{:keys [title n-sections prompt]} (get req :body)
        ;;generating article and return the status
        generating-status (article/generate-article db
                                                     {:title      title
                                                      :n-sections n-sections
                                                      :prompt     prompt})]
    (info "received body: ")
    (pres {:title title
           :n-sections n-sections
           :prompt prompt})
    {:body {:data generating-status}}))

(defn get-articles
  "Controller for getting all articles"
  [db req]
  (info "Initiating get-articles-controller...")
  (let [articles (article/get-all-articles db)]
    {:body {:data articles}}))

(defn get-sections-by-article
  "Controller for getting sections by article"
  [db req]
  (info "Initiating get-sections-by-article controller...")
  (let [{:keys [article-id]} (get req :body)
        sections (article/get-sections-by-article db article-id)]
    (info "Passing this article id to the db: " article-id)
    {:body {:data sections}}))

(defn delete-article
  "Controller for deleting article"
  [db req]
  (let [{:keys [_id]} (get req :body)]
    (article/delete-article db _id)))

;;===============proset related ctrl================

(defn get-prosets-by-section
  [db req]
  (info "Initiating get-article-prosets-by-section controller...")
  (info "received req: " (pres req))
  (info "=======================================================")
  (let [{:keys [section-id]} (get req :body)
        proset (proset/get-proset-by-id db section-id)]
    (info "Passing this id to db: " section-id)
    {:body {:data proset}}))

(defn submit-answers
  [db req]
  (info "Initiating submit-proset controller...")
  (let [{:keys [user-id proset-id submitted-answers]} (get req :body)
        proset (proset/submit-answers  db {:user-id user-id
                                            :proset-id proset-id
                                            :submitted-answers submitted-answers})]
    (info "received user-id: " user-id)
    (info "received proset-id: " proset-id)
    (info "received submitted-answers: ")
    (pres submitted-answers)
    {:body {:data proset}}))

;;===============user related ctrl================

(defn get-user-progress
  [db req]
  (info "Initiating get-user-progress controller...")
  (let [user-id (get-in req [:params :user-id])
        completed-articles (article/get-completed-articles db user-id)]
    (println "Getting user progress")
    (pres completed-articles)
    {:body {:data completed-articles}}))

(defn get-article-progress
  [db req]
  (info "Initiating get-article-progress controller...")
  (let [{:keys [user-id article-id]} (get req :body)
        article-progress (article/get-article-progress db user-id article-id)]
    (println "Getting article progress")
    (pres article-progress)
    {:body {:data article-progress}}))

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