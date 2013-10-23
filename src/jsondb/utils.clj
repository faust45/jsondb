(ns jsondb.utils
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

(defn try-parse-json
  [req]
  (if (is-json? req) 
      (assoc req :doc (json/read-str (slurp (:body req))))
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
