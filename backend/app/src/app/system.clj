(ns app.system
  (:require
    [com.stuartsierra.component :as component]
    [app.plumbing.db :as db]
    [app.utils :refer :all]
    [app.plumbing.server :as immut]
    [app.plumbing.handler :as http]
    [app.plumbing.openai :as openai]))

(defn create-system
  "It creates a system, and return the system, but not started yet"
  []
  (let [{:keys [server-path
                server-port
                server-host
                passcode
                openai-url
                openai-key
                db-mongo-uri
                db-mongo-port
                db-mongo-name
                db-mongo-quiet
                db-mongo-debug]} (read-config-true-flat)
        ;; {:keys [server db]} (read-config-flat)
        server {:port server-port :path server-path :host server-host}
        db-mongo {:uri   db-mongo-uri
                  :port  db-mongo-port
                  :db    db-mongo-name
                  :quiet db-mongo-quiet
                  :debug db-mongo-debug}
        openai {:openai-url (str openai-url) :openai-key (str openai-key)}
        other-config {:passcode passcode}]
    (component/system-map
      :openai (openai/create-openai-component openai)
      :db (-> (db/create-database-component db-mongo other-config)
              (component/using [:openai]))
      :handler (-> (http/create-handler-component)
                   (component/using [:db]))
      :server (-> (immut/create-server-component server)
                  (component/using [:handler])))))
