package com.Engine;

import DataStructures.OHLC;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.Engine.TradeManager.compareCandles;
import static com.Engine.TradeManager.compareNCandles;
import static com.Engine.TradeManager.percentageChange;

public class TradeManagerTest extends TestCase {

    private OHLC prev;
    private OHLC current;
    private HashMap<String, Double> percentageChanges;

    public void setUp() throws Exception {
        this.prev = new OHLC(100, 1000, 10, 100, 100);
        this.current = new OHLC(110, 900, 11, 90, 150);
        this.percentageChanges = percentageChange(current, prev);
    }

    public void testPercentageChange() {
        Assert.assertEquals(-0.1, percentageChanges.get("highChange"));
        Assert.assertEquals(0.1, percentageChanges.get("openChange"));
        Assert.assertEquals(0.1, percentageChanges.get("lowChange"));
        Assert.assertEquals(-0.1, percentageChanges.get("closeChange"));
        Assert.assertEquals(0.5, percentageChanges.get("volChange"));
    }

//    public void testWithinRange() {
//        Assert.assertTrue(withinRange(0.01, 0.015, 1));
//        Assert.assertTrue(withinRange(-0.01, -0.015, 1));
//        Assert.assertFalse(withinRange(100, 85, 0.1));
//        Assert.assertFalse(withinRange(100, 115, 0.1));
//        Assert.assertTrue(withinRange(100, 100, 0.1));
//        Assert.assertTrue(withinRange(100, 95, -0.1));
//    }

    public void testCompareCandlesSimilar() {

        OHLC prev1 = new OHLC(10, 100, 1, 10, 10);
        OHLC current1 = new OHLC(11, 90, 1.1, 9, 15);

        Assert.assertTrue(compareCandles(this.current, this.prev, current1, prev1));

    }

    public void testCompareCandlesNotSimilar() {
        OHLC prev1 = new OHLC(10, 100, 1, 10, 10);
        OHLC current1 = new OHLC(100, 900, 1.1, 9, 15);

        Assert.assertFalse(compareCandles(this.current, this.prev, current1, prev1));
    }

    public void testCompareNCandles() {

        OHLC prevA = new OHLC(100, 1000, 10, 100, 100);

        OHLC prev2 = new OHLC(10, 100, 1, 10, 10);
        OHLC prev1 = new OHLC(10, 100, 1, 10, 10);
        OHLC current1 = new OHLC(11, 90, 1.1, 9, 15);

        List<OHLC> candleList1 = new ArrayList<OHLC>();
        candleList1.add(current);
        candleList1.add(prev);
        candleList1.add(prevA);

        List<OHLC> candleList2 = new ArrayList<OHLC>();
        candleList2.add(current1);
        candleList2.add(prev1);
        candleList2.add(prev2);

        Assert.assertTrue(compareNCandles(candleList1, candleList2));

    }

    public void testCompareNCandlesFalse() {

        OHLC prevA = new OHLC(100, 1000, 1, 1, 100);

        OHLC prev2 = new OHLC(10, 100, 1, 10, 10);
        OHLC prev1 = new OHLC(10, 100, 1, 10, 10);
        OHLC current1 = new OHLC(11, 90, 1.1, 9, 15);

        List<OHLC> candleList1 = new ArrayList<OHLC>();
        candleList1.add(current);
        candleList1.add(prev);
        candleList1.add(prevA);

        List<OHLC> candleList2 = new ArrayList<OHLC>();
        candleList2.add(current1);
        candleList2.add(prev1);
        candleList2.add(prev2);

        Assert.assertFalse(compareNCandles(candleList1, candleList2));

    }
}