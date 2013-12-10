package com.objkit.util.date;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class ISODateFormatTest {
    ISODateFormat dateFormat;
    TimeZone gmt;
    TimeZone cst;
    TimeZone alaska;

    @Before
    public void setUp() {
        dateFormat = new ISODateFormat();
        gmt = TimeZone.getTimeZone("GMT");
        cst = TimeZone.getTimeZone("GMT+0800");
        alaska = TimeZone.getTimeZone("US/Alaska");
    }

    @Test
    public void testFormatTimeZone() {
        Date date = createDate(2000, 1, 1, 8, 0, 0, 0, cst);
        dateFormat.setTimeZone(cst);
        assertEquals("2000-01-01T08:00:00+08:00", dateFormat.format(date));
    }

    @Test
    public void testFormatGMT() {
        Date date = createDate(2000, 1, 1, 8, 0, 0, 0, cst);
        dateFormat.setTimeZone(gmt);
        assertEquals("2000-01-01T00:00:00Z", dateFormat.format(date));
    }

    @Test
    public void testFormatMilliseconds() {
        Date date = createDate(2000, 1, 1, 8, 0, 0, 1, cst);
        dateFormat.setTimeZone(cst);
        assertEquals("2000-01-01T08:00:00.001+08:00", dateFormat.format(date));
    }

    @Test
    public void testFormatDST() {
        dateFormat.setTimeZone(alaska);

        Date date = createDate(2000, 6, 1, 8, 0, 0, 1, alaska);
        assertEquals("2000-06-01T08:00:00.001-08:00", dateFormat.format(date));

        date = createDate(2000, 12, 1, 8, 0, 0, 1, alaska);
        assertEquals("2000-12-01T08:00:00.001-09:00", dateFormat.format(date));
    }


    @Test
    public void testParseYear() throws ParseException {
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), createDate(2000, 1, 1, 8, 0, 0, 0, cst));
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("2000"));
    }

    @Test
    public void testParseYearMonth() throws ParseException {
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("2000-01"));
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("200001"));
    }

    @Test
    public void testParseYMD() throws ParseException {
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("2000-01-01"));
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("20000101"));
    }

    @Test
    public void testParseYMDH() throws ParseException {
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("2000-01-01 00"));
        assertEquals(createDate(2000, 1, 1, 1, 0, 0, 0, gmt), dateFormat.parse("2000-01-01 01"));
        assertEquals(createDate(2000, 1, 1, 1, 0, 0, 0, gmt), dateFormat.parse("2000-01-01T01"));
    }
    @Test
    public void testParseYMDHM() throws ParseException {
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("2000-01-01 00:00"));
        assertEquals(createDate(2000, 1, 1, 1, 1, 0, 0, gmt), dateFormat.parse("2000-01-01T01:01"));
        assertEquals(createDate(2000, 1, 1, 1, 1, 0, 0, gmt), dateFormat.parse("2000-01-01T0101"));
    }
    @Test
    public void testParseYMDHMS() throws ParseException {
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("2000-01-01 00:00:00"));
        assertEquals(createDate(2000, 1, 1, 1, 1, 1, 0, gmt), dateFormat.parse("2000-01-01T01:01:01"));
        assertEquals(createDate(2000, 1, 1, 1, 1, 1, 0, gmt), dateFormat.parse("2000-01-01T010101"));
    }
    @Test
    public void testParseYMDHMM() throws ParseException {
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 1, gmt), dateFormat.parse("2000-01-01T00:00:00.001"));
        assertEquals(createDate(2000, 1, 1, 1, 1, 1, 1, gmt), dateFormat.parse("2000-01-01T010101.001001"));
    }
    @Test
    public void testParseYMDHMMTZ() throws ParseException {
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 0, gmt), dateFormat.parse("2000-01-01T00:00:00Z"));
        assertEquals(createDate(2000, 1, 1, 0, 0, 0, 1, gmt), dateFormat.parse("2000-01-01T00:00:00.001Z"));
        assertEquals(createDate(2000, 1, 1, 1, 1, 1, 1, cst), dateFormat.parse("2000-01-01T010101.001001+08:00"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidNumber() throws ParseException {
        dateFormat.parse("2000-99-01T08:00:00+08:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidTZ() throws ParseException {
        dateFormat.parse("2000-99-01T08:00:00+99:00");
    }

    private Date createDate(int year, int month, int day, int hour, int minutes, int seconds, int milliseconds, TimeZone timezone) {
        Calendar calendar = new GregorianCalendar(timezone);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, milliseconds);
        return calendar.getTime();
    }

}
