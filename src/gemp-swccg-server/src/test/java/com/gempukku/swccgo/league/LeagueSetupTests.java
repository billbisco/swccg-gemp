package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.setup.GameSetup;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** swccg-guild/work#119: a league stores a full game setup per serie, not only a deck size on the format code. */
public class LeagueSetupTests {
    private static final SwccgoFormatLibrary LIBRARY = VirtualTableScenario._formatLibrary;

    private final LeagueDAO _leagueDao = Mockito.mock(LeagueDAO.class);
    private final LeagueFactory _factory = new LeagueFactory(Mockito.mock(SwccgCardBlueprintLibrary.class), LIBRARY,
            Mockito.mock(SoloDraftDefinitions.class), _leagueDao, Mockito.mock(LeagueService.class));

    private static String message(LeagueFactory factory, LeagueFactory.Definition d) {
        try {
            factory.prepare(d, "c");
            return null;
        } catch (LeagueDefinitionException exp) {
            return exp.getParameter() + ": " + exp.getMessage();
        }
    }

    @Test
    public void LeagueSetupEachSerieKeepsItsOwnOptions() throws Exception {
        LeagueFactory.PreparedLeague prepared = _factory.prepare(LeagueFactoryTests.constructed("Options League", 20261101,
                new LeagueFactory.Serie("open", 7, 10, Map.of("deckSize", "100")),
                new LeagueFactory.Serie("open", 7, 10),
                new LeagueFactory.Serie("premiere_reflections_gold", 7, 10, Map.of("startingCredits", "4"))), "c");
        assertEquals("the parameters keep plain format codes", "20261101,default,3,open,7,10,open,7,10,premiere_reflections_gold,7,10",
                prepared.parameters());
        assertEquals("100 cards", prepared.serieSetup(LIBRARY, 0).chipsText());
        assertTrue(prepared.serieSetup(LIBRARY, 1).isDefault());
        assertEquals("4 starting credits", prepared.serieSetup(LIBRARY, 2).chipsText());
        assertTrue(prepared.gameSetup().startsWith("[") && prepared.gameSetup().contains("null"));
    }

    @Test
    public void LeagueSetupWithNoOptionsStoresNothing() throws Exception {
        LeagueFactory.PreparedLeague prepared = _factory.prepare(LeagueFactoryTests.constructed("Plain", 20261101,
                new LeagueFactory.Serie("open", 7, 10, Map.of("deckSize", "60"))), "c");
        assertNull("60 is Open's own size, so the league stores no setup", prepared.gameSetup());
        assertTrue(prepared.serieSetup(LIBRARY, 0).isDefault());
    }

    @Test
    public void LeagueSetupOlderSizedCodesBecomeTheDeckSizeOption() throws Exception {
        LeagueFactory.PreparedLeague prepared = _factory.prepare(LeagueFactoryTests.constructed("Sized", 20261101,
                new LeagueFactory.Serie("open@45", 7, 10)), "c");
        assertEquals("20261101,default,1,open,7,10", prepared.parameters());
        assertEquals("45", prepared.serieSetup(LIBRARY, 0).getChosen("deckSize"));
    }

    @Test
    public void LeagueSetupRefusesOptionsTheFormatOrALeagueCannotUse() {
        assertEquals("series[0].options: Serie 1: Starting credits is not used in Open",
                message(_factory, LeagueFactoryTests.constructed("Bad", 20261101, new LeagueFactory.Serie("open", 7, 10, Map.of("startingCredits", "4")))));
        assertEquals("series[1].options: Serie 2: Game timer can't be changed in this kind of game",
                message(_factory, LeagueFactoryTests.constructed("Bad", 20261101, new LeagueFactory.Serie("open", 7, 10),
                        new LeagueFactory.Serie("open", 7, 10, Map.of("timer", "blitz")))));
        assertNotNull(message(_factory, LeagueFactoryTests.constructed("Bad", 20261101, new LeagueFactory.Serie("open", 7, 10, Map.of("deckSize", "101")))));
        assertNotNull(message(_factory, LeagueFactoryTests.constructed("Bad", 20261101, new LeagueFactory.Serie("open", 7, 10, Map.of("noSuch", "1")))));
    }

    @Test
    public void LeagueSetupReadsBackFromTheStoredLeague() throws Exception {
        LeagueFactory.PreparedLeague prepared = _factory.prepare(LeagueFactoryTests.constructed("Stored", 20261101,
                new LeagueFactory.Serie("open", 7, 10), new LeagueFactory.Serie("open", 7, 10, Map.of("deckSize", "75"))), "c");
        League league = new League(null, 0, prepared.name(), prepared.code(), prepared.leagueClass(), prepared.parameters(), 0, true, false, true, false, "", 300, 50, null);
        league.setGameSetup(prepared.gameSetup());
        List<LeagueSeriesData> series = prepared.series();
        assertTrue(LeagueSetups.forSerie(LIBRARY, league, series.get(0), 0).isDefault());
        GameSetup second = LeagueSetups.forSerie(LIBRARY, league, series.get(1), 1);
        assertEquals("75 cards", second.chipsText());
        assertEquals(75, LIBRARY.effectiveFormat(second).getRequiredDeckSize());
        assertEquals(1, LeagueSetups.indexOf(series, series.get(1)));
        // a league from before #119 has no stored setup: its format codes decide, as before
        League old = new League(null, 0, "Old", "o", prepared.leagueClass(), prepared.parameters(), 0, true, false, true, false, "", 300, 50, null);
        assertTrue(LeagueSetups.forSerie(LIBRARY, old, series.get(1), 1).isDefault());
        // a damaged column falls back to the format code instead of failing the hall
        old.setGameSetup("not json");
        assertTrue(LeagueSetups.forSerie(LIBRARY, old, series.get(1), 1).isDefault());
    }
}
