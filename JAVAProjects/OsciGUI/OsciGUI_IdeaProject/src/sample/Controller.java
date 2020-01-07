package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import sample.constants.PacketType;
import sample.exceptions.BadInputException;
import sample.exceptions.PongNotReceivedException;
import sample.exceptions.SerialPortConnectionFailedException;
import sample.popup.PopupWindowClass;

public class Controller {
    // Primary stage
    Stage stage;

    // Cnavas
    @FXML
    Canvas canvas;
    GraphicsContext gc;
    CanvasCaretaker canvasCaretaker;

    @FXML
    Polyline xPolyline;

    @FXML
    Polyline yPolyline;

    @FXML
    Line xScanLine;
    @FXML
    Line xThresholdLine;
    @FXML
    Line yThresholdLine;
    @FXML
    Line yScanLine;
    @FXML
    Label xScanLineLabel;
    @FXML
    Label yScanLineLabel;
    @FXML
    Label xThresholdLineLabel;
    @FXML
    Label yThresholdLineLabel;
    @FXML
    Polygon yThresholdLineHandle;
    @FXML
    Polygon xThresholdLineHandle;
    @FXML
    Polygon yScanLineHandle;
    @FXML
    Polygon xScanLineHandle;
    @FXML
    Label lowerUnitLabel;
    @FXML
    Label upperUnitLabel;
    @FXML
    CheckBox enableXScanLineCB;
    @FXML
    CheckBox enableYScanLineCB;
    @FXML
    CheckBox enableXThresholdLineCB;
    @FXML
    CheckBox enableYThresholdLineCB;

    @FXML
    CheckBox autoUpdateCB;

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
    ChoiceBox<String> xTriggerModeCH;

    @FXML
    ChoiceBox<String> yTriggerModeCH;

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
    Button measureB;
    boolean isFirstMeasurement;

    // Internal settings
    @FXML
    TextField canvasVerticalNormalisationTF;
    @FXML
    TextField settingsUpdateRateTF;

    // Serial port settings writer
    SerialWriter serialWriter;


    // View settings : X channel only, y channel only, x and y channel, or x-y mode.
    @FXML
    ChoiceBox viewModeCH;

    ViewSettingsCaretaker viewSettingsCaretaker;


    @FXML
    TextField xTimerHoldOffTF;
    @FXML
    TextField yTimerHoldOffTF;

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

        viewSettingsCaretaker = new ViewSettingsCaretaker(this, viewModeCH);

        // Canvas init and update
        canvasCaretaker = new CanvasCaretaker(this, xPolyline, yPolyline, xThresholdLine, yThresholdLine, xScanLine,
                yScanLine, xScanLineLabel, yScanLineLabel, xThresholdLineLabel, yThresholdLineLabel,
                xScanLineHandle, yScanLineHandle, xThresholdLineHandle, yThresholdLineHandle, lowerUnitLabel, upperUnitLabel,
                enableXScanLineCB, enableYScanLineCB, enableXThresholdLineCB, enableYThresholdLineCB);

        serialWriter = new SerialWriter(this);
        isFirstMeasurement = true;

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
        } catch (PongNotReceivedException e){
            System.err.println("Pong not received.");
            serialStatusL.setText("Malformed input");
            return;
        }

        // Start data reader thread.
        SerialBlockReader sr = new SerialBlockReader(this, port);
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
        isFirstMeasurement = true;
    }

    public void measureButtonHandler(){
        if(port == null){
            PopupWindowClass.display("Error", "You are not connected.", "OK");
            return;
        }
        if(port.isOpen()){
            if(isFirstMeasurement){
                SerialWriter.launch(serialWriter);
                isFirstMeasurement = false;
            }
            SerialWriter.sendOnce(this, port, PacketType.NORMAL);
        }
    }
}
