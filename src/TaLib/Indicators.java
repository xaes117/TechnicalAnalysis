package TaLib;

import java.util.List;

public class Indicators {

    private static final int DefaultLookBack = 13;

    public static int Crossover(List<Double> list) {

        for (int i = 1; i < Indicators.DefaultLookBack; i++) {

            double current = list.get(list.size() - i);
            double prev = list.get(list.size() - i - 1);

            if (Indicators.BullishCrossover(current, prev)) {
                return 8;
            }

            if (Indicators.BearishCrossover(current, prev)) {
                return -8;
            }

        }

        if (list.get(list.size() - 1) > 0) {
            return 2;
        }

        if (list.get(list.size() - 1) < 0) {
            return -2;
        }

        return 0;
    }

    public static boolean BullishCrossover(double current, double prev) {
        return current > 0 && prev < 0;
    }

    public static boolean BearishCrossover(double current, double prev) {
        return current < 0 && prev > 0;
    }

}
