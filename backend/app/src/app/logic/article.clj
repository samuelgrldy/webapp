(ns app.logic.article
  (:require [app.logic.generator :as gen]
            [app.logic.db :as db]))


(defn generate-)

(defn generate-article
  "Generate an article and adding it to the database"
  [db-component article-spec]
  (let [title (get-in article-spec [:title])
        n-sections  (get-in article-spec [:n-sections])
        prompt (get-in article-spec [:prompt])
        article {:title title
                 :prompt prompt
                 :n-sections n-sections}
        article-to-db ]
    ))

