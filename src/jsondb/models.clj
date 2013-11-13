(ns jsondb.models
  (:require [jsondb.utils :as utils])
  (:require [jsondb.db :as db]))

(def images
  {:dishes {:original [] :small [150 150] :medium [250 350]}})

;(def places (db/collection db/default "palces"))
;(def users  (db/collection db/default "users"))

(defprotocol Model
  (id [this])
  (on-create [this])
  (on-update [this])
  (valid? [this]))

(defn collection-name
  [model]
  (->> model class str (re-find #"\w+$")))

(defn save
  [model]
  (let [id   (id model)
        coll (collection-name model)]
    ((db/collection-by-name coll) id model)))

(defn create
  [model]
  (if-let [errors (valid? model)]
    errors
    (save model)))

(def coll
  (db/collection "data"))

;(defn valid-user?
;  [doc]
;  (and (:email doc)
;       (:encrypted_password doc)))
;
;(def user-keep-attrs [:email :encrypted_password])
;(defn before-create
;  )
;
;  ;[new-doc old-doc]
;  ;(merge new-doc (select-keys old-doc [:email :encrypted_password])))
;
;(defn update-user
;  [new-doc]
;  (if-let [old-doc (users (:email new-doc))]
;    (let [doc (prepare-user new-doc old-doc)]
;      (if (valid-user? doc)
;        (users (:email doc) doc)))))
;
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
