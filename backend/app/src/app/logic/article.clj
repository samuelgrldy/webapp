(ns app.logic.article
  (:require [app.logic.generator :as gen]
            [app.logic.db :as db]
            [app.utils :refer :all]))

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
                        (generate-proset db-component))))))))

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

(def sample-article-prompt
  {:title "Why we feel what we feel?"
   :n-sections 3
   :prompt "Jembrengin kenapa kita ngerasain emosi2 yang sekarang kita rasain dari perspektif evolutionary psychology. gue pengen kebahas in-depth juga beberapa perasaan 'negatif' kyk marah, sedih, depressed, dll dan kenapa perasaan itu justru punya evolutionary advantages to the point masih survive sampe sekarang"})

(def sample-proset-prompt
  {:title "Conclusion"
   :content "<p>Jadi, gini ya guys, ngertiin dan nerima emosi kita tuh penting banget buat kesejahteraan hidup kita. Dengan ngenalin dan ngakuin perasaan kita, kita bisa jalanin hidup dengan lebih jelas dan tujuan yang lebih mantap.</p><p>Emosi tuh kayak sinyal berharga yang nunjukin ke dalam diri kita, bimbing kita buat nemuin diri sendiri dan tumbuh jadi pribadi yang lebih baik. Penting banget buat kembangin kecerdasan emosional dan bangun hubungan yang sehat sama emosi kita, soalnya emosi punya peran besar banget dalam membentuk pengalaman dan interaksi kita sama orang lain.</p><p>Ayo kita terima kompleksitas dari emosi kita dan manfaatin kekuatannya buat jalanin hidup yang lebih memuaskan.</p>, :description In conclusion, understanding and embracing our emotions is crucial for our overall well-being. By recognizing and acknowledging our feelings, we can navigate through life with greater clarity and purpose. Emotions serve as valuable signals that provide insight into our inner world, guiding us towards self-discovery and personal growth. It is essential to cultivate emotional intelligence and develop a healthy relationship with our emotions, as they play a significant role in shaping our experiences and interactions with others. Let's embrace the complexity of our emotions and harness their power to lead more fulfilling lives."
   :article-id "14879a100d8411efa2de5a0701208c73"
   :_id "52b173600d8411efa2de5a0701208c73"})