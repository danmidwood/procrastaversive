(ns procrastiversives.main
  (:gen-class)
  (:require [ring.adapter.jetty :as ring]
            [procrastiversives.web :as web]))


(defn start [port]
  (ring/run-jetty (var web/app)
                  {:port (or port 3000) :join? false}))

(defn -main
  ([] (-main 3000))
  ([port]
     (let [sys-port (System/getenv "PORT")]
       (if (nil? sys-port)
          (start (cond
                 (string? port) (Integer/parseInt port)
                  :else port))
           (start (Integer/parseInt sys-port))))))
