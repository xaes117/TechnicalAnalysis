package DBConnection;

import DataStructures.OHLC;
import DataStructures.PTuple;
import DataStructures.TickerData;
import Lib.Exchanges;
import com.Engine.SystemTwo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class DataReciever {

    public static final String[] tickers = {
            "btcusd",
            "ethusd",
            "ltcusd",
            "xmrusd",
            "xrpusd",
            "eosusd",
            "etcusd",
            "ethbtc",
            "CHRIS/CME_RP2", //EURGBP
            "CHRIS/ICE_SS1", //GBPCHF
            "CHRIS/ICE_SY1", //GBPJPY
            "CHRIS/ICE_MP2", //GBPUSD
            "CHRIS/CME_SP2", //SPY
            "CHRIS/CME_GC6", //GOLD
            "CHRIS/CME_SI3"  //SILVER
    };
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

        if (DataReciever.IsCrypto(ticker)) {
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
        } else {
            String period = "86400";

            JSONArray array = obj.getJSONObject("dataset_data").getJSONArray("data");

            TickerData data = new TickerData(Integer.parseInt(period));

            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONArray a1 = array.getJSONArray(i);
                    String open = a1.get(1).toString();
                    String high = a1.get(2).toString();
                    String low = a1.get(3).toString();
                    String close = a1.get(4).toString();
                    String volume = a1.get(7).toString();

                    OHLC ohlc = new OHLC(open, high, low, close, volume);
                    data.getOhlcs().add(ohlc);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }

            Collections.reverse(data.getOhlcs());
            addToMap(ticker, period, data);

//            "column_names": [
//            "Date",
//                    "Open",
//                    "High",
//                    "Low",
//                    "Last",
//                    "Change",
//                    "Settle",
//                    "Volume",
//                    "Previous Day Open Interest"
//    ],

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

            URL url;

            if (DataReciever.IsCrypto(ticker)) {
                url = new URL("https://api.cryptowat.ch/markets/" + exchange + "/" + ticker + "/ohlc");
            } else {
                url = new URL("https://www.quandl.com/api/v3/datasets/" + ticker + "/data.json?api_key=iLKF9C8Ep7v19AYtzxfc");
            }

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

    public static boolean IsCrypto(String ticker) {
        String[] tickers = {
                "btcusd",
                "ethusd",
                "ltcusd",
                "xmrusd",
                "xrpusd",
                "eosusd",
                "etcusd",
                "ethbtc"
        };

        for (String t : tickers) {
            if (ticker.equals(t)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        StoredData = new ArrayList<PTuple<String, String, HashMap<String, TickerData>>>();

        String s = retrieveData(Exchanges.Bitfinex, "CHRIS/CME_RP2");
        StoredData.add(new PTuple<String, String, HashMap<String, TickerData>>("CHRIS/CME_RP2", s, new HashMap<>()));

        try {
            setup("CHRIS/CME_RP2");
        } catch (Exception e) {
            e.printStackTrace();
        }

//            String line = array.get(i).toString();
//            line = line.substring(1, line.length() - 1);
//            String[] values = line.split(",");
//            OHLC ohlc = new OHLC(values[1], values[2], values[3], values[4], values[5]);
//            data.getOhlcs().add(ohlc);

//            "column_names": [
//            "Date",
//            "Open",
//            "High",
//            "Low",
//            "Last",
//            "Change",
//            "Settle",
//            "Volume",
//            "Previous Day Open Interest"
//    ],

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

