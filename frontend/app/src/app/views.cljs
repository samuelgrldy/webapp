(ns app.views
  (:require
   [re-frame.core :as re-frame]
   [app.subs :as subs]
   [app.events :as events]
   ))


(defn main-page []
  [:div
   [:h1 "Welcome, dipshit!"]
   [:button {:on-click #(re-frame/dispatch [:navigate :login])} "Login"]
   [:button {:on-click #(re-frame/dispatch [:navigate :register])} "Register"]])

(defn login-page []
  [:div
   [:h2 "Login Page"]
   [:input {:placeholder "Username" :type "text"}]
   [:input {:placeholder "Name" :type "text"}]
   [:button {:on-click #(js/alert "Login attempted")} "Login"]])

(defn register-page []
  (let [username (re-frame/subscribe [:form-username])
        name (re-frame/subscribe [:form-name])]
    [:div
     [:h2 "Register Page"]
     [:input {:placeholder "Username"
              :type "text"
              :value @username
              :on-change #(re-frame/dispatch [:update-username (-> % .-target .-value)])}]
     [:input {:placeholder "Name"
              :type "text"
              :value @name
              :on-change #(re-frame/dispatch [:update-name (-> % .-target .-value)])}]
     [:button {:on-click #(re-frame/dispatch [:events/register-user
                                              @username
                                              @name])} "Register"]]))


(defn home-page []
  [:div
   [:h1 "Welcome to the Home Page"]
   [:p "This page is a placeholder after successful login."]])

(defn root-component []
  (let [current-page (re-frame/subscribe [:current-page])]
    (fn []
      (case @current-page
        :main (main-page)
        :login (login-page)
        :register (register-page)
        (main-page)))))



