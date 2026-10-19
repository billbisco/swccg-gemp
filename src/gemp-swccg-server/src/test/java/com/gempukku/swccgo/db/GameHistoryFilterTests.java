package com.gempukku.swccgo.db;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GameHistoryFilterTests {
    @Test
    public void GameHistoryFilterTreatsBlankValuesAsNoFilter() {
        //test1: blank text fields become null
        //test2: a filter with only blanks is empty and valid
        GameHistoryFilter filter = new GameHistoryFilter("  ", "", false, " ", "", " ", null, null);

        assertNull(filter.format()); //test1
        assertNull(filter.opponent()); //test1
        assertNull(filter.event()); //test1
        assertNull(filter.side()); //test1
        assertNull(filter.result()); //test1
        assertTrue(filter.isEmpty()); //test2
        assertNull(filter.validate()); //test2
    }

    @Test
    public void GameHistoryFilterNormalizesSideNames() {
        //test1: dark / DS read as Dark
        //test2: light / LS read as Light
        assertEquals(GameHistoryFilter.DARK, sideFilter("dark").side()); //test1
        assertEquals(GameHistoryFilter.DARK, sideFilter("DS").side()); //test1
        assertEquals(GameHistoryFilter.LIGHT, sideFilter("LIGHT").side()); //test2
        assertEquals(GameHistoryFilter.LIGHT, sideFilter("ls").side()); //test2
    }

    @Test
    public void GameHistoryFilterRejectsUnknownSide() {
        assertEquals("Side must be Dark or Light.", sideFilter("Grey").validate());
    }

    @Test
    public void GameHistoryFilterNormalizesResult() {
        //test1: won / win read as W
        //test2: lost / loss read as L
        //test3: anything else is rejected
        assertEquals(GameHistoryFilter.WON, resultFilter("won").result()); //test1
        assertEquals(GameHistoryFilter.WON, resultFilter("win").result()); //test1
        assertEquals(GameHistoryFilter.LOST, resultFilter("Lost").result()); //test2
        assertEquals(GameHistoryFilter.LOST, resultFilter("l").result()); //test2
        assertEquals("Result must be W or L.", resultFilter("draw").validate()); //test3
    }

    @Test
    public void GameHistoryFilterRejectsFromAfterTo() {
        //test1: from after to is rejected
        //test2: the same day for both is fine
        GameHistoryFilter backwards = new GameHistoryFilter(null, null, false, null, null, null,
                LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 1));
        GameHistoryFilter oneDay = new GameHistoryFilter(null, null, false, null, null, null,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 1));

        assertEquals("The 'from' date is after the 'to' date.", backwards.validate()); //test1
        assertNull(oneDay.validate()); //test2
    }

    @Test
    public void GameHistoryFilterRejectsOverlongText() {
        String tooLong = "x".repeat(GameHistoryFilter.MAX_TEXT_LENGTH + 1);
        GameHistoryFilter filter = new GameHistoryFilter(null, tooLong, false, null, null, null, null, null);

        assertEquals("Filter text is too long.", filter.validate());
    }

    @Test
    public void GameHistoryFilterCasualEventIgnoresCase() {
        //test1: Casual in any case selects casual games
        //test2: a league name does not
        assertTrue(eventFilter("CASUAL").casualOnly()); //test1
        assertFalse(eventFilter("Casual Cup League").casualOnly()); //test2
    }

    private static GameHistoryFilter sideFilter(String side) {
        return new GameHistoryFilter(null, null, false, null, side, null, null, null);
    }

    private static GameHistoryFilter resultFilter(String result) {
        return new GameHistoryFilter(null, null, false, null, null, result, null, null);
    }

    private static GameHistoryFilter eventFilter(String event) {
        return new GameHistoryFilter(null, null, false, event, null, null, null, null);
    }
}
