package sample;

import com.fazecast.jSerialComm.*;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.io.InputStream;
import java.io.OutputStream;

public class UARTReader implements Runnable{
    private SerialPort port;
    private byte [] bBuffer = new byte[1];
    private Controller c;
    private long msgId;

    public UARTReader(Controller c){
        this.c = c;
        msgId = 0;
        port = SerialPort.getCommPort("COM7");
        port.setNumDataBits(8);
        port.setNumStopBits(1);
        port.setParity(SerialPort.NO_PARITY);
        port.setBaudRate(115200);
        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING,0,0);

        if(port.openPort()){
            System.out.println("Serial port  COM7 opened.");
        }
    }

    @Override
    public void run() {
        while (port.isOpen()) {
            try {
                port.readBytes(bBuffer, 1);
                msgId++;
                XYChart.Series series = new XYChart.Series();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        c.addData(msgId, bBuffer[0]);
                    }
                });
                System.out.println((char)bBuffer[0]);
                Thread.sleep(1000,10);
            } catch  (InterruptedException e) {
               e.printStackTrace();
            }

        }
    }
}
