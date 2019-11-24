package sample;

import javafx.scene.chart.LineChart;

public class ChartCaretaker {
    public static void initChartSettings(Controller c){
        c.yChannelShowCB.setSelected(true);
        c.xChannelShowCB.setSelected(true);
    }

    public static void updateChartSettings(Controller c, LineChart<Double, Double> chart){
        try {
            ChannelControlSettings cset = ChannelControlCaretaker.readChannelControlsSettings(c);

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

    public static void replaceChartDataX(){

    }

    public static void replaceChartDataY(){

    }
}

