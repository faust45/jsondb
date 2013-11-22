(ns jsondb.actions
  (:use [carica.core])
  (:require [clojure.java.io :as io])
  (:require [jsondb.utils :as utils])
  (:require [aws.sdk.s3 :as s3])
  (:require [jsondb.imgio :as imgio]))

(def cred
  (config :s3 :cred))

(def bucket
  (config :s3 :bucket))

(def upload-dir "www/uploads")

(defn id
  [[label _]]
  (-> label name (str "_" (utils/gen-id))))

(defn s3
  [file id]
  (s3/put-object cred bucket id file)
  (s3/update-object-acl cred bucket id (s3/grant :all-users :read)))

(defn save-original
  [id file]
  (io/copy file (io/file (str upload-dir "/" id))))

(defn process-images
  [[label _ :as opt] file place]
  (let [id (->> opt id (str place "/"))]
    (if (= label :original)
      () ;(save-original id file)
      (-> file (imgio/resize opt) (imgio/as-stream "jpg") (s3 id)))
    {label id}))

