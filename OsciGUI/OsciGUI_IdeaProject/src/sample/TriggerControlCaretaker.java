package sample;

import javafx.collections.FXCollections;

public class TriggerControlCaretaker {
    public static void initTriggerControlSettings(Controller c){
        c.trigModeChoice.setItems(FXCollections.observableArrayList("Continuous", "One-time"));
        c.trigModeChoice.getSelectionModel().select(0);
        c.trigLevelTF.setText(String.valueOf(GlobalConstants.TRIGGER_LEVEL_DEFAULT));
        c.trigLevelAdjustedL.setText("None");
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
        set.triggerValue = Double.parseDouble(c.trigLevelTF.getText());

        return set;
    }
}
