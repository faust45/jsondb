(ns jsondb.serializer
  (:require [taoensso.nippy :as nippy]))

(gen-class
  :name "jsondb.serializer.Map"
  :implements [org.mapdb.Serializer java.io.Serializable])

(defn -serialize
  [this out value]
  (let [data (nippy/freeze value)]
    (org.mapdb.Utils/packInt out (count data))
    (.write out data)))

(defn -deserialize
  [this in available]
  (let [size (org.mapdb.Utils/unpackInt in)
        data (byte-array size)]
    (.readFully in data)
    (nippy/thaw data)))





