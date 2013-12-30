(ns procrastiversives.web
  (:require [compojure.core :refer [defroutes routes GET POST]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as rresponse]
            [ring.middleware.reload :as rreload]
            [ring.middleware.cookies :as cookie]
            [net.cgrand.enlive-html :as html]
            [cornet.core :as cornet]
            [cornet.route :as croute]
            [taoensso.timbre :as log]
            [clj-time.core :as time]
            [procrastiversives.punishment :as p]))

(defn build-redirect-page [actual]
  (rresponse/redirect actual))

(html/deftemplate home-page "procrastiversives/index.html" [level]
  [[:input (html/attr-has :value level)]] (html/set-attr "checked" "checked"))

(defroutes app-routes
  (GET "/*" {:keys [query-params
                   cookies]}
       (when-let [desired (query-params "d")]
         (let [level (or (:value (cookies "level"))
                         "mild")]
           (log/debug (cookies "level"))
           (build-redirect-page (str (p/pick-punishment (keyword level)))))))
  (GET "/*"
       {:keys [headers] :as request}
       (log/debug request)
       (let [host (headers "host")
             path (request :uri)]
         (when (and ((comp not =) host "localhost")
                    ((comp not =) host "procrastiversives.co"))
           (rresponse/redirect (str "http://localhost?d=" host path)))))
  (GET "/"  {:keys [cookies]}
         (let [level (or (:value (cookies "level"))
                         "mild")]
           (home-page level)))
  (POST "/level" {:keys [params]}
        (assoc (rresponse/redirect "/")
          :cookies
          {:level {:value (params :level)
                   :path "/"
                   :expires (time/plus (time/now) (time/years 10))}})))



(defroutes cornet
  (croute/wrap-url-response
   (some-fn
    (cornet/static-assets-loader "assets"
                                 :from-filesystem false
                                 :mode :prod)))

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
