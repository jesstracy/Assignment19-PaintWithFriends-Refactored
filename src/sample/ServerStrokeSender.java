//package sample;
//
//
//import jodd.json.JsonSerializer;
//
//import java.io.PrintWriter;
//
///**
// * Created by jessicatracy on 9/6/16.
// */
//public class ServerStrokeSender implements Runnable {
//    private Stroke serversStrokeToSend;
//    private PrintWriter outputToClient;
//
//    public ServerStrokeSender(Stroke serversStrokeToSend) {
//        this.serversStrokeToSend = serversStrokeToSend;
//    }
//
//    public void run() {
//        System.out.println("Send to client thread starting");
//        while (serversStrokeToSend != null) {
//            sendStrokeToClient(serversStrokeToSend);
//            System.out.println("stroke sent (on thread)");
//
//        }
//    }
//
//    public void sendStrokeToClient(Stroke stroke) {
//        System.out.println("In sendStrokeToClient() method");
//        String serializedStroke = jsonSerialize(stroke);
//        outputToClient.println(serializedStroke);
//    }
//
//    public String jsonSerialize(Stroke myStroke) {
//        JsonSerializer jsonSerializer = new JsonSerializer().deep(true);
//        String jsonString = jsonSerializer.serialize(myStroke);
//        return jsonString;
//    }
//}
