(ns jsondb.models
  (use inflections.core)
  (:require [jsondb.utils :as utils])
  (:require [jsondb.db :as db]))

(def images
  {:dishes {:original [] :small [150 150] :medium [250 350]}})

(defprotocol Model
  (prepare [this])
  (validate [this]))

(defprotocol Auth
  (valid-pass? [this password]))

