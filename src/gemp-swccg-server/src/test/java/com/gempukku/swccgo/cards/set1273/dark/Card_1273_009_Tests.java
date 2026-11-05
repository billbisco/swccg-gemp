package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("doole", "1273_9");
                    put("operations", "1273_19");
                    put("spice1", "1_194");
                    put("spice2", "1_194");
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
    public void MoruthDooleStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("doole").getBlueprint();
        assertEquals("Moruth Doole, Kessel Administrator", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MoruthDooleDeploysMinusOneToKessel() {
        var scn = GetScenario();
        var doole = scn.GetDSCard("doole");
        scn.StartGame();
        var mines = LegacyVb73DarkTestSupport.putKesselSiteOnTable(scn, 7309, "Kessel: Test Mines");
        var qs = scn.game().getModifiersQuerying();
        assertEquals(3, qs.getDeployCost(scn.gameState(), doole, doole, scn.GetDSStartingLocation(), false, null, false, 0, null, false), scn.epsilon);
        assertEquals(2, qs.getDeployCost(scn.gameState(), doole, doole, mines, false, null, false, 0, null, false), scn.epsilon);
    }

    private VirtualTableScenario dooleAtKesselWithSpice(int spice) {
        var scn = GetScenario();
        var doole = scn.GetDSCard("doole");
        var operations = scn.GetDSCard("operations");
        scn.StartGame();
        var mines = LegacyVb73DarkTestSupport.putKesselSiteOnTable(scn, 7309, "Kessel: Test Mines");
        scn.MoveCardsToDSSideOfTable(operations);
        if (spice > 0) {
            scn.StackCardsOn(operations, scn.GetDSCard("spice1"));
        }
        if (spice > 1) {
            scn.StackCardsOn(operations, scn.GetDSCard("spice2"));
        }
        scn.MoveCardsToLocation(mines, doole);
        return scn;
    }

    @Test
    public void MoruthDooleAddsOneToYourForceDrainAtRelatedLocation() {
        var scn = dooleAtKesselWithSpice(1);
        var mines = scn.GetDSCard("doole").getAtLocation();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt((com.gempukku.swccgo.game.PhysicalCardImpl) mines);
        LegacyVb73DarkTestSupport.passUntilAction(scn, scn.DS, "Add 1 to Force drain");
        scn.DSChooseAction("Add 1 to Force drain");
        scn.PassResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void MoruthDooleTakesOwnSpiceCardIntoHandForThreeForce() {
        var scn = dooleAtKesselWithSpice(1);
        var spice = scn.GetDSCard("spice1");
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("doole"), "Take a 'spice' card into hand"));
        scn.DSUseCardAction(scn.GetDSCard("doole"), "Take a 'spice' card into hand");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> spice.getZone() == Zone.HAND);
        assertInZone(Zone.HAND, spice);
        assertEquals(forceBefore - 3, scn.GetDSForcePileCount());
    }

    @Test
    public void MoruthDooleNeedsSpiceToBoostDrains() {
        var scn = dooleAtKesselWithSpice(0);
        var mines = scn.GetDSCard("doole").getAtLocation();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt((com.gempukku.swccgo.game.PhysicalCardImpl) mines);
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.AwaitingDSControlPhaseActions());
        assertEquals(1, scn.GetLSLostPileCount());
    }
}
