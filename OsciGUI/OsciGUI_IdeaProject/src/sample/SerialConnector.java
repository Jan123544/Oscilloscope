package sample;

import com.fazecast.jSerialComm.SerialPort;

public class SerialConnector {

    // Tries to open serial connection, based on settings entered by the user, throws SerialPortConnectionFailedException
    // if the attempt fails.
    public static SerialPort trySerialConnect (Controller c) throws SerialPortConnectionFailedException, NumberFormatException {
        SerialSettings curSettings = SerialControlCaretaker.readSerialSettings(c);
        SerialPort port = SerialPort.getCommPort(curSettings.port);

        port.setNumDataBits(curSettings.numDataBits);
        port.setNumStopBits(curSettings.numStopBits);
        port.setParity(curSettings.parity);
        port.setBaudRate(curSettings.baudRate);
        port.setFlowControl(curSettings.flowControl);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 100);

        if(!port.openPort()){
            throw new SerialPortConnectionFailedException();
        }

        try {
            Thread.sleep(10);
        }catch (InterruptedException e){

        }
        return port;
    }

}