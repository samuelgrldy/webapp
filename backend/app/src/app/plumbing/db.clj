(ns app.plumbing.db
  (:require [com.stuartsierra.component :as component]
            [app.utils :refer :all]
            [monger.core :as mg]))

(defrecord Db [config other-config openai]
  component/Lifecycle
  (start [this]
    ;; (pres config)
    (let [conn (mg/connect config)
          db-instance (mg/get-db conn (:db config))]
      ;; just in case need some clearance
      ;; (clear-db db-instance)
      (assoc this
        :db db-instance
        :conn conn
        :passcode (str (:passcode other-config)))))
  (stop [this]
    (when-let [conn (:conn this)]
      (mg/disconnect conn))
    (dissoc this :conn)))

(defn create-database-component
  [db-config other-config]
  (map->Db {:config db-config
            :other-config other-config}))
