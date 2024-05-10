(ns app.utils
  (:require
    [ajax.edn :as edn]
    [cljs.pprint :as pp]))

(defn jawab
  [no]
  ((mapv str "ABCDEFGHIJK") no))

(defn $
  "Get element by id"
  [id]
  (.getElementById js/document id))

(defn $>
  "Get element by class, result will be in js array"
  [class]
  (.getElementsByClassName js/document class))

(defn elm-value
  [e]
  (-> e .-target .-value))

(defn info
  "Console log"
  [body]
  (pp/pprint body))

(defn vinfo
  "Like info but also produce value of the expression"
  [exprs]
  (info exprs)
  exprs)

(defn set-storage
  "Set something from local storage"
  [k v]
  (.setItem js/localStorage k v))

(defn get-storage
  "Get something from local storage"
  [k]
  (.getItem js/localStorage k))

(defn remove-storage
  "Remove a storage by key"
  [k]
  (.removeItem js/localStorage k))

(defn ajax-edn
  [method]
  {:format          (edn/edn-request-format)
   :response-format (edn/edn-response-format)
   :method          method})

(comment
  (defn re-render-mathjax []
    (js/MathJax.Hub.Queue (array "Typeset" js/MathJax.Hub))))