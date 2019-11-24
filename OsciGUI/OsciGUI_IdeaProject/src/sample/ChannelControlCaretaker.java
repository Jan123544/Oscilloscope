package sample;

public class ChannelControlCaretaker {

    public static void initChannelControls(Controller c){
        c.ySensitivityTF.setText(String.valueOf(GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT));
        c.xSensitivityTF.setText(String.valueOf(GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT));
        c.yOffsetTF.setText(String.valueOf(GlobalConstants.CHANNEL_OFFSET_DEFAULT));
        c.xOffsetTF.setText(String.valueOf(GlobalConstants.CHANNEL_OFFSET_DEFAULT));

        c.ySensitivityS.setMin(GlobalConstants.CHANNEL_SENSITIVITY_MIN);
        c.ySensitivityS.setMax(GlobalConstants.CHANNEL_SENSITIVITY_MAX);
        c.ySensitivityS.setValue(GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT);

        c.xSensitivityS.setMin(GlobalConstants.CHANNEL_SENSITIVITY_MIN);
        c.xSensitivityS.setMax(GlobalConstants.CHANNEL_SENSITIVITY_MAX);
        c.xSensitivityS.setValue(GlobalConstants.CHANNEL_SENSITIVITY_DEFAULT);

        c.xOffsetS.setMin(GlobalConstants.CHANNEL_OFFSET_MIN);
        c.xOffsetS.setMax(GlobalConstants.CHANNEL_OFFSET_MAX);
        c.xOffsetS.setValue(GlobalConstants.CHANNEL_OFFSET_DEFAULT);

        c.yOffsetS.setMin(GlobalConstants.CHANNEL_OFFSET_MIN);
        c.yOffsetS.setMax(GlobalConstants.CHANNEL_OFFSET_MAX);
        c.yOffsetS.setValue(GlobalConstants.CHANNEL_OFFSET_DEFAULT);
    }

    public static void updateFromTextFields(Controller c){
        ChannelControlCaretaker.syncFromTextFields(c);

    }

    public static void updateFromSliders(Controller c){
        ChannelControlCaretaker.syncFromSliders(c);

    }

    private static void syncFromTextFields(Controller c){
        c.xSensitivityTF.setText(String.valueOf(c.xSensitivityS.getValue()));
        c.ySensitivityTF.setText(String.valueOf(c.ySensitivityS.getValue()));
        c.xOffsetTF.setText(String.valueOf(c.xOffsetS.getValue()));
        c.yOffsetTF.setText(String.valueOf(c.yOffsetS.getValue()));
    }

    private static void syncFromSliders(Controller c){
        c.xSensitivityTF.setText(String.valueOf(c.xSensitivityS.getValue()));
        c.ySensitivityTF.setText(String.valueOf(c.ySensitivityS.getValue()));
        c.xOffsetTF.setText(String.valueOf(c.xOffsetS.getValue()));
        c.yOffsetTF.setText(String.valueOf(c.yOffsetS.getValue()));
    }

    public static ChannelControlSettings readChannelControlsSettings(Controller c){
        ChannelControlSettings set = new ChannelControlSettings();
        set.ySensitivity = Double.parseDouble(c.ySensitivityTF.getText());
        set.yOffset = Double.parseDouble(c.yOffsetTF.getText());
        set.xSensitivity = Double.parseDouble(c.xSensitivityTF.getText());
        set.xOffset = Double.parseDouble(c.xOffsetTF.getText());

        return set;
    }

}
