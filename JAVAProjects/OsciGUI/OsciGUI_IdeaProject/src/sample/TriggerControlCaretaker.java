package sample;

import javafx.collections.FXCollections;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import sample.constants.Channel;
import sample.constants.GlobalConstants;
import sample.exceptions.BadInputException;
import sample.exceptions.InvalidTriggerModeException;
import sample.settings.TriggerControlSettings;

import java.util.Locale;


class TriggerControlCaretaker {


    static private void triggerControlCallbackInsideTF(Controller c, TextField tf, Slider s, Channel channel){
        // Get the minimum trigger level for the active range.
        float activeMinimumTriggerLevel = getMinimumTriggerLevel(c, channel);
        float activeMaximumTriggerLevel = getMaximumTriggerLevel(c, channel);
        // Put read value into range, update slider and also update TF with value in range.
        GeneralOperations.setTFSControlsAndCropInRange(tf, s, Float.parseFloat(tf.getText()), activeMinimumTriggerLevel, activeMaximumTriggerLevel);
        c.canvasCaretaker.setThresholdLineVoltage(s.getValue(), channel);
        c.serialWriter.setUpdateNeeded(true);
    }

    static private void setupTriggerControlCallbackTF(Controller c, TextField tf, Slider s, Channel channel){
        tf.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                triggerControlCallbackInsideTF(c,tf,s,channel);
            }
        });
        tf.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(!t1){
                triggerControlCallbackInsideTF(c,tf,s,channel);
            }
        });
    }

    static private void initThresholdTFDefaults(Controller c, TextField tf, Slider s, Channel channel){
        float defaultTriggerLevel = getDefaultTriggerLevel(c, channel);
        tf.setText(String.valueOf(defaultTriggerLevel));
        setupTriggerControlCallbackTF(c,tf,s,channel);
    }

    static private void initThresholdSDefaults(Controller c, TextField tf, Slider s, Channel channel){
        float defaultTriggerLevel = getDefaultTriggerLevel(c, channel);
        s.setMin(defaultTriggerLevel);
        s.setMax(GlobalConstants.TRIGGER_LEVEL_DEFAULT_MAX);
        s.setValue(defaultTriggerLevel);
        s.valueProperty().addListener((observableValue, number, t1) -> {
            tf.setText(String.format(Locale.US, "%.3f", t1.doubleValue()));
            c.canvasCaretaker.setThresholdLineVoltage(t1.doubleValue(), channel);
            c.serialWriter.setUpdateNeeded(true);
        });

    }

    static void initTriggerControlSettings(Controller c){
        c.xTriggerModeCH.setItems(FXCollections.observableArrayList("Single", "Continuous"));
        c.xTriggerModeCH.getSelectionModel().select(0);

        c.yTriggerModeCH.setItems(FXCollections.observableArrayList("Single", "Continuous"));
        c.yTriggerModeCH.getSelectionModel().select(0);


        initThresholdTFDefaults(c, c.xTriggerLevelTF, c.xTriggerLevelS, Channel.CHANNEL_X);
        initThresholdTFDefaults(c, c.yTriggerLevelTF, c.yTriggerLevelS, Channel.CHANNEL_Y);
        initThresholdSDefaults(c, c.xTriggerLevelTF, c.xTriggerLevelS, Channel.CHANNEL_X);
        initThresholdSDefaults(c, c.yTriggerLevelTF, c.yTriggerLevelS, Channel.CHANNEL_Y);
    }

    private static float getDefaultTriggerLevel(Controller c, Channel channel){
        return 0;
    }

    private static float getMinimumTriggerLevel(Controller c, Channel channel){
        return 0;
    }

    private static float getMaximumTriggerLevel(Controller c, Channel channel){
        switch (channel){
            case CHANNEL_X:
                return ChannelControlCaretaker.getXRange(c);
            case CHANNEL_Y:
                return ChannelControlCaretaker.getYRange(c);
            default:
                System.err.println("Invalid channel getMaximumTriggerLevel");
                return 0;
        }
    }

    static void updateSliderRange(Controller c, Channel channel){
        switch (channel){
            case CHANNEL_X:
                c.xTriggerLevelS.setMin(getMinimumTriggerLevel(c, Channel.CHANNEL_X));
                c.xTriggerLevelS.setMax(getMaximumTriggerLevel(c, Channel.CHANNEL_X));
            case CHANNEL_Y:
                c.yTriggerLevelS.setMin(getMinimumTriggerLevel(c, Channel.CHANNEL_Y));
                c.yTriggerLevelS.setMax(getMaximumTriggerLevel(c, Channel.CHANNEL_Y));
        }
    }

    static TriggerControlSettings readTriggerControlSettings(Controller c) throws BadInputException {
        TriggerControlSettings set = new TriggerControlSettings();

        set.triggerCommand = GlobalConstants.TRIGGER_COMMAND_TRANSFORM; // Default is not triggering, just adjust transform parameters.

        // Assumes Single mode is index 0 in observable arraylist of choicebox and continous mode is index 1
        switch(c.xTriggerModeCH.getSelectionModel().getSelectedIndex()){
            case 0:
                set.triggerCommand |= GlobalConstants.TRIGGER_COMMAND_MEASURE_SINGLE_X;
                break;
            case 1:
                set.triggerCommand |= GlobalConstants.TRIGGER_COMMAND_MEASURE_CONTINUOUS_X;
                break;
            default:
                throw new InvalidTriggerModeException();
        }

        switch(c.yTriggerModeCH.getSelectionModel().getSelectedIndex()){
            case 0:
                set.triggerCommand |= GlobalConstants.TRIGGER_COMMAND_MEASURE_SINGLE_Y;
                break;
            case 1:
                set.triggerCommand |= GlobalConstants.TRIGGER_COMMAND_MEASURE_CONTINUOUS_Y;
                break;
            default:
                throw new InvalidTriggerModeException();
        }

        set.xTriggerLevel = Float.parseFloat(c.xTriggerLevelTF.getText());
        set.yTriggerLevel = Float.parseFloat(c.yTriggerLevelTF.getText());

        return set;
    }
}
