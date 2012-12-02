Avro Experimental Analysis
==========================

This is an experimental project to try out various features that Avro serialization format provides. The initial goal is to test out how flexible the schema evolution is across the readers/writers.

Usage:
-----

You can get this demo up and running easily via command line without needing to use any specific IDE. Ideally, it is recommended that you have [Maven](http://maven.apache.org/download.html) installed and follow the enumerated instructions.

1. Perform a quick compilation of the source.
        
        $ mvn compile

2. Package and copy the dependencies over locally (Without going on to generate a fatjar)
        
        $ mvn package install dependency:copy-dependencies

3. Open a new terminal and start AvroServer on it.
        
        $ java -cp target/com.bhembre.my-avro-1.0-SNAPSHOT.jar:target/dependency/avro-1.5.1.jar:target/dependency/avro-ipc-1.5.1.jar:target/dependency/jackson-core-asl-1.7.3.jar:target/dependency/jackson-mapper-asl-1.7.3.jar:target/dependency/slf4j-api-1.6.1.jar:target/dependency/jetty-6.1.26.jar:target/dependency/jetty-util-6.1.26.jar:target/dependency/servlet-api-2.5-20081211.jar com.bhembre.avro.AvroServer

4. Go back to the old terminal and run the client to PUT/GET the hardcoded message.
        
        $ java -cp target/com.bhembre.my-avro-1.0-SNAPSHOT.jar:target/dependency/avro-1.5.1.jar:target/dependency/avro-ipc-1.5.1.jar:target/dependency/jackson-core-asl-1.7.3.jar:target/dependency/jackson-mapper-asl-1.7.3.jar:target/dependency/slf4j-api-1.6.1.jar com.bhembre.avro.AvroClient
