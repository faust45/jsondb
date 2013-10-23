(ns jsondb.classifier
  (import org.yaml.snakeyaml.Yaml)
  (require [clojure.string :as s])
  (import org.apache.lucene.analysis.ru.RussianAnalyzer org.apache.lucene.analysis.snowball.SnowballAnalyzer  org.tartarus.snowball.ext.RussianStemmer)
  (import [org.apache.lucene.analysis Analyzer Analyzer$TokenStreamComponents])
  (import [org.apache.lucene.analysis.core LowerCaseFilter WhitespaceTokenizer StopAnalyzer SimpleAnalyzer WhitespaceAnalyzer])
  (import [org.apache.lucene.util Version BytesRef])
  (import [org.apache.lucene.analysis.tokenattributes CharTermAttribute]))



(def st (org.tartarus.snowball.ext.RussianStemmer.))

(defn reject
  [words s]
  (reduce #(s/replace %1 %2 "") s words))

(def stop-words 
  (map (comp re-pattern s/lower-case) (.load (Yaml. ) (slurp "config/stop-words.yml"))))

(def words-to-exclude 
  (map (comp re-pattern s/lower-case) (.load (Yaml. ) (slurp "config/exclude.yml"))))

(def tags
  (.load (Yaml. ) (slurp "config/tags.yml")))

(defn p-tags
  [[tag values]]
  {tag (map (partial reject stop-words) values)})

(defn pp-tags
  [vtags]
  (apply merge (map p-tags vtags)))

(defn stem
  [word]
  (doto st (.setCurrent word) (.stem))
  (.getCurrent st))

(defn stem-seq
  [s]
  ((comp set (partial remove (partial = "")) (partial map stem) (comp (partial map #(s/replace % #" " "")) #(s/split % #" ") s/lower-case)) s))

(def m-stem-seq (memoize stem-seq))

(defn inter
  [s base]
  (clojure.set/intersection (m-stem-seq s) (m-stem-seq base)))

(defn check
  [s [tag values]]
  (let [d (inter s (s/join " " values))]
    {tag (count d) (str "_" tag) d}))

(defn tags-for 
  [s]
  (apply merge (map (partial check s) (pp-tags tags))))

(defn add-tags
  [item]
  (let [values (->> item vals (apply str) s/lower-case (reject words-to-exclude))]
    (assoc item "tags" (tags-for values))))


