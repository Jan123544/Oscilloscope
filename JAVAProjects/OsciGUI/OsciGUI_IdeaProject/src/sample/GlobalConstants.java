package sample;

class GlobalConstants {
     final static byte GRATICULE_X_DIVISIONS = 10;
     final static byte GRATICULE_Y_DIVISIONS = 10;

     final static Float CHANNEL_SENSITIVITY_MIN = 0.0f;
     final static Float CHANNEL_SENSITIVITY_MAX = 1.0f;
     final static Float CHANNEL_SENSITIVITY_DEFAULT = 0.5f;

     final static Float CHANNEL_OFFSET_DEFAULT = 0.0f;
     final static Float CHANNEL_OFFSET_MIN = -10.0f;
     final static Float CHANNEL_OFFSET_MAX = 10.0f;

     final static int CHANNEL_RANGE_DEFAULT_INDEX_Y = 0;
     final static int CHANNEL_RANGE_DEFAULT_INDEX_X = 0;

     final static Float TIME_PER_DIVISION_DEFAULT = 0.001f;
     final static Float TIME_PER_DIVISION_DEFAULT_LOG10 = -3f;
     final static Float TIME_PER_DIVISION_MAX = 0.1f;
     final static Float TIME_PER_DIVISION_MAX_LOG10 = -1f;
     final static Float TIME_PER_DIVISION_MIN = 0.0001f;
     final static Float TIME_PER_DIVISION_MIN_LOG10 = -4f;

     final static Float TRIGGER_LEVEL_RESOLUTION_X = 1/4096.0f;
     final static Float TRIGGER_LEVEL_RESOLUTION_Y = 1/256.0f;
     final static Float TRIGGER_LEVEL_DEFAULT_MAX = 5.0f;

     final static Integer INTERNAL_CANVAS_VERTICAL_NORMALISATION = 4096;
     final static Float INTERNAL_SETTINGS_UPDATE_RATE = 1.0f;
     final static Integer INTERNAL_SETTINGS_UPDATE_RATE_RESOLUTION_MILLIS = 10;

     final static Integer TRIGGER_COMMAND_TRANSFORM = 0;
     final static Integer TRIGGER_COMMAND_MEASURE_SINGLE_X = 1;
     final static Integer TRIGGER_COMMAND_MEASURE_SINGLE_Y = 2;
     final static Integer TRIGGER_COMMAND_MEASURE_CONTINUOUS_X = 4;
     final static Integer TRIGGER_COMMAND_MEASURE_CONTINUOUS_Y = 8;
     final static Integer TRIGGER_COMMAND_STOP = 16;

     final static byte CONTINOUS_UPDATE = 1;

     final static byte START_WORD_LOWER_BYTE = (byte)0xfe;
     final static byte X_START_WORD_UPPER_BYTE = (byte)0xfe;
     final static byte Y_START_WORD_UPPER_BYTE = (byte)0xfd;
     final static Integer NUM_SAMPLES = 512;
     final static byte SAMPLE_SIZE_BYTES = 2;
     final static int NUM_DATA_BYTES = NUM_SAMPLES*SAMPLE_SIZE_BYTES;
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
     final static byte OSCI_SETTINGS_SIZE_BYTES = 40;

     final static int X_CHANNEL_ID  = 0;
     final static int Y_CHANNEL_ID  = 1;
}
