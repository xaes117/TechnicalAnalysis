package Lib;

import DataStructures.OHLC;

import javax.swing.text.NumberFormatter;
import java.util.Arrays;
import java.util.List;

public class MovingAverages {

    /**
     * Exponential Moving Average
     */


    public static double[] EMA(double[] prices, int period) throws Exception {

        if (period >= prices.length)
            throw new Exception("Given period is bigger then given set of prices");

        double smoothingConstant = 2d / (period + 1);

        double[] periodSma = new double[prices.length];
        double[] periodEma = new double[prices.length];

        for (int i = (period - 1); i < prices.length; i++) {
            double[] slice = Arrays.copyOfRange(prices, 0, i + 1);
            double[] smaResults = SMA(slice, period);
            periodSma[i] = smaResults[smaResults.length - 1];

            if (i == (period - 1)) {
                periodEma[i] = periodSma[i];
            } else if (i > (period - 1)) {
                // Formula: (Close - EMA(previous day)) x multiplier +
                // EMA(previous day)
                periodEma[i] = (prices[i] - periodEma[i - 1]) * smoothingConstant
                        + periodEma[i - 1];
            }

            periodEma[i] = periodEma[i];
        }

        return periodEma;
    }

    // To save time
    public static double[] SMA(List<OHLC> list, int period, int maxLookBack) throws Exception {

        List<OHLC> ohlcList = list.subList(list.size() - maxLookBack, list.size());

        double[] a = new double[ohlcList.size()];

        for (int i = 0; i < ohlcList.size(); i++) {
            a[i] = ohlcList.get(i).getClose();
        }

        return SMA(a, period);
    }

    public static double[] SMA(double[] price, int period) throws Exception {
        // ie: if you want 50 SMA then you need 50 data points
        if (price.length < period)
            throw new Exception("Not enough data points, given data size less then the indicated period");

        double[] results = new double[price.length];

        int maxLength = price.length - period;

        for (int i = 0; i <= maxLength; i++) {
            results[(i + period - 1)] = (Arrays.stream(Arrays.copyOfRange(price, i, (i + period))).sum()) / period;
        }

        return results;

    }
}
