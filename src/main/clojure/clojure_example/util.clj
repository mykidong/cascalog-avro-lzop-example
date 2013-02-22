(ns clojure-example.util
  (:require [cascalog.tap :as tap]
            )
  (:import [cascading.avro AvroScheme]
           [org.apache.avro Schema]
           [org.apache.avro Schema$Parser]
           [cascading.tuple Fields]
           )
  )

(deftype Foo [])
(defn get-url [path] (.getResource (.getClass (Foo.)) path))
  
(defn lfs-avro
"
    in-out-path: input or output path.
    schema-path: avro schema path from the classpath.
    sink?: to write avro into lfs is true, to read avro from lfs is false.
    fields: alias fields related to avro schema.
"
  [in-out-path schema-path sink? fields & opts]
  (let [url (get-url schema-path)
        schema (-> (Schema$Parser.)
                 (.parse (.openStream url)))
        avro-scheme (AvroScheme. schema)]
    (if (true? sink?) 
      (.setSinkFields avro-scheme (Fields. (into-array fields)))
      (.setSourceFields avro-scheme (Fields. (into-array fields))))
    (apply tap/lfs-tap avro-scheme in-out-path opts)))