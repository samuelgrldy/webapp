(ns app.proset.views
  (:require [re-frame.core :as re-frame]
            [app.proset.events :as proset-events]
            [app.proset.subs :as proset-subs]))

(defn proset-question [question index]
  [:div.question
   [:h3 (str "Question " (inc index) ": " (:text-soal question))]
   (for [{:keys [option-text idx]} (:choices question)]
     ^{:key idx}
     [:div.form-check
      [:input.form-check-input
       {:type "radio"
        :name (str "question-" index)
        :value idx}]
      [:label.form-check-label option-text]])])

(defn proset-page []
  (let [proset (re-frame/subscribe [::proset-subs/proset])]
    [:div.container
     [:h2 (:title @proset)]
     (for [[index question] (map-indexed vector (:soals @proset))]
       ^{:key index}
       [proset-question question index])
     [:button.btn.btn-primary
      {:on-click #(re-frame/dispatch [::proset-events/submit-answers @proset])}
      "Submit"]]))

(defn result-page []
  (let [result (re-frame/subscribe [::proset-subs/result])]
    [:div.container
     [:h2 "Results"]
     (for [{:keys [text-soal user-answer correct?]} (get-in @result [:data :detailed-result])]
       ^{:key text-soal}
       [:div.result
        [:h4 text-soal]
        [:p (str "Your answer: " user-answer)]
        [:p (str "Correct: " (if correct? "Yes" "No"))]])
     [:button.btn.btn-primary.mt-4
      {:on-click #(re-frame/dispatch [::proset-events/redo-practice (:proset-id @result)])}
      "Redo Practice"]]))


(defn progress-page []
  (let [completed-articles (re-frame/subscribe [::proset-subs/completed-articles])]
    (re-frame/dispatch [::proset-events/fetch-progress]) ; Dispatch the event to fetch progress
    [:div.container
     [:h2 "My Progress"]
     (for [article @completed-articles]
       ^{:key (:article-id article)}
       [:div.article
        [:h3 [:a {:href "#" :on-click #(re-frame/dispatch [::proset-events/view-article-progress (:_id article)])}
              (:title article)]]
        [:ul
         (for [section (:sections article)]
           ^{:key (:id section)}
           [:li
            [:span (str (:title section) " - Correct: " (:correct section))]
            (if (:completed section)
              [:button.btn.btn-secondary
               {:on-click #(re-frame/dispatch [::proset-events/redo-practice (:_id section)])}
               "Redo Practice"]
              [:button.btn.btn-primary
               {:on-click #(re-frame/dispatch [::proset-events/start-proset (:_id section)])}
               "Practice"])])]])]))

