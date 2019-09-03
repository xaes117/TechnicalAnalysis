package com.Engine;

import DataStructures.OHLC;
import DataStructures.Tuple;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class RiskManagerTest extends TestCase {

    private List<OHLC> matchingPattern;
    private OHLC ohlc1;
    private OHLC ohlc2;
    private OHLC ohlc3;
    private OHLC ohlc4;

    public void setUp() throws Exception {
        super.setUp();
        this.ohlc1 = new OHLC(0, 0, 0, 10, 0);
        this.ohlc2 = new OHLC(0, 0, 0, 20, 0);
        this.ohlc3 = new OHLC(0, 0, 0, 11, 0);
        this.ohlc4 = new OHLC(0, 0, 0, 9, 0);

    }

    public void testIsBullish() {
        this.matchingPattern = new ArrayList<OHLC>();
        this.matchingPattern.add(this.ohlc1);
        this.matchingPattern.add(this.ohlc2);
        Assert.assertTrue(RiskManager.IsBullish(this.matchingPattern));

        this.matchingPattern.add(this.ohlc3);
        this.matchingPattern.add(this.ohlc4);
        Assert.assertFalse(RiskManager.IsBullish(this.matchingPattern));
    }

    public void testIsBearish() {
        this.matchingPattern = new ArrayList<OHLC>();
        this.matchingPattern.add(this.ohlc1);
        this.matchingPattern.add(this.ohlc2);
        Assert.assertFalse(RiskManager.IsBearish(this.matchingPattern));

        this.matchingPattern.add(this.ohlc3);
        this.matchingPattern.add(this.ohlc4);
        Assert.assertTrue(RiskManager.IsBearish(this.matchingPattern));
    }

//            0.25	4	1
//            0.5	500	250
//            0.75	1	0.75
//            1.5	505	251.75
//            0.4985148515


    public void testCalcWeightedPercentageGain() {
        Tuple<Double, Double> a = new Tuple<Double, Double>(0.25, 4.0);
        Tuple<Double, Double> b = new Tuple<Double, Double>(0.5, 500.0);
        Tuple<Double, Double> c = new Tuple<Double, Double>(0.75, 1.0);

        List<Tuple<Double, Double>> tupleList = new ArrayList<Tuple<Double, Double>>();
        tupleList.add(a);
        tupleList.add(b);
        tupleList.add(c);

        double result = Double.parseDouble(new DecimalFormat("#.####").format(RiskManager.CalcWeightedGain(tupleList)));

        Assert.assertEquals(0.4985, result);

    }

    public void testCalcMaxLoss() {

        double entry = 100;
        double stoploss = 90;
        double positionSize = 0.1;
        double maxloss = Double.parseDouble(new DecimalFormat("#.####").format(RiskManager.CalcMaxLoss(entry, stoploss, positionSize)));

        assertEquals(0.01, maxloss);


    }

    public void testCalculatePositionSize() {
        double d = RiskManager.CalculatePositionSize(0.6, 1);
        DecimalFormat df = new DecimalFormat("#.##");
        assertEquals(Double.parseDouble(df.format(d)), 0.2);

    }

    public void testGetWeightByTimePeriod() {
        assertEquals(34.0, RiskManager.GetWeightByTimePeriod("86400"));
        assertEquals(21.0, RiskManager.GetWeightByTimePeriod("43200"));
        assertEquals(13.0, RiskManager.GetWeightByTimePeriod("21600"));
        assertEquals(8.0, RiskManager.GetWeightByTimePeriod("14400"));
        assertEquals(5.0, RiskManager.GetWeightByTimePeriod("7200"));
        assertEquals(3.0, RiskManager.GetWeightByTimePeriod("3600"));
        assertEquals(2.0, RiskManager.GetWeightByTimePeriod("1800"));
        assertEquals(1.0, RiskManager.GetWeightByTimePeriod("900"));
    }

}
