package sample;

import com.fazecast.jSerialComm.SerialPort;

import javax.sql.rowset.serial.SerialRef;
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
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 2000, 0);

        if(!port.openPort()){
            c.serialConnectPB.setId("red-pBar");
            throw new SerialPortConnectionFailedException();
        }

        // Ping the MCU, if it doesn't respond, shutdown connection.
        SerialWriter.sendOnce(c, port, PacketType.PING);
        OsciDataFrame df = SerialBlockReader.catchPong(port, 10000);

        // Reset to blocking state.
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        if(df.opcode[0] == 0 && df.opcode[1] == 0){
            c.serialConnectPB.setId("red-pBar");
            port.closePort();
            throw new PongNotReceivedException();
        }

        c.serialConnectPB.setProgress(100);

        c.serialConnectPB.setId("green-pBar");
        return port;
    }

}
