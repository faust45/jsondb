(ns jsondb.core
  (:require [jsondb.imgio :as imgio])
  (:require [jsondb.models  :as models])
  (:require [jsondb.actions :as actions])
  (:require [clojure.string :as s])
  (:require [cheshire.core :refer :all])
  (:require [jsondb.utils :as utils])
  (:require [ring.util.response :as resp])
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource ring.middleware.file-info ring.middleware.file ring.middleware.reload)
  (:use compojure.core)
  (:require [jsondb.db :as db])
  (:gen-class))


(defn upload
  [file label]
  ((comp utils/json-resp (partial apply merge) (partial map #(actions/process-images % file))) (label models/images)))

(defn places
  [req])

(defn place
  [id q]
  (->> id db/g utils/json-resp))

(defn update-place
  [doc]
  (utils/json-resp (db/save doc)))

(def my-routes
  (routes (GET  "/places/:id" [id :as {q :query-string}] (place id q))
          (POST "/places" [:as {doc :doc}] (update-place doc))
          (POST "/upload/:image-type" [image-type :as {file :tempfile}] (upload file (keyword image-type)))
          (GET  "/places" [] places)))

(def app
  (-> my-routes
      wrap-reload
      utils/wrap-parse-json
      utils/wrap-upload-file
      (wrap-file "www/")))

(defn -main [& args]
  (run-jetty app {:port 8080}))


