package sample;

import javafx.collections.FXCollections;
import javafx.scene.input.KeyCode;

import java.util.Locale;


class TriggerControlCaretaker {
    static void initTriggerControlSettings(Controller c){
        c.trigModeChoice.setItems(FXCollections.observableArrayList("Continuous", "One-time"));
        c.trigModeChoice.getSelectionModel().select(0);

        float defaultMinimumTriggerLevelX = GlobalConstants.TRIGGER_LEVEL_RESOLUTION_X*ChannelControlCaretaker.idxToRange(GlobalConstants.CHANNEL_RANGE_DEFAULT_INDEX_X);
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

        float defaultMinimumTriggerLevelY = GlobalConstants.TRIGGER_LEVEL_RESOLUTION_Y*ChannelControlCaretaker.idxToRange(GlobalConstants.CHANNEL_RANGE_DEFAULT_INDEX_Y);
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

    private static float getMinimumTriggerLevelX(Controller c){
        return GlobalConstants.TRIGGER_LEVEL_RESOLUTION_X*ChannelControlCaretaker.idxToRange(c.xChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
    }

    private static float getMinimumTriggerLevelY(Controller c){
        return GlobalConstants.TRIGGER_LEVEL_RESOLUTION_Y*ChannelControlCaretaker.idxToRange(c.yChannelVoltageRangeChoice.getSelectionModel().getSelectedIndex());
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
