package com.Engine;

import DataStructures.OHLC;
import DataStructures.Tuple;

import java.util.List;

public class RiskManager {

    public static final double SafetyMargin = 0.025;
    public static final double DefaultProbability = 0.3;
    public static final double LowerProbability = 0.1;

    public static boolean IsBullish(List<OHLC> matchingPattern) {
        return (1 + SafetyMargin) * matchingPattern.get(matchingPattern.size() / 2 - 1).getClose() < matchingPattern.get(matchingPattern.size() - 1).getClose();
    }

    public static boolean IsBearish(List<OHLC> matchingPattern) {
        return (1 - SafetyMargin) * matchingPattern.get(matchingPattern.size() / 2 - 1).getClose() > matchingPattern.get(matchingPattern.size() - 1).getClose();
    }

    public static double CalcWeightedPercentageGain(List<Tuple<String, List<OHLC>>> similarPatterns) throws Exception {

        double percentageSum = 0;
        double sumOfWeights = 0;

        for (Tuple<String, List<OHLC>> matchingPattern : similarPatterns) {

            // calculate the difference between the current and final candle
            OHLC first = matchingPattern.y.get(matchingPattern.y.size() / 2 - 1);
            OHLC last = matchingPattern.y.get(matchingPattern.y.size() - 1);
            double difference = last.getClose() - first.getClose();

            // calculate percentage gain
            double percentageDifference = difference / first.getClose();

            if (first.getClose() < 0.00001) {
                throw new Exception("Division by 0");
            }

            // get weight of time period
            double weight = GetWeightByTimePeriod(matchingPattern.x);

            percentageSum += percentageDifference * weight;
            sumOfWeights += weight;

        }

        double weightedGain = percentageSum / sumOfWeights;

        if (Double.isNaN(weightedGain)) {
            return 0;
        }

        return weightedGain;
    }

    public static double CalcWeightedGain(List<Tuple<Double, Double>> tupleList) {
        double sum = 0;
        double sumOfWeights = 0;

        for (Tuple<Double, Double> pair : tupleList) {
            double value = pair.x;
            double weight = pair.y;

            sum += value * weight;
            sumOfWeights += weight;

        }

        double weightedGain = sum / sumOfWeights;

        if (Double.isNaN(weightedGain)) {
            return 0;
        }

        return weightedGain;
    }

    public static Trade getBullishTrade(List<OHLC> ohlcList, double P) throws Exception {

        OHLC currentCandle = ohlcList.get(ohlcList.size() - 1);

        double exp = (currentCandle.getClose() - getLow(ohlcList)) * 2.5;
        double reward = exp/currentCandle.getClose();
        return getBullishTrade(ohlcList, reward, P);

    }

    // expected gain a percentage e.g. 0.1
    public static Trade getBullishTrade(List<OHLC> ohlcList, double expectedGain, double P) throws Exception {

        if (expectedGain < SafetyMargin) {
            throw new Exception("Trade too risky");
        }

        OHLC currentCandle = ohlcList.get(ohlcList.size() - 1);
        double stoploss = getLow(ohlcList);
        double exp = ohlcList.get(ohlcList.size() - 1).getClose() * (1 + expectedGain);

        double reward = exp - currentCandle.getClose();
        double risk = currentCandle.getClose() - stoploss;

        double b = reward / risk;
        double positionSize = CalculatePositionSize(P, b);

        if (positionSize < 0) {
            // set stoploss to high of current candle
            stoploss = ohlcList.get(ohlcList.size() - 1).getLow();
            if (stoploss != currentCandle.getClose()) {
                risk = currentCandle.getClose() - stoploss;
                b = reward / risk;
                if (P > LowerProbability) {
                    P = LowerProbability;
                }
                positionSize = CalculatePositionSize(P, b);
            }
        }

        if (negativePositionSize(positionSize)) {
            throw new Exception("Position Size Negative");
        }

        Trade t = new Trade(currentCandle.getClose(), positionSize, stoploss, exp);
        t.setProbability(P);

        return t;

    }

    public static Trade getBearishTrade(List<OHLC> ohlcList, double P) throws Exception {

        if (P < 0) {
            P = P * -1;
        }

        OHLC currentCandle = ohlcList.get(ohlcList.size() - 1);

        double exp = (getHigh(ohlcList) - currentCandle.getClose()) * -2.5;
        double reward = exp/currentCandle.getClose();
        return getBearishTrade(ohlcList, reward, P);

    }

    public static Trade getBearishTrade(List<OHLC> ohlcList, double expectedGain, double P) throws Exception {

        // if
        if (P < 0) {
            P = P * -1;
        }

        // expectedGain is negative for bearish trades
        if (expectedGain * -1 < SafetyMargin) {
            throw new Exception("Trade too risky");
        }

        OHLC currentCandle = ohlcList.get(ohlcList.size() - 1);
        double stoploss = getHigh(ohlcList);
        double exp = ohlcList.get(ohlcList.size() - 1).getClose() * (1 + expectedGain);

        double reward = currentCandle.getClose() - exp;
        double risk = stoploss - currentCandle.getClose();

        double b = reward / risk;
        double positionSize = CalculatePositionSize(P, b);

        if (positionSize < 0) {
            // set stoploss to high of current candle
            stoploss = ohlcList.get(ohlcList.size() - 1).getHigh();
            if (stoploss != currentCandle.getClose()) {
                risk = stoploss - currentCandle.getClose();
                b = reward / risk;
                if (P > LowerProbability) {
                    P = LowerProbability;
                }
                positionSize = CalculatePositionSize(P, b);
            }
        }

        if (negativePositionSize(positionSize)) {
            throw new Exception("Position Size Negative");
        }

        Trade t = new Trade(currentCandle.getClose(), positionSize, stoploss, exp);
        t.setProbability(P);
        return t;

    }

    /*
    f = (p(b+1) - 1) / b

    f * is the fraction of the current bankroll to wager, i.e. how much to bet;
    b is the net odds received on the wager ("b to 1"); that is, you could win $b (on top of getting back your $1 wagered) for a $1 bet
    p is the probability of winning;

     */
    public static double CalculatePositionSize(double p, double b) {
        return (p * (b + 1) - 1) / b;
    }

    static double CalcMaxLoss(double entry, double stoploss, double positionSize) {
        double change = Math.abs(entry - stoploss);
        double percentageChange = change / entry;
        return percentageChange * positionSize;
    }

    private static boolean negativePositionSize(double positionSize) {
        return positionSize < 0;
    }

    private static double getHigh(List<OHLC> ohlcList) {

        double highest = -1;

        for (OHLC ohlc : ohlcList) {
            if (ohlc.getHigh() > highest) {
                highest = ohlc.getHigh();
            }
        }
        return highest;
    }

    private static double getLow(List<OHLC> ohlcList) {

        double lowest = 99999999;

        for (OHLC ohlc : ohlcList) {
            if (ohlc.getLow() < lowest) {
                lowest = ohlc.getLow();
            }
        }
        return lowest;
    }

    public static double GetWeightByTimePeriod(String period) {
        // "3600", 7200", "14400", "21600", "43200", "86400", "172800", "259200"

        if (period.equals("259200")) {
            return 89;
        } else if (period.equals("172800")) {
            return 55;
        } else if (period.equals("86400")) {
            return 34;
        } else if (period.equals("43200")) {
            return 21;
        } else if (period.equals("21600")) {
            return 13;
        } else if (period.equals("14400")) {
            return 8;
        } else if (period.equals("7200")) {
            return 5;
        } else if (period.equals("3600")) {
            return 3;
        } else if (period.equals("1800")) {
            return 2;
        } else if (period.equals("900")) {
            return 1;
        }
        return 0;
    }

}
