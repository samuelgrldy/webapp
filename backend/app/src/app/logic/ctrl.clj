(ns app.logic.ctrl
  (:require [app.logic.article :as article]
            [app.utils :refer :all]))


(defn gen-article
  "Controller for generating article"
  [db req]
  (let [{:keys [title n-sections prompt]} (get req :body)]
    (article/generate-article db
                              {:title      title
                               :n-sections n-sections
                               :prompt     prompt})))