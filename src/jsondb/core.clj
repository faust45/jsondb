(ns jsondb.core
  (:require [jsondb.session :as session])
  (:use jsondb.auth)
  (:require [jsondb.imgio :as imgio])
  (:require [jsondb.models :as models])
  (require jsondb.users)
  (import jsondb.users.User)
  (require jsondb.places)
  (import jsondb.places.Place)
  (:require [jsondb.actions :as actions])
  (:require [clojure.string :as s])
  (:require [cheshire.core :refer :all])
  (:require [jsondb.utils :as utils])
  (:require [ring.util.response :as resp])
  (:use ring.adapter.jetty)
  (:use ring.middleware.session ring.middleware.file-info ring.middleware.file ring.middleware.reload  ring.middleware.multipart-params)
  (:use compojure.core)
  (:require [jsondb.db :as db])
  (:gen-class))


(model/all user places)
(save {})

(defsigned upload
  [id file label]
  (->> (models/images label) (map #(actions/process-images % file id)) (apply merge) utils/json-resp))

(defn auth
  [{:keys [email password]} req]
  (println (:request-method req) "asdadaDDDD")
  (println "DATA: " email password)
  (if-let [user (models/auth User email password)]
      (-> (resp/redirect "/admin") (assoc :session (:id user)))
      (-> (resp/response "") (resp/status 401))))

(defn signout
  [req]
  (-> (resp/redirect-after-post "/login") (assoc :session nil)))

(defsigned admin
  []
  (resp/file-response "index.html" {:root "www"}))

(defsigned admin-place
  [id]
  (-> (models/get Place id) utils/json-resp))

(defsigned admin-profile
  []
  (-> current-user utils/json-resp))

(defsigned update-place
  [id doc]
  (let [old (models/get Place id) 
        d (clojure.walk/keywordize-keys doc)]
    (-> (jsondb.places/new d) (models/update old) utils/json-resp)))

(put places (user {}))
((user places))
place 
(if (can-edit? user place)
  ()
  ())

(collection 'Place (user))

Place user_id


(def my-routes
  (routes (GET  "/admin/places/:id" [id :as req] (admin-place req id))
          (GET  "/admin/profile" [] admin-profile)
          (POST "/admin/places/:id" [id doc :as req] (update-place req id doc))
          (POST "/admin/upload/:id/:image-type" [id image-type :as req] (upload req id (:tempfile req) (keyword image-type)))
          (GET  "/login"  [] (resp/file-response "login.html" {:root "www"}))
          (POST "/login"  [doc :as req] (auth doc req))
          (DELETE "/admin/signout" [] signout)
          (GET  "/admin"  [] admin)))

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


(defn u
  []
  (models/all User))
