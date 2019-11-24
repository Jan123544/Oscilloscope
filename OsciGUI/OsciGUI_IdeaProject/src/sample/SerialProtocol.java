package sample;

public class SerialProtocol {
    public static byte PROTOCOL_START_WORD_LOW_BYTE = (byte)0xef;
    public static byte PROTOCOL_START_WORD_HIGH_BYTE = (byte)0xef;
    public static byte PROTOCOL_STOP_WORD_LOW_BYTE = (byte)0xef;
    public static byte PROTOCOL_STOP_WORD_HIGH_BYTE = (byte)0xcf;
    public static byte PROTOCOL_NEXT_CHANNEL_LOW_BYTE = (byte)0xef;
    public static byte PROTOCOL_NEXT_CHANNEL_HIGH_BYTE = (byte)0xdf;
    public static Integer DATA_BUFFER_LENGTH = 1024;

    public static boolean isStartByte(byte []b){
        return ((b[0] == SerialProtocol.PROTOCOL_START_WORD_LOW_BYTE) && (b[1] == SerialProtocol.PROTOCOL_START_WORD_HIGH_BYTE));
    }

    public static boolean isStartByteLower(byte b){
        return (b == SerialProtocol.PROTOCOL_START_WORD_LOW_BYTE);
    }

    public static boolean isStartByteHigher(byte b){
        return (b == SerialProtocol.PROTOCOL_START_WORD_HIGH_BYTE);
    }

    public static boolean isStopByte(byte []b){
        return ((b[0] == SerialProtocol.PROTOCOL_STOP_WORD_LOW_BYTE) && (b[1] == SerialProtocol.PROTOCOL_STOP_WORD_HIGH_BYTE));
    }

    public static boolean isNextChannelByte(byte []b){
        return ((b[0] == SerialProtocol.PROTOCOL_NEXT_CHANNEL_LOW_BYTE) && (b[1] == SerialProtocol.PROTOCOL_NEXT_CHANNEL_HIGH_BYTE));
    }

}
