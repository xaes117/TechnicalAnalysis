package com.Engine;

import DBConnection.DataReciever;
import DataStructures.OHLC;
import DataStructures.TickerData;
import Lib.ChaikinVolume;
import Lib.Indicators;
import Lib.TD;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemTwo {

    private static String[] tickers = DataReciever.tickers;
    private static String testString = "CHRIS/CME_GC1";
//    private static String[] tickers = {testString};
    private static String[] periods = {"14400", "21600", "43200", "86400", "259200"};

    public static void main(String[] args) {
        DataReciever.setupTest(testString);
        SystemTwo.run();
    }

    public static void run() {

        SystemTwo.PrintSettings();

        try {

            List<Trade> tradeList = SystemTwo.GetTrades();

            for (Trade trade : tradeList) {

                double positionSize = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.positionSize));
                double maxLoss = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.maxLoss));
                double expProfit = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.expectedProfit));
                double probability = Double.parseDouble(new DecimalFormat("#.###").format(100 * trade.getProbability()));

                trade.translate();

                Main.message.append("<br>------------");
                Main.message.append("<br>Ticker: ").append(trade.getTicker());
                Main.message.append("<br>Period: ").append(trade.getPeriod());
                Main.message.append("<br>Entry: ").append(trade.entry);
                Main.message.append("<br>Stoploss: ").append(trade.stoploss);
                Main.message.append("<br>Position Size: ").append(positionSize).append("%");
                Main.message.append("<br>Take Profit: ").append(trade.takeProfit);
                Main.message.append("<br>Expected Profit: ").append(expProfit).append("%");
                Main.message.append("<br>Max Loss: ").append(maxLoss).append("%");
                Main.message.append("<br>P: ").append(probability).append("%");
                Main.message.append("<br>------------");

                System.out.println("------------");
                System.out.println("Ticker: " + trade.getTicker());
                System.out.println("Period: " + trade.getPeriod());
                System.out.println("Entry: " + trade.entry);
                System.out.println("Stoploss: " + trade.stoploss);
                System.out.println("Position Size: " + positionSize + "%");
                System.out.println("Take Profit: " + trade.takeProfit);
                System.out.println("Expected Profit: " + expProfit + "%");
                System.out.println("Max Loss: " + maxLoss + "%");
                System.out.println("P: " + probability + "%");
                System.out.println("------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Trade> GetTrades() throws Exception {

        List<Trade> tradeList = new ArrayList<>();

        for (String ticker : tickers) {

            double score = 0;
            double highest = 0;

            HashMap<String, TickerData> data = DataReciever.getDataByTickerSymbol(ticker);

            if (DataReciever.IsCrypto(ticker)) {

                for (String period : periods) {
                    TickerData tickerData = data.get(period);

                    List<Double> CO = ChaikinVolume.ChaikinOscillator(tickerData.getOhlcs());
                    int td = TD.getTD(tickerData.getOhlcs());

//                System.out.println("Period: " + period);
//                System.out.println("TD: " + td);
//                System.out.println("Crossover: " + Indicators.Crossover(CO));

                    score += (td + Indicators.Crossover(CO)) * SystemTwo.GetWeightByTimePeriod(period);
                    highest += (8 + 8) * SystemTwo.GetWeightByTimePeriod(period);

                }
            } else {
                String period = "86400";
                TickerData tickerData = data.get(period);

                if (tickerData.getOhlcs().size() > 120) {

//                    List<OHLC> ohlcs = tickerData.getOhlcs().subList(tickerData.getOhlcs().size()-200, tickerData.getOhlcs().size());

                    List<Double> CO = ChaikinVolume.ChaikinOscillator(tickerData.getOhlcs(), 120);
                    int td = TD.getTD(tickerData.getOhlcs());
//                    System.out.println(ohlcs.size());
//                System.out.println("Period: " + period);
//                System.out.println("TD: " + td);
//                System.out.println("Crossover: " + Indicators.Crossover(CO));

                    score += (td + Indicators.Crossover(CO)) * SystemTwo.GetWeightByTimePeriod(period);
                    highest += (8 + 8) * SystemTwo.GetWeightByTimePeriod(period);
                }


            }

            int start = data.get("86400").getOhlcs().size() - 7;
            int end = data.get("86400").getOhlcs().size() - 1;

            try {

                Trade t = null;

                double p = score/(highest * 1.05);

                if (p > 0.45 || p < -0.45) {
                    p = 0.45;
                }

                if  (score >= 0) {
                    t = RiskManager.getBullishTrade(data.get("86400").getOhlcs().subList(start, end), p);
                }

                if (score < -0) {
                    t = RiskManager.getBearishTrade(data.get("86400").getOhlcs().subList(start, end), p);
                }

                t.setTicker(ticker);
                t.setPeriod("86400");
                tradeList.add(t);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        System.out.println("Test");

        return tradeList;
    }

    public static double GetWeightByTimePeriod(String period) {
        // "3600", 7200", "14400", "21600", "43200", "86400", "172800", "259200"

        if (period.equals("259200")) {
            return 8;
        } else if (period.equals("86400")) {
            return 5;
        } else if (period.equals("43200")) {
            return 3;
        } else if (period.equals("21600")) {
            return 2;
        } else if (period.equals("14400")) {
            return 1;
        }
        return 0;
    }

    private static void PrintSettings() {
        System.out.println("\n\n******************System Two Results***********************\n\n");
        System.out.println(new Timestamp(System.currentTimeMillis()));
        System.out.println("\n\n***********************************************************\n\n");

        System.out.println("\n--------------------System Two Settings-----------------\n");
        System.out.println("Safety Margin Applied: " + RiskManager.SafetyMargin);
        System.out.println("Default Probability: " + RiskManager.DefaultProbability);
        System.out.println("Lower Probability: " + RiskManager.LowerProbability);
        System.out.println("\n--------------------------------------------------------\n");

        Main.message.append("<br><br>******************System Two Results***********************<br><br>");
        Main.message.append(new Timestamp(System.currentTimeMillis()));
        Main.message.append("<br><br>***********************************************************<br><br>");

        Main.message.append("<br>--------------------System Two Settings-----------------<br>");
        Main.message.append("<br>Safety Margin Applied: " + RiskManager.SafetyMargin);
        Main.message.append("<br>Default Probability: " + RiskManager.DefaultProbability);
        Main.message.append("<br>Lower Probability: " + RiskManager.LowerProbability);
        Main.message.append("<br>--------------------------------------------------------<br>");
    }

}
