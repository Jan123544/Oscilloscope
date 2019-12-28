package sample;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SerialConnector {

    // Tries to open serial connection, based on settings entered by the user, throws SerialPortConnectionFailedException
    // if the attempt fails.
    public static SerialPort trySerialConnect (Controller c) throws SerialPortConnectionFailedException, NumberFormatException {
        SerialSettings curSettings = SerialControlCaretaker.readSerialSettings(c);
        SerialPort port = SerialPort.getCommPort(curSettings.port);

        c.serialConnectPB.setId("blue-pBar");

        port.setNumDataBits(curSettings.numDataBits);
        c.serialConnectPB.setProgress(10);
        port.setNumStopBits(curSettings.numStopBits);
        c.serialConnectPB.setProgress(20);
        port.setParity(curSettings.parity);
        c.serialConnectPB.setProgress(30);
        port.setBaudRate(curSettings.baudRate);
        c.serialConnectPB.setProgress(50);
        port.setFlowControl(curSettings.flowControl);
        c.serialConnectPB.setProgress(80);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        c.serialConnectPB.setProgress(100);

        if(!port.openPort()){
            c.serialConnectPB.setId("red-pBar");
            throw new SerialPortConnectionFailedException();
        }
        c.serialConnectPB.setId("green-pBar");
        return port;
    }

}
