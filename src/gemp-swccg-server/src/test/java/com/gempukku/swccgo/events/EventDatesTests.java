package com.gempukku.swccgo.events;

import org.junit.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EventDatesTests {
    private static final long OCT_1_2026_UTC = 1790812800000L;
    private static final long DAY = 24L * 60 * 60 * 1000;

    @Test
    public void EventDatesConvertsBetweenDayIntegersAndDates() {
        assertEquals(LocalDate.of(2026, 10, 20), EventDates.fromDateInt(20261020));
        assertEquals(20261020, EventDates.toDateInt(LocalDate.of(2026, 10, 20)));
        assertEquals(20260101, EventDates.toDateInt(LocalDate.of(2026, 1, 1)));
    }

    @Test
    public void EventDatesFormatsDayIntegersAsIsoDates() {
        //test1: single-digit month and day are zero-padded
        //test2: 0 (a column that was not read) is null rather than "0000-00-00"
        assertEquals("2026-01-05", EventDates.isoDate(20260105)); //test1
        assertEquals("2026-10-20", EventDates.isoDate(20261020));
        assertNull(EventDates.isoDate(0)); //test2
    }

    @Test
    public void EventDatesMonthBoundsAreHalfOpenDayIntegers() {
        //test1: the month runs from its 1st to the 1st of the next month
        //test2: December rolls into the next year
        assertEquals(20261001, EventDates.monthStart(YearMonth.of(2026, 10))); //test1
        assertEquals(20261101, EventDates.nextMonthStart(YearMonth.of(2026, 10))); //test1
        assertEquals(20261201, EventDates.monthStart(YearMonth.of(2026, 12))); //test2
        assertEquals(20270101, EventDates.nextMonthStart(YearMonth.of(2026, 12))); //test2
    }

    @Test
    public void EventDatesMonthBoundsInMillisAreUtcMidnights() {
        //test1: the month starts at 00:00 UTC on the 1st
        //test2: the exclusive end is 00:00 UTC on the 1st of the next month (31 days later for October)
        assertEquals(OCT_1_2026_UTC, EventDates.monthStartMillis(YearMonth.of(2026, 10))); //test1
        assertEquals(OCT_1_2026_UTC + 31 * DAY, EventDates.nextMonthStartMillis(YearMonth.of(2026, 10))); //test2
        assertEquals(OCT_1_2026_UTC + 20 * DAY, EventDates.startOfDayMillis(LocalDate.of(2026, 10, 21)));
    }

    @Test
    public void EventDatesMonthKeysComeFromDaysAndInstants() {
        //test1: from a yyyyMMdd day
        //test2: from an instant, in UTC: 23:59 on Oct 31 is still October
        //test3: from a day counted from the epoch (what FLOOR(start / 86400000) yields)
        assertEquals("2026-10", EventDates.monthKey(20261020)); //test1
        assertEquals("2026-03", EventDates.monthKey(20260301)); //test1
        assertEquals("2026-10", EventDates.monthKeyOfMillis(OCT_1_2026_UTC + 31 * DAY - 60_000)); //test2
        assertEquals("2026-11", EventDates.monthKeyOfMillis(OCT_1_2026_UTC + 31 * DAY)); //test2
        assertEquals("2026-10", EventDates.monthKeyOfEpochDay(OCT_1_2026_UTC / DAY)); //test3
        assertEquals("2026-09", EventDates.monthKeyOfEpochDay(OCT_1_2026_UTC / DAY - 1)); //test3
    }

    @Test
    public void EventDatesFormatsInstantsAsUtcDatesAndTimes() {
        long oct5At19 = OCT_1_2026_UTC + 4 * DAY + 19 * 60 * 60 * 1000;

        assertEquals("2026-10-05", EventDates.isoDateOfMillis(oct5At19));
        assertEquals("2026-10-05T19:00:00", EventDates.isoDateTimeOfMillis(oct5At19));
        assertEquals(LocalDate.of(2026, 10, 5), EventDates.dayOfMillis(oct5At19));
    }
}
