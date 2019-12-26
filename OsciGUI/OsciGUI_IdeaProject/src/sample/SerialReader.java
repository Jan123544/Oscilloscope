package sample;

import com.fazecast.jSerialComm.*;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.util.*;

import java.io.InputStream;
import java.io.OutputStream;

public class SerialReader implements Runnable{
    // State machine
    private byte WAITING_FOR_LOWER_START_BYTE = 0;
    private byte GOT_LOWER_START_BYTE = 1;
    private byte READING_CHANNEL_X = 2;
    private byte GOT_LOWER_NEXT_CHANNEL_BYTE = 3;
    private byte READING_CHANNEL_Y = 4;
    private byte GOT_LOWER_STOP_BYTE = 5;
    private byte state = WAITING_FOR_LOWER_START_BYTE;

    // Serial data
    private SerialPort port;
    private byte[] xDataBuffer;
    private byte[] yDataBuffer;

    private int xBufferOffset =  0;
    private int yBufferOffset =  0;
    private byte[] byteBuffer;

    // Scheduling gui updates
    private long lastUpdateTime;
    private Controller c;

    public SerialReader(Controller c, SerialPort port){
        this.c = c;
        this.port = port;
        xDataBuffer = new byte [SerialProtocol.SAMPLE_SIZE_BYTES*SerialProtocol.NUM_SAMPLES];
        yDataBuffer = new byte [SerialProtocol.SAMPLE_SIZE_BYTES*SerialProtocol.NUM_SAMPLES];
        byteBuffer = new byte[2];
    }

    @Override
    public void run() {
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
                // Reset offsets
                xBufferOffset = 0;
                yBufferOffset = 0;
                c.canvasCaretaker.requestClearCanvas(c);

                state = READING_CHANNEL_X;
            }

            if (state == READING_CHANNEL_X) {
                port.readBytes(byteBuffer, 1);
                if (SerialProtocol.isLowerNextChannelByte(byteBuffer[0])) {
                    state = GOT_LOWER_NEXT_CHANNEL_BYTE;
                    continue;
                }

                // If not a next channel lower byte, copy into data buffer
                xDataBuffer[xBufferOffset] = byteBuffer[0];
                xBufferOffset++;
                drawXDataPointIfArrived();
                continue;
            }

            if (state == GOT_LOWER_NEXT_CHANNEL_BYTE){
                port.readBytes(byteBuffer, 1);
                if (SerialProtocol.isUpperNextChannelByte(byteBuffer[0])){
                    state = READING_CHANNEL_Y;
                    continue;
                }

                // If not a next channel upper byte, then the previous one was just also a normal data byte and must be copied
                xDataBuffer[xBufferOffset] = SerialProtocol.PROTOCOL_NEXT_CHANNEL_LOW_BYTE; // Was the data byte
                xBufferOffset++;
                drawXDataPointIfArrived();
                xDataBuffer[xBufferOffset] = byteBuffer[0];
                xBufferOffset++;
                drawXDataPointIfArrived();
                state = READING_CHANNEL_X;
                continue;
            }

            if (state == READING_CHANNEL_Y){
                port.readBytes(byteBuffer, 1);
                if (SerialProtocol.isLowerStopByte(byteBuffer[0])) {
                    state = GOT_LOWER_STOP_BYTE;
                    continue;
                }

                // If not a stop byte, read it into the data buffer
                yDataBuffer[yBufferOffset] = byteBuffer[0];
                yBufferOffset++;
                drawYDataPointIfArrived();
                continue;
            }

            if (state == GOT_LOWER_STOP_BYTE){
                port.readBytes(byteBuffer, 1);
                if (SerialProtocol.isUpperStopByte(byteBuffer[0])) {

                    c.canvasCaretaker.requestCanvasUpdate(c, xDataBuffer, yDataBuffer);
                    state = WAITING_FOR_LOWER_START_BYTE;
                    continue;
                }

                // If not a stop byte, the previous byte was also a data byte
                yDataBuffer[yBufferOffset] = SerialProtocol.PROTOCOL_START_WORD_LOW_BYTE;
                yBufferOffset++;
                drawYDataPointIfArrived();
                yDataBuffer[yBufferOffset] = byteBuffer[0];
                yBufferOffset++;
                drawYDataPointIfArrived();
                state = READING_CHANNEL_Y;
                continue;
            }
        }
    }

    private void drawXDataPointIfArrived() {
        // If read a new dataPoint, display it
        if ((xBufferOffset > 0) && (xBufferOffset % SerialProtocol.SAMPLE_SIZE_BYTES == 0)){
            c.canvasCaretaker.requestXDataPointWrite(c, xDataBuffer, xBufferOffset - SerialProtocol.SAMPLE_SIZE_BYTES, (xBufferOffset-SerialProtocol.SAMPLE_SIZE_BYTES)*c.canvasCaretaker.unitTime(c), c.canvasCaretaker.readCanvasSettings(c));
        }
    }

    private void drawYDataPointIfArrived() {
        // If read a new dataPoint, display it
        if ((yBufferOffset > 0) && (yBufferOffset % SerialProtocol.SAMPLE_SIZE_BYTES == 0)){
            c.canvasCaretaker.requestYDataPointWrite(c, yDataBuffer, yBufferOffset - SerialProtocol.SAMPLE_SIZE_BYTES, (yBufferOffset-SerialProtocol.SAMPLE_SIZE_BYTES)*c.canvasCaretaker.unitTime(c), c.canvasCaretaker.readCanvasSettings(c));
        }
    }
}
