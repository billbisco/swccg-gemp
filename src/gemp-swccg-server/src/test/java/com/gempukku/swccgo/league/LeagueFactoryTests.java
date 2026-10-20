package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.draft2.SoloDraft;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The factory renders the same parameters strings AdminRequestHandler used to build inline, builds the series
 * through the real LeagueData classes, and refuses what cannot become a league.
 */
public class LeagueFactoryTests {
    private LeagueDAO _leagueDao;
    private LeagueService _leagueService;
    private SwccgoFormatLibrary _formatLibrary;
    private SoloDraftDefinitions _soloDraftDefinitions;
    private LeagueFactory _factory;

    @Before
    public void setUp() {
        _leagueDao = Mockito.mock(LeagueDAO.class);
        _leagueService = Mockito.mock(LeagueService.class);
        _formatLibrary = Mockito.mock(SwccgoFormatLibrary.class);
        _soloDraftDefinitions = Mockito.mock(SoloDraftDefinitions.class);
        for (String code : new String[]{"open", "premiere_ds2", "ewok", "reflections_sealed", "wattos_cube_with_objective", "cube"})
            stubFormat(code, code.toUpperCase());
        SoloDraft draft = Mockito.mock(SoloDraft.class);
        Mockito.when(draft.getFormat()).thenReturn("cube");
        Mockito.when(_soloDraftDefinitions.getSoloDraft("c7obj_draft")).thenReturn(draft);
        _factory = new LeagueFactory(Mockito.mock(SwccgCardBlueprintLibrary.class), _formatLibrary, _soloDraftDefinitions, _leagueDao, _leagueService);
    }

    private void stubFormat(String code, String name) {
        SwccgFormat format = Mockito.mock(SwccgFormat.class);
        Mockito.when(format.getName()).thenReturn(name);
        Mockito.when(_formatLibrary.getFormat(code)).thenReturn(format);
    }

    static LeagueFactory.Definition constructed(String name, int start, LeagueFactory.Serie... series) {
        LeagueFactory.Definition d = new LeagueFactory.Definition();
        d.kind = LeagueFactory.Kind.CONSTRUCTED;
        d.name = name;
        d.cost = 500;
        d.start = start;
        d.series = List.of(series);
        return d;
    }

    static LeagueFactory.Definition sealed(String name, int start, String product) {
        LeagueFactory.Definition d = constructed(name, start, new LeagueFactory.Serie(product, 7, 10));
        d.kind = LeagueFactory.Kind.SEALED;
        return d;
    }

    private static String message(LeagueFactory factory, LeagueFactory.Definition d) {
        try {
            factory.prepare(d, "c");
            return null;
        } catch (LeagueDefinitionException exp) {
            return exp.getParameter() + ": " + exp.getMessage();
        }
    }

    // --- the parameters strings (what the admin handler built inline before) ---------------------------------

    @Test
    public void ConstructedParametersAreStartCollectionCountAndTriples() throws Exception {
        //test1: the string the handler built: start,collection,count,format,duration,matches,...
        //test2: the series come out of NewConstructedLeagueData back to back
        //test3: the league is listed two days after the last serie
        LeagueFactory.PreparedLeague prepared = _factory.prepare(
                constructed("Open League", 20261101, new LeagueFactory.Serie("open", 7, 10), new LeagueFactory.Serie("premiere_ds2", 14, 8)), "1234");

        assertEquals("20261101,default,2,open,7,10,premiere_ds2,14,8", prepared.parameters()); //test1
        assertEquals(NewConstructedLeagueData.class.getName(), prepared.leagueClass());
        assertEquals("1234", prepared.code());
        assertEquals(2, prepared.series().size()); //test2
        assertEquals(20261101, prepared.series().get(0).getStart());
        assertEquals(20261107, prepared.series().get(0).getEnd());
        assertEquals(20261108, prepared.series().get(1).getStart());
        assertEquals(20261121, prepared.series().get(1).getEnd());
        assertEquals("open", prepared.series().get(0).getFormat());
        assertEquals(20261101, prepared.start());
        assertEquals(20261123, prepared.displayEnd()); //test3
        assertNull(prepared.lockedDeckType());
    }

    @Test
    public void SealedAndDraftParametersEndWithCodeAndName() throws Exception {
        //test1: sealed: product,start,duration,matches,code,name (code and name become the private collection)
        //test2: the sealed product fixes the number of series (Reflections Sealed has more than one)
        //test3: a draft league renders the same way with the draft code
        LeagueFactory.PreparedLeague sealed = _factory.prepare(sealed("Reflections Sealed - Nov", 20261101, "reflections_sealed"), "99");
        assertEquals("reflections_sealed,20261101,7,10,99,Reflections Sealed - Nov", sealed.parameters()); //test1
        assertEquals(NewSealedLeagueData.class.getName(), sealed.leagueClass());
        assertTrue(sealed.series().size() > 1); //test2
        assertEquals(20261101, sealed.series().get(0).getStart());
        assertEquals(20261107, sealed.series().get(0).getEnd());
        assertEquals(20261108, sealed.series().get(1).getStart());
        assertTrue(sealed.series().get(0).isLimited());

        LeagueFactory.Definition draft = sealed("Cube Draft", 20261101, "c7obj_draft");
        draft.kind = LeagueFactory.Kind.SOLODRAFT;
        LeagueFactory.PreparedLeague prepared = _factory.prepare(draft, "77");
        assertEquals("c7obj_draft,20261101,7,10,77,Cube Draft", prepared.parameters()); //test3
        assertEquals(NewSoloDraftLeagueData.class.getName(), prepared.leagueClass());
        assertEquals(1, prepared.series().size());
        assertEquals("cube", prepared.series().get(0).getFormat());
    }

    @Test
    public void OnlyTheFirstSerieCountsForSealedAndDraft() throws Exception {
        LeagueFactory.Definition d = sealed("Two", 20261101, "reflections_sealed");
        d.series = List.of(new LeagueFactory.Serie("reflections_sealed", 7, 10), new LeagueFactory.Serie("nonsense", 0, 0));
        assertEquals("reflections_sealed,20261101,7,10,c,Two", _factory.prepare(d, "c").parameters());
    }

    @Test
    public void RegistrationInfoLinkIsWrappedAsTheHandlerDid() {
        //test1: a bare starwarsccg.org address becomes a link (stored form)
        //test2: anything else, and an already wrapped one, is kept
        assertEquals("<a href='https://www.starwarsccg.org/x' target='_blank'>https://www.starwarsccg.org/x</a>",
                LeagueFactory.storedRegistrationInfo("https://www.starwarsccg.org/x")); //test1
        assertEquals("ask in Discord", LeagueFactory.storedRegistrationInfo("ask in Discord")); //test2
        assertEquals("www.starwarsccg.org has spaces", LeagueFactory.storedRegistrationInfo("www.starwarsccg.org has spaces")); //test2
        assertEquals("", LeagueFactory.storedRegistrationInfo(null));
        String wrapped = "<a href='https://www.starwarsccg.org/x' target='_blank'>https://www.starwarsccg.org/x</a>";
        assertEquals(wrapped, LeagueFactory.storedRegistrationInfo(wrapped)); //test2
    }

    // --- validation ---------------------------------------------------------------------------------------------

    @Test
    public void DefinitionRules() {
        //test1: a blank or too long name, a bad day, an empty rotation, a zero-day serie, an unknown format
        //test2: the message names the field
        LeagueFactory.Definition ok = constructed("Open", 20261101, new LeagueFactory.Serie("open", 7, 10));
        assertNull(message(_factory, ok));

        LeagueFactory.Definition blank = constructed("  ", 20261101, new LeagueFactory.Serie("open", 7, 10));
        assertTrue(message(_factory, blank).startsWith("name:")); //test1 //test2
        LeagueFactory.Definition longName = constructed("x".repeat(46), 20261101, new LeagueFactory.Serie("open", 7, 10));
        assertTrue(message(_factory, longName).contains("45")); //test1
        LeagueFactory.Definition badDay = constructed("Open", 20261301, new LeagueFactory.Serie("open", 7, 10));
        assertTrue(message(_factory, badDay).startsWith("start:")); //test1
        LeagueFactory.Definition noSeries = constructed("Open", 20261101);
        assertTrue(message(_factory, noSeries).startsWith("series:")); //test1
        LeagueFactory.Definition zeroDays = constructed("Open", 20261101, new LeagueFactory.Serie("open", 0, 10));
        assertTrue(message(_factory, zeroDays).contains("series[0].duration")); //test1 //test2
        LeagueFactory.Definition noMatches = constructed("Open", 20261101, new LeagueFactory.Serie("open", 7, 0));
        assertTrue(message(_factory, noMatches).contains("series[0].matches"));
        LeagueFactory.Definition unknown = constructed("Open", 20261101, new LeagueFactory.Serie("open", 7, 10), new LeagueFactory.Serie("nope", 7, 10));
        assertTrue(message(_factory, unknown).contains("series[1].format: Unknown format 'nope'")); //test1 //test2
        LeagueFactory.Definition badCollection = constructed("Open", 20261101, new LeagueFactory.Serie("open", 7, 10));
        badCollection.collectionType = "wat";
        assertTrue(message(_factory, badCollection).startsWith("collectionType:"));
        LeagueFactory.Definition badLock = constructed("Open", 20261101, new LeagueFactory.Serie("open", 7, 10));
        badLock.lockedDeckType = "forever";
        assertTrue(message(_factory, badLock).startsWith("lockedDeckType:"));
        LeagueFactory.Definition badTimeout = constructed("Open", 20261101, new LeagueFactory.Serie("open", 7, 10));
        badTimeout.decisionTimeoutSeconds = 0;
        assertTrue(message(_factory, badTimeout).startsWith("decisionTimeoutSeconds:"));
    }

    @Test
    public void SealedAndDraftRulesNameTheProduct() {
        //test1: an unknown sealed product / draft is refused before the LeagueData class can blow up on it
        //test2: a comma in a sealed name would cut the parameters string, so it is refused; a constructed name may have one
        assertTrue(message(_factory, sealed("S", 20261101, "hoth_sealed")).contains("Unknown sealed league type 'hoth_sealed'")); //test1
        LeagueFactory.Definition draft = sealed("D", 20261101, "no_such_draft");
        draft.kind = LeagueFactory.Kind.SOLODRAFT;
        assertTrue(message(_factory, draft).contains("Unknown draft 'no_such_draft'")); //test1
        assertTrue(message(_factory, sealed("Sealed, Nov", 20261101, "reflections_sealed")).contains("comma")); //test2
        assertNull(message(_factory, constructed("Open, Nov", 20261101, new LeagueFactory.Serie("open", 7, 10)))); //test2
    }

    @Test
    public void ADefinitionTooLongForTheParametersColumnIsRefused() {
        LeagueFactory.Serie[] many = new LeagueFactory.Serie[20];
        for (int i = 0; i < many.length; i++)
            many[i] = new LeagueFactory.Serie("premiere_ds2", 7, 10);
        String message = message(_factory, constructed("Long", 20261101, many));
        assertTrue(message, message.contains("too long"));
    }

    @Test
    public void LockedDecksAreOnlyForConstructedLeagues() throws Exception {
        LeagueFactory.Definition d = constructed("Open", 20261101, new LeagueFactory.Serie("open", 7, 10));
        d.lockedDeckType = "when_first_played";
        assertEquals(LockedDeckType.WHEN_FIRST_PLAYED, _factory.prepare(d, "c").lockedDeckType());
        LeagueFactory.Definition s = sealed("S", 20261101, "reflections_sealed");
        s.lockedDeckType = "when_first_played";
        assertNull(_factory.prepare(s, "c").lockedDeckType());
    }

    // --- create --------------------------------------------------------------------------------------------------

    @Test
    public void CreateStoresThroughTheLeagueDaoAndClearsTheCache() throws Exception {
        LeagueFactory.Definition d = constructed("Open League", 20261101, new LeagueFactory.Serie("open", 7, 10));
        d.registrationInfo = "https://www.starwarsccg.org/x";
        d.invitationOnly = true;
        d.allowSpectators = false;
        LeagueFactory.PreparedLeague prepared = _factory.prepare(d, "555");

        String code = _factory.create(prepared, 7);

        assertEquals("555", code);
        Mockito.verify(_leagueDao).addLeague(500, "Open League", "555", NewConstructedLeagueData.class.getName(),
                "20261101,default,1,open,7,10", 20261101, 20261109, false, false, false, true,
                "<a href='https://www.starwarsccg.org/x' target='_blank'>https://www.starwarsccg.org/x</a>", 300, 50, null, 7);
        Mockito.verify(_leagueService).clearCache();
    }

    @Test
    public void KindParsesLooselyAndIsDayChecksCalendarDays() {
        assertEquals(LeagueFactory.Kind.SEALED, LeagueFactory.Kind.parse("sealed"));
        assertEquals(LeagueFactory.Kind.SOLODRAFT, LeagueFactory.Kind.parse(" SOLODRAFT "));
        assertNull(LeagueFactory.Kind.parse("RTMD"));
        assertNull(LeagueFactory.Kind.parse(null));
        assertTrue(LeagueFactory.isDay(20261101));
        assertTrue(LeagueFactory.isDay(20280229));
        assertFalse(LeagueFactory.isDay(20270229));
        assertFalse(LeagueFactory.isDay(2026110));
        assertFalse(LeagueFactory.isDay(0));
    }

    @Test
    public void TheRealLeagueDataClassIsWhatTheHallWillLoad() throws Exception {
        // the class name stored is the one League.getLeagueData builds by reflection
        LeagueFactory.PreparedLeague prepared = _factory.prepare(constructed("Open", 20261101, new LeagueFactory.Serie("open", 7, 10)), "c");
        try {
            Class.forName(prepared.leagueClass()).getConstructor(SwccgCardBlueprintLibrary.class, SoloDraftDefinitions.class, String.class);
        } catch (Exception exp) {
            fail("League class is not loadable: " + exp);
        }
    }
}
