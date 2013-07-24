(defproject jsondb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [ 
                  [org.yaml/snakeyaml "1.9"]
                  [org.apache.lucene/lucene-analyzers-common "4.2.0"]
                  [org.apache.lucene/lucene-core "4.2.0"]
                  [org.apache.lucene/lucene-queries "4.2.0"]
                  [cheshire "5.2.0"]
                  [ring "1.2.0"]
                  [compojure "1.1.5"]
                  [org.clojure/clojure "1.5.1"]]
  :plugins [[lein-daemon "0.5.4"]]

  :daemon {:name-of-service {:ns jsondb.core
                             :pidfile "../shared/pmenu.pid"}})

