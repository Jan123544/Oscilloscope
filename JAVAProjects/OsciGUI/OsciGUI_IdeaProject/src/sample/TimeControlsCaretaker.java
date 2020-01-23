package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import sample.constants.Channel;
import sample.constants.GlobalConstants;
import sample.exceptions.BadInputException;
import sample.settings.TimeControlSettings;

import java.util.Locale;

public class TimeControlsCaretaker {

    static private void timePerDivisionTFCallbackInsides(Controller c, TextField tf, Slider s){
        // Put read value into range, update slider and also update TF with value in range.
        float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(tf.getText()), GlobalConstants.TIME_PER_DIVISION_MIN, GlobalConstants.TIME_PER_DIVISION_MAX);
        s.setValue(Math.log10(inRangeValue));
        tf.setText(String.format(Locale.US, "%g", inRangeValue));
        if(c.autoMeasureCB.isSelected()) {
            c.serialWriter.setUpdateNeeded(true);
        }
    }

    static private void initTimePerDivisionTF(Controller c, TextField tf, Slider s, Channel channel){
        tf.setText(String.valueOf(GlobalConstants.TIME_PER_DIVISION_DEFAULT));

        tf.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                timePerDivisionTFCallbackInsides(c,tf,s);
            }
        });
        tf.focusedProperty().addListener( (observableValue, aBoolean, t1) -> {
            timePerDivisionTFCallbackInsides(c,tf,s);
        });
    }

    static private void holdOffTFCallbackInsides(Controller c, TextField  tf){
        // Put read value into range, update slider and also update TF with value in range.
        float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(tf.getText()), GlobalConstants.TIMER_HOLD_OFF_MIN, GlobalConstants.TIMER_HOLD_OFF_MAX);
        tf.setText(String.format(Locale.US, "%d", (int)inRangeValue));
        if(c.autoMeasureCB.isSelected()) {
            c.serialWriter.setUpdateNeeded(true);
        }
    }

    static private void initHoldOffTF(Controller c, TextField tf, Channel channel){
        tf.setText(String.valueOf(GlobalConstants.TIMER_HOLD_OFF_DEFAULT));
        tf.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                holdOffTFCallbackInsides(c,tf);
            }
        });
        tf.focusedProperty().addListener( (observableValue, aBoolean, t1) -> {
            holdOffTFCallbackInsides(c,tf);
        });
    }

    static void initTimePerDivisionS(Controller c, TextField tf, Slider s, Channel channel){
        s.setMin(GlobalConstants.TIME_PER_DIVISION_MIN_LOG10);
        s.setMax(GlobalConstants.TIME_PER_DIVISION_MAX_LOG10);
        s.setValue(GlobalConstants.TIME_PER_DIVISION_DEFAULT_LOG10);
        s.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                tf.setText(String.format(Locale.US, "%.4g", Math.pow(10, t1.doubleValue())));
                if(c.autoMeasureCB.isSelected()) {
                    c.serialWriter.setUpdateNeeded(true);
                }
            }
        });

    }

    public static void initTimeControlSettings(Controller c){
        initTimePerDivisionTF(c, c.xTimePerDivisionTF, c.xTimePerDivisionS, Channel.CHANNEL_X);
        initHoldOffTF(c, c.xTimerHoldOffTF, Channel.CHANNEL_X);
        initTimePerDivisionS(c, c.xTimePerDivisionTF, c.xTimePerDivisionS, Channel.CHANNEL_X);

        initTimePerDivisionTF(c, c.yTimePerDivisionTF, c.yTimePerDivisionS, Channel.CHANNEL_Y);
        initHoldOffTF(c, c.yTimerHoldOffTF, Channel.CHANNEL_Y);
        initTimePerDivisionS(c, c.yTimePerDivisionTF, c.yTimePerDivisionS, Channel.CHANNEL_Y);
    }

    public static TimeControlSettings readTimeSettings(Controller c) throws BadInputException {
        TimeControlSettings set = new TimeControlSettings();
        set.xTimePerDivision = Float.parseFloat(c.xTimePerDivisionTF.getText());
        set.yTimePerDivision = Float.parseFloat(c.yTimePerDivisionTF.getText());
        set.xTimerHoldOff = (int)Float.parseFloat(c.xTimerHoldOffTF.getText());
        set.yTimerHoldOff = (int)Float.parseFloat(c.yTimerHoldOffTF.getText());
        return set;
    }
}
