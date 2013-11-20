(ns jsondb.auth
  (use ring.util.response)
  (require jsondb.users)
  (import jsondb.users.User)
  (:require [jsondb.models  :as models]))

(def ^:dynamic current-user)

(defmacro defsigned
  [mname params & body]
  `(let [f# (fn ~params ~@body)]
     (defn ~mname [req# & params#]
        (if-let [user# (models/get User (:session req#))]
          (binding [current-user user#] (apply f# params#))
          (redirect "/login")))))


