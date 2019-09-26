package org.evomaster.client.java.instrumentation.coverage.methodreplacement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static org.evomaster.client.java.instrumentation.coverage.methodreplacement.DistanceHelper.*;

public class DateTimeParsingUtils {


    private static final String ISO_LOCAL_TIME_PATTERN = "HH:MM:SS";

    private static final String ISO_LOCAL_DATE_PATTERN = "YYYY-MM-DD";

    private static final String ISO_LOCAL_DATE_TIME_PATTERN = String.format("%sT%s", ISO_LOCAL_DATE_PATTERN, ISO_LOCAL_TIME_PATTERN);

    private static final int ISO_LOCAL_TIME_LENGTH = ISO_LOCAL_TIME_PATTERN.length();

    private static final int ISO_LOCAL_DATE_LENGTH = ISO_LOCAL_DATE_PATTERN.length();

    private static final int ISO_LOCAL_DATE_TIME_LENGTH = ISO_LOCAL_DATE_TIME_PATTERN.length();

    private static final String DATE_TIME_NO_SECONDS_FORMAT = "YYYY-MM-DD HH:MM";

    /**
     * returns how close the input was to HH:MM. If the input fails
     *
     * @param input
     * @return
     */
    private static double getDistanceToLocalTimeWithoutSeconds(CharSequence input) {
        Objects.requireNonNull(input);

        return getDistanceToLocalTimeWithSeconds(input + ":00");
    }

    /**
     * returns how close the input was to HH:MM:SS. If the input
     * fails to parse, then the distance only considers hours in the range
     * 00 to 19.
     *
     * @param input
     * @return
     */
    private static double getDistanceToLocalTimeWithSeconds(CharSequence input) {
        Objects.requireNonNull(input);

        try {
            /*
                due to the simplification later on (i.e. not all valid local dates
                are considered, only a subrange), still must make sure to get a 1
                if no exception is thrown
             */
            LocalTime.parse(input);
            return H_PARSED_OK;
        } catch (DateTimeParseException ex) {
            return getApproximateDistanceToLocalTimeWithSeconds(input);

        }

    }

    /**
     * Returns the approximate (i.e. simplified) distance of a string
     * to the format HH:MM:SS.
     * For simplification, only the range of hours between 00 and 19
     * are considered.
     *
     * @param input a non-null string that fails to parse in
     *              the HH:MM:SS format
     * @return
     */
    private static double getApproximateDistanceToLocalTimeWithSeconds(CharSequence input) {
        Objects.requireNonNull(input);

        final double base = H_NOT_NULL;
        long distance = 0;

        for (int i = 0; i < input.length(); i++) {

            char c = input.charAt(i);

            //format HH:MM:SS
            //let's simplify and only allow 00:00:00 to 19:59:59

            if (i == 0) {
                distance += distanceToRange(c, '0', '1');
            } else if (i == 1 || i == 4 || i == 7) {
                distance += distanceToRange(c, '0', '9');
            } else if (i == 2 || i == 5) {
                distance += distanceToChar(c, ':');
            } else if (i == 3 || i == 6) {
                distance += distanceToRange(c, '0', '5');
            } else {
                distance += MAX_CHAR_DISTANCE;
            }
        }

        if (input.length() < ISO_LOCAL_TIME_LENGTH) {
            //too short
            distance += (MAX_CHAR_DISTANCE * (ISO_LOCAL_TIME_LENGTH - input.length()));
        }

        //recall h in [0,1] where the highest the distance the closer to 0
        return base + ((1d - base) / (distance + 1));
    }

    /**
     * returns a value that represents how close is the value to the format YYYY-MM-DD
     *
     * @param input
     * @return
     */
    public static double getDistanceToISOLocalDateTime(CharSequence input) {
        if (input == null) {
            return H_REACHED_BUT_NULL;
        }
        try {
            LocalDateTime.parse(input);
            return H_PARSED_OK;
        } catch (DateTimeParseException ex) {
            return getApproximateDistanceToISOLocalDateTime(input);
        }
    }

    /**
     * Returns the approximate (i.e. simplified) distance of a string
     * to the format YYYY-MM-DDTHH:MM (T is case insensitive)
     *
     * For simplification, only the range of days between 01 and 28,
     * and months between 01 and 09, and hours between 00 and 19.
     *
     * @param input a non-null string that fails to parse in
     *              the YYYY-MM-DDTHH:MM format
     * @return
     */
    private static double getApproximateDistanceToISOLocalDateTime(CharSequence input) {
        Objects.requireNonNull(input);

        final double base = H_NOT_NULL;

        long distance = 0;

        for (int i = 0; i < input.length(); i++) {

            char c = input.charAt(i);

            // TODO: The code below can be refactored with class DateFormatClassReplacement
            //format YYYY-MM-DDT
            if (i >= 0 && i <= 3) {
                //any Y value is ok
                distance += distanceToDigit(c);
            } else if (i == 4 || i == 7) {
                distance += distanceToChar(c, '-');
            } else if (i == 5) {
                //let's simplify and only allow 01 to 09 for MM
                distance += distanceToChar(c, '0');
            } else if (i == 6) {
                distance += distanceToRange(c, '1', '9');
            } else if (i == 8) {
                //let's simplify and only allow 01 to 28
                distance += distanceToRange(c, '0', '2');
            } else if (i == 9) {
                distance += distanceToRange(c, '1', '8');
            } else if (i == 10) {
                // The letter 'T'. Parsing is case insensitive.
                distance += Math.min(distanceToChar(c, 'T'), distanceToChar(c, 't'));
            } else if (i == 11) {
                distance += distanceToRange(c, '0', '1');
            } else if (i == 12 || i == 15 || i == 18) {
                distance += distanceToRange(c, '0', '9');
            } else if (i == 13 || i == 16) {
                distance += distanceToChar(c, ':');
            } else if (i == 14 || i == 17) {
                distance += distanceToRange(c, '0', '5');
            } else {
                distance += MAX_CHAR_DISTANCE;
            }
        }

        if (input.length() < ISO_LOCAL_DATE_TIME_LENGTH) {
            //too short
            distance += (MAX_CHAR_DISTANCE * (ISO_LOCAL_DATE_TIME_LENGTH - input.length()));
        }

        //recall h in [0,1] where the highest the distance the closer to 0
        return base + ((1d - base) / (distance + 1));
    }

    /**
     * returns how close the input was to HH:MM or HH:MM:SS.
     * If the input fails to parse in any of those formats, the
     * distance only considers hours in the 00 to 19 range.
     *
     * @param input
     * @return
     */
    public static double getDistanceToISOLocalTime(CharSequence input) {
        if (input == null) {
            return H_REACHED_BUT_NULL;
        }

        return Math.min(
                getDistanceToLocalTimeWithoutSeconds(input),
                getDistanceToLocalTimeWithSeconds(input));
    }

    /**
     * returns a distance that represents how close is the value to the format YYYY-MM-DD
     *
     * @param input
     * @return
     */
    public static double getDistanceToISOLocalDate(CharSequence input) {
        if (input == null) {
            return H_REACHED_BUT_NULL;
        }
        try {
            /*
                due to the simplification later on (i.e. not all valid local dates
                are considered, only a subrange), still must make sure to get a 1
                if no exception is thrown
             */
            LocalDate.parse(input);
            return H_PARSED_OK;
        } catch (DateTimeParseException e) {
            return getApproximateDistanceToISOLocalDate(input);
        }
    }

    /**
     * Distance for successfully parsed input
     */
    private static final double H_PARSED_OK = 0d;


    /**
     * Returns the approximate (i.e. simplified) distance of a string
     * to the format YYYY-MM-DD.
     * For simplification, only the range of days between 01 and 28,
     * and months between 01 and 09.
     *
     * @param input a non-null string that fails to parse in
     *              the YYYY-MM-DD format
     * @return
     */
    private static double getApproximateDistanceToISOLocalDate(CharSequence input) {
        Objects.requireNonNull(input);

        //nothing to do
        final double base = H_NOT_NULL;

        long distance = 0;

        for (int i = 0; i < input.length(); i++) {

            char c = input.charAt(i);

            // TODO: The code below can be refactored with class DateFormatClassReplacement
            //format YYYY-MM-DD
            if (i >= 0 && i <= 3) {
                //any Y value is ok
                distance += distanceToDigit(c);
            } else if (i == 4 || i == 7) {
                distance += distanceToChar(c, '-');
            } else if (i == 5) {
                //let's simplify and only allow 01 to 09 for MM
                distance += distanceToChar(c, '0');
            } else if (i == 6) {
                distance += distanceToRange(c, '1', '9');
            } else if (i == 8) {
                //let's simplify and only allow 01 to 28
                distance += distanceToRange(c, '0', '2');
            } else if (i == 9) {
                distance += distanceToRange(c, '1', '8');
            } else {
                distance += MAX_CHAR_DISTANCE;
            }
        }

        if (input.length() < ISO_LOCAL_DATE_LENGTH) {
            //too short
            distance += (MAX_CHAR_DISTANCE * (ISO_LOCAL_DATE_LENGTH - input.length()));
        }

        //recall h in [0,1] where the highest the distance the closer to 0
        return base + ((1d - base) / (distance + 1));
    }

    /**
     * returns a value that represents how close is the value to the format YYYY-MM-DD
     *
     * @param input
     * @return
     */
    public static double getDistanceToDateTime(CharSequence input) {
        if (input == null) {
            return H_REACHED_BUT_NULL;
        }
        try {
            /*
                due to the simplification later on (i.e. only some valid dates are considered,
                but not all), still must make sure to get a 1 if no exception is thrown
             */
            new SimpleDateFormat(DATE_TIME_NO_SECONDS_FORMAT).parse(input.toString());
            return H_PARSED_OK;
        } catch (ParseException e) {
            return getApproximateDistanceToDateTime(input);
        }

    }

    /**
     * Returns the approximate (i.e. simplified) distance of a string
     * to the format YYYY-MM-DD HH:MM.
     * For simplification, only the range of days between 01 and 28,
     * and months between 01 and 09, and hours between 00 and 19.
     *
     * @param input a non-null string that fails to parse in
     *              the YYYY-MM-DD HH:MM format
     * @return
     */
    private static double getApproximateDistanceToDateTime(CharSequence input) {
        Objects.requireNonNull(input);

        final double base = H_NOT_NULL;

        long distance = 0;

        for (int i = 0; i < input.length(); i++) {

            char c = input.charAt(i);

            //format YYYY-MM-DD HH:MM

            if (i >= 0 && i <= 3) {
                //any Y value is ok
                distance += distanceToDigit(c);
            } else if (i == 4 || i == 7) {
                distance += distanceToChar(c, '-');
            } else if (i == 5) {
                //let's simplify and only allow 01 to 09 for MM
                distance += distanceToChar(c, '0');
            } else if (i == 6) {
                distance += distanceToRange(c, '1', '9');
            } else if (i == 8) {
                //let's simplify and only allow 01 to 28 for DD
                distance += distanceToRange(c, '0', '2');
            } else if (i == 9) {
                distance += distanceToRange(c, '1', '8');
            } else if (i == 10) {
                distance += distanceToChar(c, ' ');
            } else if (i == 11) {
                // let's simplify and only allow 00 to 19 for HH
                distance += distanceToRange(c, '0', '1');
            } else if (i == 12) {
                distance += distanceToRange(c, '0', '9');
            } else if (i == 13) {
                distance += distanceToChar(c, ':');
            } else if (i == 14) {
                // allow 00 to 59 for MM
                distance += distanceToRange(c, '0', '5');
            } else if (i == 15) {
                distance += distanceToRange(c, '0', '9');
            } else {
                distance += MAX_CHAR_DISTANCE;
            }
        }

        int requiredLength = "YYYY-MM-DD HH:MM".length();
        if (input.length() < requiredLength) {
            //too short
            distance += (MAX_CHAR_DISTANCE * (requiredLength - input.length()));
        }

        //recall h in [0,1] where the highest the distance the closer to 0
        return base + ((1d - base) / (distance + 1));
    }

    public static double getDistanceToDateTimePattern(String input, String dateFormatPattern) {
        if (input == null) {
            return H_REACHED_BUT_NULL;
        }

        try {
            /*
                due to the simplification later on (i.e. only some valid dates are considered,
                but not all), still must make sure to get a 1 if no exception is thrown
             */
            new SimpleDateFormat(dateFormatPattern).parse(input.toString());
            return H_PARSED_OK;
        } catch (ParseException e) {
            // TODO translate dateFormatPattern to Java regular expression
            // TODO use distance to Java Regular Expression as an approximate gradient to satisfy the pattern
            return 1d;
        }
    }
}
