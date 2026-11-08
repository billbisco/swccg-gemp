package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original In The Hands Of The ISB (Jun 2010 slip: Critical Mission, "with Ice-Heart"), a version of 1271_27. */
public class Card_2271_027_Tests {
    private static final String RELOCATE = "Relocate ISB agent to a site you control";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("isb", "2271_27");
            put("current", "1271_27");
            put("iceHeart", "601_189");
            put("otherIsard", "200_92");
            put("storm2", "1_179");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalInTheHandsOfTheISBIsTheCurrentCardWithTheJune2010Text() {
        var s = scenario();
        assertVersion(s.GetDSCard("isb").getBlueprint(), s.GetDSCard("current").getBlueprint(), "1271_27", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2013, 4, 27),
                "Deploy on ISB Central Office. This site is a prison (if opponent controls, all captives here are released). Total ability of 6 or more required for opponent to draw battle destiny here. Whenever an ISB agent delivers a captive here, opponent loses 4 Force (once per captive per captivity). ISB agents may escort captives and, if escorting a captive during your move phase, may use 1 Force to relocate escort and captive to a related docking bay (as a regular move). Once during your move phase, may use 2 Force to relocate (as a regular move) an ISB agent present with Ice-Heart here to a site you control.",
                CardSubtype.CRITICAL);
        // printed as a Critical Mission; AR 2.0 Appendix A errata: the current card is a normal Mission
        assertEquals(CardSubtype.NORMAL, s.GetDSCard("current").getBlueprint().getCardSubtype());
    }

    @Test
    public void OriginalInTheHandsOfTheISBIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario();
                var onTable = s.GetDSCard(currentOnTable ? "current" : "isb");
                var inHand = s.GetDSCard(currentOnTable ? "isb" : "current");
                s.StartGame();
                var office = MissionTestSupport.putLocationOnTable(s, 9201, s.DS, MissionTestSupport.isbCentralOffice());
                if (otherOut)
                    s.AttachCardsTo(office, onTable);
                s.MoveCardsToDSHand(inHand);
                s.SkipToDSTurn(Phase.DEPLOY);
                s.EnsureDSForcePile(10);
                assertEquals("current on table=" + currentOnTable + ", other out=" + otherOut, !otherOut, s.DSCardPlayAvailable(inHand));
            }
        }
    }

    private boolean relocateOffered(String isardKey) {
        var s = scenario();
        var isb = s.GetDSCard("isb");
        s.StartGame();
        var office = MissionTestSupport.putLocationOnTable(s, 9201, s.DS, MissionTestSupport.isbCentralOffice());
        s.AttachCardsTo(office, isb);
        var agent = MissionTestSupport.putInHand(s, 9202, s.DS, MissionTestSupport.isbAgent());
        s.MoveCardsToLocation(office, agent, s.GetDSCard(isardKey));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("storm2"));
        assertTrue(s.HasKeyword(agent, Keyword.ISB_AGENT));
        s.SkipToDSTurn(Phase.MOVE);
        s.DSActivateForceCheat(3);
        boolean offered = s.DSActionAvailable(RELOCATE);
        if (offered) {
            s.DSUseCardAction(isb, RELOCATE);
            s.DSChooseCard(agent);
            s.DSChooseCard(s.GetDSStartingLocation());
            s.PassAllResponses();
            assertAtLocation(s.GetDSStartingLocation(), agent);
        }
        return offered;
    }

    @Test
    public void OriginalInTheHandsOfTheISBRelocatesAnAgentPresentWithIceHeart() {
        // Ice-Heart is the printed title of the [Legacy Block 7] Ysanne Isard (601_189)
        assertTrue(relocateOffered("iceHeart"));
    }

    @Test
    public void OriginalInTheHandsOfTheISBDoesNotCountAnotherYsanneIsardAsIceHeart() {
        assertFalse(relocateOffered("otherIsard"));
    }
}
