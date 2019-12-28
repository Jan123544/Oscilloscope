package sample;

import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.Locale;

public class GeneralOperations {
    static float putInRange(float v, float min, float max){
        if (v > max){
            return max;
        }else if(v < min){
            return min;
        }
        return v;
    };

    static Integer extractUShort(byte lower, byte upper){
        int tmp;
        tmp = (0x000000ff & (int)lower);
        tmp |= (0x0000ff00 & ((int)upper << 8));
        return tmp;
    }

    public static void setTFSControlsAndCropInRange(TextField tf, Slider sl, float value, float rangeMin, float rangeMax){
        float inRangeValue = GeneralOperations.putInRange(value, rangeMin, rangeMax);
        sl.setValue(inRangeValue);
        tf.setText(String.format(Locale.US, "%g",inRangeValue));
    }
}
