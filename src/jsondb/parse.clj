(ns jsondb.parse
  (require [clojure.java.io :as io])
  (require [cheshire.core :refer :all])
  (require [clojure.string :as s]))


(def file-path "./data/menu.txt")

(def file
  (clojure.java.io/reader file-path))

(def is-category?
  (comp not (partial re-find #"грн")))

(defn rm
  [s pattern]
  (s/replace s pattern ""))

(defn parse-item
  [line]
  (if (is-category? line)
    line
    (-> line (s/replace #"\d+\sгрн" "||") (rm #"\d+\sгр") (rm #"\d+\/") (rm #"за\/") (s/split #"\|\|"))))


(def skip-empty
  (partial filter (comp not empty?)))

(def category-seq
  (comp (partial partition 2) (partial partition-by #(-> % class (= java.lang.String)))))

(defn as-map
  [[category items]]
  {:title (first category), :items (map (fn [[title desc]] {:title title, :desc desc}) items)})

(def stream 
  (->> file line-seq skip-empty (map parse-item) category-seq (map as-map)))

(defn to-file []
  (with-open [wrtr (io/writer "./menu.txt")]
    (.write wrtr (generate-string stream))))
  


