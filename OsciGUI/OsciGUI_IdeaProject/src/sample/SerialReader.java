package sample;

import com.fazecast.jSerialComm.*;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.io.InputStream;
import java.io.OutputStream;

public class SerialReader implements Runnable{
    // State machine
    private byte WAITING_FOR_START_BYTE = 0;
    private byte READING_CHANNEL_X = 1;
    private byte READING_CHANNEL_Y = 2;
    private byte state = WAITING_FOR_START_BYTE;

    // Serial data
    private SerialPort port;
    private byte[] xDataBuffer = new byte [Short.BYTES*SerialProtocol.DATA_BUFFER_LENGTH];
    private byte[] yDataBuffer = new byte [Short.BYTES*SerialProtocol.DATA_BUFFER_LENGTH];

    private int xBufferOffset =  0;
    private int yBufferOffset =  0;
    private byte[] byteBuffer = new byte[1];

    // Scheduling gui updates
    private Controller c;

    public SerialReader(Controller c){
        this.c = c;
    }

    @Override
    public void run() {
        while (port.isOpen()) {
            // state 0 is waiting for start byte
            if (state == WAITING_FOR_START_BYTE) {
                // Wait for start byte
                port.readBytes(byteBuffer, 1);
                if (!SerialProtocol.isStartByte(byteBuffer[0])) {
                    continue;
                }
                state = READING_CHANNEL_X;
            }

            if (state == READING_CHANNEL_X) {
                port.readBytes(byteBuffer, 1);
                if (!SerialProtocol.isNextChannelByte(byteBuffer[0])) {
                    state = READING_CHANNEL_Y;
                    continue;
                }

                // If not a next channel byte, read the rest of the word
                xDataBuffer[xBufferOffset] = byteBuffer[0];
                xBufferOffset++;
                port.readBytes(xDataBuffer, 1, xBufferOffset);
                continue;
            }

            if (state == READING_CHANNEL_Y){
                port.readBytes(byteBuffer, 1);
                if (!SerialProtocol.isStopByte(byteBuffer[0])) {
                    state = WAITING_FOR_START_BYTE;
                    // Schedule gui update
                    // TODO
                    debugPrintData();

                    //Reset offsets, will overwrite buffer contents on next frame
                    xBufferOffset = 0;
                    yBufferOffset = 0;
                    continue;
                }

                // If not a next channel byte, read the rest of the word
                yDataBuffer[yBufferOffset] = byteBuffer[0];
                yBufferOffset++;
                port.readBytes(yDataBuffer, 1, yBufferOffset);
                continue;
            }
        }
    }

    public void debugPrintData(){
        System.err.println("Received for X channel:");
        for(int i = 0;i<xBufferOffset;i++){
           System.err.print(xDataBuffer[i]);
           System.err.print(" ");
        }

        System.err.println("Received for Y channel:");
        for(int i = 0;i<yBufferOffset;i++){
            System.err.print(yDataBuffer[i]);
            System.err.print(" ");
        }
    }

    //private void scheduleGUIUpdate() {
    //    msgId++;
    //    XYChart.Series series = new XYChart.Series();
    //    Platform.runLater(new Runnable() {
    //        @Override
    //        public void run() {
    //            c.addData(msgId, bBuffer[0]);
    //        }
    //    });
    //    System.out.println((char) bBuffer[0]);
    //}
}
