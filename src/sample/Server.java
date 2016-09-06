package sample;

import javafx.scene.canvas.GraphicsContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jessicatracy on 9/6/16.
 */
public class Server implements Runnable {
    private GraphicsContext serverGC;

    public Server(GraphicsContext gc) {
        this.serverGC = gc;
    }

    public void run() {
        try {
            startServer(serverGC);
        } catch (IOException exception) {
            System.out.println("Caught exception creating server socket or accepting client socket...");
            exception.printStackTrace();
        }
    }

    public void startServer(GraphicsContext serverGC) throws IOException {
        Main myMain = new Main();
        ServerSocket serverListener = new ServerSocket(8005);
        System.out.println("Listener ready to accept connections");

        // when it accepts a client socket, open a window.
        Socket clientSocket = serverListener.accept();

        System.out.println("Incoming connection from " + clientSocket.getInetAddress().getHostAddress());

        BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);

        // test that the graphics context works!!
//        serverGC.strokeOval(50, 50, 50, 50);
        String clientInput;
        while ((clientInput = inputFromClient.readLine()) != null) {
            System.out.println("Now printing the client's stroke: " + clientInput);
            Stroke deserializedStroke = myMain.jsonDeserializeStroke(clientInput);
            serverGC.strokeOval(deserializedStroke.getxCoordinate(), deserializedStroke.getyCoordinate(), deserializedStroke.getStrokeSize(), deserializedStroke.getStrokeSize());

            // tell client you received their stroke
            outputToClient.println("Received your stroke!");
        }

    }
}
