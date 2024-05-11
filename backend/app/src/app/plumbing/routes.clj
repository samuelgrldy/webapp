(ns app.plumbing.routes
  (:require [app.utils :refer :all]
            [app.plumbing.midware :as midware]
            [reitit.ring :as ring]
            [app.logic.ctrl :as ctrl]))

(defn api-routes
  "APIs specifically for backoffice needs"
  [db midware]
  ["/backsite-api"
   ["/v1"
    ["/health" {:get midware/api-check}]
    ;;(books-routes/api-routes db midware)
    ]])

(defn article-routes
  "API routes specifically for article needs"
  [db midware]
  ["/login"             {:post (partial midware/backware-testing ctrl/login-user db)}]
  ["/register"          {:post (partial midware/backware-testing ctrl/reg-user db)}]
  ["/article"
   ["/generate-article" {:post (partial midware/backware-testing ctrl/gen-article db)}]
   ["delete-article"    {:delete (partial midware/backware-testing ctrl/delete-article db)}]])

(defn create-routes
  "Creates the whole routes for the system"
  [db]
  (ring/router
    [(api-routes db midware/backware)
     (article-routes db midware/backware)]))




