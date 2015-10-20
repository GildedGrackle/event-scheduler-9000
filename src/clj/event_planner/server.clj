(ns event-planner.server
  (:require [clojure.java.io :as io]
            [event-planner.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [event-planner.api :refer :all]
            [compojure.core :refer [GET POST defroutes routes]]
            [compojure.route :refer [resources]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [net.cgrand.reload :refer [auto-reload]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [environ.core :refer [env]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(deftemplate page (io/resource "index.html") []
  [:body] (if is-dev? inject-devmode-html identity))

(defroutes site-routes
  (resources "/")
  (resources "/react" {:root "react"})
  (GET "/*" req (page)))

(def site-handler site-routes)

(defroutes api-routes
  (POST "/api/event" {body :body} {:status 200 :body body}))

(def api-handler
  (-> (wrap-defaults api-routes api-defaults)
      wrap-json-body
      wrap-json-response))

(defn wrap-is-dev
  [handler]
  (if is-dev?
    (reload/wrap-reload handler)
    handler))

(def http-handler
  (->
   (routes api-handler site-handler)
   wrap-is-dev))



(defn run-web-server [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (println (format "Starting web server on port %d." port))
    (run-jetty http-handler {:port port :join? false})))

(defn run-auto-reload [& [port]]
  (auto-reload *ns*)
  (start-figwheel))

(defn run [& [port]]
  (when is-dev?
    (run-auto-reload))
  (run-web-server port))

(defn -main [& [port]]
  (run port))
