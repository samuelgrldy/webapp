(ns app.logic.db
  (:require [monger.collection :as mc]
            [monger.operators :refer :all]
            [app.utils :refer :all]))


;;use this ns for db related functions

;;===========articles================

(defn get-all-articles
  "Get all articles from the db"
  [db-component]
  (info "Initiating get-all-articles from db")
  (let [db-instance (:db db-component)]
    (mc/find-maps db-instance "articles" {})))

(defn add-article
  "Inserts a book to the db"
  [db-component {:keys [title prompt n-sections]}]
  (let [db-instance (:db db-component)
        article {:_id (uuid)
                 :title title
                 :prompt prompt
                 :n-sections n-sections}]
    (mc/insert-and-return db-instance "articles" article)))

(defn add-section
  "Inserts a section to the db"
  [db-component section]
  (let [db-instance (:db db-component)
        section-with-id (assoc section :_id (uuid))]
    (info "====Adding the following section to the db====")
    (pres section-with-id)
    (info "=============================================")
    (mc/insert-and-return db-instance "sections" section-with-id)))

(defn add-sections
  "Inserts batch of sections to the db and adding their own uuid"
  [db-component sections]
  (let [db-instance (:db db-component)
        sections-with-id (mapv (fn [section]
                                 (assoc section :_id (uuid)))
                               sections)]
    (mc/insert-batch db-instance "sections" sections-with-id)))

(defn deep-delete-article
  "Deletes an article, its sections, and its proset based on the article id"
  [db-component article-id]
  (let [db-instance (:db db-component)
        query-article {:_id article-id}
        query-sections {:article-id article-id}
        section-ids (map :_id (mc/find-maps db-instance "sections" query-sections))]
    (mc/remove db-instance "articles" query-article)
    (mc/remove db-instance "sections" query-sections)
    (map #(mc/remove db-instance "prosets" {:_id %}) section-ids)
    (info "Article, sections, and prosets related to '" article-id "' deleted successfully.")))

(defn find-article-by-id
  "Find an article by its ID"
  [db-component article-id]
  (let [db-instance (:db db-component)
        query {:_id article-id}]
    (mc/find-one-as-map db-instance "articles" query)))

(defn get-sections-by-article-id
  "Get all the sections based on the article id"
  [db-component article-id]
  (let [db-instance (:db db-component)
        query {:article-id article-id}]
    (mc/find-maps db-instance "sections" query)))

(defn get-section-ids
  "Get the section ids based on the article id"
  [db-component article-id]
  (let [db-instance (:db db-component)
        query {:article-id article-id}]
    (map :_id (mc/find-maps db-instance "sections" query))))

(defn find-completed-sections
  "Find sections that have been completed by the user from the prosets"
  [db-component user-id]
  (let [db-instance (:db db-component)]
    (mc/find-maps db-instance "user-answers" {:user-id user-id})))

(defn find-articles-by-sections
  "Find articles that contain the given sections"
  [db-component section-ids]
  (let [db-instance (:db db-component)]
    (mc/find-maps db-instance "articles" {:section-id {$in section-ids}})))


(defn find-completed-sections-by-article
  "Find sections that have been completed by the user for a specific article"
  [db-component user-id article-id]
  (let [db-instance (:db db-component)
        section-ids (get-section-ids db-component article-id)]
    (mc/find-maps db-instance "user-answers" {:user-id user-id :section-id {$in section-ids}})))



;;=====helper functions=========

(defn get-proset-ids
  "Get the proset ids based on the article id
  Receive a map of section ids to return a map of proset ids"
  [db-component article-id]
  (let [db-instance (:db db-component)
        section-ids (get-section-ids db-component article-id)]
    (map (fn [section-id]
           (let [query {:content-id section-id}]
             (map :_id (mc/find-one-as-map db-instance "prosets" query))))
         section-ids)))

(defn get-prosets-by-id
  "Get all the proset based on the article id"
  [db-component article-id]
  (let [db-instance (:db db-component)
        section-ids (get-section-ids db-component article-id)]
    (map (fn [section-id]
           (let [query {:content-id section-id}]
             (mc/find-one-as-map db-instance "prosets" query)))
         section-ids)))

;;===========proset==============

(defn add-proset
  "Insert the proset to the db"
  [db-component proset]
  (let [db-instance (:db db-component)
        proset-with-id (assoc proset :_id (uuid))]
    (info "====Adding the following proset to the db====")
    (pres proset-with-id)
    (info "=============================================")
    (mc/insert-and-return db-instance "prosets" proset-with-id)))

(defn get-proset-by-section-id
  "Get the proset based on the proset id"
  [db-component section-id]
  (let [db-instance (:db db-component)
        query {:content-id section-id}]
    (info "Fetching proset with this id from the db: " section-id)
    (mc/find-one-as-map db-instance "prosets" query)))

(defn get-proset-by-id
  "Get the proset based on the proset id"
  [db-component proset-id]
  (let [db-instance (:db db-component)
        query {:_id proset-id}]
    (mc/find-one-as-map db-instance "prosets" query)))

(defn store-answer
  "store the user answers to the db"
  [db-component {:keys [user-id proset-id detailed-result]}]
  (let [db-instance (:db db-component)
        user-answer {:_id (uuid)
                     :user-id user-id
                     :proset-id proset-id
                     :detailed-result detailed-result}]
    (mc/insert-and-return db-instance "user-answers" user-answer)))


;;===========user================

(defn add-user
  "Inserts a user to the db"
  [db-component {:keys [name username]}]
  (let [db-instance (:db db-component)
        user {:_id (uuid)
              :name name
              :username username}]
    (mc/insert-and-return db-instance "users" user)))

(defn find-user
  "Find a user based on the username"
  [db-component username]
  (let [db-instance (:db db-component)]
    (mc/find-maps db-instance "users" {:username username})))



