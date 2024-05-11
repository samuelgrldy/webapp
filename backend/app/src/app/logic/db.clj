(ns app.logic.db
  (:require [monger.collection :as mc]
            [app.utils :refer :all]))


;;use this ns for db related functions

;;===========articles================

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

(defn add-proset
  "Insert the proset to the db"
  [db-component proset]
  (let [db-instance (:db db-component)
        proset-with-id (assoc proset :_id (uuid))]
    (info "====Adding the following proset to the db====")
    (pres proset-with-id)
    (info "=============================================")
    (mc/insert-and-return db-instance "prosets" proset-with-id)))

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

;;===========user================

(defn add-user
  "Inserts a user to the db"
  [db-component {:keys [name username]}]
  (let [db-instance (:db db-component)
        user {:_id (uuid)
              :name name
              :username username}]
    (mc/insert-and-return db-instance "users" user)))



