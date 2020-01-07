package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import sample.settings.SerialSettings;

public class SerialControlCaretaker {
    public static void initSerialControlSettings(Controller c){
        c.serialProtocolChoice.setItems(FXCollections.observableArrayList( "8N1"));
        c.serialProtocolChoice.getSelectionModel().select(0);
        c.serialFlowControlChoice.setItems(FXCollections.observableArrayList("NO"));
        c.serialFlowControlChoice.getSelectionModel().select(0);
        c.serialStatusL.setText("Not connected");
    }

    // Reads the settings of the serial port from the GUI, also converts strings to Integers etc. where needed.
    public static SerialSettings readSerialSettings(Controller c) throws NumberFormatException{
        SerialSettings set = new SerialSettings();
        set.port = c.serialPortTF.getText();
        set.baudRate = Integer.parseInt(c.serialBaudRateTF.getText());

        switch (c.serialProtocolChoice.getSelectionModel().getSelectedItem()){
            case "8N1":
                set.numDataBits= 8;
                set.numStopBits= 1;
                set.parity= SerialPort.NO_PARITY;
                break;
        }

        switch (c.serialFlowControlChoice.getSelectionModel().getSelectedItem()){
            case "NO":
                set.flowControl = SerialPort.FLOW_CONTROL_DISABLED;
                break;
        }

        return set;
    }
}
