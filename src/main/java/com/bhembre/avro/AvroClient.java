package com.bhembre.avro;

import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;

import org.apache.avro.Protocol;
import org.apache.avro.ipc.generic.GenericRequestor;
import org.apache.avro.ipc.HttpTransceiver;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericData;
import org.apache.avro.util.Utf8;


public class AvroClient {

    private URL url;

    private Protocol protocol;

    private static int port = 9998;

    private String protoFile = "proto-server.avpr";

    public AvroClient(URL url) throws IOException {
        this.url = url;
        this.protocol = Protocol.parse(new File(protoFile));
    }

    public Object request(String msgName, Object request) throws IOException {
        HttpTransceiver httpTransceiver = new HttpTransceiver(url);
        GenericRequestor requestor = new GenericRequestor(protocol, httpTransceiver);
        return requestor.request(msgName, request);
    }

    public ByteBuffer getRequest(GenericRecord datumKey) throws IOException {
        Schema schema = protocol.getMessages().get("get").getRequest();
        GenericRecord request = new GenericData.Record(schema);
        request.put("data", datumKey);
        return (ByteBuffer)request("get", request);
    }

    public ByteBuffer get(MessageData key) throws IOException {
        Schema schema = protocol.getType("message");
        GenericRecord datum = new GenericData.Record(schema);
        datum.put("id", new Utf8(key.getId()));
        datum.put("day", key.getDay());
        return getRequest(datum);
    }

    public void putRequest(GenericRecord datumKey, ByteBuffer data) throws IOException {
        Schema schema = protocol.getMessages().get("put").getRequest();
        GenericRecord request = new GenericData.Record(schema);
        request.put("data", datumKey);
        request.put("message", data);
        request("put", request);
    }

    public void put(MessageData key, ByteBuffer data) throws IOException {
        Schema schema = protocol.getType("message");
        GenericRecord datum = new GenericData.Record(schema);
        datum.put("id", new Utf8(key.getId()));
        datum.put("day", key.getDay());
        putRequest(datum, data);
    }

    public static void main(String[] args) {
        try {
            AvroClient avroClient = new AvroClient(new URL(String.format("http://localhost:%d/", AvroClient.port)));

            MessageData messageData = new MessageData("34b328da7f", 20120902);
            String dataBytes = new String("{'_id': '34b328da7f.00001', 'message': 'This is a test.'}");

            ByteBuffer buffer = ByteBuffer.wrap(dataBytes.getBytes());
            avroClient.put(messageData, buffer);

            buffer = avroClient.get(messageData);
            dataBytes = new String(buffer.array());
            System.out.println("Bytes returned:\n" + dataBytes);
        } catch (IOException ioe) {
            System.err.println("Exception Hit: " + ioe.getMessage());
        }

    }

    static class MessageData {
        String id;
        int day;

        public MessageData(String id, int day) {
            this.id = id;
            this.day = day;
        }

        public String getId() {
            return id;
        }

        public int getDay() {
            return day;
        }
    }

}
