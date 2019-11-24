package sample;

public class TimeControlsCaretaker {
    public static void initTimeControlSettings(Controller c){
        c.timePerDivisionTF.setText(String.valueOf(GlobalConstants.TIME_PER_DIVISION_DEFAULT));
    }

    public static TimeControlSettings readTimeSettings(Controller c) throws BadInputException{
        TimeControlSettings set = new TimeControlSettings();
        set.timePerDivision = Double.parseDouble(c.timePerDivisionTF.getText());
        return set;
    }
}
