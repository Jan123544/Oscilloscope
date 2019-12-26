package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;

import java.util.concurrent.Semaphore;

public class Controller {

    // Cnavas
    @FXML
    Canvas canvas;
    GraphicsContext gc;
    CanvasCaretaker canvasCaretaker;

    byte [] canvasXDataBuffer;
    byte [] canvasYDataBuffer;
    //long lastCanvasUpdateTime;
    Semaphore canvasDrawSemaphore = new  Semaphore(1);

    // Serial settings
    @FXML
    ProgressBar serialConnectPB;
    @FXML
    Button serialConnectB;
    @FXML
    Button serialDisconnectB;
    @FXML
    ChoiceBox<String> serialProtocolChoice;
    @FXML
    ChoiceBox<String> serialFlowControlChoice;
    @FXML
    TextField serialPortTF;
    @FXML
    TextField serialBaudRateTF;
    @FXML
    Label serialStatusL;

    SerialPort port;

    //// Controls
    // Trig
    @FXML
    ChoiceBox<String> trigModeChoice;
    @FXML
    TextField yTriggerLevelTF;
    @FXML
    Slider yTriggerLevelS;
    @FXML
    TextField xTriggerLevelTF;
    @FXML
    Slider xTriggerLevelS;

    // Y channel
    @FXML
    Slider ySensitivityS;
    @FXML
    Slider yOffsetS;

    @FXML
    TextField ySensitivityTF;
    @FXML
    TextField yOffsetTF;
    @FXML
    ChoiceBox yChannelVoltageRangeChoice;

    // X channel
    @FXML
    Slider xSensitivityS;
    @FXML
    Slider xOffsetS;

    @FXML
    TextField xSensitivityTF;
    @FXML
    TextField xOffsetTF;

    @FXML
    ChoiceBox xChannelVoltageRangeChoice;

    // Time control / Settings panel
    @FXML
    TextField timePerDivisionTF;
    @FXML
    Slider timePerDivisionS;
    @FXML
    CheckBox xChannelShowCB;
    @FXML
    CheckBox yChannelShowCB;
    @FXML
    CheckBox xyModeCB;

    @FXML
    Label validSettingsL;

    @FXML
    Button uploadSettingsB;

    // Internal settings
    @FXML
    TextField canvasVerticalNormalisationTF;
    @FXML
    TextField settingsUpdateRateTF;
    @FXML
    CheckBox autoUpdateCB;

    public void initialize(){

        // Internal settings init
        InternalSettingsCaretaker.init(this);

        // Serial settings init
        SerialControlCaretaker.initSerialControlSettings(this);

        // Controls init
        TriggerControlCaretaker.initTriggerControlSettings(this);

        // Y and X channels
        ChannelControlCaretaker.initChannelControls(this);

        // Time controls / Settings
        TimeControlsCaretaker.initTimeControlSettings(this);

        // Canvas init and update
        canvasCaretaker = new CanvasCaretaker();
        canvasCaretaker.init(this);
        // Chart init and update
    }

   // Action handlers
    public void connectButtonHandler(){
        try {
            port = SerialConnector.trySerialConnect(this);
            serialStatusL.setText("Connected");
        } catch(SerialPortConnectionFailedException e){
           System.err.println("Serial connection failed");
           return;
        } catch (BadInputException e){
           System.err.println("Malformed input");
           serialStatusL.setText("Malformed input");
           return;
        }

        // Start data reader thread.
        SerialBlockReader sr = new SerialBlockReader(this, port);
//        SerialReader sr = new SerialReader(this, port);
        Thread th = new Thread(sr);
        th.setDaemon(true);
        th.start();
    }

    public void disconnectButtonHandler(){
        if (port != null){
            port.closePort();
            serialConnectPB.setProgress(50);
            while(port.isOpen()){};
        }
        serialStatusL.setText("Disconnected");
        serialConnectPB.setProgress(0);
        autoUpdateCB.setSelected(false);
    }

    public void uploadSettingsButtonHandler(){
        if(port.isOpen()){
            SerialWriter w = new SerialWriter(this, port);
            Thread t = new Thread(w);
            t.setDaemon(true);
            t.start();
        }
    }

}
