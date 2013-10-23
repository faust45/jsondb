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

(defn add-id
  [image]
  (assoc image :id (-> :label image name (str "_" (utils/gen-id)))))

(defn s3
  [image]
  (s3/put-object cred bucket (:id image) (:file image))
  image)

(defn acl
  [image]
  (s3/update-object-acl cred bucket (:id image) (s3/grant :all-users :read))
  image)

(defn prepare
  [{:keys [label id]}]
  {label id})

(def send-to-s3
  (comp acl s3 add-id))
      
(def resize
  (comp prepare send-to-s3 (partial imgio/as-stream "jpg") (partial imgio/resize)))

(defn save-original
  [[label _] file]
  (let [fname (str upload-dir "/" (name label) "_" (utils/gen-id) (utils/file-ext file))]
    (do (io/copy file (io/file fname))
        {label fname})))

(defn process-images
  [[label _ :as image] file]
  (if (= label :original)
    (save-original image file)
    (resize image file)))

