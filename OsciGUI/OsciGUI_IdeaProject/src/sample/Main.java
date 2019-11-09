package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller c = loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
/*
        byte[] bBuffer = new byte[1];
        double msgId = 0;
        SerialPort port = SerialPort.getCommPort("COM7");
        port.setNumDataBits(8);
        port.setNumStopBits(1);
        port.setParity(SerialPort.NO_PARITY);
        port.setBaudRate(115200);
        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING,0,0);

        if(port.openPort()){
            System.out.println("Serial port  COM7 opened.");
        }while (port.isOpen()) {
            try {
                port.readBytes(bBuffer, 1);
                msgId+=1;
                XYChart.Series series = new XYChart.Series();
                c.addData(msgId, bBuffer[0]);
                System.out.println((char)bBuffer[0]);
                if (msgId > 10){
                    port.closePort();
                }
                Thread.sleep(100,10);
            } catch  (InterruptedException e) {
                e.printStackTrace();
            }

        }
 */
    startUsartReaderThread(c);
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void startUsartReaderThread(Controller c){
        UARTReader reader = new UARTReader(c);
        Thread readerThread = new Thread(reader);
        readerThread.setDaemon(true);
        readerThread.start();
    }
}
