package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.scene.control.ChoiceBox;

class ViewSettingsCaretaker {
    private static byte VIEW_MODE_X_ONLY = 0;
    private static byte VIEW_MODE_Y_ONLY = 1;
    private static byte VIEW_MODE_X_Y_BOTH = 2;
    private static byte VIEW_MODE_XY = 3;

    ChoiceBox viewModeCh;

    ViewSettingsCaretaker(ChoiceBox viewModeCH){
        this.viewModeCh = viewModeCH;
        this.viewModeCh.setItems(FXCollections.observableArrayList( "X only", "Y only", "X and Y", "X-Y"));
        this.viewModeCh.getSelectionModel().select(2);
    }

    boolean isYShowing(){
        int selected =this.viewModeCh.getSelectionModel().getSelectedIndex();
        return (selected == VIEW_MODE_Y_ONLY || selected == VIEW_MODE_X_Y_BOTH);
    }

    boolean isXShowing(){
        int selected =this.viewModeCh.getSelectionModel().getSelectedIndex();
        return (selected == VIEW_MODE_X_ONLY || selected == VIEW_MODE_X_Y_BOTH);
    }

    boolean isXYMode(){
        int selected =this.viewModeCh.getSelectionModel().getSelectedIndex();
        return selected == VIEW_MODE_XY;
    }
}
