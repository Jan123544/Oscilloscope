package sample;

import com.fazecast.jSerialComm.*;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.io.InputStream;
import java.io.OutputStream;

public class SerialReader implements Runnable{
    // State machine
    private byte WAITING_FOR_LOWER_START_BYTE = 0;
    private byte GOT_LOWER_START_BYTE = 1;
    private byte READING_CHANNEL_X = 2;
    private byte READING_CHANNEL_Y = 3;
    private byte state = WAITING_FOR_LOWER_START_BYTE;

    // Serial data
    private SerialPort port;
    private byte[] xDataBuffer;
    private byte[] yDataBuffer;

    private int xBufferOffset =  0;
    private int yBufferOffset =  0;
    private byte[] byteBuffer;

    // Scheduling gui updates
    private Controller c;

    public SerialReader(Controller c, SerialPort port){
        this.c = c;
        this.port = port;
        xDataBuffer = new byte [Short.BYTES*SerialProtocol.DATA_BUFFER_LENGTH];
        yDataBuffer = new byte [Short.BYTES*SerialProtocol.DATA_BUFFER_LENGTH];
        byteBuffer = new byte[2];
    }

    @Override
    public void run() {
        while (port.isOpen()) {
            // state 0 is waiting for start byte
            if (state == WAITING_FOR_LOWER_START_BYTE) {
                // Wait for start byte
                port.readBytes(byteBuffer, 1);
                if (!SerialProtocol.isStartByteLower(byteBuffer[0])) {
                    continue;
                }
                state = GOT_LOWER_START_BYTE;
            }

            if (state == GOT_LOWER_START_BYTE){
                port.readBytes(byteBuffer, 1);
                if(!SerialProtocol.isStartByteHigher(byteBuffer[0])){
                    state = WAITING_FOR_LOWER_START_BYTE;
                    continue;
                }
                state = READING_CHANNEL_X;
            }

            if (state == READING_CHANNEL_X) {
                port.readBytes(byteBuffer, 2);
                //System.err.println("READING_CHANNEL_X got bytes" + String.valueOf((int)byteBuffer[0]) + String.valueOf((int)byteBuffer[1]));
                if (SerialProtocol.isNextChannelByte(byteBuffer)) {
                    state = READING_CHANNEL_Y;
                    continue;
                }

                // If not a next channel byte, copy into data buffer
                xDataBuffer[xBufferOffset] = byteBuffer[0];
                xBufferOffset++;
                xDataBuffer[xBufferOffset] = byteBuffer[1];
                xBufferOffset++;
                continue;
            }

            if (state == READING_CHANNEL_Y){
                port.readBytes(byteBuffer, 2);
                if (SerialProtocol.isStopByte(byteBuffer)) {
                    state = WAITING_FOR_LOWER_START_BYTE;
                    // Schedule gui update
                    // TODO
                    debugPrintData();
                    ChartCaretaker.requestChartUpdate(c, xDataBuffer, xBufferOffset, yDataBuffer, yBufferOffset);

                    //Reset offsets, will overwrite buffer contents on next frame
                    xBufferOffset = 0;
                    yBufferOffset = 0;
                    continue;
                }

                // If not a stop byte, read it into the data buffer
                yDataBuffer[yBufferOffset] = byteBuffer[0];
                yBufferOffset++;
                yDataBuffer[yBufferOffset] = byteBuffer[1];
                yBufferOffset++;
                continue;
            }
        }
    }

    public void debugPrintData(){
        int tmp = 0;
        System.err.println("Received for X channel:");
        for(int i = 0;i<xBufferOffset;i+=2){
           tmp = 0;
           tmp = (0x000000ff & (int)xDataBuffer[i]);
           tmp |= (0x0000ff00 & ((int)(xDataBuffer[i+1]) << 8));
           System.err.print(tmp);
           System.err.print(" ");
        }
        System.err.println("");

        System.err.println("Received for Y channel:");
        for(int i = 0;i<yBufferOffset;i+=2){
            tmp = 0;
            tmp = (0x000000ff & (int)yDataBuffer[i]);
            tmp |= (0x0000ff00 & ((int)yDataBuffer[i+1] << 8));
            System.err.print(tmp);
            System.err.print(" ");
        }
        System.err.println("");
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
