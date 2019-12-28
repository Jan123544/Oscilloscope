package sample;

import com.fazecast.jSerialComm.SerialPort;

import java.nio.ByteBuffer;
import java.util.Date;

public class SerialBlockReader implements Runnable{
    // State machine
    private byte WAITING_FOR_LOWER_START_BYTE = 0;
    private byte GOT_LOWER_START_BYTE = 1;
    private byte state = WAITING_FOR_LOWER_START_BYTE;

    // Serial data
    private SerialPort port;
    private byte[] xDataBuffer;
    private byte[] yDataBuffer;
    private byte[] byteBuffer;

    // Scheduling gui updates
    private long lastUpdateTime;
    private Controller c;

    public SerialBlockReader(Controller c, SerialPort port){
        this.c = c;
        this.port = port;
        xDataBuffer = new byte [SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES];
        yDataBuffer = new byte [SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES];
        byteBuffer = new byte[2];
    }

    @Override
    public void run() {
        int numRead = 0;
        int chunkSize = SerialProtocol.SAMPLE_SIZE_BYTES*SerialProtocol.NUM_SAMPLES;
        while (port.isOpen()) {
            // state 0 is waiting for start byte
            if (state == WAITING_FOR_LOWER_START_BYTE) {
                // Wait for start byte
                port.readBytes(byteBuffer, 1);
                if (!SerialProtocol.isLowerStartByte(byteBuffer[0])) {
                    continue;
                }
                state = GOT_LOWER_START_BYTE;
            }

            if (state == GOT_LOWER_START_BYTE){
                port.readBytes(byteBuffer, 1);
                if(!SerialProtocol.isUpperStartByte(byteBuffer[0])){
                    state = WAITING_FOR_LOWER_START_BYTE;
                    continue;
                }

                // Read data and update
                Date d = new Date();
                System.err.print("Last update time:");
                System.err.println(d.getTime() - lastUpdateTime);
                lastUpdateTime = d.getTime();
                numRead = port.readBytes(xDataBuffer, chunkSize);
                if (numRead != chunkSize){
                    System.err.println("Error reading y channel. Read " + String.valueOf(numRead) + " instead of " + String.valueOf(chunkSize));
                }
                numRead = port.readBytes(yDataBuffer, chunkSize);
                if (numRead != chunkSize){
                    System.err.println("Error reading x channel. Read " + String.valueOf(numRead) + " instead of " + String.valueOf(chunkSize));
                }
                // If the port was closed after or while reading, then don't perform this update, because it's probably corrupted.
                if(!port.isOpen()){return;}
                state = WAITING_FOR_LOWER_START_BYTE;

                //d = new Date();
                //System.err.print("Read time:");
                //System.err.println(d.getTime() - lastUpdateTime);


                //debugPrintData();
                c.canvasCaretaker.requestCanvasUpdate(c, xDataBuffer, yDataBuffer);
                //ChartCaretaker.requestChartUpdate(c, xDataBuffer, SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES, yDataBuffer, SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES);
                //d = new Date();
                //System.err.print("Chart update time:");
                //System.err.println(d.getTime() - lastUpdateTime);
                continue;
            }
        }
    }

    public void debugPrintData(){
        int tmp = 0;
        long numBytesReceived = SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES;
        System.err.println("Received for X channel:");
        for(int i = 0;i<numBytesReceived;i+=2){
           tmp = 0;
           tmp = (0x000000ff & (int)xDataBuffer[i]);
           tmp |= (0x0000ff00 & ((int)(xDataBuffer[i+1]) << 8));
           System.err.print(tmp);
           System.err.print(" ");
        }
        System.err.println("");

        System.err.println("Received for Y channel:");
        for(int i = 0;i<numBytesReceived;i+=2){
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
