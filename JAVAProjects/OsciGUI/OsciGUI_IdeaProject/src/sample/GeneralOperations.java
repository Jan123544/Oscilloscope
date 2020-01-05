package sample;

import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Collections;
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

    static ArrayList<Double> convertUShort(ArrayList<Byte> buffer){
        if(buffer.size() %2 != 0){
            throw new UnsupportedOperationException("convertUShort needs even number of bytes on input");
        }
        ArrayList<Double> ret = new ArrayList<>();
        for(int i=0;i<buffer.size();i+=2){
            ret.add(extractUShort(buffer.get(i), buffer.get(i+1)).doubleValue());
        }
        return ret;
    }

    public static void setTFSControlsAndCropInRange(TextField tf, Slider sl, float value, float rangeMin, float rangeMax){
        float inRangeValue = GeneralOperations.putInRange(value, rangeMin, rangeMax);
        sl.setValue(inRangeValue);
        tf.setText(String.format(Locale.US, "%g",inRangeValue));
    }

    static float bufferAverage(byte [] buffer, int bufflen){
        float sum = 0;
        for(int i = 0;i<bufflen;i+=2){
            sum += GeneralOperations.extractUShort(buffer[i], buffer[i+1]);
        }
        return sum/bufflen;
    }
}
