package DataStructures;

public class OHLC {

    private double open;
    private double close;
    private double low;
    private double high;
    private double volume;

    public OHLC(String open, String high, String low, String close, String volume) {
        this.open = Double.parseDouble(open);
        this.close = Double.parseDouble(close);
        this.low = Double.parseDouble(low);
        this.high = Double.parseDouble(high);
        this.volume = Double.parseDouble(volume);
    }

    public OHLC(double open, double high, double low, double close, double volume) {
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.volume = volume;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getLow() {
        return low;
    }

    public double getHigh() {
        return high;
    }

    public double getVolume() {
        return volume;
    }
}
