(ns procrastiversives.web
  (:require [compojure.core :refer [defroutes routes GET POST]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as rresponse]
            [ring.middleware.reload :as rreload]
            [ring.middleware.cookies :as cookie]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [cornet.core :as cornet]
            [cornet.route :as croute]
            [taoensso.timbre :as log]
            [clj-time.core :as time]))

(defn build-redirect-page [desired actual]
  (rresponse/redirect actual))

(def scary-words {:mild
                  '("spider"
                   "tarantula"
                   "Trypophobia")
                  :scary '()
                  :extreme '()
                  :goatse "goatse"})

(defn take-while-inclusive [elem col]
  (concat (take-while (partial (comp not =) elem) col)
          (filter (partial = elem) col)))

(def levels '(:mild
              :scary
              :extreme
              :goatse))

(defn categories-in [level]
  (take-while-inclusive level levels))


(defn pick-url [levels]
  (format "https://www.google.com/search?q=%s&tbm=isch&tbs=itp:photo"
          (rand-nth (flatten (map scary-words levels)))))

(html/deftemplate home-page "procrastiversives/index.html" [])


(defroutes app-routes
  (GET "/*" {:keys [query-params
                   cookies]}
       (when-let [desired (query-params "d")]
         (let [level (or (:value (cookies "level"))
                         "mild")]
           (log/debug (cookies "level"))
           (build-redirect-page (log/spy desired)
                                (str (pick-url (categories-in (keyword level))))))))
  (GET "/*"
       {:keys [headers] :as request}
       (log/debug request)
       (let [host (headers "host")
             path (request :uri)]
         (when (and ((comp not =) host "localhost")
                    ((comp not =) host "procrastiversives.co"))
           (rresponse/redirect (str "http://localhost?d=" host path)))))

  (GET "/" request
       (home-page))
  (GET "/level" {:keys [cookies] :as request}
       (log/debug cookies)
       (let [level (-> (cookies "level")
                       :value)]
         (rresponse/response level)))
  (GET "/level/:level" [level]
        (assoc (rresponse/redirect "/")
          :cookies
          {:level {:value level
                   :path "/"
                   :expires (time/plus (time/now) (time/years 10))}})))



(defroutes cornet
  (croute/wrap-url-response
   (some-fn
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
