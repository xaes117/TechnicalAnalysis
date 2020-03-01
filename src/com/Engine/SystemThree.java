package com.Engine;

import DBConnection.DataReciever;
import DataStructures.TickerData;
import TaLib.MovingAverages;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// System Three currently in progress. Will use the FA library for fundamental analysis

public class SystemThree {

    private static String testString = "CHRIS/ICE_SY1";
    private static String[] tickers = {testString};

    //    private static String[] tickers = DataReciever.tickers;
    private static String[] periods = {"14400", "21600", "43200", "86400", "259200"};

    public static void main(String[] args) {
        DataReciever.setupTest(testString);
        DataReciever.LoadData();
        DataReciever.setupAllData();
        SystemThree.run();
    }

    private static List<Trade> GetTrades() {

        List<Trade> tradeList = new ArrayList<>();

        for (String ticker : tickers) {

            // Get 20 SMA
            try {
                HashMap<String, TickerData> data = DataReciever.getDataByTickerSymbol(ticker);

                double score = 0;
                double highest = 0;

                for (Map.Entry<String, TickerData> pair : data.entrySet()) {

                    String period = pair.getKey();
                    List ohlcs = pair.getValue().getOhlcs();

                    double[] sma = MovingAverages.SMA(ohlcs, 20, 100);
                    System.out.println(sma);

                    double weight = SystemTwo.GetWeightByTimePeriod(period);

                    score = weight;


                }

                System.out.println(data);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return tradeList;

    }

    public static void run() {

        SystemThree.PrintSettings();

        try {

            List<Trade> tradeList = SystemThree.GetTrades();

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
