package TaLib;


import DataStructures.OHLC;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// Chaikin Oscillator
public class ChaikinVolume {

//    ------------------------------------------------
//    ------------------------------------------------
//    Original PineScript code for reference
//    ------------------------------------------------
//    ------------------------------------------------
//
//    // Calculate money flow volume and money flow multiplier
//    MoneyFlowMultiplier = ((close - low) - (high - close))/(high- low)
//    MoneyFlowVolume = MoneyFlowMultiplier * volume
//
//// Set price
//            priceEMA = 12
//    LookBackLength = 20
//
//// Check Accumilation/Distribution
//    ADL = cum(MoneyFlowVolume)
//    bullishADL = rising(ADL, LookBackLength)
//    bearishADL = falling(ADL, LookBackLength)
//
//// Check CMF
//    cmf = sum(MoneyFlowVolume, 21)/sum(volume, 21)
//    bullishCMF = (cmf > 0 or crossover(cmf, 0)) and rising(ema(close, priceEMA), LookBackLength)
//    bearishCMF = (cmf < 0 or  crossunder(cmf, 0)) and falling(ema(close, priceEMA), LookBackLength)
//
//// Check Chaikin Oscillator
//    OC = ema(ADL, 3) - ema(ADL, 10)
//    bullishOC = rising(ema(close, priceEMA), LookBackLength) and crossover(OC, 0) or rising(OC, LookBackLength)
//    bearishOC = falling(ema(close, priceEMA), LookBackLength) and crossunder(OC, 0) or falling(OC, LookBackLength)
//
//    ------------------------------------------------
//    ------------------------------------------------
//    ------------------------------------------------
//    ------------------------------------------------

    // Calculate money flow
    public static double MoneyFlow(OHLC ohlc) {

        // Get OHLC data
        double close = ohlc.getClose();
        double low = ohlc.getLow();
        double high = ohlc.getHigh();
        double volume = ohlc.getVolume();

        // Check to avoid divide by 0 calculation
        if (high - low == 0) {
            return 0;
        }

        // return the moneyFlow as per Chaikin's money flow formula
        double moneyFlowMultiplier = ((close - low) - (high - close)) / (high - low);
        return moneyFlowMultiplier * volume;
    }

    // Calculate Acc/Distribution
    // Note: Worth reading up or looking at accumulation/distribution on TradingView again if you forget
    public static double[] AccumulationDistribution(List<OHLC> ohlcList) {

        // cum short for Cumulative
        double cum = 0;

        // Create list for accumulation/distribution for each candle
        double[] retList = new double[ohlcList.size()];

        // Keep rolling the cumulative number and add each new number to the list
        for (int i = 0; i < ohlcList.size(); i++) {
            cum += ChaikinVolume.MoneyFlow(ohlcList.get(i));
            retList[i] = cum;
        }

        return retList;
    }

    // Calculate Chaikin Oscillator
    // Note: Worth reading up or looking at Chaikin Oscillator on TradingView again if you forget
    public static List<Double> ChaikinOscillator(List<OHLC> ohlcList, int lookback) throws Exception {
        return ChaikinOscillator(ohlcList.subList(ohlcList.size() - lookback, ohlcList.size() - 1));
    }

    public static List<Double> ChaikinOscillator(List<OHLC> ohlcList) throws Exception {

        // Create list for each value in time for the oscillator
        List<Double> results = new LinkedList<>();

        // using ema3 and ema10 as defaults suggested on TradingView
        double[] ema3 = MovingAverages.EMA(ChaikinVolume.AccumulationDistribution(ohlcList), 3);
        double[] ema10 = MovingAverages.EMA(ChaikinVolume.AccumulationDistribution(ohlcList), 10);

        // perform ema3 - ema10 for each value in time
        for (int i = 1; i < ema10.length; i++) {
            double OC = ema3[ema3.length - i] - ema10[ema10.length - i];
            results.add(OC);
        }

        // add results to the array
        while (results.size() < ohlcList.size()) {
            results.add(0.0);
        }

        // reverse the results
        // for some reason results are in reverse
        // likely due to way data comes in
        Collections.reverse(results);

        return results;
    }

//  ----------------------------------
//  Chaikin Money Flow calculation.
//  Not needed but keep just in case for future
//  ----------------------------------
//    public static double CMF(List<OHLC> ohlcList) {
//        int lookBackLength = 20;
//        LinkedList
//        for (int i = lookBackLength; i < ohlcList.size(); i++) {
//            double cmf = sum
//        }
//    }

}
