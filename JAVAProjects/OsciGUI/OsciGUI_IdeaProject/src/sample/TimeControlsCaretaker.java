package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;

import java.util.Locale;

public class TimeControlsCaretaker {
    public static void initTimeControlSettings(Controller c){
        c.xTimePerDivisionTF.setText(String.valueOf(GlobalConstants.TIME_PER_DIVISION_DEFAULT));
        c.yTimePerDivisionTF.setText(String.valueOf(GlobalConstants.TIME_PER_DIVISION_DEFAULT));
        c.xTimerHoldOffTF.setText(String.valueOf(GlobalConstants.X_TIMER_HOLD_OFF_DEFAULT));
        c.yTimerHoldOffTF.setText(String.valueOf(GlobalConstants.Y_TIMER_HOLD_OFF_DEFAULT));
        c.xTimerHoldOffTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.xTimerHoldOffTF.getText()), GlobalConstants.X_TIMER_HOLD_OFF_MIN, GlobalConstants.X_TIMER_HOLD_OFF_MAX);
                c.xTimerHoldOffTF.setText(String.format(Locale.US, "%g", inRangeValue));
            }
        });
        c.xTimerHoldOffTF.focusedProperty().addListener( (observableValue, aBoolean, t1) -> {
            // Put read value into range, update slider and also update TF with value in range.
            float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.xTimerHoldOffTF.getText()), GlobalConstants.X_TIMER_HOLD_OFF_MIN, GlobalConstants.X_TIMER_HOLD_OFF_MAX);
            c.xTimerHoldOffTF.setText(String.format(Locale.US, "%g", inRangeValue));
        });

        c.yTimerHoldOffTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.yTimerHoldOffTF.getText()), GlobalConstants.Y_TIMER_HOLD_OFF_MIN, GlobalConstants.Y_TIMER_HOLD_OFF_MAX);
                c.yTimerHoldOffTF.setText(String.format(Locale.US, "%g", inRangeValue));
            }
        });
        c.yTimerHoldOffTF.focusedProperty().addListener( (observableValue, aBoolean, t1) -> {
            // Put read value into range, update slider and also update TF with value in range.
            float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.yTimerHoldOffTF.getText()), GlobalConstants.Y_TIMER_HOLD_OFF_MIN, GlobalConstants.Y_TIMER_HOLD_OFF_MAX);
            c.yTimerHoldOffTF.setText(String.format(Locale.US, "%g", inRangeValue));
        });

        c.xTimePerDivisionTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.xTimePerDivisionTF.getText()), GlobalConstants.TIME_PER_DIVISION_MIN, GlobalConstants.TIME_PER_DIVISION_MAX);
                c.xTimePerDivisionS.setValue(Math.log10(inRangeValue));
                c.xTimePerDivisionTF.setText(String.format(Locale.US, "%g", inRangeValue));
            }
        });
        c.xTimePerDivisionTF.focusedProperty().addListener( (observableValue, aBoolean, t1) -> {
            float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.xTimePerDivisionTF.getText()), GlobalConstants.TIME_PER_DIVISION_MIN, GlobalConstants.TIME_PER_DIVISION_MAX);
            c.xTimePerDivisionS.setValue(Math.log10(inRangeValue));
            c.xTimePerDivisionTF.setText(String.format(Locale.US, "%g", inRangeValue));
        });

        c.yTimePerDivisionTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.yTimePerDivisionTF.getText()), GlobalConstants.TIME_PER_DIVISION_MIN, GlobalConstants.TIME_PER_DIVISION_MAX);
                c.yTimePerDivisionS.setValue(Math.log10(inRangeValue));
                c.yTimePerDivisionTF.setText(String.format(Locale.US, "%g", inRangeValue));
            }
        });
        c.yTimePerDivisionTF.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.yTimePerDivisionTF.getText()), GlobalConstants.TIME_PER_DIVISION_MIN, GlobalConstants.TIME_PER_DIVISION_MAX);
            c.yTimePerDivisionS.setValue(Math.log10(inRangeValue));
            c.yTimePerDivisionTF.setText(String.format(Locale.US, "%g", inRangeValue));
        });

        c.xTimePerDivisionS.setMin(GlobalConstants.TIME_PER_DIVISION_MIN_LOG10);
        c.xTimePerDivisionS.setMax(GlobalConstants.TIME_PER_DIVISION_MAX_LOG10);
        c.xTimePerDivisionS.setValue(GlobalConstants.TIME_PER_DIVISION_DEFAULT_LOG10);
        c.xTimePerDivisionS.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                c.xTimePerDivisionTF.setText(String.format(Locale.US, "%g", Math.pow(10, t1.doubleValue())));
            }
        });

        c.yTimePerDivisionS.setMin(GlobalConstants.TIME_PER_DIVISION_MIN_LOG10);
        c.yTimePerDivisionS.setMax(GlobalConstants.TIME_PER_DIVISION_MAX_LOG10);
        c.yTimePerDivisionS.setValue(GlobalConstants.TIME_PER_DIVISION_DEFAULT_LOG10);
        c.yTimePerDivisionS.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                c.yTimePerDivisionTF.setText(String.format(Locale.US, "%g", Math.pow(10, t1.doubleValue())));
            }
        });
    }

    public static TimeControlSettings readTimeSettings(Controller c) throws BadInputException{
        TimeControlSettings set = new TimeControlSettings();
        set.xTimePerDivision = Float.parseFloat(c.xTimePerDivisionTF.getText());
        set.yTimePerDivision = Float.parseFloat(c.yTimePerDivisionTF.getText());
        set.xTimerHoldOff = (int)Float.parseFloat(c.xTimerHoldOffTF.getText());
        set.yTimerHoldOff = (int)Float.parseFloat(c.yTimerHoldOffTF.getText());
        return set;
    }
}
