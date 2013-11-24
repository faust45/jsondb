(ns jsondb.models
  (use inflections.core)
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

(defmulti  collection-name class)
(defmethod collection-name Class 
  [model]
  (->> model str (re-find #"\w+$") .toLowerCase plural))
(defmethod collection-name :default 
  [model]
  (collection-name (class model)))

(def collection (comp db/collection-by-name collection-name))

(defn get
  [klass id]
  (db/get (collection klass) id))

(defn all
  ([klass]
    (all klass {}))
  ([klass options]
    (let [limit (or (:limit options) 10)]
      (->> klass collection (take limit)))))

(defn save
  [model]
  (db/put (collection model) (:id model) model)
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
    (->> model v (assoc model :errors))))

(defn assign-id
  [model]
  (if (:id model)
    model
    (assoc model :id (utils/gen-id))))

(defn update
  [model old-model]
  (model-> model (valid-on? :update) (before-update old-model) save))

(defn conj-attr
  [model attr value]
  (update-in model [(keyword attr)] (comp #(conj % value) set)))

(defn add-to
  [model owner]
  (let [coll-name (collection-name model)]
    (->> (:id model) (conj-attr owner coll-name) (update owner))))

(defn create
  ([model]
    (model-> model (valid-on? :create) before-create assign-id uniq? save))
  ([owner model]
    (model-> model create (add-to owner))))

(defn auth
  [klass id password]
  (if-let [doc (get klass id)]
     (if (valid-pass? doc password)
       doc)))

