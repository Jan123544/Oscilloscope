package sample;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SerialProtocol {
    public static byte PROTOCOL_START_WORD_LOW_BYTE = (byte)0xef;
    public static byte PROTOCOL_START_WORD_HIGH_BYTE = (byte)0xef;
    public static byte PROTOCOL_STOP_WORD_LOW_BYTE = (byte)0xef;
    public static byte PROTOCOL_STOP_WORD_HIGH_BYTE = (byte)0xcf;
    public static byte PROTOCOL_NEXT_CHANNEL_LOW_BYTE = (byte)0xef;
    public static byte PROTOCOL_NEXT_CHANNEL_HIGH_BYTE = (byte)0xdf;
    public static Integer NUM_SAMPLES = 512;
    public static byte SAMPLE_SIZE_BYTES = 2;

    //X OFFSET SIZE BYTES = 4;
    //Y OFFSET SIZE BYTES = 4;
    //X SENSITIVITY SIZE BYTES = 4;
    //Y SENSITIVITY SIZE BYTES = 4;
    //X TRIG LEVEL SIZE BYTES = 4;
    //Y TRIG LEVEL SIZE BYTES = 4;
    //X TIME PER DIV SIZE BYTES = 4;
    //Y TIME PER DIV SIZE BYTES = 4;
    //TRIG TYPE/X range/Y range/ SIZE BYTES = 4;
    //DoMeasurement SIZE BYTES = 1
    //padding SIZE BYTES = 3
    public static byte OSCI_SETTINGS_SIZE_BYTES = 44;

    public static boolean isLowerStartByte(byte b) {
       return (b == SerialProtocol.PROTOCOL_START_WORD_LOW_BYTE);
    }

    public static boolean isUpperStartByte(byte b){
        return (b == SerialProtocol.PROTOCOL_START_WORD_HIGH_BYTE);
    }

    public static boolean isLowerStopByte(byte b){
        return (b == SerialProtocol.PROTOCOL_STOP_WORD_LOW_BYTE);
    }

    public static boolean isUpperStopByte(byte b){
        return (b == SerialProtocol.PROTOCOL_STOP_WORD_HIGH_BYTE);
    }

    public static boolean isLowerNextChannelByte(byte b){
        return (b == SerialProtocol.PROTOCOL_NEXT_CHANNEL_LOW_BYTE);
    }
    public static boolean isUpperNextChannelByte(byte b){
        return (b == SerialProtocol.PROTOCOL_NEXT_CHANNEL_HIGH_BYTE);
    }

    public static byte[] packageConfig(Controller c, byte doMeasurement) {
        ChannelSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);
        TriggerControlSettings tset = TriggerControlCaretaker.readTriggerControlSettings(c);
        TimeControlSettings tmset = TimeControlsCaretaker.readTimeSettings(c);

        ByteBuffer b = ByteBuffer.allocate(SerialProtocol.OSCI_SETTINGS_SIZE_BYTES);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt( Float.floatToIntBits(cset.xOffset));
        b.putInt(Float.floatToIntBits( cset.xSensitivity));
        b.putInt(Float.floatToIntBits(cset.yOffset) );
        b.putInt(Float.floatToIntBits(cset.ySensitivity) );
        b.putInt(Float.floatToIntBits(tmset.xTimePerDivision));
        b.putInt(Float.floatToIntBits(tmset.yTimePerDivision));
        b.putInt(Float.floatToIntBits( tset.xTriggerLevel));
        b.putInt(Float.floatToIntBits(tset.yTriggerLevel));
        b.putInt(tset.triggerType);
        b.put(cset.xVoltageRange);
        b.put(cset.yVoltageRange);
        b.put(cset.xGraticuleDivisions);
        b.put(cset.yGraticuleDivisions);
        b.put(doMeasurement);
        b.put((byte)0);
        b.put((byte)0);
        b.put((byte)0);
        byte [] res = (byte[])b.flip().array();
        return res;
    }
}
