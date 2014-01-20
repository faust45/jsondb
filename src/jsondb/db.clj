(ns jsondb.db
  (use inflections.core)
  (:require [monger.collection :as mc])
  (:require [monger.core :as mg])
  (:import [com.mongodb MongoOptions ServerAddress])
  (:import [org.bson.types ObjectId])
  (:require [jsondb.utils :as utils])
  (:require [clojure.string :as s])
  (:require [jsondb.classifier :as cl])
  (:require [clojure.data.json :as json]))


(mg/connect!)
(mg/set-db! (mg/get-db "menu"))

(mc/insert "places" { :_id (ObjectId.) :first_name "John" :last_name "Lennon" })


(defn find
  []
  (mc/find "places" {:first_name "John"}))

