package sample;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sample.constants.Channel;
import sample.constants.GlobalConstants;
import sample.constants.LineOrientation;
import sample.exceptions.BadInputException;
import sample.settings.CanvasSettings;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static sample.GeneralOperations.*;

class CanvasCaretaker {
    private double labelMargin;
    private ArrayList<Double> yNormalisedBuffer;
    private ArrayList<Double> xNormalisedBuffer;

    private Polyline xPolyline;
    private Polyline yPolyline;

    private Line xThresholdLine;
    private Line yThresholdLine;
    private Line xScanLine;
    private Line yScanLine;
    private Label xScanLineLabel;
    private Label yScanLineLabel;
    private Label xThresholdLineLabel;
    private Label yThresholdLineLabel;
    private Polygon xThresholdLineHandle;
    private Polygon yThresholdLineHandle;
    private Polygon xScanLineHandle;
    private Polygon yScanLineHandle;

    //private CheckBox enableXScanLineCB;
    //private CheckBox enableYScanLineCB;
    //private CheckBox enableXThresholdLineCB;
    //private CheckBox enableYThresholdLineCB;

    //private Label lowerUnitLabel;
    //private Label upperUnitLabel;

    private Canvas canvas;
    private Controller c;
    private GraphicsContext gc;

    CanvasCaretaker (Controller c, Polyline xPolyline, Polyline yPolyline, Line xScanLine, Line yScanLine,
                     Line xThresholdLine, Line yThresholdLine, Label xScanLineLabel, Label yScanLineLabel,
                     Label xThresholdLineLabel, Label yThresholdLineLabel, Polygon xScanLineHandle,
                     Polygon yScanLineHandle, Polygon xThresholdLineHandle, Polygon yThresholdLineHandle,
                     Label lowerUnitLabel, Label upperUnitLabel, CheckBox enableXScanLineCB,
                     CheckBox enableYScanLineCB, CheckBox enableXThresholdLineCB, CheckBox enableYThresholdLineCB){
        this.c = c;
        canvas = c.canvas;
        gc = canvas.getGraphicsContext2D();

        initMargin();
        initBuffers();
        initBackground();
        initPolyLines(xPolyline, yPolyline);
        initThresholdLinesWithHandlesAndLabels(xThresholdLine, xThresholdLineHandle, xThresholdLineLabel , yThresholdLine, yThresholdLineHandle, yThresholdLineLabel);
        initScanLinesWithHandlesAndLabels(xScanLine, xScanLineHandle, xScanLineLabel, yScanLine, yScanLineHandle, yScanLineLabel);
        initUnitLabels(lowerUnitLabel, upperUnitLabel);
        initLineEnableCheckboxes(enableXScanLineCB, enableYScanLineCB,  enableXThresholdLineCB, enableYThresholdLineCB);
    }


    private void initLineEnableCheckbox(CheckBox cb, Line l, Label lab, Polygon h){
        cb.selectedProperty().bindBidirectional(l.visibleProperty());
        cb.selectedProperty().bindBidirectional(lab.visibleProperty());
        cb.selectedProperty().bindBidirectional(h.visibleProperty());
        cb.setSelected(false);
    }

    private void initLineEnableCheckboxes(CheckBox newEnableXScanLineCB, CheckBox newEnableYScanLineCB,CheckBox newEnableXThresholdLineCB, CheckBox newEnableYThresholdLineCB){
        //this.enableXScanLineCB = newEnableXScanLineCB;
        initLineEnableCheckbox(newEnableXScanLineCB, xScanLine, xScanLineLabel, xScanLineHandle);

        //this.enableYScanLineCB = newEnableYScanLineCB;
        initLineEnableCheckbox(newEnableYScanLineCB, yScanLine, yScanLineLabel, yScanLineHandle);

        //this.enableXThresholdLineCB = newEnableXThresholdLineCB;
        initLineEnableCheckbox(newEnableXThresholdLineCB, xThresholdLine, xThresholdLineLabel, xThresholdLineHandle);

        //this.enableYThresholdLineCB = newEnableYThresholdLineCB;
        initLineEnableCheckbox(newEnableYThresholdLineCB, yThresholdLine, yThresholdLineLabel, yThresholdLineHandle);
    }

    private void initUnitLabels(Label newLowerUnitLabel, Label newUpperUnitLabel){
        double upperUnitLabelXOffset = 40;
        double upperUnitLabelYOffset = 15;
        double lowerUnitLabelXOffset = 10;
        double lowerUnitLabelYOffset = 25;

        newUpperUnitLabel = newLowerUnitLabel;
        newUpperUnitLabel.setText("[V/V]");
        newUpperUnitLabel.setFont(Font.font("", FontWeight.BOLD, 13));
        newUpperUnitLabel.setTranslateX(canvas.getWidth() - upperUnitLabelXOffset);
        newUpperUnitLabel.setTranslateY(upperUnitLabelYOffset);
        newUpperUnitLabel.setTextFill(Color.RED);

        newLowerUnitLabel = newUpperUnitLabel;
        newLowerUnitLabel.setText("[V/s]");
        newLowerUnitLabel.setFont(Font.font("", FontWeight.BOLD, 13));
        newLowerUnitLabel.setTranslateX(lowerUnitLabelXOffset);
        newLowerUnitLabel.setTranslateY(canvas.getHeight() - lowerUnitLabelYOffset);
        newLowerUnitLabel.setTextFill(Color.RED);
    }

    private void initMargin(){
        labelMargin = 45;
    }

    private void initBuffers(){
        yNormalisedBuffer = new ArrayList<>();
        xNormalisedBuffer = new ArrayList<>();
    }

    private void initPolyLine(Polyline pl, Channel channel){
        pl.setStrokeWidth(3);
        pl.setStrokeType(StrokeType.CENTERED);
        switch (channel){
            case CHANNEL_X:
                pl.setStroke(Color.RED);
                break;
            case CHANNEL_Y:
                pl.setStroke(Color.YELLOW);
        }
        pl.setVisible(false);
    }

    private void initPolyLines(Polyline newXPolyLine, Polyline newYPolyline){
        xPolyline = newXPolyLine;
        initPolyLine(xPolyline, Channel.CHANNEL_X);

        yPolyline = newYPolyline;
        initPolyLine(yPolyline, Channel.CHANNEL_Y);
    }

    private void initThresholdLinesWithHandlesAndLabels(Line xThresholdLine, Polygon xThresholdLineHandle, Label xThresholdLineLabel, Line yThresholdLine, Polygon yThresholdLineHandle, Label yThresholdLineLabel){
        this.xThresholdLine = xThresholdLine;
        this.xThresholdLineHandle = xThresholdLineHandle;
        this.xThresholdLineLabel = xThresholdLineLabel;
        this.yThresholdLine = yThresholdLine;
        this.yThresholdLineHandle = yThresholdLineHandle;
        this.yThresholdLineLabel = yThresholdLineLabel;
        initLineWithHandleAndLabel(this.xThresholdLineHandle, this.xThresholdLine, this.xThresholdLineLabel, LineOrientation.HORIZONTAL, canvas.getHeight()/2, Channel.CHANNEL_X);
        initLineWithHandleAndLabel(this.yThresholdLineHandle, this.yThresholdLine, this.yThresholdLineLabel, LineOrientation.HORIZONTAL, canvas.getHeight()/2.5, Channel.CHANNEL_Y);

        setXThresholdLineVoltage(0);
        setYThresholdLineVoltage(0);
        // This needs to be done, because label.getWidth() does not get initialized until stage is draw. So initial settings, must be corrected manually.
        xThresholdLineLabel.setTranslateY(xThresholdLineLabel.getTranslateY() - 5);
        yThresholdLineLabel.setTranslateY(yThresholdLineLabel.getTranslateY() - 5);
    }

    private void setThresholdLineVoltage(Line line, Label label, Polygon handle, double voltage, double sensitivity, double offset){
        double newY = voltageToCanvasYCoordinate(voltage, sensitivity, offset);
        line.setTranslateY(newY);
        label.setTranslateY(newY - label.getHeight()/2);
        label.setText(String.format("%.2f", voltage));
        handle.setTranslateY(newY);
    }

    void updateThresholdLines(){
        Platform.runLater( () -> {
            setThresholdLineVoltage(xThresholdLine, xThresholdLineLabel,  xThresholdLineHandle, c.xTriggerLevelS.getValue(), c.xSensitivityS.getValue(), c.xOffsetS.getValue());
            setThresholdLineVoltage(yThresholdLine, yThresholdLineLabel,  yThresholdLineHandle, c.yTriggerLevelS.getValue(), c.ySensitivityS.getValue(), c.yOffsetS.getValue());
        });

    }

    void setXThresholdLineVoltage(double voltage){
        setThresholdLineVoltage(xThresholdLine, xThresholdLineLabel,  xThresholdLineHandle, voltage, c.xSensitivityS.getValue(), c.xOffsetS.getValue());
    }

    void setYThresholdLineVoltage(double voltage){
        setThresholdLineVoltage(yThresholdLine, yThresholdLineLabel,  yThresholdLineHandle, voltage, c.ySensitivityS.getValue(), c.yOffsetS.getValue());
    }

    void setThresholdLineVoltage(double voltage, Channel channel){
        switch (channel){
            case CHANNEL_X:
                setThresholdLineVoltage(xThresholdLine, xThresholdLineLabel,  xThresholdLineHandle, voltage, c.xSensitivityS.getValue(), c.xOffsetS.getValue());
                break;
            case CHANNEL_Y:
                setThresholdLineVoltage(yThresholdLine, yThresholdLineLabel,  yThresholdLineHandle, voltage, c.ySensitivityS.getValue(), c.yOffsetS.getValue());
        }
    }

    double voltageToCanvasYCoordinate(double voltage, double sensitivity, double offset){
        return -((voltage + offset)/sensitivity/GlobalConstants.GRATICULE_Y_DIVISIONS*getDrawableHeight()+drawingYOffset()-canvas.getHeight());
    }

    double canvasYCoordinateToVoltage(double coordinate, double sensitivity, double offset){
        return ((canvas.getHeight()-coordinate-drawingYOffset())/getDrawableHeight())*sensitivity*GlobalConstants.GRATICULE_Y_DIVISIONS - offset;
    }

    private void initScanLinesWithHandlesAndLabels(Line xScanLine, Polygon xScanLineHandle, Label xScanLineLabel, Line yScanLine, Polygon yScanLineHandle, Label yScanLineLabel){
        this.xScanLine = xScanLine;
        this.xScanLineHandle = xScanLineHandle;
        this.xScanLineLabel = xScanLineLabel;
        this.yScanLine = yScanLine;
        this.yScanLineHandle = yScanLineHandle;
        this.yScanLineLabel = yScanLineLabel;
        float scanLineLabelDefaultOffset = 11;
        initLineWithHandleAndLabel(this.xScanLineHandle, this.xScanLine, this.xScanLineLabel, LineOrientation.VERTICAL, canvas.getWidth()/2,  Channel.CHANNEL_X);
        initLineWithHandleAndLabel(this.yScanLineHandle, this.yScanLine, this.yScanLineLabel, LineOrientation.VERTICAL, canvas.getWidth()/2.5, Channel.CHANNEL_Y);

        xScanLineLabel.setTranslateX(canvas.getWidth()/2 - scanLineLabelDefaultOffset);
        yScanLineLabel.setTranslateX(canvas.getWidth()/2.5 - scanLineLabelDefaultOffset);
        updateScanLineLabels();
    }

    void updateScanLineLabels(){
        int timeIndex = (int)((xScanLine.getTranslateX()-drawingXOffset())/unitTime(c));
        if(xPolyline.getPoints().isEmpty()){
            xScanLineLabel.setText("N/A");
        }else {
            double scanCoordinate = xPolyline.getPoints().get(GeneralOperations.putInRange(2 * timeIndex + 1, 0, xPolyline.getPoints().size()));
            double scanVoltage = canvasYCoordinateToVoltage(scanCoordinate, c.xSensitivityS.getValue(), c.xOffsetS.getValue());
            if (GeneralOperations.isInRangeInc(scanVoltage, getMinVisibleValue(Channel.CHANNEL_X), getMaxVisibleValue(Channel.CHANNEL_X))){
                xScanLineLabel.setText(String.format("%.2f", scanVoltage));
            }else{
                // Out of visible range.
                xScanLineLabel.setText("O/R");
            }

        }

        timeIndex = (int)((yScanLine.getTranslateX()-drawingXOffset())/unitTime(c));
        if(yPolyline.getPoints().isEmpty()){
            yScanLineLabel.setText("N/A");
        }else {
            double scanCoordinate = yPolyline.getPoints().get(GeneralOperations.putInRange(2 * timeIndex + 1, 0, yPolyline.getPoints().size()));
            double scanVoltage = canvasYCoordinateToVoltage(scanCoordinate, c.ySensitivityS.getValue(), c.yOffsetS.getValue());
            if (GeneralOperations.isInRangeInc(scanVoltage, getMinVisibleValue(Channel.CHANNEL_Y), getMaxVisibleValue(Channel.CHANNEL_Y))) {
                yScanLineLabel.setText(String.format("%.2f", scanVoltage));
            }else{
                // Out of visible range.
                yScanLineLabel.setText("O/R");
            }
        }
    }

    private void initTraceLine(Line l, double startLocation, Channel channel, LineOrientation type){
        l.setStrokeWidth(1);
        l.getStrokeDashArray().addAll(3.0,1.0,1.0,1.0,3.0);
        switch (channel){
            case CHANNEL_X:
                l.setStroke(Color.RED);
                l.setStrokeDashOffset(0);
                break;
            case CHANNEL_Y:
                l.setStroke(Color.YELLOW);
                l.setStrokeDashOffset(3);
                break;
        }
        switch (type){
            case HORIZONTAL:
                l.setStartY(0);
                l.setEndY(0);
                l.setStartX(drawingXOffset());
                l.setEndX(canvas.getWidth() - drawingXOffset());
                l.setTranslateY(startLocation);
                break;
            case VERTICAL:
                l.setStartX(0);
                l.setEndX(0);
                l.setEndY(canvas.getHeight() - drawingYOffset());
                l.setStartY(drawingYOffset());
                l.setTranslateX(startLocation);
        }

    }

    private void initTraceLabel(Label l, double height, Channel channel, LineOrientation orientation){
        switch (channel){
            case CHANNEL_X:
                l.setTextFill(Color.RED);
                break;
            case CHANNEL_Y:
                l.setTextFill(Color.YELLOW);
        }
        switch (orientation){
            case HORIZONTAL:
                l.setTranslateX(canvas.getWidth() - drawingXOffset()/1.5 );
                break;
            case VERTICAL:
                l.setTranslateX(height);
        }
    }

    private void initTraceHandle(Polygon h, Line l, Label lab, double startLocation, Channel channel, LineOrientation orientation){
        h.getPoints().clear();
        switch (channel){
            case CHANNEL_X:
                h.setFill(Color.RED);
                break;
            case CHANNEL_Y:
                h.setFill(Color.YELLOW);
        }
        switch (orientation){
            case HORIZONTAL:
                h.getPoints().addAll(0.0, 0.0, 10.0, -8.0, 10.0, 8.0);
                h.setTranslateX(canvas.getWidth() - drawingXOffset());
                h.setTranslateY(startLocation);
                break;
            case VERTICAL:
                h.getPoints().clear();
                h.getPoints().addAll(0.0, 0.0, 8.0, -10.0, -8.0, -10.0);
                h.setTranslateX(startLocation);
                h.setTranslateY(drawingYOffset());
                h.setOnMouseDragged(mouseEvent -> {
                    double newX = GeneralOperations.putInRange(
                            h.getTranslateX() + mouseEvent.getX(),
                            drawingXOffset(),
                            canvas.getWidth() - drawingXOffset());
                    l.setTranslateX(newX);
                    h.setTranslateX(newX);
                    lab.setTranslateX(newX - lab.getWidth() / 2);
                    updateScanLineLabels();
                });
        }

    }

    private void initLineWithHandleAndLabel(Polygon handle, Line line, Label label, LineOrientation orientation, double startLocation, Channel channel){
        initTraceLine(line, startLocation, channel, orientation);
        initTraceLabel(label, startLocation, channel, orientation);
        initTraceHandle(handle, line, label, startLocation, channel, orientation);

        line.setVisible(true);
        handle.setVisible(true);
        label.setVisible(true);
    }

    private void initBackground(){
        clearCanvas();
        drawGraticule();
        refreshLabels();
    }

    private double getDrawableWidth(){
       return  canvas.getWidth() - 2*labelMargin;
    }

    private double getDrawableHeight(){
        return  canvas.getHeight() - 2*labelMargin;
    }

    private double drawingXOffset(){
        return  labelMargin;
    }

    private double drawingYOffset(){
        return  labelMargin;
    }

    private void drawGraticule(){
        double horizontalGraticuleStep = getDrawableWidth()/GlobalConstants.GRATICULE_X_DIVISIONS;
        double verticalGraticuleStep = getDrawableHeight()/GlobalConstants.GRATICULE_Y_DIVISIONS;

        gc.setLineDashes(5);
        gc.setStroke(Color.GRAY);
        for(int i=0;i<=GlobalConstants.GRATICULE_X_DIVISIONS;i++){
            gc.strokeLine(drawingXOffset() + i*horizontalGraticuleStep, drawingYOffset(), i*horizontalGraticuleStep + drawingXOffset(), c.canvas.getHeight() - drawingYOffset());
        }

        for(int i=0;i<=GlobalConstants.GRATICULE_Y_DIVISIONS;i++){
            gc.strokeLine(drawingXOffset(), drawingYOffset() + i*verticalGraticuleStep, c.canvas.getWidth() - drawingXOffset(),  i*verticalGraticuleStep + drawingYOffset());
        }
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(0);
    }

    public void refreshLabels(){
       clearLabels();
       drawLabels();
    }

    private void clearLabels(){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, drawingXOffset(), canvas.getHeight());
        gc.fillRect(0,canvas.getHeight() - drawingXOffset(), canvas.getWidth(), canvas.getWidth());
    }

    private double getVerticalGraticuleStep(){
        return getDrawableHeight()/GlobalConstants.GRATICULE_Y_DIVISIONS;
    }

    private double getHorizontalGraticuleStep(){
        return getDrawableHeight()/GlobalConstants.GRATICULE_Y_DIVISIONS;
    }

    private double getMaxVisibleValue(Channel channel){
        switch (channel){
            case CHANNEL_X:
                return GlobalConstants.GRATICULE_X_DIVISIONS*c.xSensitivityS.getValue() - c.xOffsetS.getValue();
            case CHANNEL_Y:
                return GlobalConstants.GRATICULE_Y_DIVISIONS*c.ySensitivityS.getValue() - c.yOffsetS.getValue();
            default:
                System.err.println("Invalid channel argument for getMaxVisibleValue in CanvasCaretaker.");
                return 1.0;
        }
    }

    private double getMinVisibleValue(Channel channel){
        switch (channel){
            case CHANNEL_X:
                return -c.xOffsetS.getValue();
            case CHANNEL_Y:
                return -c.yOffsetS.getValue();
            default:
                System.err.println("Invalid channel argument for getMaxVisibleValue in CanvasCaretaker.");
                return 1.0;
        }
    }

    private void drawLabels(){
        double horizontalGraticuleStep = getHorizontalGraticuleStep();
        double verticalGraticuleStep = getVerticalGraticuleStep();
        double labelXOffset = 15;

        gc.setFont(Font.font("", FontWeight.NORMAL, 13));

        gc.setFill(Color.RED);
        for(int i=0;i<=GlobalConstants.GRATICULE_Y_DIVISIONS;i++){
            gc.fillText(String.format("%.2f",  i*c.xSensitivityS.getValue() - c.xOffsetS.getValue()),labelXOffset, c.canvas.getHeight() - (drawingYOffset() + 0.20*drawingYOffset()+  i*verticalGraticuleStep));
        }
        //for(int i=0;i<=GlobalConstants.GRATICULE_X_DIVISIONS;i++){
        //    gc.fillText(String.format("%2.1e",  i*Math.pow(10, c.xTimePerDivisionS.getValue())),drawingXOffset() +  i*horizontalGraticuleStep, c.canvas.getHeight() - (5 + 0.20*drawingYOffset()));
        //}
        gc.setFill(Color.YELLOW);
        for(int i=0;i<=GlobalConstants.GRATICULE_Y_DIVISIONS;i++){
            gc.fillText(String.format("%.2f",  i*c.ySensitivityS.getValue() - c.yOffsetS.getValue()),labelXOffset, c.canvas.getHeight() - (drawingYOffset() - 0.20*drawingYOffset()+  i*verticalGraticuleStep));
        }
        //for(int i=0;i<=GlobalConstants.GRATICULE_X_DIVISIONS;i++){
        //    gc.fillText(String.format("%2.1e",  i*Math.pow(10, c.yTimePerDivisionS.getValue())),drawingXOffset() + i*horizontalGraticuleStep, c.canvas.getHeight() - (5 - 0.20*drawingYOffset()));
        //}
        gc.setFill(Color.BLACK);
    }

    private void clearCanvas(){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,c.canvas.getWidth(), c.canvas.getHeight());
    }

    private double unitTime(Controller c){
        return getDrawableWidth()/GlobalConstants.NUM_SAMPLES;
    }

    public  CanvasSettings readCanvasSettings(Controller c) throws BadInputException {
            CanvasSettings set = new CanvasSettings();

            set.yShowing = c.viewSettingsCaretaker.isYShowing();
            set.xShowing = c.viewSettingsCaretaker.isXShowing();
            set.xyMode = c.viewSettingsCaretaker.isXYMode();
            set.canvasVerticalNormalisation = Integer.parseInt(c.canvasVerticalNormalisationTF.getText());

            return set;
    }

    void requestXYRedrawFromBuffers(){
        Platform.runLater( () -> {
            xUpdateFromBuffer((byte)0);
            yUpdateFromBuffer((byte)0);
        });
    }

    private void hideChannelLine(Channel channel){
        switch (channel){
            case CHANNEL_X:
                xPolyline.setVisible(false);
                break;
            case CHANNEL_Y:
                yPolyline.setVisible(false);
        }
    }

    private void deleteChannelLineData(Channel channel){
        switch (channel){
            case CHANNEL_X:
                xPolyline.getPoints().clear();
                break;
            case CHANNEL_Y:
                yPolyline.getPoints().clear();
        }
    }

    private void showChannelLine(Channel channel){
        switch (channel){
            case CHANNEL_X:
                xPolyline.setVisible(true);
                break;
            case CHANNEL_Y:
                yPolyline.setVisible(true);
        }
    }

    private void redrawChannelLine(Channel channel){
        if(c.viewSettingsCaretaker.isXYMode()){
            xPolyline.getPoints().addAll(XYMerge());
            showChannelLine(Channel.CHANNEL_X);
        }else {
            switch (channel) {
                case CHANNEL_X:
                    xPolyline.getPoints().addAll(interleaveTime(xNormalisedBuffer));
                    if (c.viewSettingsCaretaker.isXShowing())
                        xPolyline.setVisible(true);
                    break;
                case CHANNEL_Y:
                    yPolyline.getPoints().addAll(interleaveTime(yNormalisedBuffer));
                    if (c.viewSettingsCaretaker.isYShowing())
                        yPolyline.setVisible(true);
                    break;
            }
        }
    }

    private void xUpdateFromBuffer(Byte updateType){
        if((updateType & GlobalConstants.CONTINOUS_UPDATE) == 0)
            refreshLabels();
        hideChannelLine(Channel.CHANNEL_X);
        deleteChannelLineData(Channel.CHANNEL_X);
        redrawChannelLine(Channel.CHANNEL_X);
        updateScanLineLabels();
    }

    private void yUpdateFromBuffer(Byte updateType){
        if((updateType & GlobalConstants.CONTINOUS_UPDATE) == 0)
            refreshLabels();
        hideChannelLine(Channel.CHANNEL_Y);
        deleteChannelLineData(Channel.CHANNEL_Y);
        redrawChannelLine(Channel.CHANNEL_Y);
        updateScanLineLabels();
    }

    void requestXChannelUpdate(ArrayList<Byte> buffer, Byte updateType){
        Platform.runLater( () -> {
            debugPrint10(convertUShort(buffer));
            this.xNormalisedBuffer = canvasNormalise(convertUShort(buffer),  readCanvasSettings(c));
            xUpdateFromBuffer(updateType);
        });
    }

    private void debugPrint10(ArrayList<Double> vals){
        for(int i=0;i<10;i++){
            System.out.print(vals.get(i));
            System.out.print(" ");
        }
        System.out.println("");
    }

    void requestYChannelUpdate(ArrayList<Byte> buffer, Byte updateType){
        Platform.runLater( () -> {
            this.yNormalisedBuffer =canvasNormalise(convertUShort(buffer), readCanvasSettings(c));
            yUpdateFromBuffer(updateType);
        });
    }

    private ArrayList<Double> canvasNormalise(ArrayList<Double> inBuffer, CanvasSettings set){
        return inBuffer.stream().map( val -> val/set.canvasVerticalNormalisation).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<Double> XYMerge(){
       int len = Math.min(xNormalisedBuffer.size(), yNormalisedBuffer.size()) ;
       ArrayList<Double> ret=  new ArrayList<>();
       for (int i=0;i<len;i++){
           ret.add(toCanvasWidthCoordinates(xNormalisedBuffer.get(i)));
           ret.add(toCanvasHeightCoordinates(yNormalisedBuffer.get(i)));
       }
       return ret;
    }

    private ArrayList<Double> interleaveTime(ArrayList<Double> normalisedData){
        // Every odd entry will be time entry as required by Polyline
        double unitTime = unitTime(c);
        ArrayList<Double> returnlist = new ArrayList<>();
        for (int i =0;i<normalisedData.size();i++){
            returnlist.add(drawingXOffset() + i*unitTime);
            returnlist.add(toCanvasHeightCoordinates(normalisedData.get(i)));
        }
        return returnlist;
    }

    private double toCanvasHeightCoordinates(double normalisedHeight){
        return  canvas.getHeight() - (drawingYOffset() + getDrawableHeight()*normalisedHeight);
    }

    private double toCanvasWidthCoordinates(double normalisedWidth){
        return  drawingYOffset() + getDrawableWidth()*normalisedWidth;
    }

}
