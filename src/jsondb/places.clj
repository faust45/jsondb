(ns jsondb.places
  (:use validateur.validation)
  (require [jsondb.utils :as utils])
  (require jsondb.models)
  (require [jsondb.db :as db])
  (import (jsondb.models Model)))


