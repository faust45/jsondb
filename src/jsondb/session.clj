(ns jsondb.session
  (:require [jsondb.db :as db])
  (:use ring.middleware.session.store))

(defn store
  [path]
  (let [coll (db/collection (db/open path) "all")]
    (reify SessionStore
      (read-session   [_ k]   (db/get coll k))
      (write-session  [_ k v] (db/put coll (or k (java.util.UUID/randomUUID)) v))
      (delete-session [_ k]   (db/delete coll k)))))


