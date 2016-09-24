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

    //returns a random integer between min and max
    public static int RandInt(int x, int y) {
        return x + rand.nextInt(y - x);
    }

    public static double Clamp(double value, double min, double max)
    {
        if (value < min)
        {
            value = min;
        }

        if (value > max)
        {
            value = max;
        }

        return value;
    }

    private static Random rand = new Random();
}
