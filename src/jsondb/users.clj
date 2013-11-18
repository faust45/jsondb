(ns jsondb.users
  (:use validateur.validation)
  (require [jsondb.utils :as utils])
  (use clj-stacktrace.core)
  (require jsondb.models)
  (import (jsondb.models Errors Model Auth)))

(defn- crypt-pass
  [user]
  (->> (:password user) utils/encrypt (assoc user :encrypted_password)))

(def valid-user 
  {:create (validation-set
             (presence-of :email)
             (presence-of :password))
   :update (validation-set
             (presence-of :email))})

(defrecord User [id]
  Model
  (validations [this] valid-user)
  (before-update [this attrs]
    (->> (dissoc attrs :encrypted_password :email) (merge this)))
  (before-create [this]
    (if (:password this)
      (-> this crypt-pass (dissoc :password) (assoc :id (:email this)))))
  Auth
  (valid-pass? [this password1]
    (utils/check password1 (:encrypted_password this))))

(defn new
  [attrs]
  (map->User attrs))
