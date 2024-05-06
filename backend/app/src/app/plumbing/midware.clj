(ns app.plumbing.midware
  (:require [app.utils :refer :all]
            [hiccup2.core :as h]))

(defn api-check
  [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    {:status  "ok"
             :message "Backsite API is working"}})



(defn backware
  "Create a base-auth function to be universally used across the backsite"
  [fun db request]
  (info "=======================================================================")
  (info "URI : " (:uri request))
  (if (= (get-in request [:headers "Authorization"]) (str "Bearer " (:passcode db)))
    (merge {:status  200
            :headers {"Content-type" "application/json"}}
           (fun db request))
    {:status  401
     :headers {"Content-Type" "application/json"}
     :body    {:status  "error"
               :message "Failed to authenticate, please include the passcode in the headers.Authorization"}}))

