(ns app.logic.db
  (:require [monger.collection :as mc]
            [app.utils :refer :all]))


;;use this ns for db related functions

(defn add-article
  "Inserts a book to the db"
  [db-component {:keys [title prompt n-sections]}]
  (let [db-instance (:db db-component)
        article {:_id (uuid)
                 :title title
                 :prompt prompt
                 :n-sections n-sections}]
    (mc/insert-and-return db-instance "articles" article)))