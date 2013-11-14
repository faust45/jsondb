(ns jsondb.users
  (require [jsondb.utils :as utils])
  (use clj-stacktrace.core)
  (require jsondb.models)
  (import jsondb.models.Model))

(defn- crypt-pass
  [user]
  (->> (:password user) utils/encrypt (assoc user :encrypted_password)))

(defrecord User [id]
  Model
  (valid? [this] true)
  (on-update [this attrs]
    (->> (dissoc attrs :encrypted_password :email) (merge this)))
  (on-create [this]
    (if (:password this)
      (-> this crypt-pass (dissoc :password) (assoc :id (:email this))))))

(defn new
  [attrs]
  (map->User attrs))
