(ns app.logic.proset
  (:require [app.utils :refer :all]
            [app.logic.db :as db]
            [monger.collection :as mc]))



(defn submit-answers
  "Check each submitted answers against the correct answers in the proset"
  [db-component {:keys [user-id proset-id submitted-answers]}]
  (let [proset (db/get-proset-by-id db-component proset-id)
        soals (:soals proset)
        detailed-result (map (fn [submitted]
                               (let [soal (first (filter #(= (:text-soal %) (:text-soal submitted)) soals))
                                     correct-choice (first (filter #(= (:idx %) (:idx submitted)) (:choices soal)))]
                                 {:text-soal (:text-soal submitted)
                                  :user-answer (:idx submitted)
                                  :correct? (:correct correct-choice)}))
                             submitted-answers)
        store-result {:user-id user-id
                      :proset-id proset-id
                      :detailed-result detailed-result}]
    (db/store-answer db-component store-result)))

(defn get-proset-by-id
  "Get the proset based on the section id"
  [db-component section-id]
  (info "Proset: passing this id to the db: " section-id)
  (db/get-proset-by-section-id db-component section-id))

(def sample-submitted-answers
  [{:text-soal "What impact can an unhealthy diet have on the immune system?"
    :idx 1}
   {:text-soal "How does prolonged stress affect the immune system?"
    :idx 2}
   {:text-soal "What role does physical activity play in immune health?"
    :idx 2}
   {:text-soal "What are practical tips for boosting immune health?"
    :idx 4}
   {:text-soal "What is the future of immune system research?"
    :idx 1}])