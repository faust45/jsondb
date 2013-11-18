(ns jsondb.db
  (:require [clojure.core.async :as async :refer [<! >! <!! timeout chan alt! go]])
  (:require [clojure.string :as s])
  (:require [jsondb.classifier :as cl])
  (:require [clojure.data.json :as json]))

(defn id
  []
  (str (-> (java.util.Date.) .getTime) (int (* (rand) 100))))

(deftype Collection [db collection]
  clojure.lang.ISeq
    (next [this]
      (next (seq this)))
    (first [this]
      (first (seq this)))
  clojure.lang.Seqable
    (seq [this] 
      (seq (.values collection)))
  clojure.lang.IFn
    (invoke
      [this k]
      (if k (.get collection (str k))))
    (invoke
      [this k v]
      (let [key (or k (id))]
        (.put collection (str key) v)
        (.commit db)
        key))
  Object
    (toString [this] collection))


(defn open
  [path]
  (let [db (.make (doto (org.mapdb.DBMaker/newFileDB (java.io.File. path))
                    (.closeOnJvmShutdown)))]
    db))

(def default (open "db/testdbdd"))

(defn collection
  ([coll-name]
    (collection default coll-name))
  ([db coll-name]
    (collection db coll-name (jsondb.serializer.Map.)))
  ([db coll-name serializer]
    (let [m (.makeOrGet (doto (.createHashMap db coll-name) (.valueSerializer serializer)))]
      (Collection. db m))))

(def collections (atom {}))

(defn collection-by-name
  [collection-name]
  (let [coll (or (@collections collection-name)
                 (collection collection-name))]
    (swap! collections assoc collection-name coll)
    coll))

(defn values
  []
  (.getTreeMap default "users"))


;(defn lo
;  []
;  (-> "db/1.json" slurp json/read-str))


