(ns jsondb.places
  (:use validateur.validation)
  (require [jsondb.utils :as utils])
  (require jsondb.models)
  (import (jsondb.models Model)))

(def valid-place
  {:create (validation-set
             (presence-of :name))
   :update (validation-set
             (presence-of :name))})

(defrecord Place [id]
  Model
  (validations [this] valid-place)
  (before-update [this old] this)
  (before-create [this] this))

(defn new
  [attrs]
  (map->Place attrs))
