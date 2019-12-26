package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;

import java.util.Locale;

public class TimeControlsCaretaker {
    public static void initTimeControlSettings(Controller c){
        c.timePerDivisionTF.setText(String.valueOf(GlobalConstants.TIME_PER_DIVISION_DEFAULT));
        c.timePerDivisionTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                float inRangeValue = GeneralOperations.putInRange(Float.parseFloat(c.timePerDivisionTF.getText()), GlobalConstants.TIME_PER_DIVISION_MIN, GlobalConstants.TIME_PER_DIVISION_MAX);
                c.timePerDivisionS.setValue(Math.log10(inRangeValue));
                c.timePerDivisionTF.setText(String.format(Locale.US, "%g", inRangeValue));
            }
        });

        c.timePerDivisionS.setMin(GlobalConstants.TIME_PER_DIVISION_MIN_LOG10);
        c.timePerDivisionS.setMax(GlobalConstants.TIME_PER_DIVISION_MAX_LOG10);
        c.timePerDivisionS.setValue(GlobalConstants.TIME_PER_DIVISION_DEFAULT_LOG10);
        c.timePerDivisionS.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                c.timePerDivisionTF.setText(String.format(Locale.US, "%g", Math.pow(10, t1.doubleValue())));
            }
        });
    }

    public static TimeControlSettings readTimeSettings(Controller c) throws BadInputException{
        TimeControlSettings set = new TimeControlSettings();
        set.timePerDivision = Float.parseFloat(c.timePerDivisionTF.getText());
        return set;
    }
}
