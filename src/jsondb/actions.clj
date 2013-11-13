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
  [dir id file]
  (let [path (str bucket "/" dir)]
    (s3/put-object cred path id file)
    (s3/update-object-acl cred path id (s3/grant :all-users :read))))

(def resize
  (comp (partial imgio/as-stream "jpg") (partial imgio/resize)))

(defn save-original
  [id file]
  (io/copy file (io/file (str upload-dir "/" id))))

(defn process-images
  [[label _ :as opt] file place]
  (let [id (id opt)]
    (if (= label :original)
      (save-original id file)
      (->> file (resize opt) (s3 (str place) id)))))

