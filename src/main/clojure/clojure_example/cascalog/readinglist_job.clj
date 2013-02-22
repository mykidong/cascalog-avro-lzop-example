(ns clojure-example.cascalog.readinglist-job
  (:refer-clojure :exclude [bytes])
  (:use cascalog.api
        clojure.test
        clojure-example.domain
        [midje sweet cascalog])
  (:require [cascalog.ops :as c]
            [cascalog.conf :as conf]
            [clojure-example.util :as util]
            )
  (:import [cascading.tuple Fields]))

(def user-reading-book-struct-list [ (struct user-reading-book "mykidong" "111-222-333" "user-review-id-01")
                     (struct user-reading-book "mykidong" "222-222-333" "user-review-id-02")
                     (struct user-reading-book "smartpremier" "333-222-333" "user-review-id-03")
                     (struct user-reading-book "mykidong" "333-222-333" "user-review-id-04")
                     (struct user-reading-book "guriguri" "111-222-333" "user-review-id-05")
                   ])

(defmapop struct2vec 
  "convert struct to vector"
  [s]
  [(:user-id s) (:isbn s) (:user-review-id s)])

(defn job-conf [compress?]
  {"mapred.output.compress" compress?
   "mapred.compress.map.output" compress?
   "mapred.output.compression.codec" "com.hadoop.compression.lzo.LzopCodec"
   "io.compression.codecs" "com.hadoop.compression.lzo.LzopCodec"
   "io.compression.codec.lzo.class" "com.hadoop.compression.lzo.LzopCodec"
   })

(defn delete [path]
  (-> (org.apache.hadoop.fs.FileSystem/get (org.apache.hadoop.conf.Configuration.))
    (.delete (org.apache.hadoop.fs.Path. path) true)))

(defn write-avro-lzop
  "write avro in compressed lzop into lfs."
  []
  (delete "/tmp/avro-result")
  (?<- (util/lfs-avro "/tmp/avro-result" "/avro/user-reading-book.avsc" true ["?user-id" "?isbn" "?user-review-id"])
       [?user-id ?isbn ?user-review-id]
       (user-reading-book-struct-list ?user-reading-book)
       (struct2vec :< ?user-reading-book :> ?user-id ?isbn ?user-review-id)
       ))

(deftest run-avro-lzop
  (conf/set-job-conf! (job-conf true)) ; lzop compression enabled.
  (write-avro-lzop) ; write avro in lzop compressed format.
  (delete "/tmp/avro-aggr-result") 
  ;
  ; read lzop compressed avro from lfs.
  ;
  (let [avro-src (util/lfs-avro "/tmp/avro-result" "/avro/user-reading-book.avsc" false  ["?user-id" "?isbn" "?user-review-id"])]
		(?<- (util/lfs-avro "/tmp/avro-aggr-result" "/avro/user-reading-book-aggr.avsc" true ["?user-id" "?count"])
          [?user-id ?count]
          (avro-src :> ?user-id ?isbn ?user-review-id)
          (c/!count ?isbn :> ?count)
          )
  )
  ;
  ; show the result in stdout.
  ;
  (conf/set-job-conf! (job-conf false)) ; lzop compression disabled.
  (let [avro-src (util/lfs-avro "/tmp/avro-aggr-result" "/avro/user-reading-book-aggr.avsc" false  ["?user-id" "?count"])]
      (?<- (stdout)
          [?user-id ?count]
          (avro-src :> ?user-id ?count)
          )))
