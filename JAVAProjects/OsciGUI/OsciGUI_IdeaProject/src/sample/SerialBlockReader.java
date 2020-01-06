package sample;

import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SerialBlockReader implements Runnable{

    // Serial data
    private SerialPort port;
    private byte[] xDataBuffer;
    private byte[] xUpdateTypeBuffer;
    private byte[] yDataBuffer;
    private byte[] yUpdateTypeBuffer;
    private byte[] byteBuffer;

    // Scheduling gui updates
    private long lastUpdateTime;
    private Controller c;

    public SerialBlockReader(Controller c, SerialPort port){
        this.c = c;
        this.port = port;
        xDataBuffer = new byte [GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES];
        xUpdateTypeBuffer = new byte[1];
        yDataBuffer = new byte [GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES];
        yUpdateTypeBuffer = new byte[1];
        byteBuffer = new byte[2];
    }

    private void readIntoBuffer(byte [] buffer) {
        int numRead = port.readBytes(buffer, GlobalConstants.NUM_DATA_BYTES);
        if (numRead != GlobalConstants.NUM_DATA_BYTES) {
            System.err.println("Error reading channel. Read " + String.valueOf(numRead) + " instead of " + String.valueOf(GlobalConstants.NUM_DATA_BYTES));
        }
    }

    private ArrayList<Byte> convertBufferToArrayList(byte [] buffer, int len){
        ArrayList<Byte> ret = new ArrayList<>();
        for (int i =0;i<len;i++){
            ret.add(buffer[i]);
        }
        return ret;
    }

    private void readFrameType(byte [] buffer){
        if(port.isOpen()) {
            port.readBytes(buffer, 1);
        }
    }

    private void readAndUpdateX() {
        readIntoBuffer(xDataBuffer);
        //readUpdateType(xUpdateTypeBuffer);
        c.canvasCaretaker.requestXChannelUpdate(convertBufferToArrayList(xDataBuffer, GlobalConstants.NUM_DATA_BYTES), xUpdateTypeBuffer[0]);
    }

    private void readAndUpdateY() {
        readIntoBuffer(yDataBuffer);
        //readUpdateType(yUpdateTypeBuffer);
        c.canvasCaretaker.requestYChannelUpdate(convertBufferToArrayList(yDataBuffer, GlobalConstants.NUM_DATA_BYTES), yUpdateTypeBuffer[0]);
    }

    private void tickUpdateTime(){
        System.out.println(String.format("Update received. Time from last update: %d", System.currentTimeMillis() - lastUpdateTime));
        lastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (port.isOpen()) {
            port.readBytes(byteBuffer, 1);
            if(byteBuffer[0] == GlobalConstants.START_WORD_LOWER_BYTE) {
                port.readBytes(byteBuffer, 1);
                switch (byteBuffer[0]) {
                    case GlobalConstants.X_START_WORD_UPPER_BYTE:
                        readAndUpdateX();
                        tickUpdateTime();
                        break;
                    case GlobalConstants.Y_START_WORD_UPPER_BYTE:
                        readAndUpdateY();
                        tickUpdateTime();
                        break;
                }
            }
        }
    }
}
