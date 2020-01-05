package sample;

import javafx.collections.FXCollections;
import javafx.scene.input.KeyCode;

import java.util.Locale;


class TriggerControlCaretaker {

    static void initTriggerControlSettings(Controller c){
        c.xTriggerModeCH.setItems(FXCollections.observableArrayList("Single", "Continuous"));
        c.xTriggerModeCH.getSelectionModel().select(0);

        c.yTriggerModeCH.setItems(FXCollections.observableArrayList("Single", "Continuous"));
        c.yTriggerModeCH.getSelectionModel().select(0);

        float defaultMinimumTriggerLevelX = getMinimumTriggerLevelXDefault();
        c.xTriggerLevelTF.setText(String.valueOf(defaultMinimumTriggerLevelX));
        c.xTriggerLevelTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Get the minimum trigger level for the active range.
                float activeMinimumTriggerLevelX = getMinimumTriggerLevelX(c);
                float activeMaximumTriggerLevelX = getMaximumTriggerLevelX(c);
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.xTriggerLevelTF, c.xTriggerLevelS, Float.parseFloat(c.xTriggerLevelTF.getText()), activeMinimumTriggerLevelX, activeMaximumTriggerLevelX);
            }
        });

        float defaultMinimumTriggerLevelY = getMinimumTriggerLevelYDefault();
        c.yTriggerLevelTF.setText(String.valueOf(defaultMinimumTriggerLevelY));
        c.yTriggerLevelTF.setOnKeyReleased( (key) -> {
            if (key.getCode() == KeyCode.ENTER){
                // Get the minimum trigger level for the active range.
                float activeMinimumTriggerLevelY = getMinimumTriggerLevelY(c);
                float activeMaximumTriggerLevelY = getMaximumTriggerLevelY(c);
                // Put read value into range, update slider and also update TF with value in range.
                GeneralOperations.setTFSControlsAndCropInRange(c.yTriggerLevelTF, c.yTriggerLevelS, Float.parseFloat(c.yTriggerLevelTF.getText()), activeMinimumTriggerLevelY, activeMaximumTriggerLevelY);
            }
        });

        c.xTriggerLevelS.setMin(defaultMinimumTriggerLevelX);
        c.xTriggerLevelS.setMax(GlobalConstants.TRIGGER_LEVEL_DEFAULT_MAX);
        c.xTriggerLevelS.setValue(defaultMinimumTriggerLevelX);
        c.xTriggerLevelS.valueProperty().addListener((observableValue, number, t1) -> c.xTriggerLevelTF.setText(String.format(Locale.US, "%g", t1.doubleValue())));

        c.yTriggerLevelS.setMin(defaultMinimumTriggerLevelY);
        c.yTriggerLevelS.setMax(GlobalConstants.TRIGGER_LEVEL_DEFAULT_MAX);
        c.yTriggerLevelS.setValue(defaultMinimumTriggerLevelY);
        c.yTriggerLevelS.valueProperty().addListener((observableValue, number, t1) -> c.yTriggerLevelTF.setText(String.format(Locale.US, "%g", t1.doubleValue())));
    }

    private static float getMinimumTriggerLevelXDefault(){
        // return GlobalConstants.TRIGGER_LEVEL_RESOLUTION_X*ChannelControlCaretaker.idxToRange(GlobalConstants.CHANNEL_RANGE_DEFAULT_INDEX_X);
        return 0;
    }

    private static float getMinimumTriggerLevelYDefault(){
        //return GlobalConstants.TRIGGER_LEVEL_RESOLUTION_Y*ChannelControlCaretaker.idxToRange(GlobalConstants.CHANNEL_RANGE_DEFAULT_INDEX_Y);
        return 0;
    }

    private static float getMinimumTriggerLevelX(Controller c){
        //return GlobalConstants.TRIGGER_LEVEL_RESOLUTION_X*ChannelControlCaretaker.idxToRange(c.xChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
        return 0;
    }

    private static float getMinimumTriggerLevelY(Controller c){
        //return GlobalConstants.TRIGGER_LEVEL_RESOLUTION_Y*ChannelControlCaretaker.idxToRange(c.yChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
        return 0;
    }

    private static float getMaximumTriggerLevelY(Controller c){
        return ChannelControlCaretaker.idxToRange(c.yChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
    }

    private static float getMaximumTriggerLevelX(Controller c){
        return ChannelControlCaretaker.idxToRange(c.xChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
    }

    static void updateSliderRangeX(Controller c){
        c.xTriggerLevelS.setMin(getMinimumTriggerLevelX(c));
        c.xTriggerLevelS.setMax(getMaximumTriggerLevelX(c));
    }

    static void updateSliderRangeY(Controller c){
        c.yTriggerLevelS.setMin(getMinimumTriggerLevelY(c));
        c.yTriggerLevelS.setMax(getMaximumTriggerLevelY(c));
    }

    static TriggerControlSettings readTriggerControlSettings(Controller c) throws BadInputException{
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
