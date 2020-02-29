package com.Engine;

import DBConnection.DataReciever;
import DataStructures.TickerData;
import TaLib.ChaikinVolume;
import TaLib.Indicators;
import TaLib.TD;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemTwo {

    private static String testString = "CHRIS/ICE_SY1";
    private static String[] tickers = {testString};

    //    private static String[] tickers = DataReciever.tickers;
    private static String[] periods = {"14400", "21600", "43200", "86400", "259200"};

    public static void main(String[] args) {
        DataReciever.setupTest(testString);
        DataReciever.LoadData();
        DataReciever.setupAllData();
        SystemTwo.run();
    }

    public static void run() {

        // Print SystemTwo's settings
        SystemTwo.PrintSettings();

        try {

            // Get Trades
            List<Trade> tradeList = SystemTwo.GetTrades();

            // for each trade in the tradeList create a Trade
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

        // Create trade list
        List<Trade> tradeList = new ArrayList<>();

        // Go through each ticker
        for (String ticker : tickers) {

            // set default score 0 and highest 0
            double score = 0;
            double highest = 0;

            // Get OHLC data
            HashMap<String, TickerData> data = DataReciever.getDataByTickerSymbol(ticker);

            // If the ticker is a crypto ticker
            if (DataReciever.IsCrypto(ticker)) {

                // Go through every time period listed
                for (String period : periods) {

                    TickerData tickerData = data.get(period);

                    // Check Chaikin Volume
                    List<Double> CO = ChaikinVolume.ChaikinOscillator(tickerData.getOhlcs());

                    // Check TD
                    int td = TD.getTD(tickerData.getOhlcs());

//                System.out.println("Period: " + period);
//                System.out.println("TD: " + td);
//                System.out.println("Crossover: " + Indicators.Crossover(CO));

                    // Update score and highest score
                    score += (td + Indicators.Crossover(CO)) * SystemTwo.GetWeightByTimePeriod(period);
                    highest += (8 + 8) * SystemTwo.GetWeightByTimePeriod(period);

                }
            } else {

                // Stick to daily chart for non-crypto tickers
                String period = "86400";
                TickerData tickerData = data.get(period);

                // if the data set is greater than 120 candles attempt to shorten the set
                if (tickerData.getOhlcs().size() > 120) {

                    // Check Chaikin volume
                    List<Double> CO = ChaikinVolume.ChaikinOscillator(tickerData.getOhlcs(), 120);

                    // Check TD
                    int td = TD.getTD(tickerData.getOhlcs());

//                System.out.println(ohlcs.size());
//                System.out.println("Period: " + period);
//                System.out.println("TD: " + td);
//                System.out.println("Crossover: " + Indicators.Crossover(CO));

                    // Update scores
                    score += (td + Indicators.Crossover(CO)) * SystemTwo.GetWeightByTimePeriod(period);
                    highest += (8 + 8) * SystemTwo.GetWeightByTimePeriod(period);
                }


            }

            // start and end candles for later
            int start = data.get("86400").getOhlcs().size() - 7;
            int end = data.get("86400").getOhlcs().size();

            try {

                Trade t = null;

                // calculate probability with some penalization for uncertainty
                double uncertainty = 0.05;
                double p = score / (highest * (1.0 + uncertainty));

                // if p greater than 45% then make it no higher than 45%
                if (p > 0.45 || p < -0.45) {
                    p = 0.45;
                }

                // Get bullish trade scenario
                if (score >= 0) {
                    t = RiskManager.getBullishTrade(data.get("86400").getOhlcs().subList(start, end), p);
                }

                // Get bearish trade scenario
                if (score < -0) {
                    t = RiskManager.getBearishTrade(data.get("86400").getOhlcs().subList(start, end), p);
                }

                // Create trade
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
