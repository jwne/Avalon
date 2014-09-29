package me.aventium.avalon.utils;

import org.joda.time.Duration;

public class ParsingUtils {

    // borrowed from http://stackoverflow.com/questions/604424/java-convert-string-to-enum
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        return getEnumFromString(c, string, null);
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string, T defaultValue) {
        if(c != null && string != null) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase().replace(' ', '_'));
            } catch(IllegalArgumentException ex) {
                // intentionally left blank
            }
        }
        return defaultValue;
    }

    public static Duration parseDuration(String string, Duration def) {
        if(string == null) return def;
        return PeriodFormats.SHORTHAND.parsePeriod(string).toStandardDuration();
    }

}
