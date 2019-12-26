package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;

public class SerialWriter implements  Runnable{
    Controller c;
    SerialPort port;
    long lastUpdateTime;

    public SerialWriter(Controller c, SerialPort port){
        this.c = c;
        this.port = port;
        lastUpdateTime = 0;
    }

    @Override
    public void run() {
        int numSent = 0;

        InternalSettings iSet;
        do{
            iSet = InternalSettingsCaretaker.readInternalSettings(c);
            // Do not send update too often.
            if (System.currentTimeMillis() - lastUpdateTime < (double) 1000 * iSet.settingsUpdateRate) {
                // Retry after delay
                try { Thread.sleep(GlobalConstants.INTERNAL_SETTINGS_UPDATE_RATE_RESOLUTION_MILLIS); } catch (InterruptedException e){};
                continue;
            }

            // Send update frame and update gui state.
            if (port.isOpen()) {
                byte[] config = SerialProtocol.packageConfig(c);
                numSent = port.writeBytes(config, SerialProtocol.OSCI_SETTINGS_SIZE_BYTES);
                if (numSent == SerialProtocol.OSCI_SETTINGS_SIZE_BYTES) {
                    System.err.println("Sent " + String.valueOf(numSent));
                    //Platform.runLater(() -> {
                    //    c.validSettingsL.setText("New settings sent.");
                    //});
                } else {
                    //Platform.runLater(() -> {
                    //    c.validSettingsL.setText("Failed to send settings.");
                    //});
                }

            } else {
                //Platform.runLater(() -> {
                //    c.validSettingsL.setText("Not connected!");
                //});
            }

            // Retry only after delay
            lastUpdateTime = System.currentTimeMillis();
        }while (c.autoUpdateCB.isSelected());
    }
}
