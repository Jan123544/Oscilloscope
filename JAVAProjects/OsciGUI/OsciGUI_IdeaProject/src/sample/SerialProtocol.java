package sample;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class SerialProtocol {
    static byte PROTOCOL_START_WORD_LOW_BYTE = (byte)0xef;
    static byte PROTOCOL_START_WORD_HIGH_BYTE = (byte)0xef;
    static byte PROTOCOL_STOP_WORD_LOW_BYTE = (byte)0xef;
    static byte PROTOCOL_STOP_WORD_HIGH_BYTE = (byte)0xcf;
    static byte PROTOCOL_NEXT_CHANNEL_LOW_BYTE = (byte)0xef;
    static byte PROTOCOL_NEXT_CHANNEL_HIGH_BYTE = (byte)0xdf;
    static Integer NUM_SAMPLES = 512;
    static byte SAMPLE_SIZE_BYTES = 2;

    //X OFFSET 4B
    //X SENSITIVITY 4B
    //Y OFFSET 4B
    //Y SENSITIVITY 4B
    //X TIME PER DIV 4B
    //Y TIME PER DIV 4B
    //X TRIG TH 4B
    //Y TRIG TH 4B
    //TRIG CMD 4B
    //X RANGE 1B
    //Y RANGE 1B
    //X GRATICULE DIVISIONS 1B
    //Y GRATICULE DIVISIONS 1B
    static byte OSCI_SETTINGS_SIZE_BYTES = 40;

    static boolean isLowerStartByte(byte b) {
       return (b == SerialProtocol.PROTOCOL_START_WORD_LOW_BYTE);
    }

    static boolean isUpperStartByte(byte b){
        return (b == SerialProtocol.PROTOCOL_START_WORD_HIGH_BYTE);
    }

    static boolean isLowerStopByte(byte b){
        return (b == SerialProtocol.PROTOCOL_STOP_WORD_LOW_BYTE);
    }

    static boolean isUpperStopByte(byte b){
        return (b == SerialProtocol.PROTOCOL_STOP_WORD_HIGH_BYTE);
    }

    static boolean isLowerNextChannelByte(byte b){
        return (b == SerialProtocol.PROTOCOL_NEXT_CHANNEL_LOW_BYTE);
    }
    static boolean isUpperNextChannelByte(byte b){
        return (b == SerialProtocol.PROTOCOL_NEXT_CHANNEL_HIGH_BYTE);
    }

    static byte[] packageConfig(Controller c) {
        ChannelSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);
        TriggerControlSettings tset = TriggerControlCaretaker.readTriggerControlSettings(c);
        TimeControlSettings tmset = TimeControlsCaretaker.readTimeSettings(c);

        ByteBuffer b = ByteBuffer.allocate(SerialProtocol.OSCI_SETTINGS_SIZE_BYTES);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(Float.floatToIntBits(cset.xOffset));
        b.putInt(Float.floatToIntBits(cset.xSensitivity));
        b.putInt(Float.floatToIntBits(cset.yOffset) );
        b.putInt(Float.floatToIntBits(cset.ySensitivity) );
        b.putInt(Float.floatToIntBits(tmset.xTimePerDivision));
        b.putInt(Float.floatToIntBits(tmset.yTimePerDivision));
        b.putInt(Float.floatToIntBits(tset.xTriggerLevel));
        b.putInt(Float.floatToIntBits(tset.yTriggerLevel));
        b.putInt(tset.triggerCommand);
        b.put(cset.xVoltageRange);
        b.put(cset.yVoltageRange);
        b.put(cset.xGraticuleDivisions);
        b.put(cset.yGraticuleDivisions);
        byte [] res = (byte[]) b.flip().array();
        return res;
    }
}
