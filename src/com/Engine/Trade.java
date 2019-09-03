package com.Engine;

public class Trade {

    private String ticker;
    private String period;
    private int matchedPairs;
    private double probability;

    private double weightedGain;
    private double weightedGainUpside;
    private double weightedGainDownside;

    public final double entry;
    public final double stoploss;
    public final double takeProfit;
    public final double positionSize;
    public final double maxLoss;
    public final double expectedProfit;

    public Trade(double entry, double positionSize, double stoploss, double takeProfit) {
        this.entry = entry;
        this.stoploss = stoploss;
        this.takeProfit = takeProfit;
        this.positionSize = positionSize;
        this.maxLoss = RiskManager.CalcMaxLoss(this.entry, this.stoploss, this.positionSize);
        this.expectedProfit = Math.abs(positionSize * (takeProfit - entry)/entry);
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public int getMatchedPairs() {
        return matchedPairs;
    }

    public void setMatchedPairs(int matchedPairs) {
        this.matchedPairs = matchedPairs;
    }

    public double getWeightedGain() {
        return weightedGain;
    }

    public void setWeightedGain(double weightedGain) {
        this.weightedGain = weightedGain;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTicker() {
        return ticker;
    }

    public String getPeriod() {
        return period;
    }

    public double getWeightedGainUpside() {
        return weightedGainUpside;
    }

    public void setWeightedGainUpside(double weightedGainUpside) {
        this.weightedGainUpside = weightedGainUpside;
    }

    public double getWeightedGainDownside() {
        return weightedGainDownside;
    }

    public void setWeightedGainDownside(double weightedGainDownside) {
        this.weightedGainDownside = weightedGainDownside;
    }
}

