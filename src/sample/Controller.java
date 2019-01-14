package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class Controller{

    @FXML
    private Button StartB;
    @FXML
    private Button StopB;
    @FXML
    private TextField Status;
    @FXML
    public TextArea Log;
    @FXML
    private TextField Port;
    @FXML
    private AnchorPane ServerSetting;
    @FXML
    private AnchorPane ServerStatus;
    @FXML
    private TextField Ram;
    @FXML
    private TextField ClientN;


    boolean ServerStart=false;
    int port;



    public void initialize() {

        Platform.runLater( () -> ServerSetting.requestFocus() );

        StartB.setOnAction(event -> {
            if(!ServerStart) {
                Status.setText("Running...");
                Status.setStyle("-fx-background-color: BLUE;");

                Server server = new Server(port);
                new Thread(server).start();
                ServerStart = true;
                Status.setText("Is running");
                Status.setStyle("-fx-background-color: GREEN;");


                Task task = new Task<Void>(){
                    long usedBytes;
                    @Override
                    public Void call(){
                        while(true) {
                            //ram
                            usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                            usedBytes = usedBytes / 1048576;
                            Ram.setText(String.valueOf(usedBytes)+" МБ");

                            ClientN.setText(String.valueOf(server.getNclient()));

                            try{
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };
                new Thread(task).start();
            }
        });
    }


    public void printMessage(String str){
        Platform.runLater(() -> {
            Log.appendText(str);
        });
    }

    public void getPort(ActionEvent actionEvent) {
        if(!Port.getText().equals("")) {
            port = Integer.parseInt(Port.getText());
            ServerSetting.requestFocus();
            ServerStatus.setDisable(false);

        }
    }
}
