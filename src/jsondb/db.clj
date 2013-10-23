(ns jsondb.db
  (:require [clojure.core.async :as async :refer [<! >! <!! timeout chan alt! go]])
  (:require [clojure.string :as s])
  (:require [jsondb.classifier :as cl])
  (:require [clojure.data.json :as json]))


(def db-path "db/dev.db")
(def index-path "db/dev.index")

(def mdb
  (.make (doto (org.mapdb.DBMaker/newFileDB (java.io.File. "db/testdb"))
               (.closeOnJvmShutdown)
               (.encryptionEnable "password"))))

(defn coll
  [name]
  (.getTreeMap mdb name))

(def coll-all
  (coll "all"))

(defn gen-id
  []
  (str (-> (java.util.Date.) .getTime) (int (* (rand) 100))))

(defn add-id
  [{id "_id" :as doc}]
  (if id 
    doc
    (assoc doc "_id" (gen-id))))

(defn save
  [doc]
  (let [{id "_id" :as doc1} (-> doc add-id)]
    (.put coll-all id doc1)
    (.commit mdb)
    id))

(defn g 
  [id]
  (.get coll-all id))

(defn lo
  []
  (-> "db/1.json" slurp json/read-str))
