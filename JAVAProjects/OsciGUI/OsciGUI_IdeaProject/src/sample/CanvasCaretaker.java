package sample;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static sample.GeneralOperations.*;

class CanvasCaretaker {
    private double labelMargin;

    byte [] xDataBuffer;
    byte [] yDataBuffer;
    long lastCanvasUpdateTime;
    Semaphore drawingSem = new  Semaphore(1);

    Polyline xPolyline;
    Polyline yPolyline;

    Canvas canvas;
    Controller c;
    GraphicsContext gc;

    CanvasCaretaker (Controller c, Polyline xPolyline, Polyline yPolyline){
        this.c = c;
        canvas = c.canvas;
        gc = canvas.getGraphicsContext2D();

        initMargin();
        initBuffers();
        initBackground();
        initPolyLines(xPolyline, yPolyline);

    }

    private void initMargin(){
        labelMargin = 35;
    }

    private void initBuffers(){
        xDataBuffer = new byte[GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES];
        yDataBuffer = new byte[GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES];
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

    private void initBackground(){
        clearCanvas();
        drawGraticule();
        refreshLabels();
    }

    private double getDrawableWidth(Controller c){
       return  c.canvas.getWidth() - 2*labelMargin;
    }

    private double getDrawableHeight(Controller c){
        return  c.canvas.getHeight() - 2*labelMargin;
    }

    private double drawingXOffset(){
        return  labelMargin;
    }

    private double drawingYOffset(){
        return  labelMargin;
    }

    private void drawGraticule(){
        double horizontalGraticuleStep = getDrawableWidth(c)/GlobalConstants.GRATICULE_X_DIVISIONS;
        double verticalGraticuleStep = getDrawableHeight(c)/GlobalConstants.GRATICULE_Y_DIVISIONS;

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
        gc.setStroke(Color.BLACK);
        gc.fillRect(0,0, drawingXOffset(), canvas.getHeight());
        gc.fillRect(0,canvas.getHeight() - drawingXOffset(), canvas.getWidth(), canvas.getWidth());
    }

    private void drawLabels(){

        double horizontalGraticuleStep = getDrawableWidth(c)/GlobalConstants.GRATICULE_X_DIVISIONS;
        double verticalGraticuleStep = getDrawableHeight(c)/GlobalConstants.GRATICULE_Y_DIVISIONS;

        gc.setFill(Color.RED);
        for(int i=0;i<=GlobalConstants.GRATICULE_Y_DIVISIONS;i++){
            gc.fillText(String.format("%.2f",  i*c.xSensitivityS.getValue() - c.xOffsetS.getValue()),5, c.canvas.getHeight() - (drawingYOffset() + 0.25*drawingYOffset()+  i*verticalGraticuleStep));
        }
        //for(int i=0;i<=GlobalConstants.GRATICULE_X_DIVISIONS;i++){
        //    gc.fillText(String.format("%2.1e",  i*Math.pow(10, c.xTimePerDivisionS.getValue())),drawingXOffset() +  i*horizontalGraticuleStep, c.canvas.getHeight() - (5 + 0.20*drawingYOffset()));
        //}
        gc.setFill(Color.YELLOW);
        for(int i=0;i<=GlobalConstants.GRATICULE_Y_DIVISIONS;i++){
            gc.fillText(String.format("%.2f",  i*c.ySensitivityS.getValue() - c.yOffsetS.getValue()),5, c.canvas.getHeight() - (drawingYOffset() - 0.25*drawingYOffset()+  i*verticalGraticuleStep));
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
                drawDatapoint(c, drawingXOffset() + (double) extractUShort(xData[i], xData[i+1])*getDrawableWidth(c)/set.canvasVerticalNormalisation, drawingYOffset() + getDrawableHeight(c) - (double) extractUShort(yData[i], yData[i+1])*getDrawableHeight(c)/set.canvasVerticalNormalisation);
            }
            gc.setFill(Color.BLACK);
            return;
        }

        if (set.xShowing){
            gc.setFill(Color.RED);
            for(int i =0;i<numBytes;i+=2){
                drawDatapoint(c, drawingXOffset () + (double) i/4*unitTime(c), drawingYOffset() + getDrawableHeight(c) - (double) extractUShort(xData[i], xData[i+1])*getDrawableHeight(c)/set.canvasVerticalNormalisation);
            }
            gc.setFill(Color.BLACK);
        }

        if (set.yShowing){
            gc.setFill(Color.YELLOW);
            for(int i =0;i<numBytes;i+=2){
                drawDatapoint(c, drawingXOffset () + (double) i/2*unitTime(c), drawingYOffset() +  getDrawableHeight(c) - (double) extractUShort(yData[i], yData[i+1])*getDrawableHeight(c)/set.canvasVerticalNormalisation);
            }
            gc.setFill(Color.BLACK);
        }

    }

    private void clearCanvas(){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,c.canvas.getWidth(), c.canvas.getHeight());
    }


    public  double unitTime(Controller c){
        return getDrawableWidth(c)/GlobalConstants.NUM_SAMPLES;
    }

    public  void requestCanvasUpdate(Controller c, byte [] xData, byte [] yData){
        return;

        //try {
        //    drawingSem.acquire();
        //} catch (InterruptedException e){ ; }
        //CanvasSettings set = readCanvasSettings(c);
        //ChannelSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);

        //System.arraycopy(xData, 0, xDataBuffer, 0, GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES);
        //System.arraycopy(yData, 0, yDataBuffer, 0, GlobalConstants.NUM_SAMPLES*GlobalConstants.SAMPLE_SIZE_BYTES);

        //Platform.runLater (() -> {
        //    clearCanvas(c);
        //    drawGraticule(c);
        //    drawGraphs(c, xData, yData, set);
        //    drawLabels(c, set, cset);
        //    drawingSem.release();
        //});
    }

    public  CanvasSettings readCanvasSettings(Controller c) throws BadInputException{
            CanvasSettings set = new CanvasSettings();

            set.yShowing = c.viewSettingsCaretaker.isYShowing();
            set.xShowing = c.viewSettingsCaretaker.isXShowing();
            set.xyMode = c.viewSettingsCaretaker.isXYMode();
            set.canvasVerticalNormalisation = Integer.parseInt(c.canvasVerticalNormalisationTF.getText());

            return set;
    }


    void requestXChannelUpdate(ArrayList<Byte> buffer){
        Platform.runLater( () -> {
            refreshLabels();
            deleteXPath();
            redrawXPath(canvasNormalise(convertUShort(buffer), readCanvasSettings(c)));
        });
    }

    void requestYChannelUpdate(ArrayList<Byte> buffer){
        Platform.runLater( () -> {
            refreshLabels();
            deleteYPath();
            redrawYPath(canvasNormalise(convertUShort(buffer), readCanvasSettings(c)));
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
        xPolyline.getPoints().addAll(interleaveTime(normalisedData));
        xPolyline.setVisible(true);
    }

    private void redrawYPath(ArrayList<Double> normalisedData){
        yPolyline.getPoints().addAll(interleaveTime(normalisedData));
        yPolyline.setVisible(true);
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
        return  canvas.getHeight() - (drawingYOffset() + getDrawableHeight(c)*normalisedHeight);
    }

}
