package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.Locale;


public class TriggerControlCaretaker {
    public static void initTriggerControlSettings(Controller c){
        c.trigModeChoice.setItems(FXCollections.observableArrayList("Continuous", "One-time"));
        c.trigModeChoice.getSelectionModel().select(0);

        c.xTriggerLevelTF.setText(String.valueOf(GlobalConstants.TRIGGER_LEVEL_DEFAULT));
        c.xTriggerLevelTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.xTriggerLevelTF, c.xTriggerLevelS, Float.parseFloat(c.xTriggerLevelTF.getText()), GlobalConstants.TRIGGER_LEVEL_DEFAULT_MIN, GlobalConstants.TRIGGER_LEVEL_DEFAULT_MAX);
            }
        });

        c.yTriggerLevelTF.setText(String.valueOf(GlobalConstants.TRIGGER_LEVEL_DEFAULT));
        c.yTriggerLevelTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.yTriggerLevelTF, c.yTriggerLevelS, Float.parseFloat(c.yTriggerLevelTF.getText()), GlobalConstants.TRIGGER_LEVEL_DEFAULT_MIN, GlobalConstants.TRIGGER_LEVEL_DEFAULT_MAX);
            }
        });

        c.xTriggerLevelS.setMin(GlobalConstants.TRIGGER_LEVEL_DEFAULT_MIN);
        c.xTriggerLevelS.setMax(GlobalConstants.TRIGGER_LEVEL_DEFAULT_MAX);
        c.xTriggerLevelS.setValue(GlobalConstants.TRIGGER_LEVEL_DEFAULT);
        c.xTriggerLevelS.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                c.xTriggerLevelTF.setText(String.format(Locale.US, "%g", t1.doubleValue()));
            }
        });

        c.yTriggerLevelS.setMin(GlobalConstants.TRIGGER_LEVEL_DEFAULT_MIN);
        c.yTriggerLevelS.setMax(GlobalConstants.TRIGGER_LEVEL_DEFAULT_MAX);
        c.yTriggerLevelS.setValue(GlobalConstants.TRIGGER_LEVEL_DEFAULT);
        c.yTriggerLevelS.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                c.yTriggerLevelTF.setText(String.format(Locale.US, "%g", t1.doubleValue()));
            }
        });
    }

    public static void setXTriggerVoltageRangeModifyCallbacks(Controller c, float xRangeMin, float xRangeMax) {
        c.xTriggerLevelTF.setOnKeyReleased((key) -> {
            if (key.getCode() == KeyCode.ENTER) {
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.xTriggerLevelTF, c.xTriggerLevelS, Float.parseFloat(c.xTriggerLevelTF.getText()), xRangeMin, xRangeMax);
            }
        });
        c.xTriggerLevelS.setMin(xRangeMin);
        c.xTriggerLevelS.setMax(xRangeMax);
    }

    public static void setYTriggerVoltageRangeModifyCallbacks(Controller c, float yRangeMin, float yRangeMax) {
        c.yTriggerLevelTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.yTriggerLevelTF, c.yTriggerLevelS, Float.parseFloat(c.yTriggerLevelTF.getText()), yRangeMin, yRangeMax);
            }
        });

        c.yTriggerLevelS.setMin(yRangeMin);
        c.yTriggerLevelS.setMax(yRangeMax);
    }

    public static TriggerControlSettings readTriggerControlSettings(Controller c) throws BadInputException{
        TriggerControlSettings set = new TriggerControlSettings();
        switch (c.trigModeChoice.getSelectionModel().getSelectedIndex()){
            case 0:
                set.triggerType = GlobalConstants.TRIGGER_TYPE_CONTINUOUS;
                break;
            case 1:
                set.triggerType = GlobalConstants.TRIGGER_TYPE_ONE_TIME;
                break;
        }
        set.xTriggerLevel = Float.parseFloat(c.xTriggerLevelTF.getText());
        set.yTriggerLevel = Float.parseFloat(c.yTriggerLevelTF.getText());

        return set;
    }
}
