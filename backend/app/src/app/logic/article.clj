(ns app.logic.article
  (:require [app.logic.generator :as gen]
            [app.logic.db :as db]
            [app.utils :refer :all]
            [monger.collection :as mc]
            [monger.operators :refer :all]))

(declare generate-proset)

(defn generate-article
  "Generate an article and adding it to the database"
  [db-component article-spec]
  (let [title (get-in article-spec [:title])
        n-sections  (get-in article-spec [:n-sections])
        prompt (get-in article-spec [:prompt])
        article {:title title
                 :prompt prompt
                 :n-sections n-sections}
        article-id (->> article
                        (db/add-article db-component)
                        (#(get-in % [:_id])))]
    (->> article
         (gen/gen-sections (:openai db-component))
         (#(get-in % [:sections]))
         (mapv (fn [section]
                 (let [section-data {:article-title title
                                     :section-title (:title section)
                                     :section-description (:description section)}]
                   (->> section-data
                        (gen/gen-content-extender (:openai db-component))
                        (#(assoc % :article-title title))
                        (gen/gen-content (:openai db-component))
                        (#(assoc % :article-id article-id))
                        (db/add-section db-component)
                        (generate-proset db-component))))))
    (info "Article generated successfully.")
    {:status "ok"
     :message "Article generated successfully."}))

(defn generate-proset
  "Generate a proset and adding it to the database"
  [db-component proset-spec]
  (let [title (get-in proset-spec [:title])
        content (get-in proset-spec [:content])
        content-id (get-in proset-spec [:_id])
        proset {:title title
                :content content}]
    (->> proset
         (gen/gen-proset (:openai db-component))
         (#(assoc % :content-id content-id))
         (db/add-proset db-component))))

(defn delete-article
  "Delete an article, its sections, and its proset based on the article id"
  [db-component article-id]
  (db/deep-delete-article db-component article-id))

;;bikin fungsi get all article for certain user (consult dulu)

(defn get-all-articles
  "Get all articles from the database"
  [db-component]
  (db/get-all-articles db-component))

(defn get-sections-by-article
  "Get all sections by article id"
  [db-component article-id]
  (info "Getting sections by article id: " article-id)
  (db/get-sections-by-article-id db-component article-id))

(defn get-completed-articles
  "Get completed articles for a user"
  [db-component user-id]
  (let [db-instance (:db db-component)
        ;; Step 1: Fetch user answers based on the user-id
        completed-proset (mc/find-maps db-instance "user-answers" {:user-id user-id})
        ;; Step 2: Extract proset-ids from these answers
        proset-ids (map :proset-id completed-proset)
        ;; Step 3: Fetch prosets based on these proset-ids
        prosets (mc/find-maps db-instance "prosets" {:_id {$in proset-ids}})
        ;; Step 4: Extract content-ids (section-ids) from the prosets
        section-ids (map :content-id prosets)
        ;; Step 5: Fetch sections based on these section-ids
        sections (mc/find-maps db-instance "sections" {:_id {$in section-ids}})
        ;; Step 6: Extract article-ids from these sections (renamed)
        extracted-article-ids (set (map :article-id sections))
        ;; Step 7: Fetch article titles based on these article-ids
        articles-with-titles (map (fn [article-id]
                                    (let [article (db/find-article-by-id db-component article-id)]
                                      {:article-id article-id
                                       :title (:title article)}))
                                  extracted-article-ids)]
    ;; Step 8: Return a list of article titles
    articles-with-titles))




(defn get-article-progress
  "Get detailed progress for a specific article for a user"
  [db-component user-id article-id]
  (let [completed-sections (db/find-completed-sections-by-article db-component user-id article-id)
        article (db/find-article-by-id db-component article-id)]
    (assoc article :sections completed-sections)))

(def sample-article-prompt
  {:title "Why we feel what we feel?"
   :n-sections 3
   :prompt "Jembrengin kenapa kita ngerasain emosi2 yang sekarang kita rasain dari perspektif evolutionary psychology. gue pengen kebahas in-depth juga beberapa perasaan 'negatif' kyk marah, sedih, depressed, dll dan kenapa perasaan itu justru punya evolutionary advantages to the point masih survive sampe sekarang"})

(def sample-article-prompt2
  {:title "The visible world of immune system"
   :n-sections 4
   :prompt "Jembrengin hal-hal yang berhubungan sama immune system ke 'macro world' dari perspektif evolutionary biology and dikit2 evolutionary anthropology, kyk misalnya bau badan kita yg justru ngebantu kita pilih 'biologically compatible' partner."})

(def sample-proset-prompt
  {:title "Conclusion"
   :content "<p>Jadi, gini ya guys, ngertiin dan nerima emosi kita tuh penting banget buat kesejahteraan hidup kita. Dengan ngenalin dan ngakuin perasaan kita, kita bisa jalanin hidup dengan lebih jelas dan tujuan yang lebih mantap.</p><p>Emosi tuh kayak sinyal berharga yang nunjukin ke dalam diri kita, bimbing kita buat nemuin diri sendiri dan tumbuh jadi pribadi yang lebih baik. Penting banget buat kembangin kecerdasan emosional dan bangun hubungan yang sehat sama emosi kita, soalnya emosi punya peran besar banget dalam membentuk pengalaman dan interaksi kita sama orang lain.</p><p>Ayo kita terima kompleksitas dari emosi kita dan manfaatin kekuatannya buat jalanin hidup yang lebih memuaskan.</p>, :description In conclusion, understanding and embracing our emotions is crucial for our overall well-being. By recognizing and acknowledging our feelings, we can navigate through life with greater clarity and purpose. Emotions serve as valuable signals that provide insight into our inner world, guiding us towards self-discovery and personal growth. It is essential to cultivate emotional intelligence and develop a healthy relationship with our emotions, as they play a significant role in shaping our experiences and interactions with others. Let's embrace the complexity of our emotions and harness their power to lead more fulfilling lives."
   :article-id "14879a100d8411efa2de5a0701208c73"
   :_id "52b173600d8411efa2de5a0701208c73"})