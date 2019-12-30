package sample;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class CanvasCaretaker {
    Canvas canvas;
    Integer gridNumberOfPositionsX;
    Integer gridNumberOfPositionsY;
    Double gridCellWidth;
    Double gridCellHeight;
    ArrayList<ArrayList<Boolean>> touchGrid;
    Double dotRadius;

    public CanvasCaretaker(Canvas controlledCanvas, int numXPos, int numYPos){
        canvas = controlledCanvas;

        gridNumberOfPositionsX = numXPos;
        gridNumberOfPositionsY = numYPos;

        touchGrid = new ArrayList<ArrayList<Boolean>>();
        for(int x=0;x<numXPos;x++) {
            touchGrid.add(new ArrayList<Boolean>());
            for(int y=0;y<numYPos;y++){
                touchGrid.get(x).add(false);
            }
        }

        canvas.setOnMouseClicked(mouseEvent -> handleMouseTouch(mouseEvent.getX(), mouseEvent.getY()));
        canvas.setOnMouseDragEntered(mouseDragEvent -> handleMouseTouch(mouseDragEvent.getX(), mouseDragEvent.getY()));

        dotRadius = 5.0;
    }

    public void handleMouseTouch(double x, double y){
        int touchX = (int)(gridNumberOfPositionsX*x/canvas.getWidth());
        int touchY = (int)(gridNumberOfPositionsY*y/canvas.getWidth());
        touchGrid.get(touchX).stream().map( (e) -> false);
        touchGrid.get(touchX).set(touchY, true);

        gridCellWidth  = canvas.getWidth()/gridNumberOfPositionsX;
        gridCellHeight = canvas.getHeight()/gridNumberOfPositionsY;

        replaceDotInColumn(touchX, touchY);
    }

    public void replaceDotInColumn(int x, int newY){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for(int y=0;y<touchGrid.get(x).size();y++){
            touchGrid.get(x).set(y, false);
            clearCell(gc, x, y);
        }
        touchGrid.get(x).set(newY, true);
        fillCell(gc, x, newY);
    }

    public void clearCell(GraphicsContext gc, int x, int y){
        Platform.runLater(() -> {
            gc.clearRect((x+0.5)*gridCellWidth, canvas.getHeight() - (y+0.5)*gridCellHeight, gridCellWidth/2, gridCellWidth/2);
        });
    }

    public void fillCell(GraphicsContext gc, int x, int y){
        Platform.runLater(() -> {
            gc.fillOval((x+0.5)*gridCellWidth, canvas.getHeight() - (y+0.5)*gridCellHeight, dotRadius, dotRadius);
        });
    }
}
