(ns app.logic.routes
  (:require [app.utils :refer :all]
            [app.plumbing.midware :as midware]
            [app.logic.ctrl :as ctrl]))


(defn article-api-check
  "Helper function for testing api"
  [db req]
  (println "Getting into api-check for article routes")
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    {:status  "ok"
             :message "Articles API is running fine"
             :data (str req)}})

(defn midware-testing
  [fun db req]
  (println "Getting into midware-testing")
  (merge {:status  200
          :headers {"Content-Type" "application/json"}}
         (fun db req)))

(defn midware-testing-get
  [fun db]
  (info "Getting into midware-testing-get")
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (fun db)})

(defn article-routes
  "Routes for APIs"
  [db]
  [""
   ["/register" {:post (partial midware-testing ctrl/reg-user db)}]
   ["/login" {:post (partial midware-testing ctrl/login-user db)}]
   ["/article"
    ["/articles" {:get (partial midware-testing-get ctrl/get-articles db)}] ;;masih gabisa
    ["/generate" {:post (partial midware-testing ctrl/gen-article db)}]
    ["/delete" {:post (partial midware-testing ctrl/delete-article db)}]]
   ["/generate-section-testing" {:post (partial article-api-check db)}]
   ["/article-testing" {:get midware/api-check}]])