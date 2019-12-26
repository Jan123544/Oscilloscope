package sample;

import javafx.application.Platform;

public class InternalSettingsCaretaker {
    public static void init(Controller c){
        c.autoUpdateCB.selectedProperty().addListener( (observable, oldValue, newValue) -> {
            if (newValue) { c.uploadSettingsButtonHandler(); }
        });

        Platform.runLater( () -> {
            c.canvasVerticalNormalisationTF.setText(String.valueOf(GlobalConstants.INTERNAL_CANVAS_VERTICAL_NORMALISATION));
            c.settingsUpdateRateTF.setText(String.valueOf(GlobalConstants.INTERNAL_SETTINGS_UPDATE_RATE));
        });
    }

    public static InternalSettings readInternalSettings(Controller c){
        InternalSettings set = new InternalSettings();
        set.canvasVerticalNormalisation = Integer.parseInt(c.canvasVerticalNormalisationTF.getText());
        set.settingsUpdateRate = Float.parseFloat(c.settingsUpdateRateTF.getText());
        return set;
    }
}
