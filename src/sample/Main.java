package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jodd.json.JsonParser;
import jodd.json.JsonSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {
    final double DEFAULT_SCENE_WIDTH = 800;
    final double DEFAULT_SCENE_HEIGHT = 600;
    private boolean keepDrawing = true;
//    boolean myTurn = true;
    private boolean isClientRunning = false;
    int strokeSize = 8;
    Server myServer;

//    private Paint color = Color.color(Math.random(), Math.random(), Math.random());
    private Paint color = Color.BLACK;
    private ArrayList<Stroke> strokeList = new ArrayList<Stroke>();

//    Canvas canvas;
    GraphicsContext gc;
    GraphicsContext secondGC;

    long drawDelay = 0;
    long delayIncrements = 20;

    PrintWriter out;
    BufferedReader in;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main myMain = new Main();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");

        // we're using a grid layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setGridLinesVisible(true);

        // add buttons and canvas to the grid
        Text sceneTitle = new Text("Welcome to Paint application");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0);

        HBox hbButton = new HBox(10);
        hbButton.setAlignment(Pos.TOP_LEFT);
        grid.add(hbButton, 0, 1);

//        Button startServerButton = new Button("Start server");
//        hbButton.getChildren().add(startServerButton);
//        startServerButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent e) {
//                System.out.println("Starting server...");
//                // start the server on a new thread and pass it the graphics context.
//                myServer = new Server(gc, myMain);
//                Thread serverThread = new Thread(myServer);
//                serverThread.start();
//
//            }
//        });


        Button replayDrawingButton = new Button("Replay my drawing!");
        hbButton.getChildren().add(replayDrawingButton);

        replayDrawingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (gc != null) {
                    System.out.println("Replaying drawing...");
                    try {
                        gc.clearRect(0, 0, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT-100);
//                        Thread.sleep(500);
                        drawDelay = 0;
                        for (Stroke stroke : strokeList) {
                            System.out.println("Replaying stroke now");
//                            sleepBetweenStrokes(stroke);
//                            Thread.sleep(stroke.getStrokeTime().toEpochMilli() - myClient.getTimeOfFirstStroke().toEpochMilli());
//                            wait(stroke.getStrokeTime().toEpochMilli() - myClient.getTimeOfFirstStroke().toEpochMilli());
//                            gc.strokeOval(stroke.getxCoordinate(), stroke.getyCoordinate(), stroke.getStrokeSize(), stroke.getStrokeSize());
                            DelayedTask<Void> sleeper = new DelayedTask<Void>(stroke, gc);
                            Thread waitingThread = new Thread(sleeper);
                            waitingThread.start();
                        }
                    } catch (Exception exception) {
                        System.out.println("Exception caught in sleep time.");
                        exception.printStackTrace();
                    }
                } else {
                    System.out.println("No strokes to replay yet!");
                }
            }
        });

        Text comboBoxHeading = new Text("Show your drawing to: ");
        hbButton.getChildren().add(comboBoxHeading);

        ObservableList<String> ipOptions = FXCollections.observableArrayList("localhost", "Ben");
        ComboBox ipComboBox = new ComboBox(ipOptions);
        hbButton.getChildren().add(ipComboBox);


        Button connectButton = new Button("Connect!");
        hbButton.getChildren().add(connectButton);

        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // new version!!!
                System.out.println("Now opening client socket to connect to: " + ipComboBox.getValue().toString());

                String ipAddress = "";
                if (ipComboBox.getValue().toString().equals("localhost")) {
                    ipAddress = "localhost";
                }
                if (ipComboBox.getValue().toString().equals("Ben")) {
                    ipAddress = "10.0.0.28";
                }
                try {
                    Socket clientSocket = new Socket(ipAddress, 8005);
                    isClientRunning = true;
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                } catch (IOException exception) {
                    System.out.println("Exception caught when making client socket...");
                    exception.printStackTrace();
                }
            }
        });

        Button iWantToDrawButton = new Button("I want to draw");
        hbButton.getChildren().add(iWantToDrawButton);

        iWantToDrawButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isClientRunning) {
                    out.println("switch");

                }
                myServer.setMyTurn(true);
                gc.setStroke(Color.BLUE);
                out.println(Color.BLUE);
            }
        });



        // add canvas
        Canvas canvas = new Canvas(DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT-100);

        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setStroke(color);
        gc.setLineWidth(5);


        myServer = new Server(gc);
        Thread serverThread = new Thread(myServer);
        serverThread.start();

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent e) {
//                System.out.println("x: " + e.getX() + ", y: " + e.getY());
                if (keepDrawing && myServer.isMyTurn()) {
                    gc.strokeOval(e.getX(), e.getY(), strokeSize, strokeSize);
                    // save stroke to client's arrayList for replay button (NOT DONE YET)
//                    Stroke saveStroke = new Stroke(e.getX(), e.getY(), strokeSize);
//                    myClient.addStrokeToArrayList(saveStroke);
                    Stroke clientStrokeMain = new Stroke(e.getX(), e.getY(), strokeSize);
                    strokeList.add(clientStrokeMain);

                    // To avoid error messages before second screen is open
                    if (secondGC != null) {
                        // draw on second screen
                        secondGC.strokeOval(e.getX(), e.getY(), strokeSize, strokeSize);
                    }

                    // only add stroke to client's strokeList if the client is running! (NOT DONE)
                    if (isClientRunning) {
                        sendStrokeToServer(clientStrokeMain);

                    }
                }
            }
        });

        grid.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent e) {
                System.out.println(e.getCode());
                System.out.println(e.getText());

                if (e.getText().equalsIgnoreCase("D")) {
                    keepDrawing = !keepDrawing;
                }
//
//                if (e.getText().equalsIgnoreCase("A")) {
//                    color = Color.color(Math.random(), Math.random(), Math.random());
//                    gc.setStroke(color);
//                }

                if(e.getText().equalsIgnoreCase("C")){
                    Paint currentColor = Color.color(Math.random(), Math.random(), Math.random());
                    gc.setStroke(currentColor);
//                    String JSONColor = seri.serialize(currentColor);
                    out.println(currentColor);
                }

                if (e.getCode() == KeyCode.UP) {
                    strokeSize++;
                    int maxStrokeSize = 60;
                    if (strokeSize > maxStrokeSize) {
                        System.out.println("sample.Stroke size can't increase past " + maxStrokeSize + "!");
                        strokeSize = maxStrokeSize;
                    }
                }

                if (e.getCode() == KeyCode.DOWN) {
                    strokeSize--;
                    if (strokeSize < 1) {
                        System.out.println("sample.Stroke size must be at least 1!");
                        strokeSize = 1;
                    }
                }
            }
        });


        grid.add(canvas, 0, 2);

        // set our grid layout on the scene
        Scene defaultScene = new Scene(grid, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT);

        primaryStage.setScene(defaultScene);
        primaryStage.show();
    }

    public void sendStrokeToServer(Stroke stroke) {
//        try {
            String serializedStroke = jsonSerialize(stroke);
            System.out.println("What client is trying to send: " + serializedStroke);
            out.println(serializedStroke);

//        in.readLine();

//            System.out.println("Server's response: " + in.readLine());
//        } catch (IOException exception) {
//            System.out.println("Exception caught when reading in from server...");
//            exception.printStackTrace();
//        }
    }

    // serialize and deserialize methods
    public String jsonSerialize(Stroke myStroke) {
        JsonSerializer jsonSerializer = new JsonSerializer().deep(true);
        String jsonString = jsonSerializer.serialize(myStroke);
        return jsonString;
    }

    public Stroke jsonDeserializeStroke (String jsonString) {
        JsonParser myParser = new JsonParser();
        Stroke myStrokeObject = myParser.parse(jsonString, Stroke.class);
        return myStrokeObject;
    }


    // second local window & canvas
    public void startSecondStage() {
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Second Stage");

        // we're using a grid layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setGridLinesVisible(true);
//        grid.setPrefSize(primaryStage.getMaxWidth(), primaryStage.getMaxHeight());

        // add buttons and canvas to the grid
        Text sceneTitle = new Text("Welcome to Paint application");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0);

        Button button = new Button("Sample paint button");
        HBox hbButton = new HBox(10);
        hbButton.setAlignment(Pos.TOP_LEFT);
        hbButton.getChildren().add(button);
        grid.add(hbButton, 0, 1);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("I can switch to another scene here ...");
            }
        });



        // add canvas
        Canvas canvas = new Canvas(DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT-100);


        secondGC = canvas.getGraphicsContext2D();
        secondGC.setFill(Color.GREEN);
        secondGC.setStroke(Color.BLUE);
        secondGC.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
        secondGC.setLineWidth(5);



        grid.add(canvas, 0 ,2);

        // set our grid layout on the scene
        Scene defaultScene = new Scene(grid, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT);


        secondaryStage.setScene(defaultScene);
        System.out.println("About to show the second stage");

        secondaryStage.show();
    }

    /**
     * We extend the Task class so that we can be "runnable" from
     * within a UI thread
     *
     * Note: this is an inner class inside of the Main class to make it easier
     * to manage the scope of the strokes and the draw delay and delay increments
     * @param <Void>
     */
    class DelayedTask<Void> extends Task<Void> {

        Stroke stroke;
        GraphicsContext graphicsContext;

        /**
         * Constructor to initialize the Stroke and GraphicsContext objects
         * @param stroke
         * @param graphicsContext
         */
        public DelayedTask(Stroke stroke, GraphicsContext graphicsContext) {
            this.graphicsContext = graphicsContext;
            this.stroke = stroke;
        }

        /**
         * This is the same thing as a Runnable object's run() method - it gets called
         * when the Thread that has this "Task" object is started
         * @return
         * @throws Exception
         */
//        @Override
        protected Void call() throws Exception {
            long sleepTime = getDrawDelay();
            try {
                Thread.sleep(sleepTime);
                graphicsContext.strokeOval(stroke.getxCoordinate(), stroke.getyCoordinate(), stroke.getStrokeSize(), stroke.getStrokeSize());
            } catch (InterruptedException e) {
            }
            return null;
        }

    }

    /**
     * All the delayed tasks threads will be started pretty much at the same time,
     * so we delay them all by an increasingly longer time to ensure that they execute
     * a) sequentially and b) with "delayIncrements" amount of time in between each other
     * @return
     */
    private long getDrawDelay() {
        drawDelay = drawDelay + delayIncrements;
        return drawDelay;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
