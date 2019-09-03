package JfreeCharts;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import DataStructures.OHLC;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing a candlestick chart.
 */
public class PlotChart extends ApplicationFrame {

    /**
     * A demonstration application showing a candlestick chart.
     *
     * @param title  the frame title.
     */

    public PlotChart(String title, List<OHLC> ohlcList) {
        super(title);
        JPanel chartPanel = createDemoPanel(title, ohlcList);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return The dataset.
     */
    private static JFreeChart createChart(OHLCDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createCandlestickChart(
                title,
                "",
                "Value",
                dataset,
                true
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);
        axis.setUpperMargin(0.0);
        axis.setLowerMargin(0.0);
        return chart;
    }

    private static final Calendar calendar = Calendar.getInstance();

    /**
     * Returns a date using the default locale and timezone.
     *
     * @param y  the year (YYYY).
     * @param m  the month (1-12).
     * @param d  the day of the month.
     * @param hour  the hour of the day.
     * @param min  the minute of the hour.
     *
     * @return A date.
     */
    private static Date createDate(int y, int m, int d, int hour, int min) {
        calendar.clear();
        calendar.set(y, m - 1, d, hour, min);
        return calendar.getTime();
    }

    public static void plotData(String title, List<OHLC> ohlcList) {
        PlotChart demo = new PlotChart(title, ohlcList);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    public static OHLCDataset createDataset(String key, List<OHLC> ohlcList) {
        Date[] date = new Date[ohlcList.size()];
        double[] high = new double[ohlcList.size()];
        double[] low = new double[ohlcList.size()];
        double[] open = new double[ohlcList.size()];
        double[] close = new double[ohlcList.size()];
        double[] volume = new double[ohlcList.size()];

        int hour = 1;
        int day = 1;

        for (int i = 0; i < ohlcList.size(); i++) {
            date[i] = createDate(2001, 1, day, hour, 0);
            high[i] = ohlcList.get(i).getHigh();
            low[i] = ohlcList.get(i).getLow();
            open[i] = ohlcList.get(i).getOpen();
            close[i] = ohlcList.get(i).getClose();
            volume[i] = ohlcList.get(i).getVolume();

            hour++;

            if (hour > 24) {
                hour = 1;
                day++;
            }
        }
        return new DefaultHighLowDataset(key, date, high, low, open, close, volume);
    }

    public static JPanel createDemoPanel(String key, List<OHLC> ohlcList) {
        JFreeChart chart = createChart(createDataset(key, ohlcList), key);
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

}