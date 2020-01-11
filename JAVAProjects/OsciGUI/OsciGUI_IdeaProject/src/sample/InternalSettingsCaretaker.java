package sample;

import javafx.application.Platform;
import sample.constants.GlobalConstants;
import sample.settings.InternalSettings;

public class InternalSettingsCaretaker {
    public static void init(Controller c){
        Platform.runLater( () -> {
            c.canvasVerticalNormalisationTF.setText(String.valueOf(GlobalConstants.INTERNAL_CANVAS_VERTICAL_NORMALISATION));
            c.settingsUpdateRateTF.setText(String.valueOf(GlobalConstants.INTERNAL_SETTINGS_UPDATE_RATE));
        });
    }

    public static InternalSettings readInternalSettings(Controller c){
        InternalSettings set = new InternalSettings();
        try {
        set.canvasVerticalNormalisation = Integer.parseInt(c.canvasVerticalNormalisationTF.getText());
        } catch (NumberFormatException e){
            System.err.println(String.format("Invalid canvas normalisation! Using default which is: %d.", GlobalConstants.INTERNAL_CANVAS_VERTICAL_NORMALISATION));
            set.settingsUpdateRate = GlobalConstants.INTERNAL_SETTINGS_UPDATE_RATE;
        }
        try {
            set.settingsUpdateRate = Float.parseFloat(c.settingsUpdateRateTF.getText());
        } catch (NumberFormatException e){
            System.err.println(String.format("Invalid settings update rate! Using default which is: %.2f", GlobalConstants.INTERNAL_SETTINGS_UPDATE_RATE));
            set.settingsUpdateRate = GlobalConstants.INTERNAL_SETTINGS_UPDATE_RATE;
        }
        return set;
    }
}
