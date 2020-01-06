package sample;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static sample.GeneralOperations.*;

class CanvasCaretaker {
    private double labelMargin;

    //byte [] xDataBuffer;
    //byte [] yDataBuffer;
    ArrayList<Double> yNormalisedBuffer;
    ArrayList<Double> xNormalisedBuffer;

    long lastCanvasUpdateTime;
    Semaphore drawingSem = new  Semaphore(1);

    Polyline xPolyline;
    Polyline yPolyline;

    Line xThresholdLine;
    Line yThresholdLine;
    Line xScanLine;
    Line yScanLine;
    Label xScanLineLabel;
    Label yScanLineLabel;
    Label xThresholdLineLabel;
    Label yThresholdLineLabel;
    Polygon xThresholdLineHandle;
    Polygon yThresholdLineHandle;
    Polygon xScanLineHandle;
    Polygon yScanLineHandle;

    CheckBox enableXScanLineCB;
    CheckBox enableYScanLineCB;
    CheckBox enableXThresholdLineCB;
    CheckBox enableYThresholdLineCB;

    Label lowerUnitLabel;
    Label upperUnitLabel;

    Canvas canvas;
    Controller c;
    GraphicsContext gc;

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

    private void initLineEnableCheckboxes(CheckBox newEnableXScanLineCB, CheckBox newEnableYScanLineCB,CheckBox newEnableXThresholdLineCB, CheckBox newEnableYThresholdLineCB){
        enableXScanLineCB = newEnableXScanLineCB;
        enableXScanLineCB.selectedProperty().bindBidirectional(xScanLine.visibleProperty());
        enableXScanLineCB.selectedProperty().bindBidirectional(xScanLineLabel.visibleProperty());
        enableXScanLineCB.selectedProperty().bindBidirectional(xScanLineHandle.visibleProperty());
        enableXScanLineCB.setSelected(false);

        enableYScanLineCB = newEnableYScanLineCB;
        enableYScanLineCB.selectedProperty().bindBidirectional(yScanLine.visibleProperty());
        enableYScanLineCB.selectedProperty().bindBidirectional(yScanLineLabel.visibleProperty());
        enableYScanLineCB.selectedProperty().bindBidirectional(yScanLineHandle.visibleProperty());
        enableYScanLineCB.setSelected(false);

        enableXThresholdLineCB = newEnableXThresholdLineCB;
        enableXThresholdLineCB.selectedProperty().bindBidirectional(xThresholdLine.visibleProperty());
        enableXThresholdLineCB.selectedProperty().bindBidirectional(xThresholdLineLabel.visibleProperty());
        enableXThresholdLineCB.selectedProperty().bindBidirectional(xThresholdLineHandle.visibleProperty());
        enableXThresholdLineCB.setSelected(false);

        enableYThresholdLineCB = newEnableYThresholdLineCB;
        enableYThresholdLineCB.selectedProperty().bindBidirectional(yThresholdLine.visibleProperty());
        enableYThresholdLineCB.selectedProperty().bindBidirectional(yThresholdLineLabel.visibleProperty());
        enableYThresholdLineCB.selectedProperty().bindBidirectional(yThresholdLineHandle.visibleProperty());
        enableYThresholdLineCB.setSelected(false);
    }

    private void initUnitLabels(Label newLowerUnitLabel, Label newUpperUnitLabel){
        double upperUnitLabelXOffset = 40;
        double upperUnitLabelYOffset = 15;
        double lowerUnitLabelXOffset = 10;
        double lowerUnitLabelYOffset = 25;

        upperUnitLabel = newLowerUnitLabel;
        upperUnitLabel.setText("[V/V]");
        upperUnitLabel.setFont(Font.font("", FontWeight.BOLD, 13));
        upperUnitLabel.setTranslateX(canvas.getWidth() - upperUnitLabelXOffset);
        upperUnitLabel.setTranslateY(upperUnitLabelYOffset);
        upperUnitLabel.setTextFill(Color.RED);

        lowerUnitLabel = newUpperUnitLabel;
        lowerUnitLabel.setText("[V/s]");
        lowerUnitLabel.setFont(Font.font("", FontWeight.BOLD, 13));
        lowerUnitLabel.setTranslateX(lowerUnitLabelXOffset);
        lowerUnitLabel.setTranslateY(canvas.getHeight() - lowerUnitLabelYOffset);
        lowerUnitLabel.setTextFill(Color.RED);
    }

    private void initMargin(){
        labelMargin = 45;
    }

    private void initBuffers(){
        //xDataBuffer = new byte[GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES];
        //yDataBuffer = new byte[GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES];
        yNormalisedBuffer = new ArrayList<>();
        xNormalisedBuffer = new ArrayList<>();
    }

    private void initPolyLines(Polyline newXPolyLine, Polyline newYPolyline){
        xPolyline = newXPolyLine;
        xPolyline.setStrokeWidth(3);
        xPolyline.setStrokeType(StrokeType.CENTERED);
        xPolyline.setStroke(Color.RED);
        xPolyline.setVisible(false);

        yPolyline = newYPolyline;
        yPolyline.setStrokeWidth(3);
        yPolyline.setStrokeType(StrokeType.CENTERED);
        yPolyline.setStroke(Color.YELLOW);
        yPolyline.setVisible(false);
    }

    private void initThresholdLinesWithHandlesAndLabels(Line xThresholdLine, Polygon xThresholdLineHandle, Label xThresholdLineLabel, Line yThresholdLine, Polygon yThresholdLineHandle, Label yThresholdLineLabel){
        this.xThresholdLine = xThresholdLine;
        this.xThresholdLineHandle = xThresholdLineHandle;
        this.xThresholdLineLabel = xThresholdLineLabel;
        this.yThresholdLine = yThresholdLine;
        this.yThresholdLineHandle = yThresholdLineHandle;
        this.yThresholdLineLabel = yThresholdLineLabel;
        double thresholdLineLabelOffsetDefault = 5;
        initLineWithHandleAndLabel(this.xThresholdLineHandle, this.xThresholdLine, this.xThresholdLineLabel, true, canvas.getHeight()/2, Color.RED, 1, 0, GlobalConstants.X_CHANNEL_ID);
        initLineWithHandleAndLabel(this.yThresholdLineHandle, this.yThresholdLine, this.yThresholdLineLabel, true, canvas.getHeight()/2.5, Color.YELLOW, 1, 3, GlobalConstants.Y_CHANNEL_ID);

        xThresholdLineLabel.setText("0");
        yThresholdLineLabel.setText("0");
        setXThresholdLineVoltage(0);
        setYThresholdLineVoltage(0);
        xThresholdLineLabel.setTranslateY(xThresholdLineLabel.getTranslateY() - thresholdLineLabelOffsetDefault);
        yThresholdLineLabel.setTranslateY(yThresholdLineLabel.getTranslateY() - thresholdLineLabelOffsetDefault);
    }

    private void setThresholdLineVoltage(Line line, Label label, Polygon handle, double voltage, double sensitivity, double offset){
        double newY = voltageToCanvasYCoordinate(voltage, sensitivity, offset);
        line.setTranslateY(newY);
        label.setTranslateY(newY - label.getHeight()/2);
        label.setText(String.format("%.2f", voltage));
        handle.setTranslateY(newY);
    }

    void setXThresholdLineVoltage(double voltage){
        setThresholdLineVoltage(xThresholdLine, xThresholdLineLabel,  xThresholdLineHandle, voltage, c.xSensitivityS.getValue(), c.xOffsetS.getValue());
    }

    void setYThresholdLineVoltage(double voltage){
        setThresholdLineVoltage(yThresholdLine, yThresholdLineLabel,  yThresholdLineHandle, voltage, c.ySensitivityS.getValue(), c.yOffsetS.getValue());
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
        initLineWithHandleAndLabel(this.xScanLineHandle, this.xScanLine, this.xScanLineLabel, false, canvas.getWidth()/2, Color.RED, 1, 0, GlobalConstants.X_CHANNEL_ID);
        initLineWithHandleAndLabel(this.yScanLineHandle, this.yScanLine, this.yScanLineLabel, false, canvas.getWidth()/2.5, Color.YELLOW, 1, 3, GlobalConstants.Y_CHANNEL_ID);

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
            xScanLineLabel.setText(String.format("%.2f",
                    canvasYCoordinateToVoltage(scanCoordinate, c.xSensitivityS.getValue(),
                            c.xOffsetS.getValue())));
        }

        timeIndex = (int)((yScanLine.getTranslateX()-drawingXOffset())/unitTime(c));
        if(yPolyline.getPoints().isEmpty()){
            yScanLineLabel.setText("N/A");
        }else {
            double scanCoordinate = yPolyline.getPoints().get(GeneralOperations.putInRange(2 * timeIndex + 1, 0, yPolyline.getPoints().size()));
            yScanLineLabel.setText(String.format("%.2f",
                    canvasYCoordinateToVoltage(scanCoordinate, c.ySensitivityS.getValue(),
                            c.yOffsetS.getValue())));
        }
    }

    private void initLineWithHandleAndLabel(Polygon handle, Line line, Label label, boolean isHorizontal, double startLocation, Paint color, double lineWidth, double lineDashOffset, int channelID){

        handle.getPoints().clear();
        label.setTextFill(color);
        if(isHorizontal){
            label.setTranslateX(canvas.getWidth() - drawingXOffset()/1.5 );

            line.setStroke(color);
            line.setStrokeWidth(lineWidth);
            line.setStrokeDashOffset(lineDashOffset);
            line.getStrokeDashArray().addAll(3.0,1.0,1.0,1.0,3.0);
            line.setStartX(drawingXOffset());
            line.setStartY(0);
            line.setEndY(0);
            line.setEndX(canvas.getWidth() - drawingXOffset());
            line.setTranslateY(startLocation);

            handle.getPoints().addAll(0.0, 0.0, 10.0, -8.0, 10.0, 8.0);
            handle.setTranslateX(canvas.getWidth() - drawingXOffset());
            handle.setTranslateY(startLocation);
            handle.setFill(color);
        }else{
            label.setTranslateX(startLocation);

            line.setStroke(color);
            line.setStrokeWidth(lineWidth);
            line.setStrokeDashOffset(lineDashOffset);
            line.getStrokeDashArray().addAll(3.0,1.0,1.0,1.0,3.0);
            line.setStartX(0);
            line.setStartY(drawingYOffset());
            line.setEndX(0);
            line.setTranslateX(startLocation);
            line.setEndY(canvas.getHeight() - drawingYOffset());

            handle.getPoints().clear();
            handle.getPoints().addAll(0.0, 0.0, 8.0, -10.0, -8.0, -10.0);
            handle.setTranslateX(startLocation);
            handle.setTranslateY(drawingYOffset());
            handle.setFill(color);

            handle.setOnMouseDragged(mouseEvent -> {
                double newX = GeneralOperations.putInRange(
                        handle.getTranslateX() + mouseEvent.getX(),
                        drawingXOffset(),
                        canvas.getWidth()-drawingXOffset());
                line.setTranslateX(newX);
                handle.setTranslateX(newX);
                label.setTranslateX(newX - label.getWidth()/2);
                updateScanLineLabels();
            });
        }
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

    private  void drawDatapoint(Controller c, double x, double y){
        gc.fillOval(x,y,3,3);
    }

    private  void drawGraphs(Controller c, byte [] xData, byte [] yData, CanvasSettings set){
        int numBytes = GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES;

        if (set.xyMode){
            gc.setFill(Color.RED);
            for(int i =0;i<numBytes;i+=2){
                drawDatapoint(c, drawingXOffset() + (double) extractUShort(xData[i], xData[i+1])*getDrawableWidth()/set.canvasVerticalNormalisation, drawingYOffset() + getDrawableHeight() - (double) extractUShort(yData[i], yData[i+1])*getDrawableHeight()/set.canvasVerticalNormalisation);
            }
            gc.setFill(Color.BLACK);
            return;
        }

        if (set.xShowing){
            gc.setFill(Color.RED);
            for(int i =0;i<numBytes;i+=2){
                drawDatapoint(c, drawingXOffset () + (double) i/4*unitTime(c), drawingYOffset() + getDrawableHeight() - (double) extractUShort(xData[i], xData[i+1])*getDrawableHeight()/set.canvasVerticalNormalisation);
            }
            gc.setFill(Color.BLACK);
        }

        if (set.yShowing){
            gc.setFill(Color.YELLOW);
            for(int i =0;i<numBytes;i+=2){
                drawDatapoint(c, drawingXOffset () + (double) i/2*unitTime(c), drawingYOffset() +  getDrawableHeight() - (double) extractUShort(yData[i], yData[i+1])*getDrawableHeight()/set.canvasVerticalNormalisation);
            }
            gc.setFill(Color.BLACK);
        }

    }

    private void clearCanvas(){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,c.canvas.getWidth(), c.canvas.getHeight());
    }


    public  double unitTime(Controller c){
        return getDrawableWidth()/GlobalConstants.NUM_SAMPLES;
    }

    public  CanvasSettings readCanvasSettings(Controller c) throws BadInputException{
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

    private void xUpdateFromBuffer(Byte updateType){
        if((updateType & GlobalConstants.CONTINOUS_UPDATE) == 0)
            refreshLabels();
        deleteXPath();
        redrawXPath(this.xNormalisedBuffer);
        updateScanLineLabels();
    }

    private void yUpdateFromBuffer(Byte updateType){
        if((updateType & GlobalConstants.CONTINOUS_UPDATE) == 0)
            refreshLabels();
        deleteYPath();
        redrawYPath(this.yNormalisedBuffer);
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

    private void deleteXPath(){
        xPolyline.getPoints().clear();
    }

    private void deleteYPath(){
        yPolyline.getPoints().clear();
    }

    private void redrawXPath(ArrayList<Double> normalisedData){
        if(!c.viewSettingsCaretaker.isXYMode()) {
            xPolyline.getPoints().addAll(interleaveTime(normalisedData));
            if(c.viewSettingsCaretaker.isXShowing())
                xPolyline.setVisible(true);
        }else{
            xPolyline.getPoints().addAll(XYMerge());
        }
    }

    private void redrawYPath(ArrayList<Double> normalisedData){
        if(!c.viewSettingsCaretaker.isXYMode()) {
            yPolyline.getPoints().addAll(interleaveTime(normalisedData));
            if (c.viewSettingsCaretaker.isYShowing())
                yPolyline.setVisible(true);
        }else{
            xPolyline.getPoints().addAll(XYMerge());
        }
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
