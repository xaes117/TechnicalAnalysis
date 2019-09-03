package DBConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBConnection {

    private static Connection connection;

    public static void connectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trading", "root", "0x38be(2015)");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTickers() {
        List<String> tickers = new ArrayList<String>();
        try {
            // Step 2: Allocate a 'Statement' object in the Connection
            Statement query = connection.createStatement();

            // Step 3: Execute a SQL SELECT query, the query result
            //  is returned in a 'ResultSet' object.
            String strSelect = "SELECT * FROM trading.tickers;";
            ResultSet rset = query.executeQuery(strSelect);

            // Step 4: Process the ResultSet by scrolling the cursor forward via next().
            //  For each row, retrieve the contents of the cells with getXxx(columnName).
            while (rset.next()) {   // Move the cursor to the next row, return false if no more row
                tickers.add(rset.getString("idtickers"));
            }
// INSERT INTO `trading`.`ohlc` (`idtickers`, `time`, `open`, `high`, `low`, `close`, `volume`)
// VALUES ('btcusd', '2018-01-01 23:59:59', '10', '11', '15', '12', '20');
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tickers;
    }

    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    public static void insertOHLC(String ticker, Date date, double open, double high, double low, double close, double volume) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        try {
            Statement query = connection.createStatement();
            String queryString = "INSERT INTO `trading`.`ohlc` (`idtickers`, `time`, `open`, `high`, `low`, `close`, `volume`)" +
                    "VALUES ('"+ticker+"', '"+sdfDate.format(date)+"', '"+open+"', '"+high+"', '"+low+"', '"+close+"', '"+volume+"');";
            query.execute(queryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        connectToDB();
        // Date(int year, int month, int date, int hrs, int min, int sec)
        Date date = new Date(2018, 3, 3, 4, 40, 46);
        insertOHLC("ethusd", date, 1,1,2,3,4);

    }
}
