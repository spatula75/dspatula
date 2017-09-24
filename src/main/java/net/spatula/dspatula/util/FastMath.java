package net.spatula.dspatula.util;

/**
 * Faster versions of some common Java Math.* functions, at the expense of some accuracy.
 * 
 * @author spatula
 *
 */
public final class FastMath {

    private FastMath() {

    }

    private static final int SINE_TABLE_SIZE = 8192;
    private static final double[] sineTable = new double[SINE_TABLE_SIZE];
    private static final double PI = Math.PI;

    static {
        for (int i = 0; i < SINE_TABLE_SIZE; i++) {
            sineTable[i] = Math.sin(2 * PI * i / SINE_TABLE_SIZE);
        }
    }

    /**
     * Fast, table-lookup-based sine function, expected to be accurate to within 0.00005 in most cases.
     * 
     * @param radians
     * @return the sine of the value
     */
    public static double sin(double radians) {
        final double bucket = (radians * (SINE_TABLE_SIZE / (2 * PI)));
        int wholeBucket = (int) bucket;
        final int hundredths = (int) ((bucket - wholeBucket) * 100);

        final double fractionalBucket = hundredths / 100D; // preserves just the tenths place and nothing else
        wholeBucket %= SINE_TABLE_SIZE; // sine is periodic, and we store one full period in our table

        // If we're partway between buckets, do a linear interpolation of the values between the buckets
        // by calculating the difference and then multiplying by a fraction of how far we are between
        // the two buckets, adding that to the value of the lower bucket.
        if (hundredths > 0) {
            final int nextBucket = (wholeBucket + 1) % SINE_TABLE_SIZE;
            final double valueDifference = sineTable[nextBucket] - sineTable[wholeBucket];
            return sineTable[wholeBucket] + valueDifference * fractionalBucket;
        } else {
            return sineTable[wholeBucket];
        }
    }

    /**
     * Fast, table-lookup-based cosine function, expected to be accurate to within 0.00005 in most cases.
     * 
     * @param radians
     * @return the cosine of the value
     */
    public static double cos(double radians) {
        return sin(radians + (PI / 2));
    }

}
