package org.danielholmes.smartsweepers;

import java.util.Random;

public class Utils {
    public static double RandomClamped() {
        return RandFloat() - RandFloat();
    }

    // Rand float 0 >= x < 1
    public static double RandFloat() {
        return rand.nextDouble();
    }

    private static Random rand = new Random();
}
