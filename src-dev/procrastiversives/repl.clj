(ns procrastiversives.repl
  (:require [ring.server.standalone :as ring-server]
            [taoensso.timbre :as log]
            [procrastiversives.web :as web]))


(defonce ^:private server (atom nil))

(defn start [& args]
  (log/set-config! [:appenders :spit :enabled?] true)
  (log/set-config! [:shared-appender-config :spit-filename] "/Users/dan/repos/procrastiversives/target/app.log")
  (reset! server (ring-server/serve web/app {:open-browser? false})))

(defn stop []
  (when (not (nil? @server))
    (.stop @server)
    (reset! server nil)))

(def restart (comp start stop))
