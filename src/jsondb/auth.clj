(ns jsondb.auth
  (use ring.util.response)
  (require jsondb.users)
  (import jsondb.users.User)
  (:require [jsondb.models  :as models]))

(def ^:dynamic current-user)

(defmacro defaction 
  [mname auth args body]
  )

(defmacro defsigned
  [mname & body]
  `(defn ~mname [req#]
     (if-let [user# (models/get User (:session req#))]
       (binding [current-user user#] ~@body)
       (redirect "/login"))))


