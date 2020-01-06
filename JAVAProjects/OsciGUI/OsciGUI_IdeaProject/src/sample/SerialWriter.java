package sample;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InvalidObjectException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SerialWriter implements  Runnable{
    private Controller c;
    private long lastUpdateTime;
    private boolean shouldStop;

    public SerialWriter(Controller c){
        this.c = c;
        lastUpdateTime = 0;
    }

    public static void launch(SerialWriter writer){
        Thread th = new Thread(writer);
        th.setDaemon(true);
        th.start();
    }

    public static void sendOnce(Controller c, SerialPort port, PacketType packType){
        int numSent = 0;
        // Send update frame and update gui state.
        if (port.isOpen()) {
            byte[] config = packageConfig(c, packType);
            numSent = port.writeBytes(config, GlobalConstants.OSCI_SETTINGS_SIZE_BYTES);
            if (numSent == GlobalConstants.OSCI_SETTINGS_SIZE_BYTES) {
                System.err.println("Sent " + String.valueOf(numSent));
            }else{
                System.err.println("Sending failed");
            }
        }
    }

    static byte[] packageConfig(Controller c, PacketType packType) {

        ByteBuffer b = ByteBuffer.allocate(GlobalConstants.OSCI_SETTINGS_SIZE_BYTES);
        b.order(ByteOrder.LITTLE_ENDIAN);
        ChannelSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);
        TriggerControlSettings tset = TriggerControlCaretaker.readTriggerControlSettings(c);
        TimeControlSettings tmset = TimeControlsCaretaker.readTimeSettings(c);

        b.putInt(Float.floatToIntBits(cset.xOffset));
        b.putInt(Float.floatToIntBits(cset.xSensitivity));
        b.putInt(Float.floatToIntBits(cset.yOffset));
        b.putInt(Float.floatToIntBits(cset.ySensitivity));
        b.putInt(Float.floatToIntBits(tmset.xTimePerDivision));
        b.putInt(Float.floatToIntBits(tmset.yTimePerDivision));
        b.putInt(Float.floatToIntBits(tset.xTriggerLevel));
        b.putInt(Float.floatToIntBits(tset.yTriggerLevel));
        switch (packType) {
            case TRANSFORM:
                b.putInt(GlobalConstants.TRIGGER_COMMAND_TRANSFORM);
                break;
            case EXIT:
                b.putInt(GlobalConstants.TRIGGER_COMMAND_STOP);
                break;
            case NORMAL:
                b.putInt(tset.triggerCommand);
                break;
            case PING:
                b.putInt(GlobalConstants.TRIGGER_COMMAND_PING);
                break;
            default:
                System.err.println("invalid pack type");
        }
        b.put(cset.xVoltageRange);
        b.put(cset.yVoltageRange);
        b.put(cset.xGraticuleDivisions);
        b.put(cset.yGraticuleDivisions);
        b.putInt(tmset.xTimerHoldOff);
        b.putInt(tmset.yTimerHoldOff);
        byte [] res = (byte[]) b.flip().array();
        return res;
    }

    public void forceStop(){
        shouldStop = true;
    }

    @Override
    public void run() {
        InternalSettings iSet;
        int numSent = 0;

        shouldStop = false;
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
                    byte[] config = packageConfig(c, PacketType.TRANSFORM);
                    numSent = c.port.writeBytes(config, GlobalConstants.OSCI_SETTINGS_SIZE_BYTES);
                    if (numSent == GlobalConstants.OSCI_SETTINGS_SIZE_BYTES) {
                        System.err.println("Sent " + String.valueOf(numSent));
                    }
                }

                c.canvasCaretaker.refreshLabels();
                c.canvasCaretaker.updateThresholdLines();

                // Retry only after delay
                lastUpdateTime = System.currentTimeMillis();
            }
            if(shouldStop) {
                return;
            }
        }
    }
}
