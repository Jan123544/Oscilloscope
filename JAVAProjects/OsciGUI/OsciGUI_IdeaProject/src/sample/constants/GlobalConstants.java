package sample.constants;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class GlobalConstants {
     public final static byte GRATICULE_X_DIVISIONS = 10;
     public final static byte GRATICULE_Y_DIVISIONS = 10;

     public final static Float CHANNEL_SENSITIVITY_MIN = 0.01f;
     public final static Float CHANNEL_SENSITIVITY_MAX = 2.0f;
     public final static Float CHANNEL_SENSITIVITY_DEFAULT = 0.5f;

     public final static Float CHANNEL_OFFSET_DEFAULT = 0.0f;
     public final static Float CHANNEL_OFFSET_MIN = -20.0f;
     public final static Float CHANNEL_OFFSET_MAX = 20.0f;

     public final static float CHANNEL_THRESHOLD_DEFAULT = 0;

     public final static Float TIME_PER_DIVISION_DEFAULT = 0.01f;
     public final static Float TIME_PER_DIVISION_DEFAULT_LOG10 = -2f;
     public final static Float TIME_PER_DIVISION_MAX = 0.1f;
     public final static Float TIME_PER_DIVISION_MAX_LOG10 = -1f;
     public final static Float TIME_PER_DIVISION_MIN = 0.0001f;
     public final static Float TIME_PER_DIVISION_MIN_LOG10 = -4f;

     public final static int TIMER_HOLD_OFF_DEFAULT = 1000;
     public final static int TIMER_HOLD_OFF_MIN = 0;
     public final static int TIMER_HOLD_OFF_MAX = 10000;

     public final static Float TRIGGER_LEVEL_DEFAULT_MAX = 5.0f;

     public final static Integer INTERNAL_CANVAS_VERTICAL_NORMALISATION = 4096;
     public final static Float INTERNAL_SETTINGS_UPDATE_RATE = 0.1f;
     public final static Integer INTERNAL_SETTINGS_UPDATE_RATE_RESOLUTION_MILLIS = 10;

     public final static Integer TRIGGER_COMMAND_TRANSFORM = 0;
     public final static Integer TRIGGER_COMMAND_MEASURE_SINGLE_X = 1;
     public final static Integer TRIGGER_COMMAND_MEASURE_SINGLE_Y = 2;
     public final static Integer TRIGGER_COMMAND_MEASURE_CONTINUOUS_X = 4;
     public final static Integer TRIGGER_COMMAND_MEASURE_CONTINUOUS_Y = 8;
     public final static Integer TRIGGER_COMMAND_STOP = 16;
     public final static Integer TRIGGER_COMMAND_PING = 32;

     public final static byte CONTINOUS_UPDATE = 1;
     public final static byte START_WORD_LOWER_BYTE = (byte)0xfe;
     public final static byte X_START_WORD_UPPER_BYTE = (byte)0xfe;
     public final static byte Y_START_WORD_UPPER_BYTE = (byte)0xfd;

     public final static Integer NUM_SAMPLES = 512;
     public final static byte SAMPLE_SIZE_BYTES = 2;
     public final static int NUM_DATA_BYTES = NUM_SAMPLES*SAMPLE_SIZE_BYTES;
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
      //x timer Hold-off 1B
      //y timer Hold-off 1B
     public final static byte OSCI_SETTINGS_SIZE_BYTES = 48;

     public final static Paint X_OUT_OF_RANGE_COLOR = Color.MAGENTA;
     public final static Paint Y_OUT_OF_RANGE_COLOR = Color.CYAN;
     public final static Paint X_NORMAL_COLOR = Color.RED;
     public final static Paint Y_NORMAL_COLOR = Color.YELLOW;
}
