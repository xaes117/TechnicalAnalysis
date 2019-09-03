package DataStructures;

public class PTuple<X, Y, Z> {

    public final X x;
    public final Y y;
    public final Z z;

    // <ticker, Historical (period I think. Should double check), TickerData>
    public PTuple(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}