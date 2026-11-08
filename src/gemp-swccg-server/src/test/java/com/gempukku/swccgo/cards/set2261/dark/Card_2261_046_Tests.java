package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Pre-errata One Beautiful Thing (Sep 2009 sheet) and its PC-E1 (2013 Master List) text, versions of 1261_46. */
public class Card_2261_046_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()),
                new HashMap<>(Map.of("original", "2261_46", "e1", "2261_1046", "current", "1261_46", "blackleader", "1117_12")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OneBeautifulThingOriginalIdentity() {
        var scn = scenario();
        var bp = scn.GetDSCard("original").getBlueprint();
        assertLegacyVersion(bp, scn.GetDSCard("current").getBlueprint(), "1261_46", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2013, 4, 27),
                "Lose 1 Force to ▲ (or retrieve) Black Leader. OR During battle where you have no Dark Jedi or droids and only two cards with ability there (a male and a female), add one destiny to total power (and attrition against you = 0).");
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
    }

    @Test
    public void OneBeautifulThingPCE1Identity() {
        var scn = scenario();
        var bp = scn.GetDSCard("e1").getBlueprint();
        assertLegacyVersion(bp, scn.GetDSCard("current").getBlueprint(), "1261_46", "PC-E1",
                LocalDate.of(2013, 4, 27), null,
                "Lose 1 Force to ▲ (or retrieve) Juno Eclipse. OR During battle where you have no Dark Jedi or droids and only two cards with ability there (a male and a female), add one destiny to total power (and attrition against you = 0).");
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
    }

    @Test
    public void OneBeautifulThingOriginalTakesBlackLeaderIntoHandAndGoesToUsedPile() {
        var scn = scenario();
        var obt = scn.GetDSCard("original");
        var blackLeader = scn.GetDSCard("blackleader");
        scn.StartGame();
        scn.MoveCardsToDSHand(obt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToBottomOfDSReserveDeck(blackLeader);
        assertTrue(scn.DSCardPlayAvailable(obt, "Take Black Leader into hand"));
        scn.DSPlayCard(obt, "Take Black Leader into hand");
        for (int i = 0; i < 8 && blackLeader.getZone() != Zone.HAND; i++) {
            if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(blackLeader)) scn.DSChooseCard(blackLeader);
            else if (scn.DSAnyDecisionsAvailable() && scn.DSDecisionAvailable("Force to lose"))
                scn.DSChoose(new String[]{scn.DSGetCardChoices().getFirst()});
            else if (scn.DSAnyDecisionsAvailable() && !scn.DSGetCardChoices().isEmpty())
                fail(scn.DSGetDecision().getText() + " " + scn.DSGetCardChoices());
            else scn.PassAllResponses();
        }
        assertInZone(Zone.HAND, blackLeader);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, obt);
    }
}
