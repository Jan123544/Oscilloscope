package sample;

public class GlobalConstants {
    public static byte GRATICULE_X_DIVISIONS = 10;
    public static byte GRATICULE_Y_DIVISIONS = 10;

    public static Float CHANNEL_SENSITIVITY_MIN = 0.0f;
    public static Float CHANNEL_SENSITIVITY_MAX = 1.0f;
    public static Float CHANNEL_SENSITIVITY_DEFAULT = 0.5f;

    public static Float CHANNEL_OFFSET_DEFAULT = 0.0f;
    public static Float CHANNEL_OFFSET_MIN = -10.0f;
    public static Float CHANNEL_OFFSET_MAX = 10.0f;

    public static Float TIME_PER_DIVISION_DEFAULT = 0.001f;
    public static Float TIME_PER_DIVISION_DEFAULT_LOG10 = -3f;
    public static Float TIME_PER_DIVISION_MAX = 0.1f;
    public static Float TIME_PER_DIVISION_MAX_LOG10 = -1f;
    public static Float TIME_PER_DIVISION_MIN = 0.0001f;
    public static Float TIME_PER_DIVISION_MIN_LOG10 = -4f;

    public static byte TRIGGER_TYPE_CONTINUOUS = 0;
    public static byte TRIGGER_TYPE_ONE_TIME = 1;
    public static Float TRIGGER_LEVEL_DEFAULT = 2.0f;
    public static Float TRIGGER_LEVEL_DEFAULT_MIN = 0.0f;
    public static Float TRIGGER_LEVEL_DEFAULT_MAX = 5.0f;

    public static Integer INTERNAL_CANVAS_VERTICAL_NORMALISATION = 4096;
    public static Float INTERNAL_SETTINGS_UPDATE_RATE = 1.0f;
    public static Integer INTERNAL_SETTINGS_UPDATE_RATE_RESOLUTION_MILLIS = 10;

    public static byte OSCI_SETTINGS_DOMEASUREMENT_NO = 0;
    public static byte OSCI_SETTINGS_DOMEASUREMENT_X = 1;
    public static byte OSCI_SETTINGS_DOMEASUREMENT_Y = 2;
}
