package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class Controller {
    @FXML LineChart lc1;
    XYChart.Series<Double, Double> osciSeries = new XYChart.Series();

    public void initialize(){
        lc1.getData().add(osciSeries);
    }

    void addData(double x, double y){
        osciSeries.getData().add(new XYChart.Data<>(x,y));
    }

    void setData(XYChart.Series inSeries){
        lc1.getData().setAll(inSeries);
    }
}
