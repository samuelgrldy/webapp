(ns app.article.components
  (:require [re-frame.core :as re-frame]
            [app.article.subs :as subs]
            [app.article.events :as events])
  )

(defn form-valid? [form-state]
  (every? (fn [[_ value]]
            (not (empty? value)))
          form-state))

(defn generate-article-form []
  (let [form-state (re-frame/subscribe [::subs/form-state])]
    [:div.container
     [:h1 "Generate New Article"]
     [:form
      {:on-submit #(do
                     (.preventDefault %))}
      [:div.form-group
       [:label "Title"]
       [:input.form-control
        {:type      "text"
         :value     (:title @form-state)
         :on-change #(re-frame/dispatch [::events/update-form-field :title (-> % .-target .-value)])}]]
      [:div.form-group
       [:label "Prompt"]
       [:input.form-control
        {:type      "text"
         :value     (:prompt @form-state)
         :on-change #(re-frame/dispatch [::events/update-form-field :prompt (-> % .-target .-value)])}]]
      [:div.form-group
       [:label "Number of Sections"]
       [:input.form-control
        {:type      "number"
         :value     (:n-sections @form-state)
         :on-change #(re-frame/dispatch [::events/update-form-field :n-sections (-> % .-target .-value)])}]]
      [:button.btn.btn-primary
       {:disabled (not (form-valid? @form-state))
        :on-click #(do
                      (re-frame/dispatch [::events/generate-article @form-state])
                      (re-frame/dispatch [::events/reset-form]))}
       "Generate Article"]]]))


