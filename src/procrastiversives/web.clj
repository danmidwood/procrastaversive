(ns procrastiversives.web
  (:require [compojure.core :refer [defroutes routes GET]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as rresponse]
            [ring.middleware.reload :as rreload]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [cornet.core :as cornet]
            [cornet.route :as croute]
            [taoensso.timbre :as log]))

(defn build-redirect-page [desired actual]
  (rresponse/redirect actual))

(def scary-words '("spider"
                   "tarantula"
                   "Trypophobia"
                   "snake venomous"))

(defn pick-url []
  (format "https://www.google.com/search?q=%s&tbm=isch" (rand-nth scary-words)))

(defroutes app-routes
  (GET "/*"
       {:keys [headers] :as request}
       (log/debug headers)
       (let [desired (headers "host")]
         (build-redirect-page desired (pick-url))
))

  (GET "/*"
       {:keys [headers] :as request}
       "default scare page"))

(defroutes cornet
  (croute/wrap-url-response
   (some-fn
    (cornet.processors.lesscss/wrap-lesscss-processor (cornet.loader/resource-loader "assets/less")
                                                      :mode :dev)
    (cornet/static-assets-loader "assets"
                                 :from-filesystem false
                                 :mode :dev)))

  (route/not-found "Not Found"))

(defn log-exceptions [f]
  (fn [request]
    (try (f request)
      (catch Exception e
        (log/error e)
        (throw e)))))

(def app
  (-> (routes app-routes cornet)
      (handler/site)
      (log-exceptions)
      (rreload/wrap-reload '())))
