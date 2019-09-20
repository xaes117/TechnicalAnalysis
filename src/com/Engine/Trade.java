package com.Engine;

public class Trade {

    private String ticker;
    private String period;
    private int matchedPairs;
    private double probability;

    private double weightedGain;
    private double weightedGainUpside;
    private double weightedGainDownside;

    public double entry;
    public double stoploss;
    public double takeProfit;
    public double positionSize;
    public double maxLoss;
    public double expectedProfit;

    public Trade(String ticker, String period) {
        this.ticker = ticker;
        this.period = period;
    }

    public Trade(double entry, double positionSize, double stoploss, double takeProfit) {
        this.entry = entry;
        this.stoploss = stoploss;
        this.takeProfit = takeProfit;
        this.positionSize = positionSize;
        this.maxLoss = RiskManager.CalcMaxLoss(this.entry, this.stoploss, this.positionSize);
        this.expectedProfit = Math.abs(positionSize * (takeProfit - entry) / entry);
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

    public void translate() {
//        "CHRIS/CME_RP2", //EURGBP
//                "CHRIS/ICE_SS1", //GBPCHF
//                "CHRIS/ICE_SY1", //GBPJPY
//                "CHRIS/ICE_MP2", //GBPUSD
//                "CHRIS/CME_SP2", //SPY
//                "CHRIS/CME_GC6", //GOLD
//                "CHRIS/CME_SI3"  //SILVER
//                "CHRIS/CME_PL1" // PLATINUM

        // "CHRIS/CME_EC1" // EURUSD
        // "CHRIS/CME_AD1" // AUDUSD
        // "CHRIS/CME_CD2" // CADUSD
        // "CHRIS/LIFFE_Z1" // FTSE100
        // "CHRIS/CME_NQ1" // NASDAQ

        if (this.getTicker().equals("CHRIS/CME_RP1")) {
            this.setTicker("EURGBP" + " (CME_RP1)");
        }
        if (this.getTicker().equals("CHRIS/ICE_SS1")) {
            this.setTicker("GBPCHF" + " (ICE_SS1)");
        }
        if (this.getTicker().equals("CHRIS/ICE_SY1")) {
            this.setTicker("GBPJPY" + " (ICE_SY1)");
        }
        if (this.getTicker().equals("CHRIS/ICE_MP1")) {
            this.setTicker("GBPUSD" + " (ICE_MP1)");
        }
        if (this.getTicker().equals("CHRIS/CME_SP1")) {
            this.setTicker("SPY" + " (CME_SP1)");
        }
        if (this.getTicker().equals("CHRIS/CME_GC1")) {
            this.setTicker("GOLD" + " (CME_GC1)");
        }
        if (this.getTicker().equals("CHRIS/CME_SI1")) {
            this.setTicker("SILVER" + " (CME_SI1)");
        }
        if (this.getTicker().equals("CHRIS/CME_EC1")) {
            this.setTicker("EURUSD (CHRIS/CME_EC1)");
        }
        if (this.getTicker().equals("CHRIS/CME_AD1")) {
            this.setTicker("AUDUSD (CME_AD1)");
        }
        if (this.getTicker().equals("CHRIS/CME_CD1")) {
            this.setTicker("CADUSD (CME_CD1)");
        }
        if (this.getTicker().equals("CHRIS/CME_PL1")) {
            this.setTicker("PLATINUM (CME_PL1)");
        }

        if (this.getTicker().equals("CHRIS/LIFFE_Z1")) {
            this.setTicker("FTSE100 (LIFFE_Z1)");
        }

        if (this.getTicker().equals("CHRIS/CME_NQ1")) {
            this.setTicker("NASDAQ (CME_NQ1");
        }
    }
}

