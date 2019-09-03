package com.Engine;

import DBConnection.DataReciever;
import DataStructures.OHLC;
import DataStructures.TickerData;
import DataStructures.Tuple;
import DataStructures.PTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TradeManager {

    private static int minSampleSize = 20;

    private static final String lowChange = "lowChange";
    private static final String highChange = "highChange";
    private static final String closeChange = "closeChange";
    private static final String openChange = "openChange";
    private static final String volChange = "volChange";

    // calculate the percentage change between two candles
    public static HashMap<String, Double> percentageChange(OHLC current, OHLC prev) {

        double lowM = (current.getLow() - prev.getLow()) / prev.getLow();
        double closeM = (current.getClose() - prev.getClose()) / prev.getClose();
        double highM = (current.getHigh() - prev.getHigh()) / prev.getHigh();
        double openM = (current.getOpen() - prev.getOpen()) / prev.getOpen();
        double volM = (current.getVolume() - prev.getVolume()) / prev.getVolume();

        HashMap<String, Double> changes = new HashMap<String, Double>();
        changes.put(lowChange, lowM);
        changes.put(highChange, highM);
        changes.put(closeChange, closeM);
        changes.put(openChange, openM);
        changes.put(volChange, volM);

        return changes;
    }

    // check if b is within a % range of a and vice versa
    public static boolean withinRange(double a, double b) {

        if (Math.abs(a) < 0.002 && Math.abs(b) < 0.002) {
            return true;
        }

        if (a < 0 && b < 0) {
            return true;
        }

        if (a > 0 && b > 0) {
            return true;
        }

        if (b == a) {
            return true;
        }

        return false;

    }

    // compare the OHLC of the current and prev candles on two different pairs of candles and see if they are similar
    public static boolean compareCandles(OHLC current, OHLC prev, OHLC current1, OHLC prev1) {

        Map<String, Double> changes = percentageChange(current, prev);
        Map<String, Double> changes1 = percentageChange(current1, prev1);

        // iterate through each entry of both sets
        for (Map.Entry<String, Double> entry : changes.entrySet()) {
            // check if open, high, low and close is similar for each pair of candles
            if (!withinRange(entry.getValue(), changes1.get(entry.getKey()))) {
                // return false if change is too big
                return false;
            }
        }
        return true;
    }

    // argument two list of candles (OHLC data) and return true if two lists are similar
    public static boolean compareNCandles(List<OHLC> candles, List<OHLC> candles2) {

        // iterate through each pair of candles on both lists
        for (int i = 0; i < candles.size() - 1; i++) {
            // see if changes between each two pairs of both lists are similar
            if (!compareCandles(candles.get(i), candles.get(i + 1), candles2.get(i), candles2.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    public static List<OHLC> getAnalysisData(String ticker, String period, int length) throws Exception {
        HashMap<String, TickerData> data = DataReciever.getDataByTickerSymbol(ticker);
        TickerData tickerData = data.get(period);

        List<OHLC> ohlcs = tickerData.getOhlcs();
        LinkedList<OHLC> analysis = new LinkedList<OHLC>();

        for (int i = 0; i < length; i++) {
            analysis.addFirst(ohlcs.get(ohlcs.size() - 1 - i));
        }

        return analysis;
    }

    public static List<Tuple<String, List<OHLC>>> findPatterns(List<OHLC> analysisOHLCs) {

        List<Tuple<String, List<OHLC>>> similarPatterns = new ArrayList<Tuple<String, List<OHLC>>>();

        try {

            List<HashMap<String, TickerData>> allOHLCData = new ArrayList<HashMap<String, TickerData>>();

            for (PTuple<String, String, HashMap<String, TickerData>> p : DataReciever.getStoredData()) {
                allOHLCData.add(p.z);
            }

            int counter = 0;

            // Iterate through each ticker
            for (HashMap<String, TickerData> h : allOHLCData) {

                // Go through each time period for the ticker
                for (Map.Entry<String, TickerData> entry : h.entrySet()) {
                    List<OHLC> compareSet = entry.getValue().getOhlcs();

                    // Go through each candle pair for the given period
                    for (int i = 0; i < compareSet.size() - 1; i++) {
                        for (int j = 0; j < analysisOHLCs.size() - 1; j++) {

                            OHLC current = compareSet.get(i + 1);
                            OHLC prev = compareSet.get(i);
                            OHLC current1 = analysisOHLCs.get(j + 1);
                            OHLC prev1 = analysisOHLCs.get(j);

                            // If candle pairs are in opposite directions skip to next pair
                            if (!compareCandles(current, prev, current1, prev1)) {
                                break;
                            }

                            counter++;
                            i++;

                            // If enough candles are similar then proceed to add the candles to a list
                            if (counter == analysisOHLCs.size() - 1) {
                                similarPatterns.add(new Tuple<String, List<OHLC>>(entry.getKey(), new ArrayList<OHLC>()));
                                for (int k = i - counter; k < i - counter + (analysisOHLCs.size() * 2); k++) {
                                    similarPatterns.get(similarPatterns.size() - 1).y.add(compareSet.get(k));
                                }
                            }

                            if (i >= compareSet.size() - 1 - (2 * analysisOHLCs.size())) {
                                break;
                            }
                        }
                        counter = 0;
                    }
                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return similarPatterns;
    }

    public static Trade FindTrade(String ticker, String analysisPeriod, int minimumNumberOfMatches) throws Exception {

        List<Tuple<String, List<OHLC>>> allMatchedPatterns = new ArrayList<Tuple<String, List<OHLC>>>();
        List<Tuple<String, List<OHLC>>> bullishMatchedPatterns = new ArrayList<Tuple<String, List<OHLC>>>();
        List<Tuple<String, List<OHLC>>> bearishMatchedPatterns = new ArrayList<Tuple<String, List<OHLC>>>();

        List<OHLC> analysisOHLCs = new LinkedList<OHLC>();

        for (int length = 12; length > minimumNumberOfMatches; length--) {
            try {
                analysisOHLCs = getAnalysisData(ticker, analysisPeriod, length);
            } catch (Exception e) {
//                    e.printStackTrace();
            }

            List<Tuple<String, List<OHLC>>> similarPatterns = findPatterns(analysisOHLCs);

            // Check if any result have been found
            if (!similarPatterns.isEmpty()) {
//                String searchQuery = "ORIGINAL Search: " + ticker + " for period " + analysisPeriod;
//                PlotChart.plotData(searchQuery, analysisOHLCs);
                for (Tuple<String, List<OHLC>> pattern : similarPatterns) {
//                    PlotChart.plotData(searchQuery + " : " + pattern.x, pattern.y);

                    List<OHLC> matchingPattern = pattern.y;

                    // Check if you will be likely affected by chop
                    boolean isBullish = RiskManager.IsBullish(matchingPattern);
                    boolean isBearish = RiskManager.IsBearish(matchingPattern);

                    if (isBearish && isBullish) {
                        throw new Exception("CANNOT BE BOTH BULLISH AND BEARISH. \n\n**AMEND BUG IMMEDIATELY**\n\n");
                    }

                    // Get weighted moves for the same time period.
                    // Since different time period have different levels of volatility
                    if (pattern.x.equals(analysisPeriod) || RiskManager.GetWeightByTimePeriod(analysisPeriod) > RiskManager.GetWeightByTimePeriod(pattern.x)) {
                        if (isBullish) {
                            bullishMatchedPatterns.add(new Tuple<String, List<OHLC>>(analysisPeriod, matchingPattern));
                        } else if (isBearish) {
                            bearishMatchedPatterns.add(new Tuple<String, List<OHLC>>(analysisPeriod, matchingPattern));
                        }
                    }
                    // System.out.println("Pattern plotted");
                    allMatchedPatterns.add(new Tuple<String, List<OHLC>>(pattern.x, matchingPattern));
                }
            }
        }

        return setTradeVariables(allMatchedPatterns, bullishMatchedPatterns, bearishMatchedPatterns, ticker, analysisPeriod, minimumNumberOfMatches);

    }

    private static Trade setTradeVariables(List<Tuple<String, List<OHLC>>> allMatchedPatterns, List<Tuple<String, List<OHLC>>> bullishMatchedPatterns, List<Tuple<String, List<OHLC>>> bearishMatchedPatterns, String ticker, String analysisPeriod, int minimumNumberOfMatches) throws Exception {

        double averageWeightedGain = RiskManager.CalcWeightedPercentageGain(allMatchedPatterns);
        double averageWeightedBullishGain = RiskManager.CalcWeightedPercentageGain(bullishMatchedPatterns);
        double averageWeightedBearishGain = RiskManager.CalcWeightedPercentageGain(bearishMatchedPatterns);

        int indicatorThreshold = bullishMatchedPatterns.size() - bearishMatchedPatterns.size();

//        System.out.println("Indicator value:" + indicatorThreshold);

        // get extra historical data to help find pivot lows and highs
        List<OHLC> a = getAnalysisData(ticker, analysisPeriod, minimumNumberOfMatches > 4 ? minimumNumberOfMatches : 4);
        Trade t;

        double probability;

        boolean isBullish = averageWeightedBullishGain > averageWeightedBearishGain * -1 && averageWeightedGain > RiskManager.SafetyMargin;
        boolean isBearish = averageWeightedBearishGain < averageWeightedBullishGain * -1 && averageWeightedGain < RiskManager.SafetyMargin * -1;

        if (isBullish && allMatchedPatterns.size() > minSampleSize) {
            // bullish
            probability = (double) bullishMatchedPatterns.size() / (double) allMatchedPatterns.size();
            t = RiskManager.getBullishTrade(a, averageWeightedBullishGain, probability);
        } else if (isBearish && allMatchedPatterns.size() > minSampleSize) {
            // bearish
            probability = (double) bearishMatchedPatterns.size() / (double) allMatchedPatterns.size();
            t = RiskManager.getBearishTrade(a, averageWeightedBearishGain, probability);
        } else {
            throw new Exception("Neutral Trade");
        }

        t.setTicker(ticker);
        t.setPeriod(analysisPeriod);
        t.setMatchedPairs(minimumNumberOfMatches);

        t.setWeightedGain(averageWeightedGain);
        t.setWeightedGainUpside(averageWeightedBullishGain);
        t.setWeightedGainDownside(averageWeightedBearishGain);

        return t;

    }

}
