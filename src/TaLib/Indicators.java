package TaLib;

import java.util.List;

public class Indicators {

    // Default look back set to 13
    // Number of candles to look back from starting point
    private static final int DefaultLookBack = 13;

    // Crossover check
    public static int Crossover(List<Double> list) {

        // Check if a crossover has occurred over the default look back period
        for (int i = 1; i < Indicators.DefaultLookBack; i++) {

            double current = list.get(list.size() - i);
            double prev = list.get(list.size() - i - 1);

            // Return 8 if bullish crossover
            if (Indicators.BullishCrossover(current, prev)) {
                return 8;
            }

            // Return -8 if bearish crossover
            if (Indicators.BearishCrossover(current, prev)) {
                return -8;
            }

        }

        // If the current value is above 0 return 2
        if (list.get(list.size() - 1) > 0) {
            return 2;
        }

        // If the current value is below 0 return -2
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
