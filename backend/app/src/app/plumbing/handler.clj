(ns app.plumbing.handler
  (:require [com.stuartsierra.component :as component]
            [app.plumbing.routes :as routes]
            [reitit.ring :as ring]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.resource :refer [wrap-resource]]
            [jumblerg.middleware.cors :as jcors]))

(defn create-handler [db]
  (-> (routes/create-routes db)
      (ring/ring-handler)
      (jcors/wrap-cors #".*")
      wrap-params
      (wrap-json-body {:keywords? true :bigdecimals? true})
      wrap-cookies
      wrap-session
      wrap-json-response
      (wrap-resource "resources/public/")
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))

(defrecord Handler [db]
  component/Lifecycle
  (start [this]
    (assoc this :handler (create-handler db)))
  (stop [this]
    this))

(defn create-handler-component []
  (map->Handler {}))
