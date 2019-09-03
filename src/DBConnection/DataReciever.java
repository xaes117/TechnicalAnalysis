package DBConnection;

import DataStructures.OHLC;
import DataStructures.PTuple;
import DataStructures.TickerData;
import Lib.Exchanges;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataReciever {

    public static final String[] tickers = {"btcusd", "ethusd", "ltcusd", "xmrusd", "xrpusd", "eosusd", "etcusd", "ethbtc"};
    public static final String[] periods = {"900", "1800", "3600", "7200", "14400", "21600", "43200", "86400", "259200"};

    private static List<PTuple<String, String, HashMap<String, TickerData>>> StoredData;

    private static void addToMap(String ticker, String period, TickerData data) throws Exception {
        boolean tickerFound = false;
        for (PTuple<String, String, HashMap<String, TickerData>> p : StoredData) {
            if (p.x.equals(ticker)) {
                p.z.put(period, data);
                tickerFound = true;
            }
        }

        if (!tickerFound) {
            throw new Exception("Ticker not found");
        }

    }


    public static void setupAllData() {
        try {
            for (String ticker : tickers) {
                DataReciever.setup(ticker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setup(String ticker) throws Exception {

        String source = "";

        boolean found = false;

        for (PTuple<String, String, HashMap<String, TickerData>> p : StoredData) {
            if (ticker.equals(p.x)) {
                source = p.y;
                found = true;
            }
        }

        if (!found) {
            throw new Exception("Ticker not found");
        }

        JSONObject obj = new JSONObject(source);

        for (String period : periods) {
            JSONArray array = obj.getJSONObject("result").getJSONArray(period);

            TickerData data = new TickerData(Integer.parseInt(period));

            for (int i = 0; i < array.length(); i++) {
                String line = array.get(i).toString();
                line = line.substring(1, line.length() - 1);
                String[] values = line.split(",");
                OHLC ohlc = new OHLC(values[1], values[2], values[3], values[4], values[5]);
                data.getOhlcs().add(ohlc);
            }

            addToMap(ticker, period, data);
        }

        System.out.println("Set up complete for " + ticker);

    }

    public static void LoadData() {

        StoredData = new ArrayList<PTuple<String, String, HashMap<String, TickerData>>>();
        for (String ticker : tickers) {

            String retrievedData = retrieveData(Exchanges.Bitfinex, ticker);
            HashMap<String, TickerData> map = new HashMap<String, TickerData>();

            StoredData.add(new PTuple<String, String, HashMap<String, TickerData>>(ticker, retrievedData, map));

        }
    }

    public static List<PTuple<String, String, HashMap<String, TickerData>>> getStoredData() {
        return StoredData;
    }

    // http://localhost:8080/RESTfulExample/json/product/get
    private static String retrieveData(String exchange, String ticker) {

        String output = "";

        try {

            URL url = new URL("https://api.cryptowat.ch/markets/" + exchange + "/" + ticker + "/ohlc");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String line;
            System.out.println("Output from Server ....");
            System.out.println("Exchange: " + exchange);
            System.out.println("Ticker: " + ticker + "\n");
            while ((line = br.readLine()) != null) {
                output += line;
            }
            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return output;

    }

    public static HashMap<String, TickerData> getDataByTickerSymbol(String ticker) throws Exception {

        for (PTuple<String, String, HashMap<String, TickerData>> p : StoredData) {
            if (ticker.equals(p.x)) {
                return p.z;
            }
        }

        throw new Exception("Ticker not found exception");

    }

//        60	1m
//        180	3m
//        300	5m
//        900	15m
//        1800	30m
//        3600	1h
//        7200	2h
//        14400	4h
//        21600	6h
//        43200	12h
//        86400	1d
//        259200	3d
//        604800	1w

}

