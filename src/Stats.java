/**
 * Utility class providing basic statistical functions over a sample of measurements.
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public final class Stats {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Stats() {}

    /**
     * Calculates the arithmetic mean of a set of values.
     *
     * @param values Values to calculate the mean of
     * @return Arithmetic mean of {@code values}
     */
    public static double mean(double[] values) {
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.length;
    }

    /**
     * Calculates the sample standard deviation of a set of values.
     *
     * @param values Values to calculate the standard deviation of
     * @param mean   Mean of {@code values}
     * @return Sample standard deviation of {@code values} around {@code mean}
     */
    public static double stdDev(double[] values, double mean) {
        double sumSquaredDiffs = 0;
        for (double v : values) {
            sumSquaredDiffs += (v - mean) * (v - mean);
        }
        return Math.sqrt(sumSquaredDiffs / (values.length - 1));
    }
}