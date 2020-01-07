package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import sample.constants.Channel;
import sample.constants.GlobalConstants;
import sample.exceptions.InvalidVoltageRangeException;
import sample.settings.ChannelSettings;

import java.util.Locale;

class ChannelControlCaretaker {
    private static ObservableList<String> xVoltageRanges;
    private static ObservableList<String> yVoltageRanges;

    static void initChannelControls(Controller c){
        initRanges(c);
        initChannelTF(c, c.xSensitivityTF, c.xSensitivityS, Channel.CHANNEL_X, GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT, GlobalConstants.CHANNEL_SENSITIVITY_MIN, GlobalConstants.CHANNEL_SENSITIVITY_MAX);
        initChannelTF(c, c.ySensitivityTF, c.ySensitivityS, Channel.CHANNEL_Y, GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT, GlobalConstants.CHANNEL_SENSITIVITY_MIN, GlobalConstants.CHANNEL_SENSITIVITY_MAX);
        initChannelTF(c, c.xOffsetTF, c.xOffsetS, Channel.CHANNEL_X, GlobalConstants.CHANNEL_OFFSET_DEFAULT, GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
        initChannelTF(c, c.yOffsetTF, c.yOffsetS, Channel.CHANNEL_Y, GlobalConstants.CHANNEL_OFFSET_DEFAULT, GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);

        initChannelS(c, c.xSensitivityTF, c.xSensitivityS, Channel.CHANNEL_X, GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT, GlobalConstants.CHANNEL_SENSITIVITY_MIN, GlobalConstants.CHANNEL_SENSITIVITY_MAX);
        initChannelS(c, c.ySensitivityTF, c.ySensitivityS, Channel.CHANNEL_Y, GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT, GlobalConstants.CHANNEL_SENSITIVITY_MIN, GlobalConstants.CHANNEL_SENSITIVITY_MAX);
        initChannelS(c, c.xOffsetTF, c.xOffsetS, Channel.CHANNEL_X, GlobalConstants.CHANNEL_OFFSET_DEFAULT, GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
        initChannelS(c, c.yOffsetTF, c.yOffsetS, Channel.CHANNEL_Y, GlobalConstants.CHANNEL_OFFSET_DEFAULT, GlobalConstants.CHANNEL_OFFSET_MIN, GlobalConstants.CHANNEL_OFFSET_MAX);
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

    private static float idxToRange(int idx){
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

    static float getXRange(Controller c){
        return idxToRange(c.xChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
    }

    static float getYRange(Controller c){
        return idxToRange(c.yChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
    }

    static private void initChannelTF(Controller c, TextField tf, Slider s, Channel channel, float def, float min, float max){
        tf.setText(String.valueOf(def));
        tf.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(tf, s, Float.parseFloat(tf.getText()), min, max);
                c.serialWriter.setUpdateNeeded(true);
            }
        });
        tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                GeneralOperations.setTFSControlsAndCropInRange(tf, s, Float.parseFloat(tf.getText()), min, max);
                c.serialWriter.setUpdateNeeded(true);
            }
        });
    }

    static private void initChannelS(Controller c, TextField tf, Slider s, Channel channel, float def, float min, float max){
        s.setMin(min);
        s.setMax(max);
        s.setValue(def);
        s.valueProperty().addListener((observableValue, number, t1) -> {
            tf.setText(String.format(Locale.US, "%.2g", t1.doubleValue()));
            c.serialWriter.setUpdateNeeded(true);
        });
    }

    static private void initRanges(Controller c){
        xVoltageRanges = FXCollections.observableArrayList("5", "10", "20");
        yVoltageRanges = FXCollections.observableArrayList("5", "10", "20");

        c.xChannelVoltageRangeChoice.setItems(yVoltageRanges);
        c.yChannelVoltageRangeChoice.setItems(xVoltageRanges);
        // Select first range default
        c.xChannelVoltageRangeChoice.getSelectionModel().select(0);
        c.yChannelVoltageRangeChoice.getSelectionModel().select(0);
        // Make sure sliders are cropped if voltage range is changed
        c.xChannelVoltageRangeChoice.setOnAction( (event) -> TriggerControlCaretaker.updateSliderRange(c, Channel.CHANNEL_X));
        c.yChannelVoltageRangeChoice.setOnAction( (event) -> TriggerControlCaretaker.updateSliderRange(c, Channel.CHANNEL_Y));
    }
}
