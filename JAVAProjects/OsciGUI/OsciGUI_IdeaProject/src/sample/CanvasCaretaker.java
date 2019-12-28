package sample;

import javafx.application.Platform;
import javafx.scene.paint.Color;

public class CanvasCaretaker {
    double labelMargin;

    public void init(Controller c){
        c.yChannelShowCB.setSelected(true);
        c.xChannelShowCB.setSelected(true);
        c.gc = c.canvas.getGraphicsContext2D();

        c.canvasXDataBuffer = new byte[SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES];
        c.canvasYDataBuffer = new byte[SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES];

        // Default settings
        labelMargin = 20;
    }

    public double getDrawableWidth(Controller c){
       return  c.canvas.getWidth() - 2*labelMargin;
    }

    public double getDrawableHeight(Controller c){
        return  c.canvas.getHeight() - 2*labelMargin;
    }

    public double drawingXOffset(){
        return  labelMargin;
    }

    public double drawingYOffset(){
        return  labelMargin;
    }

    public void drawGraticule(Controller c){
        double horizontalGraticuleStep = getDrawableWidth(c)/GlobalConstants.GRATICULE_X_DIVISIONS;
        double verticalGraticuleStep = getDrawableHeight(c)/GlobalConstants.GRATICULE_Y_DIVISIONS;

        c.gc.setLineDashes(5);
        c.gc.setStroke(Color.GRAY);
        for(int i=0;i<=GlobalConstants.GRATICULE_X_DIVISIONS;i++){
            c.gc.strokeLine(drawingXOffset() + i*horizontalGraticuleStep, drawingYOffset(), i*horizontalGraticuleStep + drawingXOffset(), c.canvas.getHeight() - drawingYOffset());
        }

        for(int i=0;i<=GlobalConstants.GRATICULE_Y_DIVISIONS;i++){
            c.gc.strokeLine(drawingXOffset(), drawingYOffset() + i*verticalGraticuleStep, c.canvas.getWidth() - drawingXOffset(),  i*verticalGraticuleStep + drawingYOffset());
        }
        c.gc.setStroke(Color.BLACK);
        c.gc.setLineDashes(0);
    }

    public void drawLabels(Controller c, CanvasSettings set, ChannelSettings cset){

        //double horizontalGraticuleStep = getDrawableWidth(c)/GlobalConstants.GRATICULE_X_DIVISIONS;
        //double verticalGraticuleStep = getDrawableHeight(c)/GlobalConstants.GRATICULE_Y_DIVISIONS;

        //for(int i=0;i<=GlobalConstants.GRATICULE_X_DIVISIONS;i++){
        //    c.gc.fillText(String.format("%g",  i*c.xSensitivityS.getValue()),drawingXOffset() + i*horizontalGraticuleStep, 0.5*drawingYOffset());
        //}
    }

    private  void drawDatapoint(Controller c, double x, double y){
        c.gc.fillOval(x,y,3,3);
    }

    private  void drawGraphs(Controller c, byte [] xData, byte [] yData, CanvasSettings set){
        int numBytes = SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES;

        if (set.xyMode){
            c.gc.setFill(Color.RED);
            for(int i =0;i<numBytes;i+=2){
                drawDatapoint(c, drawingXOffset() + (double) GeneralOperations.extractUShort(xData[i], xData[i+1])*getDrawableWidth(c)/set.canvasVerticalNormalisation, drawingYOffset() + getDrawableHeight(c) - (double) GeneralOperations.extractUShort(yData[i], yData[i+1])*getDrawableHeight(c)/set.canvasVerticalNormalisation);
            }
            c.gc.setFill(Color.BLACK);
            return;
        }

        if (set.xShowing){
            c.gc.setFill(Color.RED);
            for(int i =0;i<numBytes;i+=2){
                drawDatapoint(c, drawingXOffset () + (double) i/2*unitTime(c), drawingYOffset() + getDrawableHeight(c) - (double) GeneralOperations.extractUShort(xData[i], xData[i+1])*getDrawableHeight(c)/set.canvasVerticalNormalisation);
            }
            c.gc.setFill(Color.BLACK);
        }

        if (set.yShowing){
            c.gc.setFill(Color.YELLOW);
            for(int i =0;i<numBytes;i+=2){
                drawDatapoint(c, drawingXOffset () + (double) i/2*unitTime(c), drawingYOffset() +  getDrawableHeight(c) - (double) GeneralOperations.extractUShort(yData[i], yData[i+1])*getDrawableHeight(c)/set.canvasVerticalNormalisation);
            }
            c.gc.setFill(Color.BLACK);
        }

    }

    private static void clearCanvas(Controller c){
        c.gc.clearRect(0,0,c.canvas.getWidth(), c.canvas.getHeight());
    }

    public  void requestClearCanvas(Controller c){
        Platform.runLater( () -> {
            clearCanvas(c);
        });
    }

    public  void requestXDataPointWrite(Controller c, byte [] x, int xIdx, double t, CanvasSettings set){
        Platform.runLater(() -> {
            c.gc.setFill(Color.RED);
            drawDatapoint(c, drawingXOffset () + (double) t*unitTime(c), drawingYOffset() +  c.canvas.getHeight() - (double) GeneralOperations.extractUShort(x[xIdx], x[xIdx+1])*c.canvas.getHeight()/set.canvasVerticalNormalisation );
            c.gc.setFill(Color.BLACK);
        });
    }

    public  double unitTime(Controller c){
        return getDrawableWidth(c)/SerialProtocol.NUM_SAMPLES;
    }

    public  void requestYDataPointWrite(Controller c, byte [] y, int yIdx, double t, CanvasSettings set){
        Platform.runLater(() -> {
            c.gc.setFill(Color.YELLOW);
            drawDatapoint(c, drawingXOffset() + (double) t*unitTime(c), drawingYOffset() +  c.canvas.getHeight() - (double) GeneralOperations.extractUShort(y[yIdx], y[yIdx+1])*c.canvas.getHeight()/set.canvasVerticalNormalisation );
            c.gc.setFill(Color.BLACK);
        });
    }

    public  void requestCanvasUpdate(Controller c, byte [] xData, byte [] yData){

        try {
            c.canvasDrawSemaphore.acquire();
        } catch (InterruptedException e){ ; }
        CanvasSettings set = readCanvasSettings(c);
        ChannelSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);

        System.arraycopy(xData, 0, c.canvasXDataBuffer, 0, SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES);
        System.arraycopy(yData, 0, c.canvasYDataBuffer, 0, SerialProtocol.NUM_SAMPLES*SerialProtocol.SAMPLE_SIZE_BYTES);

        Platform.runLater (() -> {
            clearCanvas(c);
            drawGraticule(c);
            drawGraphs(c, xData, yData, set);
            drawLabels(c, set, cset);
            c.canvasDrawSemaphore.release();
        });
    }

    public  CanvasSettings readCanvasSettings(Controller c) throws BadInputException{
            CanvasSettings set = new CanvasSettings();

            set.yShowing = c.yChannelShowCB.isSelected();
            set.xShowing = c.xChannelShowCB.isSelected();
            set.xyMode = c.xyModeCB.isSelected();
            set.canvasVerticalNormalisation = Integer.parseInt(c.canvasVerticalNormalisationTF.getText());

            return set;
    }
}
