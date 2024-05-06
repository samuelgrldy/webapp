(defproject app "0.1.0"
  :dependencies
  [
   ;; basic clojure setup and app management
   [org.clojure/clojure "1.11.2"]
   [com.stuartsierra/component "1.1.0"]
   ;; basic web plumbing & web needs
   [org.immutant/web "2.1.10" :exclusions [commons-codec]]
   [clj-http "3.12.3"]
   [ring/ring-defaults "0.4.0" :exclusions [commons-codec]]
   [jumblerg/ring-cors "3.0.0"]
   [ring/ring-json "0.5.1"]
   [ring "1.12.1" :exclusions [commons-codec]]
   [ring-cors "0.1.13"]
   [ring/ring-anti-forgery "1.3.0"]
   [metosin/reitit "0.6.0"]
   ;; [selmer "1.12.56" :exclusions [commons-codec]]
   [hiccup "2.0.0-RC3"]

   ;; database & data processing
   [com.novemberain/monger "3.6.0"]
   ;; [cheshire "5.13.0"]

   ;; utilities
   [com.taoensso/timbre "6.5.0"]
   [environ "1.2.0"]
   [clojure.java-time "1.4.2"]
   ;; [me.raynes/fs "1.4.6"]
   [danlentz/clj-uuid "0.1.9"]

   ;; file/formatting and development utilities
   [org.clojure/tools.namespace "1.5.0"]
   [pjstadig/humane-test-output "0.11.0"]
   [ring/ring-mock "0.4.0"]
   ]

  :injections [(require 'pjstadig.humane-test-output)
               (pjstadig.humane-test-output/activate!)]

  :uberjar-name "uberjar-app.jar"
  :jar-name "appstore.jar"

  :min-lein-version "2.5.3"

  :source-paths ["src" "dev"]

  :resource-paths ["resources"]
  :main ^:skip-aot app.core
  :repl-options {:init-ns user}

  :plugins [[lein-environ "1.2.0"]]
  :pom-plugins [[com.google.cloud.tools/jib-maven-plugin "2.1.0"
                 (:configuration
                   [:from [:image "clojure:temurin-8-lein-jammy"]]
                   [:container
                    [:mainClass "app.core"]
                    [:creationTime "USE_CURRENT_TIMESTAMP"]])]]

  :profiles {:dev           [:project/dev :profiles/dev]
             :test          [:project/test :profiles/test]
             ;; only edit :profiles/* in profiles.clj
             :profiles/dev  {}
             :profiles/test {}
             :project/dev   {:source-paths ["clj" "dev"]}
             :project/test  {:source-paths ["clj" "dev"]}
             :uberjar       {:aot          :all
                             :source-paths ["clj"]
                             :main         app.core}})