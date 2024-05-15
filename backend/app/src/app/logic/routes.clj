(ns app.logic.routes
  (:require [app.utils :refer :all]
            [app.plumbing.midware :as midware]
            [app.logic.ctrl :as ctrl]
            [app.logic.proset :as proset]))


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


(defn article-routes
  "Routes for APIs"
  [db]
  [""
   ["/register" {:post (partial midware-testing ctrl/reg-user db)}]
   ["/login" {:post (partial midware-testing ctrl/login-user db)}] ;;to do: set frontend buat local and return
   ["/article"
    ["/articles" {:get (partial midware-testing ctrl/get-articles db)}]
    ["/sections" {:post (partial midware-testing ctrl/get-sections-by-article db)}]
    ["/generate" {:post (partial midware-testing ctrl/gen-article db)}]
    ["/delete" {:post (partial midware-testing ctrl/delete-article db)}]]
   ["/practice"
    ["/proset" {:post (partial midware-testing ctrl/get-prosets-by-section db)}]
    ["/submit" {:post (partial midware-testing ctrl/submit-answers db)}]
    ["/user-progress" {:get (partial midware-testing ctrl/get-user-progress db)}]
    ["/article-progress" {:get (partial midware-testing ctrl/get-article-progress db)}]]
   ["/article-testing" {:get midware/api-check}]])


