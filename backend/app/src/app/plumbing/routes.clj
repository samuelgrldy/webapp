(ns app.plumbing.routes
  (:require [app.utils :refer :all]
            [app.plumbing.midware :as midware]
            [reitit.ring :as ring]
            [app.logic.routes :as logic]))


(defn midware-testing
  [fun db req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (fun db req)})

(defn midware-testing-get
  [fun db]
  (info "Getting into midware-testing-get")
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (fun db)})

(defn api-check
  [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    {:status  "ok"
             :message "Backsite API is working"
             :request (str req)}})

(defn article-api-check
  "Helper function for testing api"
  [req]
  (println "Getting into api-check for article routes")
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    {:status  "ok"
             :message "Articles API is running fine"}})

(defn api-routes
  "APIs specifically for backoffice needs"
  [db midware]
  ["/backsite-api"
   ["/v1"
    ["/health" {:get api-check}]]])

(defn create-routes
  "Creates the whole routes for the system"
  [db]
  (ring/router
    [(logic/article-routes db)
     (api-routes db midware/backware)]))




