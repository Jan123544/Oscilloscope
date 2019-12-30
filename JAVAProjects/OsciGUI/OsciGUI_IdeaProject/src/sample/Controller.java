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
    TextField xTimePerDivisionTF;
    @FXML
    TextField yTimePerDivisionTF;
    @FXML
    Slider xTimePerDivisionS;
    @FXML
    Slider yTimePerDivisionS;
    @FXML
    CheckBox xChannelShowCB;
    @FXML
    CheckBox yChannelShowCB;
    @FXML
    CheckBox xyModeCB;

    @FXML
    CheckBox yDoMeasurementCB;

    @FXML
    CheckBox xDoMeasurementCB;

    @FXML
    Button measureB;

    // Internal settings
    @FXML
    TextField canvasVerticalNormalisationTF;
    @FXML
    TextField settingsUpdateRateTF;

    // Caretakers
    GeneralMeasurementSettingsCaretaker generalMeasurementSettingsCaretaker;

    // Serial port settings writer
    SerialWriter serialWriter;

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

        // General settings (which channel to measure if at all etc.)
        generalMeasurementSettingsCaretaker = new GeneralMeasurementSettingsCaretaker(xDoMeasurementCB, yDoMeasurementCB);

        serialWriter = new SerialWriter(this);
        SerialWriter.launch(serialWriter);
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
            serialWriter.stopSending();
            serialWriter.waitUntilStopped();
            port.closePort();
            serialConnectPB.setProgress(50);
            while(port.isOpen()){};
        }
        serialStatusL.setText("Disconnected");
        serialConnectPB.setProgress(0);
    }

    public void measureButtonHandler(){
        if(port.isOpen()){
            serialWriter.stopSending();
            serialWriter.waitUntilStopped();
            SerialWriter.sendOnce(this, port);
        }
    }
}
