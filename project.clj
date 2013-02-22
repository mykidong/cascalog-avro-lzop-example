(defproject cascalog-avro-lzop-example "0.1.0-SNAPSHOT"
  :source-paths ["src/main/clojure"]
  :resource-paths ["src/main/resources"]
  :compile-path "target/classes" 
  :target-path "target/"   
  
  :dependencies [[org.clojure/clojure "1.4.0"]                 
                 [org.clojure/tools.logging "0.2.3"]                
                 [ch.qos.logback/logback-classic "1.0.6"]
                 [org.slf4j/log4j-over-slf4j "1.6.6"]
                 [midje "1.4.0"]
                 [cascalog "1.10.0"]
                 [midje-cascalog "0.4.0"]
                 [junit "4.8"]
                 [org.apache.hadoop/hadoop-core "1.0.4"
                  :exclusions [[org.codehaus.jackson/jackson-mapper-asl]
                               [org.codehaus.jackson/jackson-core-asl]
                               [org.slf4j/slf4j-api]]]
                 [cascading.avro/avro-scheme "2.1.1"
                  :exclusions [[org.slf4j/slf4j-api]
                               [cascading/cascading-core]]]
                 [cascading/cascading-core "2.0.4" 
                  :exclusions [org.slf4j/slf4j-api]]
                 [com.hadoop.gplcompression/hadoop-lzo "0.4.15"]
                 ]
  :plugins [[lein-midje "2.0.0-SNAPSHOT"]]
  
  :repositories [["java.net" "http://download.java.net/maven/2"]
                 ["sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"                         
                              :snapshots false                           
                              :checksum :fail                           
                              :update :always                           
                              :releases {:checksum :fail :update :always}}]
                 ["conjars" "http://conjars.org/repo"]
                 ["twttr" "http://maven.twttr.com"]]
  )