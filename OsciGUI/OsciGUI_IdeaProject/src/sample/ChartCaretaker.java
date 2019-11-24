package sample;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class ChartCaretaker {
    public static void initChartSettings(Controller c){
        c.yChannelShowCB.setSelected(true);
        c.xChannelShowCB.setSelected(true);
        c.chart.setAnimated(false);
    }

    public static void updateChartSettings(Controller c, LineChart<Double, Double> chart){
        try {
            ChannelSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);

            c.chartYAxis.setUpperBound((GlobalConstants.GRATICULE_Y_DIVISIONS/2.0)*(cset.ySensitivity));
            c.chartYAxis.setLowerBound(-(GlobalConstants.GRATICULE_Y_DIVISIONS/2.0)*(cset.ySensitivity));
            c.chartYAxis.setTickUnit(cset.ySensitivity);
            c.chartXAxis.setUpperBound((GlobalConstants.GRATICULE_X_DIVISIONS/2.0)*(cset.xSensitivity));
            c.chartXAxis.setLowerBound(-(GlobalConstants.GRATICULE_X_DIVISIONS/2.0)*(cset.xSensitivity));
            c.chartXAxis.setTickUnit(cset.xSensitivity);

        } catch (BadInputException e){
        }
    }

    public static ChartSettings readChartSettings(Controller c) throws BadInputException{

        ChartSettings set = new ChartSettings();

        set.yShowing = c.yChannelShowCB.isSelected();
        set.xShowing = c.xChannelShowCB.isSelected();

        return set;
    }

    public static void requestChartUpdate(Controller c,  byte [] xData, int xOffset, byte [] yData, int yOffset){
        c.xDataSeries=  new XYChart.Series<>();
        c.yDataSeries=  new XYChart.Series<>();

        ChartSettings set=  readChartSettings(c);
        ChannelSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);

        //int tmp;
        //double lowerBound;
        //double upperBound;

        //ArrayList<XYChart.Series<Double, Double>> sList = new ArrayList<>();

        //XYChart.Series<Double, Double> xSeries = null;
        //if (set.xShowing){
        //    xSeries = new XYChart.Series<>();
        //    lowerBound =-(GlobalConstants.GRATICULE_X_DIVISIONS/2.0)*(cset.xSensitivity);
        //    upperBound = (GlobalConstants.GRATICULE_X_DIVISIONS/2.0)*(cset.xSensitivity);
        //    for(int i =0;i<xOffset;i+=2){
        //        tmp = (int)xData[i];
        //        tmp |= ((int)(xData[i+1]) << 8);
        //        if (xOffset > 2) {
        //            XYChart.Data<Double, Double> d = new XYChart.Data<Double, Double>(lowerBound + (double) i * (upperBound - lowerBound) / (xOffset - 2), (double) tmp / GlobalConstants.MEASUREMENT_LEVELS);
        //            xSeries.getData().add(d);
        //        }else{
        //            XYChart.Data<Double, Double> d = new XYChart.Data<Double, Double>(lowerBound, (double) tmp / GlobalConstants.MEASUREMENT_LEVELS);
        //            xSeries.getData().add(d);
        //        }
        //    }
        //    sList.add(xSeries);
        //}

        //XYChart.Series<Double, Double> ySeries = null;
        //if(set.yShowing){
        //    ySeries = new XYChart.Series<>();
        //    lowerBound =-(GlobalConstants.GRATICULE_Y_DIVISIONS/2.0)*(cset.ySensitivity);
        //    upperBound = (GlobalConstants.GRATICULE_Y_DIVISIONS/2.0)*(cset.ySensitivity);
        //    for(int i =0;i<yOffset;i+=2){
        //        tmp = (int)yData[i];
        //        tmp |= ((int)(yData[i+1]) << 8);
        //        if (yOffset > 2){
        //            XYChart.Data<Double, Double> d = new XYChart.Data<Double, Double>(lowerBound + (double)i*(upperBound - lowerBound)/(yOffset-2), (double)tmp/GlobalConstants.MEASUREMENT_LEVELS);
        //            ySeries.getData().add(d);
        //        }else{
        //            XYChart.Data<Double, Double> d = new XYChart.Data<Double, Double>(lowerBound, (double)tmp/GlobalConstants.MEASUREMENT_LEVELS);
        //            ySeries.getData().add(d);
        //        }
        //    }
        //    sList.add(ySeries);
        //}

        //c.chart.getData().setAll(xSeries, ySeries);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                int tmp;
                double lowerBound;
                double upperBound;

                ArrayList<XYChart.Series<Double,Double>> sList = new ArrayList<>();

                XYChart.Series<Double, Double> xSeries = null;
                if (set.xShowing){
                    xSeries = new XYChart.Series<>();
                    lowerBound =-(GlobalConstants.GRATICULE_X_DIVISIONS/2.0)*(cset.xSensitivity);
                    upperBound = (GlobalConstants.GRATICULE_X_DIVISIONS/2.0)*(cset.xSensitivity);
                    for(int i =0;i<xOffset;i+=2){
                        tmp = 0;
                        tmp = (0x000000ff & (int)xData[i]);
                        tmp |= (0x0000ff00 & ((int)xData[i+1] << 8));
                        if (xOffset > 2) {
                            XYChart.Data<Double, Double> d = new XYChart.Data<Double, Double>(lowerBound + (double) i * (upperBound - lowerBound) / (xOffset - 2), (double) tmp / GlobalConstants.MEASUREMENT_LEVELS);
                            xSeries.getData().add(d);
                        }else{
                            XYChart.Data<Double, Double> d = new XYChart.Data<Double, Double>(lowerBound, (double) tmp / GlobalConstants.MEASUREMENT_LEVELS);
                            xSeries.getData().add(d);
                        }
                    }
                    sList.add(xSeries);
                }

                XYChart.Series<Double, Double> ySeries = null;
                if(set.yShowing){
                    ySeries = new XYChart.Series<>();
                    lowerBound =-(GlobalConstants.GRATICULE_Y_DIVISIONS/2.0)*(cset.ySensitivity);
                    upperBound = (GlobalConstants.GRATICULE_Y_DIVISIONS/2.0)*(cset.ySensitivity);
                    for(int i =0;i<yOffset;i+=2){
                        tmp = 0;
                        tmp = (0x000000ff & (int)yData[i]);
                        tmp |= (0x0000ff00 & ((int)yData[i+1] << 8));
                        if (yOffset > 2){
                            XYChart.Data<Double, Double> d = new XYChart.Data<Double, Double>(lowerBound + (double)i*(upperBound - lowerBound)/(yOffset-2), (double)tmp/GlobalConstants.MEASUREMENT_LEVELS);
                            ySeries.getData().add(d);
                        }else{
                            XYChart.Data<Double, Double> d = new XYChart.Data<Double, Double>(lowerBound, (double)tmp/GlobalConstants.MEASUREMENT_LEVELS);
                            ySeries.getData().add(d);
                        }
                    }
                    sList.add(ySeries);
                }

                c.chart.getData().setAll(xSeries, ySeries);

            }
        });
    }
}

