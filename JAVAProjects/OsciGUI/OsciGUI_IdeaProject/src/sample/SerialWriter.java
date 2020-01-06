package sample;

import com.fazecast.jSerialComm.SerialPort;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SerialWriter implements  Runnable{
    private Controller c;
    private long lastUpdateTime;

    public SerialWriter(Controller c){
        this.c = c;
        lastUpdateTime = 0;
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
            byte[] config = packageConfig(c, false);
            numSent = port.writeBytes(config, GlobalConstants.OSCI_SETTINGS_SIZE_BYTES);
            if (numSent == GlobalConstants.OSCI_SETTINGS_SIZE_BYTES) {
                System.err.println("Sent " + String.valueOf(numSent));
            }else{
                System.err.println("Sending failed");
            }
        }
    }

    static byte[] packageConfig(Controller c, boolean isOnlyTransform) {
        ChannelSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);
        TriggerControlSettings tset = TriggerControlCaretaker.readTriggerControlSettings(c);
        TimeControlSettings tmset = TimeControlsCaretaker.readTimeSettings(c);

        ByteBuffer b = ByteBuffer.allocate(GlobalConstants.OSCI_SETTINGS_SIZE_BYTES);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(Float.floatToIntBits(cset.xOffset));
        b.putInt(Float.floatToIntBits(cset.xSensitivity));
        b.putInt(Float.floatToIntBits(cset.yOffset) );
        b.putInt(Float.floatToIntBits(cset.ySensitivity) );
        b.putInt(Float.floatToIntBits(tmset.xTimePerDivision));
        b.putInt(Float.floatToIntBits(tmset.yTimePerDivision));
        b.putInt(Float.floatToIntBits(tset.xTriggerLevel));
        b.putInt(Float.floatToIntBits(tset.yTriggerLevel));
        if(isOnlyTransform) {
            b.putInt(0);
        } else{
            b.putInt(tset.triggerCommand);
        }
        b.put(cset.xVoltageRange);
        b.put(cset.yVoltageRange);
        b.put(cset.xGraticuleDivisions);
        b.put(cset.yGraticuleDivisions);
        byte [] res = (byte[]) b.flip().array();
        return res;
    }

    @Override
    public void run() {
        InternalSettings iSet;
        int numSent = 0;

        while(c.port.isOpen()){
            if(c.autoUpdateCB.isSelected()) {
                iSet = InternalSettingsCaretaker.readInternalSettings(c);
                // Do not send update too often.
                if (System.currentTimeMillis() - lastUpdateTime < (double) 1000 * iSet.settingsUpdateRate) {
                    // Retry after delay
                    try {
                        Thread.sleep(GlobalConstants.INTERNAL_SETTINGS_UPDATE_RATE_RESOLUTION_MILLIS);
                    } catch (InterruptedException e) {
                    }
                    ;
                    continue;
                }

                // Send update frame and update gui state.
                if (c.port.isOpen()) {
                    byte[] config = packageConfig(c, true);
                    numSent = c.port.writeBytes(config, GlobalConstants.OSCI_SETTINGS_SIZE_BYTES);
                    if (numSent == GlobalConstants.OSCI_SETTINGS_SIZE_BYTES) {
                        System.err.println("Sent " + String.valueOf(numSent));
                    }
                }

                // Retry only after delay
                lastUpdateTime = System.currentTimeMillis();
            }
        }
    }
}
