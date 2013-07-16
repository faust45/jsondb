(ns jsondb.core
  (:require [ring.util.response :as resp])
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource ring.middleware.file-info ring.middleware.file ring.middleware.reload)
  (:use compojure.core))



(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello "})

(def my-routes 
  (routes (GET "/search" [] handler)))

(def app
  (-> my-routes
      wrap-reload
      (wrap-file "public")))

(defn main []
  (run-jetty app {:port 3000}))
