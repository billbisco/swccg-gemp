package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_032_Tests {
    private static final String UPLOAD = "Take All Wrapped Up into hand from Reserve Deck";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("goo", "7_62", "luke", "1_19")),
                new HashMap<>(Map.of("hutt", "1261_32", "rancor", "6_166", "carkoon", "6_170",
                        "nal", "6_168", "objective", "110_6", "zuckuss", "4_107", "creature", "6_139", "vader", "1_168",
                        "wrapped", "6_141")),
                23, 23, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedEffectIdentityAndAlterImmunity() {
        var scn = scenario();
        var bp = scn.GetDSCard("hutt").getBlueprint();
        assertEquals("Hutt Influence", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(4, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(PlayCardZoneOption.ATTACHED, bp.getSinglePlayCardZoneOption());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.PREMIUM, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertTrue(bp.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void bothRealPitsAreLegalDeploymentTargetsAndANormalSiteIsNot() {
        for (String destination : List.of("rancor", "carkoon")) {
            var scn = scenario();
            var hutt = scn.GetDSCard("hutt");
            start(scn);
            scn.MoveCardsToDSHand(hutt);
            scn.SkipToDSTurn(Phase.DEPLOY);
            scn.EnsureDSForcePile(6);
            scn.DSDeployCard(hutt);
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
            scn.DSChooseCard(scn.GetDSCard(destination));
            scn.PassAllResponses();
            assertSame(scn.GetDSCard(destination), hutt.getAttachedTo());
        }
    }

    @Test
    public void onlyTheNamedFaceEnablesBountyHunterForfeitAndRealGooNeeTayImmunity() {
        for (boolean flipped : List.of(false, true)) {
            var scn = scenario();
            start(scn);
            var hutt = scn.GetDSCard("hutt");
            var objective = scn.GetDSCard("objective");
            var zuckuss = scn.GetDSCard("zuckuss");
            var vader = scn.GetDSCard("vader");
            scn.AttachCardsTo(scn.GetDSCard("rancor"), hutt);
            scn.MoveCardsToDSSideOfTable(scn.GetLSCard("goo"), objective);
            if (flipped) {
                scn.MoveCardsToLocation(scn.GetDSCard("rancor"), scn.GetDSCard("creature"), scn.GetLSCard("luke"));
                scn.gameState().flipCard(scn.game(), objective, true);
            }
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), zuckuss, vader);
            assertEquals(flipped, objective.isFlipped());
            assertEquals(zuckuss.getBlueprint().getForfeit() + (flipped ? 2 : 0), scn.GetForfeit(zuckuss), scn.epsilon);
            assertEquals(vader.getBlueprint().getForfeit(), scn.GetForfeit(vader), scn.epsilon);
            scn.MoveCardsToDSHand(zuckuss, vader);
            assertEquals(zuckuss.getBlueprint().getDeployCost() + (flipped ? 0 : 2), costAtSite(scn, zuckuss), scn.epsilon);
            assertEquals(vader.getBlueprint().getDeployCost() + 2, costAtSite(scn, vader), scn.epsilon);
        }
    }

    @Test
    public void nalHuttaEnablesARealReserveUploadExactlyOncePerGame() {
        for (boolean nalOnTable : List.of(false, true)) {
            var scn = scenario();
            var hutt = scn.GetDSCard("hutt");
            var wrapped = scn.GetDSCard("wrapped");
            start(scn);
            scn.AttachCardsTo(scn.GetDSCard("rancor"), hutt);
            if (nalOnTable)
                scn.MoveLocationToTable(scn.GetDSCard("nal"));
            scn.SkipToDSTurn(Phase.CONTROL);
            scn.MoveCardsToTopOfDSReserveDeck(wrapped);
            assertEquals(nalOnTable, scn.DSCardActionAvailable(hutt, UPLOAD));
            if (!nalOnTable)
                continue;
            scn.DSUseCardAction(hutt, UPLOAD);
            if (scn.DSHasCardChoiceAvailable(wrapped))
                scn.DSChooseCard(wrapped);
            scn.PassAllResponses();
            assertTrue(scn.GetDSHand().contains(wrapped));
            scn.MoveCardsToTopOfDSReserveDeck(wrapped);
            if (scn.AwaitingLSControlPhaseActions())
                scn.LSPass();
            assertFalse(scn.DSCardActionAvailable(hutt, UPLOAD));
        }
    }

    private void start(VirtualTableScenario scn) {
        scn.MoveCardsToDSHand(scn.GetDSCard("objective"));
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("rancor"));
        scn.MoveLocationToTable(scn.GetDSCard("carkoon"));
    }

    private float costAtSite(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card,
                scn.GetDSStartingLocation(), false, null, false, 0, null, false);
    }
}
