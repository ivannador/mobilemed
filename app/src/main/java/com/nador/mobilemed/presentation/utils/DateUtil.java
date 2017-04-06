package com.nador.mobilemed.presentation.utils;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nador on 19/07/16.
 */
public class DateUtil {

    private DateUtil() {}

    public static final long getTimestamp(final String date, final String pattern) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            Date dateObj = dateFormat.parse(date);
            return dateObj.getTime();
        } catch (ParseException e) {
            return 0;
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    public static final String getDateString(final long timestamp) {
        try {
            if (timestamp == 0) {
                throw new InvalidParameterException();
            }

            DateFormat dateFormat = new SimpleDateFormat("MMM dd, HH:mm a");
            Date date = new Date(timestamp);
            return dateFormat.format(date);
        } catch (Exception e) {
            return "-";
        }
    }

    public static final String getDateString(final long timestamp, final String format) {
        try {
            if (timestamp == 0) {
                throw new InvalidParameterException();
            }

            DateFormat dateFormat = new SimpleDateFormat(format);
            Date date = new Date(timestamp);
            return dateFormat.format(date);
        } catch (Exception e) {
            return "-";
        }
    }

    public static final String getHourMinuteString(final long timestamp) {
        try {
            if (timestamp == 0) {
                throw new InvalidParameterException();
            }

            DateFormat dateFormat = new SimpleDateFormat("HH:mm a");
            Date date = new Date(timestamp);
            return dateFormat.format(date);
        } catch (Exception e) {
            return "-";
        }
    }
}
