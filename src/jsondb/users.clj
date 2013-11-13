(ns jsondb.users
  (use clj-stacktrace.core)
  (require jsondb.models)
  (import jsondb.models.Model))


(defrecord User [id]
  Model 
  (id [this] 101)
  (valid? [this] true))

(defn user
  [attrs]
  (map->User attrs))

(defn create
  []
  (try 
    (jsondb.models/coll 1 (user {:name "Alexa"}))
    (catch Exception e 
      (parse-exception e))))
;(defmacro defmodel
;  [type]
;  )

;(defmodel User
;  {:id :email}
;  (valid? [this] true)
;  (on-create [this]))
;
;create (User. {:name "Alexa"})
