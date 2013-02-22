# cascalog-avro-lzop-example

A Cascalog example to write/read Lzop compressed Avro data to/from lfs using Cascalog.

## Usage

Make sure that the native lzop is installed on the machine,
for more details, please see https://github.com/twitter/hadoop-lzo.

To run test

	lein midje clojure-example.cascalog.readinglist-job
