package com.Engine;

import DBConnection.DataReciever;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.Engine.SystemOne.FindTrade;

public class Main {

    static StringBuilder message = new StringBuilder();


    public static void main(String[] args) {

        DataReciever.LoadData();
        DataReciever.setupAllData();

        ShowSystemSettings();
        SystemOne.run();
        SystemTwo.run();

        String m = message.toString();
        System.out.println(m);

//        Mailer.setMsg(m);
//        Mailer.SendMail();

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

    public static void ShowSystemSettings() {
        System.out.println("\n\n*****************************************\n\n");
        System.out.println(new Timestamp(System.currentTimeMillis()));
        System.out.println("\n\n*****************************************\n\n");

        System.out.println("\n-------------------------------------\n");
        System.out.println("Safety Margin Applied: " + RiskManager.SafetyMargin);
        System.out.println("Default Probability: " + RiskManager.DefaultProbability);
        System.out.println("Lower Probability: " + RiskManager.LowerProbability);
        System.out.println("\n-------------------------------------\n");

        message.append("<br><br>*****************************************<br><br>");
        message.append(new Timestamp(System.currentTimeMillis()));
        message.append("<br><br>*****************************************<br><br>");

        message.append("<br>-------------------------------------<br>");
        message.append("<br>Safety Margin Applied: " + RiskManager.SafetyMargin);
        message.append("<br>Default Probability: " + RiskManager.DefaultProbability);
        message.append("<br>Lower Probability: " + RiskManager.LowerProbability);
        message.append("<br>-------------------------------------<br>");
    }

}
