(ns jsondb.core
  (:require [jsondb.session :as session])
  (:use jsondb.auth)
  (:require [jsondb.imgio :as imgio])
  (:require [jsondb.models :as models])
  (require jsondb.users)
  (import jsondb.users.User)
  (require jsondb.places)
  (import jsondb.places.Place)
  ;(:require [jsondb.actions :as actions])
  (:require [clojure.string :as s])
  (:require [cheshire.core :refer :all])
  (:require [jsondb.utils :as utils])
  (:require [ring.util.response :as resp])
  (:use ring.adapter.jetty)
  (:use ring.middleware.session ring.middleware.file-info ring.middleware.file ring.middleware.reload  ring.middleware.multipart-params)
  (:use compojure.core)
  (:require [jsondb.db :as db])
  (:gen-class))


(defn upload
  [file label]
  )

(defn auth
  [email password]
  (if-let [user (models/auth User email password)]
      (-> (resp/redirect "/admin") (assoc :session (:id user)))
      (utils/json-resp "fail")))

(defsigned admin
  []
  (resp/file-response "index.html" {:root "www"}))

(defsigned admin-place
  [id]
  (-> (models/get Place id) utils/json-resp))

(defsigned admin-profile
  []
  (-> current-user utils/json-resp))

;(defn place
;  [id q]
;  (->> id (models/get Place) utils/json-resp))
;
;(defn update-place
;  [doc]
;  (utils/json-resp 2))
;
(def my-routes
  (routes (GET  "/admin/places/:id" [id :as req] (admin-place req id))
          (GET  "/admin/profile" [] admin-profile)
          (POST "/places" [:as {doc :doc}] (update-place doc))
          (POST "/upload/:image-type" [image-type :as {file :tempfile}] (upload file (keyword image-type)))
          (GET  "/login"  [] (resp/file-response "login.html" {:root "www"}))
          (GET  "/admin"  [] admin)
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


