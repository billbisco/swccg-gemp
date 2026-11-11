package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.resolveUntil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Kessel Surveillance System (Jun 2011 slip: "Spice Mine Administrator"), a version of 1273_10. */
public class Card_2273_010_Tests {
    private static final String DEPLOY = "Deploy Spice Mine Administrator here";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("surveillance", "2273_10");
            put("current", "1273_10");
            put("office", "1273_18");
            put("doole", "1273_9");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void PcE1KesselSurveillanceSystemIsTheCurrentCardWithTheJuly2011ErrataText() {
        // Legacy batch B41: the forum errata of 2011-07-14 (p636351), which the re-made 7.3 sheet prints, is PC-E1.
        var s = scenario();
        assertVersion(s.GetDSCard("surveillance").getBlueprint(), s.GetDSCard("current").getBlueprint(), "1273_10", "PC-E1",
                LocalDate.of(2011, 7, 14), LocalDate.of(2012, 7, 21),
                "Deploy on a Kessel site. Opponent's Force drains are -1 here. Once per turn, if you are about to lose Force from Reserve Deck, may place a 'spice' card in owner's Lost Pile to make that Force satisfy 1 additional Force loss. Once per game, may deploy Spice Mine Administrator here from Reserve Deck; reshuffle.");
    }

    @Test
    public void OriginalKesselSurveillanceSystemIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario();
                var onTable = s.GetDSCard(currentOnTable ? "current" : "surveillance");
                var inHand = s.GetDSCard(currentOnTable ? "surveillance" : "current");
                s.StartGame();
                MissionTestSupport.ensurePlanetLayout(s, "Kessel", 8);
                var office = s.GetDSCard("office");
                s.MoveLocationToTable(office);
                if (otherOut)
                    s.AttachCardsTo(office, onTable);
                s.MoveCardsToDSHand(inHand);
                s.SkipToDSTurn(Phase.DEPLOY);
                s.EnsureDSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other out=" + otherOut, !otherOut, s.DSDeployAvailable(inHand));
            }
        }
    }

    @Test
    public void OriginalKesselSurveillanceSystemDeploysSpiceMineAdministratorHereOncePerGame() {
        var s = scenario();
        s.StartGame();
        MissionTestSupport.ensurePlanetLayout(s, "Kessel", 8);
        var office = s.GetDSCard("office");
        var surveillance = s.GetDSCard("surveillance");
        var doole = s.GetDSCard("doole");
        s.MoveLocationToTable(office);
        s.AttachCardsTo(office, surveillance);
        s.MoveCardsToLocation(office, s.GetLSCard("luke"));
        assertEquals(1, s.game().getModifiersQuerying().getForceDrainAmount(s.gameState(), office, s.LS), s.epsilon);
        s.SkipToDSTurn(Phase.DEPLOY);
        s.MoveCardsToTopOfDSReserveDeck(doole);
        // Spice Mine Administrator is Moruth Doole, Kessel Administrator's printed title
        assertTrue(s.DSCardActionAvailable(surveillance, DEPLOY));
        s.DSUseCardAction(surveillance, DEPLOY);
        resolveUntil(s, () -> doole.getZone() == Zone.AT_LOCATION);
        assertEquals(office, doole.getAtLocation());
        s.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(s.DSCardActionAvailable(surveillance, DEPLOY));
    }
}
