package com.Engine;

import DBConnection.DataReciever;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.Engine.TradeManager.FindTrade;

public class Main {

    public static void main(String[] args) {

        DataReciever.LoadData();
        DataReciever.setupAllData();

        System.out.println("\n\n*****************************************\n\n");
        System.out.println(new Timestamp(System.currentTimeMillis()));
        System.out.println("\n\n*****************************************\n\n");

        System.out.println("\n-------------------------------------\n");
        System.out.println("Safety Margin Applied: " + RiskManager.SafetyMargin);
        System.out.println("Default Probability: " + RiskManager.DefaultProbability);
        System.out.println("Lower Probability: " + RiskManager.LowerProbability);
        System.out.println("\n-------------------------------------\n");

        List<Trade> tradeList = new ArrayList<Trade>();

        String[] tickers = {"btcusd", "ethusd", "xrpusd", "ltcusd", "xmrusd", "eosusd", "ethbtc"};
        String[] periods = {"14400", "21600", "43200", "86400", "259200"};

//        String[] tickers = {"ethusd"};
//        String[] periods = {"43200"};

        int k = 0;

        for (int i = 3; i < 9; i++) {
            for (String ticker : tickers) {
                for (String period : periods) {
                    try {
                        tradeList.add(FindTrade(ticker, period, i));
                        System.out.println(++k + ": Trade found");
                    } catch (Exception e) {
//                    e.printStackTrace();
                    }
                }
            }
        }

        StringBuilder message = new StringBuilder();

        for (Trade trade : tradeList) {

            double positionSize = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.positionSize));
            double maxLoss = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.maxLoss));
            double expProfit = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.expectedProfit));
            double probability = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.getProbability()));

            double avgWeighted = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.getWeightedGain()));
            double bgu = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.getWeightedGainUpside()));
            double bgd = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.getWeightedGainDownside()));

            message.append("<br>------------");
            message.append("<br>Ticker: ").append(trade.getTicker());
            message.append("<br>Period: ").append(trade.getPeriod());
            message.append("<br>Entry: ").append(trade.entry);
            message.append("<br>Stoploss: ").append(trade.stoploss);
            message.append("<br>Position Size: ").append(positionSize).append("%");
            message.append("<br>Take Profit: ").append(trade.takeProfit);
            message.append("<br>Expected Profit: ").append(expProfit).append("%");
            message.append("<br>Max Loss: ").append(maxLoss).append("%");
            message.append("<br>P: ").append(probability).append("%");
            message.append("<br>Average Weighted Gain: ").append(avgWeighted).append("%");
            message.append("<br>Average Weighted Bullish Gain: ").append(bgu).append("%");
            message.append("<br>Average Weighted Bearish Gain: ").append(bgd).append("%");
            message.append("<br>------------");

            System.out.println("------------");
            System.out.println("Ticker: " + trade.getTicker());
            System.out.println("Period: " + trade.getPeriod());
            System.out.println("Entry: " + trade.entry);
            System.out.println("Stoploss: " + trade.stoploss);
            System.out.println("Position Size: " + positionSize + "%");
            System.out.println("Take Profit: " + trade.takeProfit);
            System.out.println("Expected Profit: " + expProfit + "%");
            System.out.println("Max Loss: " + maxLoss + "%");
//            System.out.println("Match length: " + trade.getMatchedPairs());
            System.out.println("P: " + probability + "%");
            System.out.println("Average Weighted Gain: " + avgWeighted + "%");
            System.out.println("Average Weighted Bullish Gain: " + bgu + "%");
            System.out.println("Average Weighted Bearish Gain: " + bgd + "%");
            System.out.println("------------");
        }

        String m = message.toString();
//        System.out.println(m);

        Mailer.setMsg(m);
        Mailer.SendMail();

        // 60	1m
        // 180	3m
        // 300	5m
        // 900	15m
        // 1800	30m
        // 3600	1h
        // 7200	2h
        // 14400	4h
        // 21600	6h
        // 43200	12h
        // 86400	1d
        // 259200	3d
        // 604800	1w

        System.out.println("Hello");

    }
}
