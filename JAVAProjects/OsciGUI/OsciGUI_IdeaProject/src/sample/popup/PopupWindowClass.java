package sample.popup;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class PopupWindowClass {
    public static void display(String title, String msg, String buttonMsg)
    {
        Stage popupwindow=new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle(title);
        Label label1= new Label(msg);
        Button button1= new Button(buttonMsg);
        button1.setOnAction(e -> popupwindow.close());
        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 200, 70);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

}

