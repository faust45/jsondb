(ns jsondb.models
  (:require [jsondb.utils :as utils])
  (:require [jsondb.db :as db]))

(def images
  {:dishes {:original [] :small [150 150] :medium [250 350]}})

(defprotocol Model
  (validations [this])
  (before-create [this])
  (before-update [this attrs]))

(defprotocol Auth
  (valid-pass? [this password]))

(defmacro model->
  [expr & forms]
  (let [g (gensym)
        pstep (fn [step] `(if (-> ~g :errors empty? not) ~g (-> ~g ~step)))]
    `(let [~g ~expr
           ~@(interleave (repeat g) (map pstep forms))]
       ~g)))

(defn collection-name
  [model]
  (if (instance? Class model)
    (->> model str (re-find #"\w+$"))
    (->> model class str (re-find #"\w+$"))))

(def collection (comp db/collection-by-name collection-name))

(defn save
  [model]
  ((collection model) (:id model) model)
  model)

(defn uniq?
  [model]
  (let [doc ((collection model) (:id model))]
    (if doc
      (assoc model :errors {:id "not uniq"})
      model)))

(defn valid-on? 
  [model group]
  (let [v (-> model validations group)]
    (->> (v model) (assoc model :errors))))

(defn create
  [model]
  (model-> model (valid-on? :create) before-create uniq? save))

(defn auth
  [klass id password]
  (if-let [doc ((collection klass) id)]
     (if (valid-pass? doc password)
       doc)))

