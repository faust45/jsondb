(ns jsondb.utils
  (:import org.mindrot.jbcrypt.BCrypt)
  (:import [java.io FileWriter BufferedWriter File FileOutputStream])
  (:require [clojure.data.json :as json])) 

(defn json-resp [data]
  {:status 200
   :headers {"Content-Type" "application/json; charset=UTF-8"}
   :body (json/write-str data)})

(defn is-json?
  [req]
  (re-find #"application/json" (-> req :content-type str)))

(defn is-file?
  [req]
  (re-find #"application/octet-stream" (-> req :content-type str)))

(defn is-post?
  [req]
  (= (:request-method req) :post))

(defn try-parse-json
  [req]
  (if (and (is-post? req) (is-json? req))
      (assoc-in req [:params :doc] (-> (:body req) slurp json/read-str clojure.walk/keywordize-keys))
      req))

(defn save-tmp
  [in]
  (println (.available in))
  (let [tmp (File/createTempFile "tempfile" ".tmp")]
    (with-open [tmp (FileOutputStream. tmp)] 
      (loop [c (.read in)]
        (if ((comp not neg?) c)
          (do
            (.write tmp c)
            (recur (.read in))))))
    tmp))

(defn file-ext
  [fname]
  (let [i (.lastIndexOf (.toString fname) ".")]
   (.substring (.toString fname) i)))

(defn try-save-file
  [req]
  (if (is-file? req)
      (let [fname (-> req :headers (get "x-file-name"))
            tmp (File/createTempFile "tempfile" (file-ext fname))]
        (clojure.java.io/copy (:body req) tmp)
        (merge req {:tempfile tmp :file-name fname}))
      req))

(defn wrap-parse-json
  [handler]
  (fn [req]
    (-> req try-parse-json handler)))

(defn wrap-upload-file
  [handler]
  (fn [req]
    (-> req try-save-file handler)))

(defn c-update 
  [m [k & path] f]
  (if path
    (map #(update-in % [k] (fn [l] (c-update l path f))) m)
    (map #(update-in % [k] f) m)))

(defn find-first
  [pred coll]
  (->> coll (filter pred) first))

(defn by_id
  [id]
  #(= id (% "_id")))

(defn select-values
  [k coll]
  (map (partial coll) k))

(defn abs
  [a]
  (if (< a 0)
    (* a -1)
    a))

(defn gen-id
  []
  (str (-> (java.util.Date.) .getTime) (int (* (rand) 100))))

(defn encrypt
  "Encrypt a password string using the BCrypt algorithm. The optional work
  factor is the log2 of the number of hashing rounds to apply. The default
  work factor is 10."
  ([raw]
     (BCrypt/hashpw raw (BCrypt/gensalt)))
  ([raw work-factor]
     (BCrypt/hashpw raw (BCrypt/gensalt work-factor))))

(defn check
  "Compare a raw string with a string encrypted with the
  crypto.password.bcrypt/encrypt function. Returns true the string match, false
  otherwise."
  [raw encrypted]
  (try
    (BCrypt/checkpw raw encrypted)
    (catch Exception e)))

;(defn conj-attr
;  [model attr value]
;  (update-in model [(keyword attr)] (comp #(conj % value) set)))
;
;(defn uniq?
;  [model]
;  (let [doc ((collection model) (:id model))]
;    (if doc
;      (assoc model :errors {:id "not uniq"})
;      model)))
;
;(defmacro model->
;  [expr & forms]
;  (let [g (gensym)
;        pstep (fn [step] `(if (-> ~g :errors empty? not) ~g (-> ~g ~step)))]
;    `(let [~g ~expr
;           ~@(interleave (repeat g) (map pstep forms))]
;       ~g)))
;
;
;(defn assign-id
;  [model]
;  (if (:id model)
;    model
;    (assoc model :id (utils/gen-id))))
;
;(defn write-to-db
;  [model]
;  (db/put (collection model) (:id model) model))
;
;(defmulti  collection-name class)
;(defmethod collection-name Class
;  [model]
;  (->> model str (re-find #"\w+$") .toLowerCase plural))
;(defmethod collection-name :default 
;  [model]
;  (collection-name (class model)))
;
;(def collection (comp db/collection-by-name collection-name))
;
(defn id
  []
  (str (-> (java.util.Date.) .getTime) (int (* (rand) 100))))


