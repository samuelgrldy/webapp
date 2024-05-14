(ns app.components
  (:require [re-frame.core :as re-frame]
            [app.events :as events]))

(declare back-button)

(defn footer []
  [:footer
   {:style {:position "fixed"
            :bottom "0"
            :width "100%"
            :background-color "#f8f9fa"
            :padding "10px 0"
            :text-align "center"}}
   [:div.container
    [back-button]]])

(defn back-button []
  [:button.btn.btn-secondary
   {:on-click #(re-frame/dispatch [::events/navigate-back])}
   "Back"])