package com.bhembre.avro;

import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.avro.Protocol;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericData;

import org.apache.avro.util.Utf8;

import org.apache.avro.ipc.HttpServer;
import org.apache.avro.ipc.generic.GenericResponder;

public class AvroServer {
    private static ConcurrentHashMap<String, ByteBuffer> internalMap = new ConcurrentHashMap<String, ByteBuffer>();

    static class Responder extends GenericResponder {
        public Responder(Protocol protocol) {
            super(protocol);
        }

        public String getKey(GenericRecord requestKey) {
            String mapKey = "";
            mapKey += requestKey.get("id");
            mapKey += "-";
            mapKey += requestKey.get("day");
            return mapKey;
        }

        public Object respond(Protocol.Message message, Object request) {
            String msgName = message.getName();
            GenericRecord record = (GenericData.Record) request;

            if (msgName == "put") {
                String mapKey = getKey((GenericData.Record) record.get("data"));
                internalMap.put(mapKey, (ByteBuffer) record.get("message"));
                return null;
            } else if (msgName == "get") {
                String mapKey = getKey((GenericData.Record) record.get("data"));
                ByteBuffer reply = ByteBuffer.wrap("".getBytes());
                if (internalMap.containsKey(mapKey)) {
                    reply = internalMap.get(mapKey);
                }
                return reply;

            }
            throw new AvroRuntimeException("Unexpected message: " + msgName);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        File protoFile = new File("proto-server.avpr");
        if (!protoFile.exists()) {
            System.err.println("No protocol file named 'proto-server.avpr' found.");
            return;
        }
        Protocol protocol = Protocol.parse(protoFile);
        HttpServer server = new HttpServer(new AvroServer.Responder(protocol), 9998);
        server.start();
        System.out.println("Avro Server started on port: " + server.getPort());
        server.join();
    }
}
