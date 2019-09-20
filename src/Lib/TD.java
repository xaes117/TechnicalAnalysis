package Lib;

import DataStructures.OHLC;

import java.util.List;

public class TD {

    public static int getTD(List<OHLC> ohlcList) {

        int green = 0;
        int red = 0;

        int a = 0;
        int b = 0;

//        a = nz(sellsetup[1]) + 1
//        sellsetup = close > close[4] ? (a > 9 ? 1 : a) : 0
//
//        b = nz(buysetup[1]) + 1
//        buysetup = close < close[4] ? (b > 9 ? 1 : b) : 0

        for (int i = 4; i < ohlcList.size(); i++) {
            a = green + 1;
            green = ohlcList.get(i).getClose() > ohlcList.get(i - 4).getClose() ? (a > 9 ? 1 : a) : 0;

            b = red + 1;
            red = ohlcList.get(i).getClose() < ohlcList.get(i - 4).getClose() ? (b > 9 ? 1 : b) : 0;

        }

        // ---------------------
        // SCOUT SHORT TRADE
        // if red 2 below red 1
        if (red == 2) {
            if (TD.isBelow(TD.getCandle(ohlcList), TD.getSecondLastCandle(ohlcList))) {
                return -8;
            }
        }

        // if red 3 below red 2
        if (red == 3) {
            if (TD.isBelow(TD.getCandle(ohlcList), TD.getSecondLastCandle(ohlcList))) {
                return -5;
            }
        }

        if (red == 4) {
            if (TD.isBelow(TD.getCandle(ohlcList), TD.getSecondLastCandle(ohlcList))) {
                return -3;
            }
        }

        if (red == 5) {
            if (TD.isBelow(TD.getCandle(ohlcList), TD.getSecondLastCandle(ohlcList))) {
                return -2;
            }
        }

        // if green 9
        if (green == 9) {
            return -8;
        }

        // if green 8
        if (green == 8) {
            return -5;
        }

        // if green 7
        if (green == 7) {
            return -3;
        }

        // if green 6
        if (green == 6) {
            return -2;
        }
        // ---------------------

        // ---------------------
        // SCOUT LONG TRADE
        // if green 2 above green 1
        if (green == 2) {
            if (TD.isAbove(TD.getCandle(ohlcList), TD.getSecondLastCandle(ohlcList))) {
                return 8;
            }
        }

        if (green == 3) {
            if (TD.isAbove(TD.getCandle(ohlcList), TD.getSecondLastCandle(ohlcList))) {
                return 5;
            }
        }

        if (green == 4) {
            if (TD.isAbove(TD.getCandle(ohlcList), TD.getSecondLastCandle(ohlcList))) {
                return 3;
            }
        }

        if (green == 5) {
            if (TD.isAbove(TD.getCandle(ohlcList), TD.getSecondLastCandle(ohlcList))) {
                return 2;
            }
        }

        // if red 9
        if (red == 9) {
            return 8;
        }

        // if red 8
        if (red == 8) {
            return 5;
        }

        // if red 7
        if (red == 7) {
            return 3;
        }

        if (red == 6) {
            return 2;
        }
        // ---------------------

        return 0;

    }

    private static boolean isBelow(OHLC a, OHLC b) {
        return a.getLow() < b.getLow();
    }

    private static boolean isAbove(OHLC a, OHLC b) {
        return a.getHigh() < b.getHigh();
    }

    private static OHLC getCandle(List<OHLC> ohlcs) {
        return ohlcs.get(ohlcs.size() - 1);
    }

    private static OHLC getSecondLastCandle(List<OHLC> ohlcs) {
        return ohlcs.get(ohlcs.size() - 2);
    }

}
