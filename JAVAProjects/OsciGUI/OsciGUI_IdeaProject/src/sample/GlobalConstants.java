package sample;

class GlobalConstants {
     static byte GRATICULE_X_DIVISIONS = 10;
     static byte GRATICULE_Y_DIVISIONS = 10;

     static Float CHANNEL_SENSITIVITY_MIN = 0.0f;
     static Float CHANNEL_SENSITIVITY_MAX = 1.0f;
     static Float CHANNEL_SENSITIVITY_DEFAULT = 0.5f;

     static Float CHANNEL_OFFSET_DEFAULT = 0.0f;
     static Float CHANNEL_OFFSET_MIN = -10.0f;
     static Float CHANNEL_OFFSET_MAX = 10.0f;

     static int CHANNEL_RANGE_DEFAULT_INDEX_Y = 0;
     static int CHANNEL_RANGE_DEFAULT_INDEX_X = 0;

     static Float TIME_PER_DIVISION_DEFAULT = 0.001f;
     static Float TIME_PER_DIVISION_DEFAULT_LOG10 = -3f;
     static Float TIME_PER_DIVISION_MAX = 0.1f;
     static Float TIME_PER_DIVISION_MAX_LOG10 = -1f;
     static Float TIME_PER_DIVISION_MIN = 0.0001f;
     static Float TIME_PER_DIVISION_MIN_LOG10 = -4f;

     static Float TRIGGER_LEVEL_RESOLUTION_X = 1/4096.0f;
     static Float TRIGGER_LEVEL_RESOLUTION_Y = 1/256.0f;
     static Float TRIGGER_LEVEL_DEFAULT_MAX = 5.0f;

     static Integer INTERNAL_CANVAS_VERTICAL_NORMALISATION = 4096;
     static Float INTERNAL_SETTINGS_UPDATE_RATE = 1.0f;
     static Integer INTERNAL_SETTINGS_UPDATE_RATE_RESOLUTION_MILLIS = 10;

     static Integer TRIGGER_COMMAND_TRANSFORM = 0;
     static Integer TRIGGER_COMMAND_MEASURE_SINGLE_X = 1;
     static Integer TRIGGER_COMMAND_MEASURE_SINGLE_Y = 2;
     static Integer TRIGGER_COMMAND_MEASURE_CONTINUOUS_X = 4;
     static Integer TRIGGER_COMMAND_MEASURE_CONTINUOUS_Y = 8;
     static Integer TRIGGER_COMMAND_STOP = 16;

     static Integer NUM_SAMPLES = 512;
}
