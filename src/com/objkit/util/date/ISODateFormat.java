package com.objkit.util.date;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ISODateFormat extends DateFormat {
    private static final String GMT_ID = "GMT";
    private static Calendar CALENDAR = new GregorianCalendar();
    private static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone(GMT_ID);

    protected ISODateFormat() {
        this.calendar = CALENDAR;
    }

    @Override
    public StringBuffer format(Date date, StringBuffer formatted, FieldPosition fieldPosition) {
        calendar.setTime(date);
        padInt(formatted, calendar.get(Calendar.YEAR), 4);
        formatted.append('-');
        padInt(formatted, calendar.get(Calendar.MONTH) + 1, 2);
        formatted.append('-');
        padInt(formatted, calendar.get(Calendar.DAY_OF_MONTH), 2);
        formatted.append('T');
        padInt(formatted, calendar.get(Calendar.HOUR_OF_DAY), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(Calendar.MINUTE), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(Calendar.SECOND), 2);
        int milliseconds = calendar.get(Calendar.MILLISECOND);
        if (milliseconds > 0) {
            formatted.append('.');
            padInt(formatted, calendar.get(Calendar.MILLISECOND), 3);
        }

        int offset = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / 60000;
        if (offset == 0) {
            formatted.append('Z');
        } else {
            formatted.append(offset > 0 ? '+' : '-');
            offset = Math.abs(offset);
            padInt(formatted, offset / 60, 2);
            formatted.append(':');
            padInt(formatted, offset % 60, 2);
        }
        return formatted;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        Exception exception;
        try {
            int length = source.length();
            int offset = 0;

            //noinspection UnusedAssignment
            int year = 1;
            int month = 1;
            int day = 1;
            int hour = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;
            TimeZone timezone = TIMEZONE_GMT;

            do {
                year = parseInt(source, offset, offset += 4);
                if (offset >= length) break;

                if (source.charAt(offset) == '-') offset += 1;
                month = parseInt(source, offset, offset += 2);
                if (offset >= length) break;

                if (source.charAt(offset) == '-') offset += 1;
                day = parseInt(source, offset, offset += 2);
                if (offset >= length) break;

                char sep = source.charAt(offset);
                if (sep != 'T' && sep != ' ')
                    throw new IndexOutOfBoundsException("Expected 'T' or ' ' character but found '" + sep + "'");

                hour = parseInt(source, offset += 1, offset += 2);
                if (offset >= length) break;

                if (source.charAt(offset) == ':') offset += 1;
                minutes = parseInt(source, offset, offset += 2);
                if (offset >= length) break;

                if (source.charAt(offset) == ':') offset += 1;
                seconds = parseInt(source, offset, offset += 2);
                if (offset >= length) break;


                if (source.charAt(offset) >= '0' && source.charAt(offset) <= '9') {
                    milliseconds = parseInt(source, offset, offset += 3);
                } else if (source.charAt(offset) == '.') {
                    offset += 1;
                    milliseconds = parseInt(source, offset, offset += 3);
                }
                if (offset >= length) break;

                // Java doesn't support microseconds, just ignore it.
                if (source.charAt(offset) >= '0' && source.charAt(offset) <= '9') offset += 3;
                if (offset >= length) break;

                String timezoneId;
                char timezoneIndicator = source.charAt(offset);
                if (timezoneIndicator == '+' || timezoneIndicator == '-') {
                    timezoneId = GMT_ID + source.substring(offset);
                } else if (timezoneIndicator == 'Z') {
                    timezoneId = GMT_ID;
                } else {
                    throw new IndexOutOfBoundsException("Invalid time zone indicator " + timezoneIndicator);
                }
                timezone = TimeZone.getTimeZone(timezoneId);
                if (!timezone.getID().equals(timezoneId)) {
                    throw new IndexOutOfBoundsException();
                }
            } while (false);

            pos.setIndex(length);
            Calendar cal = new GregorianCalendar(timezone);
            cal.setLenient(false);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, seconds);
            cal.set(Calendar.MILLISECOND, milliseconds);

            return cal.getTime();
        } catch (IndexOutOfBoundsException e) {
            exception = e;
        } catch (NumberFormatException e) {
            exception = e;
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        throw new IllegalArgumentException("Failed to parse date [" + source + "]: " + exception.getMessage(), exception);
    }

    /**
     * Parse an integer located between 2 given offsets in a string
     *
     * @param value      the string to parse
     * @param beginIndex the start index for the integer in the string
     * @param endIndex   the end index for the integer in the string
     * @return the int
     * @throws NumberFormatException if the value is not a number
     */
    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        int index = beginIndex;
        int result = 0;
        int digit;
        while (index < endIndex) {
            digit = value.charAt(index++) - '0';
            if (digit < 0 || digit > 9) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result *= 10;
            result += digit;
        }
        return result;
    }

    /**
     * Zero pad a number to a specified length
     *
     * @param buffer buffer to use for padding
     * @param value  the integer value to pad if necessary.
     * @param length the length of the string we should zero pad
     */
    private static void padInt(StringBuffer buffer, int value, int length) {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--) {
            buffer.append('0');
        }
        buffer.append(strValue);
    }
}
