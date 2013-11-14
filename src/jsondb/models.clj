(ns jsondb.models
  (:require [jsondb.utils :as utils])
  (:require [jsondb.db :as db]))

(def images
  {:dishes {:original [] :small [150 150] :medium [250 350]}})

;(def places (db/collection db/default "palces"))
;(def users  (db/collection db/default "users"))

(defprotocol Model
  (prepare [this])
  (update  [this attrs])
  (valid?  [this]))

(defn collection-name
  [model]
  (->> model class str (re-find #"\w+$")))

(def collection (comp db/collection-by-name collection-name))

(defn save
  [model]
  ((collection model) (:id model) model))

(defn create
  [model]
  (if-let [errors (valid? model)]
    errors
    (-> model on-create save)))


;(defn create-user
;  [email password]
;  (let [doc {:email email :encrypted_password (utils/encrypt password)}]
;    (if (and (not (users email)) (valid-user? doc))
;      (users email doc))))
;
;(defn auth-user
;  [email password]
;  (if-let [user (users email)]
;    (if (utils/check password (:encrypted_password user)) 
;      user)))
;
;
;
