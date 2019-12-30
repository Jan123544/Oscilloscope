package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    Canvas timeDomainCanvas;

    @FXML
    Button timeDomainUploadB;

    @FXML
    Slider timeDomainPeriodS;

    @FXML
    TextField timeDomainPeriodTF;

    CanvasCaretaker timeDomainCanvasCaretaker;

    public void initialize (){
        timeDomainCanvasCaretaker = new CanvasCaretaker(timeDomainCanvas, 1000, 4096);
    }

}
