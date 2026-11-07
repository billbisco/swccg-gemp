package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_044_Tests {
    private static final String TRANSPORT = "Transport' characters";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("rebel", "1_19")),
                new HashMap<>(Map.of("lana", "1261_44", "vader", "1_168", "trooper", "1_194",
                        "elsewhere", "1_177", "battleground", "5_166", "interior", "5_168", "exterior", "7_277")),
                25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedUsedComboIdentity() {
        var scn = scenario();
        var bp = scn.GetDSCard("lana").getBlueprint();
        assertEquals("Lana Dobreed & Sacrifice", bp.getTitle());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(4, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertFalse(bp.hasVirtualSuffix());
        assertTrue(bp.getTitles().containsAll(List.of(Title.Lana_Dobreed, Title.Sacrifice)));
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.EPISODE_I,
                Icon.CORUSCANT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test(timeout = 10000)
    public void mayPayOneDestinyAmountToMoveTwoFromOneSiteToInteriorBattlegroundOrExteriorNonBattleground() {
        for (String destination : List.of("battleground", "exterior")) {
            var scn = scenario();
            setup(scn, Phase.MOVE);
            scn.EnsureDSForcePile(7);
            scn.PrepareDSDestiny(3);
            int before = scn.GetDSForcePileCount();
            select(scn, destination, true);
            scn.PassAllResponses();
            scn.DSChooseYes();
            scn.PassAllResponses();
            assertEquals(before - 3, scn.GetDSForcePileCount());
            assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("lana")));
            assertSame(scn.GetDSCard(destination), location(scn, scn.GetDSCard("vader")));
            assertSame(scn.GetDSCard(destination), location(scn, scn.GetDSCard("trooper")));
            assertSame(scn.GetLSStartingLocation(), location(scn, scn.GetDSCard("elsewhere")));
            assertSame(scn.GetDSStartingLocation(), location(scn, scn.GetLSCard("rebel")));
        }
    }

    @Test(timeout = 10000)
    public void canDeclineAnAffordableTransportOrFailAnUnaffordableOneAndLoseOnlyTheInterrupt() {
        for (boolean affordable : List.of(true, false)) {
            var scn = scenario();
            setup(scn, Phase.MOVE);
            scn.EnsureDSForcePile(5);
            if (!affordable)
                scn.DrainDSForcePileTo(1);
            scn.PrepareDSDestiny(3);
            int before = scn.GetDSForcePileCount();
            select(scn, "battleground", false);
            scn.PassAllResponses();
            if (affordable)
                scn.DSChooseNo();
            scn.PassAllResponses();
            assertEquals(before, scn.GetDSForcePileCount());
            assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("lana")));
            assertSame(scn.GetDSStartingLocation(), location(scn, scn.GetDSCard("vader")));
            assertSame(scn.GetDSStartingLocation(), location(scn, scn.GetDSCard("trooper")));
        }
    }

    @Test
    public void transportIsNotOfferedInDeployPhase() {
        var scn = scenario();
        setup(scn, Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("lana"), TRANSPORT));
    }

    private void setup(VirtualTableScenario scn, Phase phase) {
        scn.StartGame();
        for (String key : List.of("battleground", "interior", "exterior"))
            scn.MoveLocationToTable(scn.GetDSCard(key));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),
                scn.GetDSCard("vader"), scn.GetDSCard("trooper"), scn.GetLSCard("rebel"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("elsewhere"));
        scn.MoveCardsToDSHand(scn.GetDSCard("lana"));
        scn.SkipToDSTurn(phase);
    }

    private void select(VirtualTableScenario scn, String destination, boolean both) {
        scn.DSUseCardAction(scn.GetDSCard("lana"), TRANSPORT);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("interior")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        scn.DSChooseCard(scn.GetDSCard(destination));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("rebel")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("elsewhere")));
        if (both)
            scn.DSChooseCards(scn.GetDSCard("vader"), scn.GetDSCard("trooper"));
        else
            scn.DSChooseCard(scn.GetDSCard("vader"));
    }

    private PhysicalCard location(VirtualTableScenario scn, PhysicalCard card) {
        return scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), card);
    }
}
