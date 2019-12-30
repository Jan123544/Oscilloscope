package sample;

import javafx.scene.control.CheckBox;

public class GeneralMeasurementSettingsCaretaker {
    private CheckBox xDoMeasurementCheckBox;
    private CheckBox yDoMeasurementCheckBox;

    public GeneralMeasurementSettingsCaretaker(CheckBox xDoMeasurementCheckBox, CheckBox yDoMeasurementCheckBox){
        this.xDoMeasurementCheckBox = xDoMeasurementCheckBox;
        this.yDoMeasurementCheckBox = yDoMeasurementCheckBox;
    }

    public GeneralMeasurementSettings readGeneralMeasurementSettings(){
        GeneralMeasurementSettings gms = new GeneralMeasurementSettings();
        if(xDoMeasurementCheckBox.isSelected()){
            gms.doMeasurement |= GlobalConstants.OSCI_SETTINGS_DOMEASUREMENT_X;
        }
        if(yDoMeasurementCheckBox.isSelected()){
            gms.doMeasurement |= GlobalConstants.OSCI_SETTINGS_DOMEASUREMENT_Y;
        }
        return gms;
    }
}
