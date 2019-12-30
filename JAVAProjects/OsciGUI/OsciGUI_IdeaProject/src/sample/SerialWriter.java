package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;

import java.util.concurrent.Semaphore;

public class SerialWriter implements  Runnable{
    private Controller c;
    private SerialPort port;
    private long lastUpdateTime;
    private boolean shouldSend;
    //Semaphore stopLock = new  Semaphore(1);

    public SerialWriter(Controller c){
        this.c = c;
        lastUpdateTime = 0;
        shouldSend = false;
    }

    public static void launch(SerialWriter writer){
        Thread th = new Thread(writer);
        th.setDaemon(true);
        th.start();
    }

    public static void sendOnce(Controller c, SerialPort port){
        int numSent = 0;
        // Send update frame and update gui state.
        if (port.isOpen()) {
            byte[] config = SerialProtocol.packageConfig(c, c.generalMeasurementSettingsCaretaker.readGeneralMeasurementSettings().doMeasurement);
            numSent = port.writeBytes(config, SerialProtocol.OSCI_SETTINGS_SIZE_BYTES);
            if (numSent == SerialProtocol.OSCI_SETTINGS_SIZE_BYTES) {
                System.err.println("Sent " + String.valueOf(numSent));
            }else{
                System.err.println("Sending failed");
            }
        }
    }

    boolean isStopped(){
        return !shouldSend;
    }

    void waitUntilStopped(){
        while(shouldSend);
    }

    void stopSending(){
        shouldSend = false;
    }

    void startSending(SerialPort port){
        this.port = port;
        shouldSend = true;
    }

    @Override
    public void run() {
        InternalSettings iSet;
        int numSent = 0;
        while(true){
            if(this.shouldSend){
                iSet = InternalSettingsCaretaker.readInternalSettings(c);
                // Do not send update too often.
                if (System.currentTimeMillis() - lastUpdateTime < (double) 1000 * iSet.settingsUpdateRate) {
                    // Retry after delay
                    try { Thread.sleep(GlobalConstants.INTERNAL_SETTINGS_UPDATE_RATE_RESOLUTION_MILLIS); } catch (InterruptedException e){};
                    continue;
                }

                // Send update frame and update gui state.
                if (port.isOpen()) {
                    byte[] config = SerialProtocol.packageConfig(c, GlobalConstants.OSCI_SETTINGS_DOMEASUREMENT_NO);
                    numSent = port.writeBytes(config, SerialProtocol.OSCI_SETTINGS_SIZE_BYTES);
                    if (numSent == SerialProtocol.OSCI_SETTINGS_SIZE_BYTES) {
                        System.err.println("Sent " + String.valueOf(numSent));
                    }
                }

                // Retry only after delay
                lastUpdateTime = System.currentTimeMillis();
            }
        }
    }
}
