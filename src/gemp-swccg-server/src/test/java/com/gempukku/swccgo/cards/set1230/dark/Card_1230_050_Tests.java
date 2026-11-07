package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import static org.junit.Assert.fail;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_050_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("dungeon", "1230_50");
                    put("tatooine", "1_289");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void DungeonStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("dungeon").getBlueprint();
        assertEquals("Dungeon (Prison)", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.LOCATION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.PRISON); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DungeonDeploysToTatooine() {
        var scn = GetScenario();
        var dungeon = scn.GetDSCard("dungeon");
        var tatooine = scn.GetDSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToDSHand(dungeon);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(dungeon));
        scn.DSPlayCard(dungeon);
        if (scn.DSHasCardChoiceAvailable(tatooine)) {
            scn.DSChooseCard(tatooine);
        }
        if (scn.DSDecisionAvailable("On which side")) {
            scn.DSChoose("Left");
        }
        scn.PassAllResponses();
        assertTrue(dungeon.getZone() == Zone.AT_LOCATION || dungeon.getZone() == Zone.LOCATIONS);
    }
    private static final String RETRIEVE = "Retrieve 2 Force";

    private VirtualTableScenario DeliveryStarted() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("r2", "2_14", "luke", "1_19")),
                new HashMap<>(Map.of("prison", "1230_50", "trooper", "1_194")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("prison"));
        return scn;
    }

    private VirtualTableScenario EscortToPrison(String captiveName) {
        var scn = DeliveryStarted();
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(scn.GetDSCard("prison"), trooper);
        scn.CaptureCardWith(trooper, scn.GetLSCard(captiveName));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1), scn.GetDSFiller(2), scn.GetDSFiller(3), scn.GetDSFiller(4));
        scn.SkipToDSTurn(Phase.MOVE);
        assertTrue(scn.DSActionAvailable("Deliver captive to prison"));
        scn.DSChooseAction("Deliver captive to prison");
        return scn;
    }

    /** Passes responses until the Dungeon retrieve is offered (true) or the delivery has resolved (false). */
    private boolean ReachReward(VirtualTableScenario scn) {
        var prison = scn.GetDSCard("prison");
        for (int step = 0; step < 14; step++) {
            if (scn.DSDecisionAvailable("optional response") && scn.DSCardActionAvailable(prison, RETRIEVE)) return true;
            if (scn.LSDecisionAvailable("optional response")) scn.LSPass();
            else if (scn.DSDecisionAvailable("optional response")) scn.DSPass();
            else if (scn.AwaitingDSMovePhaseActions() || scn.AwaitingLSMovePhaseActions()) return false;
            else fail("Unexpected delivery decision: " + scn.GetCurrentDecision().getText());
        }
        fail("Delivery never resolved");
        return false;
    }

    @Test
    public void ImprisoningACaptiveWithoutABountyHereMayRetrieveTwoForce() {
        var scn = EscortToPrison("luke");
        assertTrue(ReachReward(scn));
        int lost = scn.GetDSLostPileCount();
        scn.DSUseCardAction(scn.GetDSCard("prison"), RETRIEVE);
        scn.PassAllResponses();
        assertEquals(lost - 2, scn.GetDSLostPileCount());
        assertTrue(scn.GetLSCard("luke").isImprisoned());
        assertEquals(scn.GetDSCard("prison"), scn.GetLSCard("luke").getAttachedTo());
    }
}
