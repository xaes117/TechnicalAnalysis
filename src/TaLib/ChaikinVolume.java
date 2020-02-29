package TaLib;


import DataStructures.OHLC;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// Chaikin Oscillator
public class ChaikinVolume {

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

    public static double MoneyFlow(OHLC ohlc) {
        double close = ohlc.getClose();
        double low = ohlc.getLow();
        double high = ohlc.getHigh();
        double volume = ohlc.getVolume();

        if (high - low == 0) {
            return 0;
        }

        double moneyFlowMultiplier = ((close - low) - (high - close)) / (high - low);
        return moneyFlowMultiplier * volume;
    }

    public static double[] AccumulationDistribution(List<OHLC> ohlcList) {
        double cum = 0;
        double[] retList = new double[ohlcList.size()];
        for (int i = 0; i < ohlcList.size(); i++) {
            cum += ChaikinVolume.MoneyFlow(ohlcList.get(i));
            retList[i] = cum;
        }
        return retList;
    }

    public static List<Double> ChaikinOscillator(List<OHLC> ohlcList, int lookback) throws Exception {
        return ChaikinOscillator(ohlcList.subList(ohlcList.size() - lookback, ohlcList.size() - 1));
    }

    public static List<Double> ChaikinOscillator(List<OHLC> ohlcList) throws Exception {
        List<Double> results = new LinkedList<>();

        double[] ema3 = MovingAverages.EMA(ChaikinVolume.AccumulationDistribution(ohlcList), 3);
        double[] ema10 = MovingAverages.EMA(ChaikinVolume.AccumulationDistribution(ohlcList), 10);

        for (int i = 1; i < ema10.length; i++) {
            double OC = ema3[ema3.length - i] - ema10[ema10.length - i];
            results.add(OC);
        }


        while (results.size() < ohlcList.size()) {
            results.add(0.0);
        }

        Collections.reverse(results);

        return results;
    }
//
//    public static double CMF(List<OHLC> ohlcList) {
//        int lookBackLength = 20;
//        LinkedList
//        for (int i = lookBackLength; i < ohlcList.size(); i++) {
//            double cmf = sum
//        }
//    }

}
