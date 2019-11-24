package sample;

public class SerialProtocol {
    public static Short PROTOCOL_START_BYTE = Short.parseShort("0xefef", 16);
    public static Short PROTOCOL_STOP_BYTE = Short.parseShort("0xefef", 16);
    public static Short PROTOCOL_NEXT_CHANNEL = Short.parseShort("0xdfef", 16);
    public static Integer DATA_BUFFER_LENGTH = 1024;

    public static boolean isStartByte(byte b){
        return (b == SerialProtocol.PROTOCOL_START_BYTE);
    }

    public static boolean isStopByte(byte b){
        return (b == SerialProtocol.PROTOCOL_STOP_BYTE);
    }

    public static boolean isNextChannelByte(byte b){
        return (b == SerialProtocol.PROTOCOL_NEXT_CHANNEL);
    }
}
