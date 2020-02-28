package FA;

// Description
//
// DG (Short for depression guage)
// In progress
//

import java.util.ArrayList;
import java.util.List;

public class DG {

    enum Number {
        ODD,
        EVEN
    }

    public static void PrintSortedList(int[] list) {

        for (int i = 0; i < list.length - 1; i++) {
            for (int j = i + 1; j < list.length; j++) {

                if (list[i] < list[j]) {
                    int k = list[j];
                    list[j] = list[i];
                    list[i] = k;
                }
            }
        }

        for (int i : list) {
            System.out.println(i);
        }

    }

    public static void PrintNumbers(int[] list, Number number) {
        for (int i : list) {

            if (number.equals(Number.EVEN)) {
                if (i % 2 == 0) {
                    System.out.println(i);
                }
            }

            if (number.equals(Number.ODD)) {
                if (i % 2 == 1) {
                    System.out.println(i);
                }
            }
        }
    }

    public static boolean IsFibonacci(int num) {
        return IsFibonacci(1, 0, num);
    }

    private static boolean IsFibonacci(int a, int b, int num) {

        if (a + b == num) {
            return true;
        }

        if (a + b > num) {
            return false;
        }

        if (a + b < num) {
            return IsFibonacci(b, a + b, num);
        }

        return false;
    }

    public static void main(String[] args) {
        int[] arr = {4, 2, 3, 5, 1};
        PrintNumbers(arr, Number.ODD);

        if (IsFibonacci(13)) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }
    }
}