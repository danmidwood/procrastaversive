(defproject procrastiversives "0.1.0-SNAPSHOT"
  :description "Punishing procrastination"
  :url "http://procrastiversives"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [enlive "1.1.1"]
                 [ring "1.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [compojure "1.1.5"]
                 [com.cemerick/friend "0.1.5"]
                 [environ "0.4.0"]
                 [cornet "0.1.0"]
                 [com.taoensso/timbre "2.6.3"]
                 [korma "0.3.0-RC6"]
                 [postgresql/postgresql "9.1-901.jdbc4"]]
  :profiles {:dev {:dependencies [[ring-server "0.3.0"]]
                   :source-paths ["src-dev"]}
             :production {:env {:production true}}}
  :hooks [environ.leiningen.hooks]
  :plugins [[lein-ring "0.8.3"]
            [environ/environ.lein "0.2.1"]
            [s3-wagon-private "1.1.2"]
            [org.clojars.wokier/lein-bower "0.3.0"]]
  :deploy-repositories [["snapshots" {:url "s3p://jvm-repository/snapshots/"
                                      :creds :gpg}
                         "releases" {:url "s3p://jvm-repositoy/releases/"
                                     :creds :gpg}]]
  :prep-tasks ["javac" "compile" "bower"]
  :ring {:handler procrastiversives.web/app}
  ;; :main procrastiversives.main
  ;; :aot [procrastiversives.main]
  :global-vars {*warn-on-reflection* true}
  :min-lein-version "2.0.0")