package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;

import java.util.Locale;

class ChannelControlCaretaker {
    private static ObservableList<String> xVoltageRanges;
    private static ObservableList<String> yVoltageRanges;

    static float idxToRange(int idx){
        switch(idx){
            case 0:
                return 5.0f;
            case 1:
                return 10.0f;
            case 2:
                return 20.0f;
            default:
                throw new InvalidVoltageRangeException();
        }
    }

    static double getXRange(Controller c){
        return idxToRange(c.xChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
    }

    static double getYRange(Controller c){
        return idxToRange(c.yChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
    }


    static void initChannelControls(Controller c){
        xVoltageRanges = FXCollections.observableArrayList("5", "10", "20");
        yVoltageRanges = FXCollections.observableArrayList("5", "10", "20");

        c.xChannelVoltageRangeChoice.setItems(yVoltageRanges);
        c.yChannelVoltageRangeChoice.setItems(xVoltageRanges);
        // Select first range default
        c.xChannelVoltageRangeChoice.getSelectionModel().select(0);
        c.yChannelVoltageRangeChoice.getSelectionModel().select(0);
        // Make sure sliders are cropped if voltage range is changed
        c.xChannelVoltageRangeChoice.setOnAction( (event) -> TriggerControlCaretaker.updateSliderRangeX(c));
        c.yChannelVoltageRangeChoice.setOnAction( (event) -> TriggerControlCaretaker.updateSliderRangeY(c));

        c.ySensitivityTF.setText(String.valueOf(GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT));
        c.ySensitivityTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.ySensitivityTF, c.ySensitivityS, Float.parseFloat(c.ySensitivityTF.getText()), GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
            }
        });
        c.ySensitivityTF.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                GeneralOperations.setTFSControlsAndCropInRange(c.ySensitivityTF, c.ySensitivityS, Float.parseFloat(c.ySensitivityTF.getText()), GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
            }
        });

        c.xSensitivityTF.setText(String.valueOf(GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT));
        c.xSensitivityTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.xSensitivityTF, c.xSensitivityS, Float.parseFloat(c.xSensitivityTF.getText()), GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
            }
        });
        c.xSensitivityTF.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                GeneralOperations.setTFSControlsAndCropInRange(c.xSensitivityTF, c.xSensitivityS, Float.parseFloat(c.xSensitivityTF.getText()), GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
            }
        });

        c.yOffsetTF.setText(String.valueOf(GlobalConstants.CHANNEL_OFFSET_DEFAULT));
        c.yOffsetTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.yOffsetTF, c.yOffsetS, Float.parseFloat(c.yOffsetTF.getText()), GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
            }
        });
        c.yOffsetTF.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                GeneralOperations.setTFSControlsAndCropInRange(c.yOffsetTF, c.yOffsetS, Float.parseFloat(c.yOffsetTF.getText()), GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
            }
        });

        c.xOffsetTF.setText(String.valueOf(GlobalConstants.CHANNEL_OFFSET_DEFAULT));
        c.xOffsetTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.xOffsetTF, c.xOffsetS, Float.parseFloat(c.xOffsetTF.getText()), GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
            }
        });
        c.xOffsetTF.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                GeneralOperations.setTFSControlsAndCropInRange(c.xOffsetTF, c.xOffsetS, Float.parseFloat(c.xOffsetTF.getText()), GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
            }
        });

        c.ySensitivityS.setMin(GlobalConstants.CHANNEL_SENSITIVITY_MIN);
        c.ySensitivityS.setMax(GlobalConstants.CHANNEL_SENSITIVITY_MAX);
        c.ySensitivityS.setValue(GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT);
        c.ySensitivityS.valueProperty().addListener((observableValue, number, t1) -> c.ySensitivityTF.setText(String.format(Locale.US, "%g", t1.doubleValue())));

        c.xSensitivityS.setMin(GlobalConstants.CHANNEL_SENSITIVITY_MIN);
        c.xSensitivityS.setMax(GlobalConstants.CHANNEL_SENSITIVITY_MAX);
        c.xSensitivityS.setValue(GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT);
        c.xSensitivityS.valueProperty().addListener((observableValue, number, t1) -> c.xSensitivityTF.setText(String.format(Locale.US, "%g", t1.doubleValue())));

        c.xOffsetS.setMin(GlobalConstants.CHANNEL_OFFSET_MIN);
        c.xOffsetS.setMax(GlobalConstants.CHANNEL_OFFSET_MAX);
        c.xOffsetS.setValue(GlobalConstants.CHANNEL_OFFSET_DEFAULT);
        c.xOffsetS.valueProperty().addListener((observableValue, number, t1) -> c.xOffsetTF.setText(String.format(Locale.US, "%g", t1.doubleValue())));

        c.yOffsetS.setMin(GlobalConstants.CHANNEL_OFFSET_MIN);
        c.yOffsetS.setMax(GlobalConstants.CHANNEL_OFFSET_MAX);
        c.yOffsetS.setValue(GlobalConstants.CHANNEL_OFFSET_DEFAULT);
        c.yOffsetS.valueProperty().addListener((observableValue, number, t1) -> c.yOffsetTF.setText(String.format(Locale.US, "%g", t1.doubleValue())));
    }

    static ChannelSettings readChannelControlsSettings(Controller c){
        ChannelSettings set = new ChannelSettings();
        set.ySensitivity = Float.parseFloat(c.ySensitivityTF.getText());
        set.yOffset = Float.parseFloat(c.yOffsetTF.getText());
        set.xSensitivity = Float.parseFloat(c.xSensitivityTF.getText());
        set.xOffset = Float.parseFloat(c.xOffsetTF.getText());
        set.xVoltageRange = Byte.parseByte(xVoltageRanges.get(c.xChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex()));
        set.yVoltageRange = Byte.parseByte(yVoltageRanges.get(c.yChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex()));
        set.xGraticuleDivisions = GlobalConstants.GRATICULE_X_DIVISIONS;
        set.yGraticuleDivisions = GlobalConstants.GRATICULE_Y_DIVISIONS;

        return set;
    }
}
