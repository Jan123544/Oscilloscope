package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.constants.PacketType;
import sample.popup.PopupWindowClass;

public class Main extends Application {

    FXMLLoader loader;
    Controller c;

    @Override
    public void start(Stage primaryStage) throws Exception{
        loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        c = loader.getController();
        primaryStage.setTitle("Osci app");
        Scene osciScene = new Scene(root, 1024, 546);
        osciScene.getStylesheets().add(this.getClass().getResource("oscistyle.css").toExternalForm());
        primaryStage.setScene(osciScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        if(c.port.isOpen()){
            SerialWriter.sendOnce(c, c.port, PacketType.EXIT);
        }
    }
}
