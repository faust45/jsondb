(ns jsondb.core
  (:require [jsondb.session :as session])
  (:use jsondb.auth)
  (:require [jsondb.imgio :as imgio])
  (:require [jsondb.models  :as models])
  (require jsondb.users)
  (import jsondb.users.User)
  ;(:require [jsondb.actions :as actions])
  (:require [clojure.string :as s])
  (:require [cheshire.core :refer :all])
  (:require [jsondb.utils :as utils])
  (:require [ring.util.response :as resp])
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource ring.middleware.session ring.middleware.file-info ring.middleware.file ring.middleware.reload  ring.middleware.multipart-params)
  (:use compojure.core)
  (:require [jsondb.db :as db])
  (:gen-class))


(defn upload
  [file label]
  )

(defn auth
  [email password]
  (let [user (models/auth User email password)]
    (if user
      (merge {:session email} (utils/json-resp "success"))
      (utils/json-resp "fail"))))

;(defsigned admin
;  [req]
;  (resp/file-response "index.html" {:root "www"}))

(defn places
  [s]
  (println "DEBUG SESSION:" s)
  {:session "Love"})

(defn place
  [id q]
  ;(->> id models/places utils/json-resp))
  )

(defn update-place
  [doc]
  (utils/json-resp 2))

(def my-routes
  (routes (GET  "/places/:id" [id :as {q :query-string session :session}] (place id q))
          (POST "/places" [:as {doc :doc}] (update-place doc))
          (POST "/upload/:image-type" [image-type :as {file :tempfile}] (upload file (keyword image-type)))
          (GET  "/login"  [] (resp/file-response "login.html" {:root "www"}))
          (POST "/login"  [email password] (auth email password))))

(def app
  (-> my-routes
      wrap-reload
      wrap-multipart-params
      (wrap-session {:store (session/store "db/sessions.db")})
      utils/wrap-parse-json
      utils/wrap-upload-file
      (wrap-file "www/")))

(defn -main [& args]
  (run-jetty app {:port 8080}))


