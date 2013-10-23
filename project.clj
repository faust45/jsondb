(defproject jsondb "0.1.0-SNAPSHOT"
  :main jsondb.core
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [ 
                  [sonian/carica "1.0.3"]
                  [clj-aws-s3 "0.3.7"]
                  [org.imgscalr/imgscalr-lib "4.2"]
                  [org.mapdb/mapdb "0.9.6"]
                  [org.yaml/snakeyaml "1.9"]
                  [org.apache.lucene/lucene-analyzers-common "4.2.0"]
                  [org.apache.lucene/lucene-core "4.2.0"]
                  [org.apache.lucene/lucene-queries "4.2.0"]
                  [cheshire "5.2.0"]
                  [org.clojure/data.json "0.2.2"]
                  [ring "1.2.0"]
                  [compojure "1.1.5"]
                  [org.clojure/clojure "1.5.1"]
                  [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]]

  :daemon {:pmenu {:ns jsondb.core
                   :pidfile "/var/www/PersonalMenu/shared/pids/pmenu.pid"}})

