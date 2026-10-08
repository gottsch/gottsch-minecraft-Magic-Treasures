package mod.gottsch.neo.magic_treasures.core.util;

import java.text.DecimalFormat;

/**
 *
 * @author Mark Gottschling May 28, 2024
 *
 */
public class MathUtil {
    public static String r2d(double number) {
        return new DecimalFormat("0.00").format(number);
    }

    public static String r2d(float number) {
        return new DecimalFormat("0.00").format(number);
    }

    public static String r1d(double number) {
        return new DecimalFormat("0.0").format(number);
    }

    public static String r1d(float number) {
        return new DecimalFormat("0.0").format(number);
    }

    public static String r0d(double number) {
        return new DecimalFormat("0").format(number);
    }

    public static String r0d(float number) {
        return new DecimalFormat("0").format(number);
    }
}
