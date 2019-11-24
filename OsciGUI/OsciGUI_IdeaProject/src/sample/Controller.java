package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

public class Controller {

    // Chart
    @FXML
    LineChart<Double, Double> chart;
    @FXML
    NumberAxis chartXAxis;
    @FXML
    NumberAxis chartYAxis;

    XYChart.Series<Double, Double> yDataSeries;
    XYChart.Series<Double, Double> xDataSeries;

    // Serial settings
    @FXML
    ProgressBar serialConnectPB;
    @FXML
    Button serialConnectB;
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
    Label trigLevelAdjustedL;
    @FXML
    TextField trigLevelTF;

    // Y channel
    @FXML
    Slider ySensitivityS;
    @FXML
    Slider yOffsetS;

    @FXML
    TextField ySensitivityTF;
    @FXML
    TextField yOffsetTF;

    // X channel
    @FXML
    Slider xSensitivityS;
    @FXML
    Slider xOffsetS;

    @FXML
    TextField xSensitivityTF;
    @FXML
    TextField xOffsetTF;

    // Time control / Settings panel
    @FXML
    TextField timePerDivisionTF;
    @FXML
    CheckBox xChannelShowCB;
    @FXML
    CheckBox yChannelShowCB;

    @FXML
    Label validSettingsL;

    public void initialize(){

        // Serial settings init
        SerialControlCaretaker.initSerialControlSettings(this);

        // Controls init
        TriggerControlCaretaker.initTriggerControlSettings(this);

        // Y and X channels
        ChannelControlCaretaker.initChannelControls(this);

        // Time controls / Settings
        TimeControlsCaretaker.initTimeControlSettings(this);

        // Chart init and update
        ChartCaretaker.initChartSettings(this);
        ChartCaretaker.updateChartSettings(this, chart);
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
        SerialReader sr = new SerialReader(this, port);
        Thread th = new Thread(sr);
        th.setDaemon(true);
        th.start();
    }
}
