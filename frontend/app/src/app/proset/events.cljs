(ns app.proset.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [app.db :as db]
            [app.utils :as u]))

(def base-url (db/default-db :base-url))

(re-frame/reg-event-fx
  ::start-proset
  (fn [{:keys [db]} [_ section-id]]
    {:http-xhrio {:method          :post
                  :uri             (str base-url "/practice/proset")
                  :params          {:section-id section-id}
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::fetch-proset-success]
                  :on-failure      [::fetch-proset-failure]}}))

(re-frame/reg-event-db
  ::fetch-proset-success
  (fn [db [_ response]]
    (let [proset (:data response)]
      (println "Proset: " proset)
      (println response)
      (-> db
         (assoc :proset proset)
         (assoc :current-page :proset-page)))))

(re-frame/reg-event-db
  ::fetch-proset-failure
  (fn [db [_ error]]
    (-> db
        (assoc :loading false)
        (assoc :error error))))

(re-frame/reg-event-db
  ::redo-practice
  (fn [db [_ result]]
    (let [section-id (:content-id result)]
      (re-frame/dispatch [::start-proset section-id])
      (assoc db :current-page :proset-page))))

;;=============helper for submit answers============

(defn get-selected-answer [question-index]
  (let [radio-buttons (js/document.getElementsByName (str "question-" question-index))]
    (loop [i 0]
      (if (< i (.-length radio-buttons))
        (let [radio-button (aget radio-buttons i)]
          (if (.-checked radio-button)
            (int (.-value radio-button))
            (recur (inc i))))
        nil))))

(defn extract-answers [proset]
  (for [[index question] (map-indexed vector (:soals proset))]
    {:text-soal (:text-soal question)
     :idx (get-selected-answer index)}))


;;===============submit answers================

;;fetch and start
(re-frame/reg-event-fx
  ::submit-answers
  (fn [{:keys [db]} [_ proset]]
    (let [user-id (u/get-storage ":user-id")
          proset-id (:_id proset)
          submitted-answers (extract-answers proset)]
      (println "user-id: "   user-id
               "proset-id: " proset-id
               "submitted-answers: " submitted-answers)
      {:http-xhrio {:method          :post
                    :uri             (str base-url "/practice/submit")
                    :params          {:user-id user-id
                                      :proset-id proset-id
                                      :submitted-answers submitted-answers}
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::submit-answers-success]
                    :on-failure      [::submit-answers-failure]}})))

(re-frame/reg-event-db
  ::submit-answers-success
  (fn [db [_ response]]
    (-> db
        (assoc :loading false)
        (assoc :result response)
        (assoc :current-page :result-page))))

(re-frame/reg-event-db
  ::submit-answers-failure
  (fn [db [_ error]]
    (-> db
        (assoc :loading false)
        (assoc :error error))))

(re-frame/reg-event-db
  ::submit-answers-success
  (fn [db [_ response]]
    (-> db
        (assoc :result response)
        (assoc :current-page :result-page))))

(re-frame/reg-event-db
  ::submit-answers-failure
  (fn [db [_ error]]
    (-> db
        (assoc :loading false)
        (assoc :error error))))

;;=================post-proset events=================

(re-frame/reg-event-fx
  ::fetch-progress
  (fn [{:keys [db]} [_]]
    (let [user-id (u/get-storage ":user-id")]
      {:http-xhrio {:method          :get
                    :uri             (str base-url "/practice/user-progress")
                    :params          {:user-id user-id}
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-progress-success]
                    :on-failure      [::fetch-progress-failure]}})))

(re-frame/reg-event-db
  ::fetch-progress-success
  (fn [db [_ response]]
    (let [completed-articles (:data response)]
     (assoc db :completed-articles completed-articles))))

(re-frame/reg-event-db
  ::fetch-progress-failure
  (fn [db [_ error]]
    (assoc db :error error)))


(re-frame/reg-event-fx
  ::view-article-progress
  (fn [{:keys [db]} [_ article-id]]
    (let [user-id (u/get-storage ":user-id")]
      {:http-xhrio {:method          :get
                    :uri             (str base-url "/article-progress")
                    :params          {:user-id user-id :article-id article-id}
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::view-article-progress-success article-id]
                    :on-failure      [::view-article-progress-failure]}})))

(re-frame/reg-event-db
  ::view-article-progress-success
  (fn [db [_ response article-id]]
    (-> db
        (assoc-in [:article-progress article-id] response)
        (assoc :current-page {:page :article-progress :article-id article-id}))))

(re-frame/reg-event-db
  ::view-article-progress-failure
  (fn [db [_ error]]
    (assoc db :error error)))
