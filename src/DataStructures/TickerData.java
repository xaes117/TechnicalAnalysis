package DataStructures;

import DataStructures.OHLC;

import java.util.ArrayList;

public class TickerData {

    private int period;
    private ArrayList<OHLC> ohlcs;

    public TickerData(int period) {
        this.period = period;
        this.ohlcs = new ArrayList<OHLC>();
    }

    public int getPeriod() {
        return period;
    }

    public ArrayList<OHLC> getOhlcs() {
        return ohlcs;
    }
}
