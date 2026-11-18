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
        assertEquals("Deck: 100 cards", prepared.serieSetup(LIBRARY, 0).chipsText());
        assertTrue(prepared.serieSetup(LIBRARY, 1).isDefault());
        assertEquals("Starting: 4 credits", prepared.serieSetup(LIBRARY, 2).chipsText());
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
        assertEquals("series[0].options: Serie 1: \"Starting: credits\" is not used in Open",
                message(_factory, LeagueFactoryTests.constructed("Bad", 20261101, new LeagueFactory.Serie("open", 7, 10, Map.of("startingCredits", "4")))));
        assertEquals("series[1].options: Serie 2: \"Timer: game\" can't be changed in this kind of game",
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
        assertEquals("Deck: 75 cards", second.chipsText());
        assertEquals(75, LIBRARY.effectiveFormat(second).getRequiredDeckSize());
        assertEquals(1, LeagueSetups.indexOf(series, series.get(1)));
        // a league from before #119 has no stored setup: its format codes decide, as before
        League old = new League(null, 0, "Old", "o", prepared.leagueClass(), prepared.parameters(), 0, true, false, true, false, "", 300, 50, null);
        assertTrue(LeagueSetups.forSerie(LIBRARY, old, series.get(1), 1).isDefault());
        // a damaged column falls back to the format code instead of failing the hall
        old.setGameSetup("not json");
        assertTrue(LeagueSetups.forSerie(LIBRARY, old, series.get(1), 1).isDefault());
    }

    @Test
    public void LeagueSetupOperativeChoiceSurvivesStoredSeriesWithoutChangingOtherSeries() throws Exception {
        var prepared = _factory.prepare(LeagueFactoryTests.constructed("Operative choices", 20261118,
                new LeagueFactory.Serie("open", 7, 10, Map.of("operativeRule", "false")),
                new LeagueFactory.Serie("open", 7, 10)), "c");
        var league = new League(null, 0, prepared.name(), prepared.code(), prepared.leagueClass(), prepared.parameters(),
                0, true, false, true, false, "", 300, 50, null);
        league.setGameSetup(prepared.gameSetup());
        var chosen = LeagueSetups.forSerie(LIBRARY, league, prepared.series().get(0), 0);
        var ordinary = LeagueSetups.forSerie(LIBRARY, league, prepared.series().get(1), 1);
        assertEquals("false", chosen.get("operativeRule", LIBRARY.getFormat("open")));
        assertEquals("Rules: no operative rule", chosen.chipsText());
        assertEquals("true", ordinary.get("operativeRule", LIBRARY.getFormat("open")));
        assertTrue(ordinary.isDefault());
        assertEquals("Open", LIBRARY.effectiveFormat(chosen).getName());
    }


    @Test
    public void LeagueSetupTelepathyChoiceSurvivesStoredSeriesAndKeepsFormatDefaultsSeparate() throws Exception {
        for (String choice : List.of("printed", "current")) {
            var prepared = _factory.prepare(LeagueFactoryTests.constructed("Telepathy choices", 20261118,
                    new LeagueFactory.Serie("open", 7, 10, Map.of("projectiveTelepathy", choice)),
                    new LeagueFactory.Serie("premiere_ds2", 7, 10)), "c");
            var league = new League(null, 0, prepared.name(), prepared.code(), prepared.leagueClass(), prepared.parameters(),
                    0, true, false, true, false, "", 300, 50, null);
            league.setGameSetup(prepared.gameSetup());
            var chosen = LeagueSetups.forSerie(LIBRARY, league, prepared.series().get(0), 0);
            var ordinary = LeagueSetups.forSerie(LIBRARY, league, prepared.series().get(1), 1);
            assertEquals(choice, chosen.getChosen("projectiveTelepathy"));
            assertEquals(choice.equals("printed") ? "5_149o" : "5_149", LIBRARY.effectiveFormat(chosen).resolveCardId("5_149o"));
            assertTrue(ordinary.isDefault());
            assertEquals("5_149o", LIBRARY.effectiveFormat(ordinary).resolveCardId("5_149"));
        }
    }

}
