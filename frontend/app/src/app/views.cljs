(ns app.views
  (:require
   [re-frame.core :as re-frame]
   [app.subs :as subs]
   [app.article.subs :as a-subs]
   [app.events :as events]
   [app.components :as comp]
   [app.article.views :as article-views]
   [app.proset.views :as proset-views]))


(defn main-page []
  [:div.container
   [:div.row.justify-content-center.mt-5
    [:div.col-md-8.text-center
     [:h1 "Welcome, whoever you are!"]
     [:div.d-flex.flex-column.align-items-center.mt-4
      [:button.btn.btn-primary.mb-3
       {:on-click #(re-frame/dispatch [::events/navigate :login])} "Login"]
      [:button.btn.btn-secondary
       {:on-click #(re-frame/dispatch [::events/navigate :register])} "Register"]]]]])



(defn login-page []
  (let [username (re-frame/subscribe [::subs/form-username])
        name     (re-frame/subscribe [::subs/form-name])
        loading  (re-frame/subscribe [::subs/loading])]
    [:div.container
     [:h2 "Login"]
     [:form
      [:div.form-group
       [:label {:for "inputUsername"} "Username"]
       [:input.form-control
        {:type "text"
         :id "inputUsername"
         :placeholder "Username"
         :value @username
         :on-change #(re-frame/dispatch [::events/update-username (-> % .-target .-value)])}]]
      [:div.form-group
       [:label {:for "inputName"} "Name"]
       [:input.form-control
        {:type "text"
         :id "inputName"
         :placeholder "Name"
         :value @name
         :on-change #(re-frame/dispatch [::events/update-name (-> % .-target .-value)])}]]
      [:button.btn.btn-primary {:type "button" :on-click #(re-frame/dispatch [::events/login-user @username @name])} "Login"]
      (when @loading [:div "Logging in..."])]]))


(defn register-page []
  (let [username (re-frame/subscribe [::subs/form-username])
        name     (re-frame/subscribe [::subs/form-name])
        loading  (re-frame/subscribe [::subs/loading])]
    [:div.container
     [:h2 "Register"]
     [:form
      [:div.form-group
       [:label {:for "inputUsername"} "Username"]
       [:input.form-control
        {:type "text"
         :id "inputUsername"
         :placeholder "Username"
         :value @username
         :on-change #(re-frame/dispatch [::events/update-username (-> % .-target .-value)])}]]
      [:div.form-group
       [:label {:for "inputName"} "Name"]
       [:input.form-control
        {:type "text"
         :id "inputName"
         :placeholder "Name"
         :value @name
         :on-change #(re-frame/dispatch [::events/update-name (-> % .-target .-value)])}]]
      [:button.btn.btn-primary {:type "button" :on-click #(re-frame/dispatch [::events/register-user @username @name])} "Register"]
      (when @loading [:div "Registering..."])]]))


(defn home-page []
  (let [name (re-frame/subscribe [::subs/form-name])]
    [:div.container
     [:h1 "Welcome, " @name "!"]
     [:h2 "Whachu gonna do today?"]
     [:div.btn-group {:role "group"}
      [:button.btn.btn-info.mt-4
       {:on-click #(re-frame/dispatch [::events/navigate :generate])} "Generate Article"]
      [:button.btn.btn-info.mt-4
       {:on-click #(re-frame/dispatch [::events/navigate :view])} "View Articles"]
      [:button.btn.btn-info.mt-4
       {:on-click #(re-frame/dispatch [::events/navigate :progress])} "My Progress"]]]))

(defn test-page []
  [:div
   [:h1 "Boo."]])

(defn root-component []
  (let [current-page (re-frame/subscribe [::subs/current-page])
        selected-article-id (re-frame/subscribe [::a-subs/selected-article-id])]
    (fn []
      [:div
       [:div.content
        {:style {:padding-bottom "60px"}}
        (case @current-page
        :main (main-page)
        :login (login-page)
        :register (register-page)
        :home (home-page)
        :generate (article-views/generate-page)
        :progress (proset-views/progress-page)
        :view (if (some? @selected-article-id)
                (article-views/article-page @selected-article-id)
                (article-views/view-all-article-page))
        :proset-page (proset-views/proset-page)
        :result-page (proset-views/result-page)
        (main-page))]
       [comp/footer]])))




