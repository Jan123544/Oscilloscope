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

    static private void readDataIntoBuffer(byte [] buffer, SerialPort port) {
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

    private void readAndUpdateX() {
        readDataIntoBuffer(xDataBuffer, port);
        //readUpdateType(xUpdateTypeBuffer);
        c.canvasCaretaker.requestXChannelUpdate(convertBufferToArrayList(xDataBuffer, GlobalConstants.NUM_DATA_BYTES), xUpdateTypeBuffer[0]);
    }

    private void readAndUpdateY() {
        readDataIntoBuffer(yDataBuffer, port);
        //readUpdateType(yUpdateTypeBuffer);
        c.canvasCaretaker.requestYChannelUpdate(convertBufferToArrayList(yDataBuffer, GlobalConstants.NUM_DATA_BYTES), yUpdateTypeBuffer[0]);
    }

    private void tickUpdateTime(){
        System.out.println(String.format("Update received. Time from last update: %d", System.currentTimeMillis() - lastUpdateTime));
        lastUpdateTime = System.currentTimeMillis();
    }

    static OsciDataFrame catchPong(SerialPort port, int bytesLookup){
        byte [] opcode = sniffPongOpcode(port, bytesLookup);
        if(opcode[0] == 0 && opcode[1] == 0){
            // SniffingFailed return empty dataframe
            return new OsciDataFrame();
        }
        OsciDataFrame df = new OsciDataFrame();
        df.opcode = opcode;
        readDataIntoBuffer(df.data, port);
        return df;
    }

    static private byte[] sniffPongOpcode(SerialPort port, int bytesLookup){
        byte [] opcodeBuffer = new byte[2];
        byte [] byteBuffer = new byte[2];

        int numLookup = 0;
        int numRead;

        while(numLookup < bytesLookup){
            numRead = port.readBytes(byteBuffer,1);
            if(numRead != 1) { return opcodeBuffer; } // Error or timeout.
            if(byteBuffer[0] == Opcodes.RESPONSE_LOWER && byteBuffer[1] == Opcodes.RESPONSE_HIGHER){
                opcodeBuffer[0] = byteBuffer[0];
                opcodeBuffer[1] = byteBuffer[1];
                return opcodeBuffer;
            }
            byteBuffer[1] = byteBuffer[0];
            numLookup++;
        }

        return opcodeBuffer;
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
