(ns app.plumbing.routes
  (:require [app.utils :refer :all]
            [app.plumbing.midware :as midware]
            [reitit.ring :as ring]))

(defn api-routes
  "APIs specifically for backoffice needs"
  [db midware]
  ["/backsite-api"
   ["/v1"
    ["/health" {:get midware/api-check}]
    ;;(books-routes/api-routes db midware)
    ]])

(defn create-routes
  "Creates the whole routes for the system"
  [db]
  (ring/router
    [(api-routes db midware/backware)
     ;;(gen/generator-routes db)
     ]))
