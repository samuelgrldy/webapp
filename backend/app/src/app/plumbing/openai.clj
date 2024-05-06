(ns app.plumbing.openai
  (:require
    [com.stuartsierra.component :as component]
    [app.utils :refer :all]
    [clj-http.client :as http]
    [cheshire.core :as json]))

(declare base-request generate models)

(defrecord Openai [openai-url openai-key]
  component/Lifecycle
  (start [this]
    (info "Starting the openai component")
    (info "Openai started")
    (info "Openai URL: " openai-url)
    (assoc this
      :openai (fn [{:keys [model messages]}] 
                (info "Generating from openai")
                (let [send-to-openai {:model      model
                                      :openai-url openai-url
                                      :messages   messages
                                      :openai-key openai-key}]
                  (pres send-to-openai)
                  (generate send-to-openai)))))
  (stop [this]
    (info "Openai stopped")
    this))

(defn create-openai-component
  "Openai component constructor"
  [{:keys [openai-url openai-key]}]
  (map->Openai {:openai-url openai-url
                :openai-key openai-key}))

(defn base-request
  [api-token]
  {:accept       :json
   :content-type :json
   :headers      {"Authorization" (str "Bearer " api-token)}})

(def models
  {:gpt-3 "gpt-3.5-turbo-0125"
   "gpt-3" "gpt-3.5-turbo-0125"
   :gpt-4 "gpt-4-turbo"
   "gpt-4" "gpt-4-turbo"})

(defn generate
  "Just call this one to generate the response from openAI"
  [{:keys [model openai-url messages openai-key]}]
  (let [resp (->> {:model           (models model)
                   :messages        messages
                   :response_format {:type "json_object"}
                   :max_tokens      4000
                   :temperature     0.21
                   :n               1}
                  (json/generate-string)
                  (assoc (base-request openai-key) :body)
                  (http/post openai-url))
        resp1 (-> (:body resp)
                  (json/parse-string true))]
    (-> (select-keys resp1 [:usage])
        (assoc :result (-> (get-in resp1 [:choices 0 :message :content])
                           (json/parse-string true))))))

