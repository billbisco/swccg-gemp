package com.gempukku.swccgo.events;

import com.gempukku.swccgo.DateUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * The date arithmetic behind the Events tab's calendar and completed-event browser, in the two forms the database
 * stores dates in: {@code league.start} / {@code league.end} are integers of the form {@code yyyyMMdd} (as are the
 * league series' dates), and {@code tournament.start} is epoch milliseconds.  Everything is UTC, which is the hall's
 * server time.  Month ranges are half-open ([first day of the month, first day of the next month)), so the boundaries
 * work for both integer days and millisecond instants.
 */
public final class EventDates {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private EventDates() {
    }

    /** Today in server time, as {@code yyyyMMdd}. */
    public static int today() {
        return DateUtils.getCurrentDate();
    }

    /** {@code 20261020} to 2026-10-20.  Throws for a number that is not a calendar day. */
    public static LocalDate fromDateInt(int yyyymmdd) {
        return LocalDate.of(yyyymmdd / 10000, (yyyymmdd / 100) % 100, yyyymmdd % 100);
    }

    /** 2026-10-20 to {@code 20261020}. */
    public static int toDateInt(LocalDate date) {
        return date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
    }

    /** {@code 20261020} to "2026-10-20"; null for 0 (an unknown date). */
    public static String isoDate(int yyyymmdd) {
        if (yyyymmdd <= 0)
            return null;
        return String.format("%04d-%02d-%02d", yyyymmdd / 10000, (yyyymmdd / 100) % 100, yyyymmdd % 100);
    }

    /** The first day of the month as {@code yyyyMMdd}. */
    public static int monthStart(YearMonth month) {
        return toDateInt(month.atDay(1));
    }

    /** The first day of the following month as {@code yyyyMMdd} (the exclusive upper bound of the month). */
    public static int nextMonthStart(YearMonth month) {
        return toDateInt(month.plusMonths(1).atDay(1));
    }

    /** Midnight UTC starting the month, in epoch milliseconds. */
    public static long monthStartMillis(YearMonth month) {
        return startOfDayMillis(month.atDay(1));
    }

    /** Midnight UTC starting the following month, in epoch milliseconds (the exclusive upper bound of the month). */
    public static long nextMonthStartMillis(YearMonth month) {
        return startOfDayMillis(month.plusMonths(1).atDay(1));
    }

    /** Midnight UTC starting the day, in epoch milliseconds. */
    public static long startOfDayMillis(LocalDate day) {
        return day.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    /** "yyyy-MM" of a {@code yyyyMMdd} day. */
    public static String monthKey(int yyyymmdd) {
        return String.format("%04d-%02d", yyyymmdd / 10000, (yyyymmdd / 100) % 100);
    }

    /** "yyyy-MM" of a UTC instant in epoch milliseconds. */
    public static String monthKeyOfMillis(long millis) {
        LocalDate day = dayOfMillis(millis);
        return String.format("%04d-%02d", day.getYear(), day.getMonthValue());
    }

    /** "yyyy-MM" of a day counted from the epoch (what {@code FLOOR(start / 86400000)} yields in SQL). */
    public static String monthKeyOfEpochDay(long epochDay) {
        LocalDate day = LocalDate.ofEpochDay(epochDay);
        return String.format("%04d-%02d", day.getYear(), day.getMonthValue());
    }

    /** The UTC day of an instant in epoch milliseconds. */
    public static LocalDate dayOfMillis(long millis) {
        return Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate();
    }

    /** "2026-10-05" of an instant in epoch milliseconds (UTC). */
    public static String isoDateOfMillis(long millis) {
        return dayOfMillis(millis).toString();
    }

    /** "2026-10-05T19:00:00" of an instant in epoch milliseconds (UTC); the calendar shows the hh:mm of it. */
    public static String isoDateTimeOfMillis(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC).format(DATE_TIME);
    }
}
