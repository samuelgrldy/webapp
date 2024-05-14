(ns app.views
  (:require
   [re-frame.core :as re-frame]
   [app.subs :as subs]
   [app.article.subs :as a-subs]
   [app.events :as events]
   [app.article.views :as article-views]))


(defn main-page []
  [:div
   [:h1 "Welcome, whoever you are!"]
   [:button {:on-click #(re-frame/dispatch [::events/navigate :login])} "Login"]
   [:button {:on-click #(re-frame/dispatch [::events/navigate :register])} "Register"]])

(defn login-page []
  (let [username (re-frame/subscribe [::subs/form-username])
        name     (re-frame/subscribe [::subs/form-name])
        loading  (re-frame/subscribe [::subs/loading])]
    [:div
     [:h2 "Login Page"]
     [:input {:placeholder "Username"
              :type "text"
              :value @username
              :on-change #(re-frame/dispatch [::events/update-username (-> % .-target .-value)])}]
     [:input {:placeholder "Name"
              :type "text"
              :value @name
              :on-change #(re-frame/dispatch [::events/update-name (-> % .-target .-value)])}]
     [:button {:on-click #(re-frame/dispatch [::events/login-user @username @name])} "Login"]
     (when @loading "Logging in...")]))

(defn register-page []
  (let [username (re-frame/subscribe [::subs/form-username])
        name (re-frame/subscribe [::subs/form-name])
        loading (re-frame/subscribe [::subs/loading])]
    [:div
     [:h2 "Register Page"]
     [:input {:placeholder "Username"
              :type "text"
              :value @username
              :on-change #(re-frame/dispatch [::events/update-username (-> % .-target .-value)])}]
     [:input {:placeholder "Name"
              :type "text"
              :value @name
              :on-change #(re-frame/dispatch [::events/update-name (-> % .-target .-value)])}]
     [:button {:on-click #(re-frame/dispatch [::events/register-user
                                              @username @name])} "Register"]
     (when @loading "Registering...")]))


(defn home-page []
  (let [name (re-frame/subscribe [::subs/form-name])]
    [:div.container
     [:h1 "Welcome, " @name "!"]
     [:h2 "Whachu gonna do today?"]
     [:div.btn-group {:role "group"}
      [:button.btn.btn-primary {:on-click #(re-frame/dispatch [::events/navigate :generate])} "Generate Article"]
      [:button.btn.btn-primary {:on-click #(re-frame/dispatch [::events/navigate :view])} "View Articles"]]]))

(defn root-component []
  (let [current-page (re-frame/subscribe [::subs/current-page])
        selected-article-id (re-frame/subscribe [::a-subs/selected-article-id])]
    (fn []
      (case @current-page
        :main (main-page)
        :login (login-page)
        :register (register-page)
        :home (home-page)
        :generate (article-views/generate-page)
        :view (if (not (nil? @selected-article-id))
                (article-views/article-page @selected-article-id)
                (article-views/view-article-page))
        (main-page)))))




