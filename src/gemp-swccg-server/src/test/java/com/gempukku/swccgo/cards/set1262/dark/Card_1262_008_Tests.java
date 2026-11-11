package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsToAttritionOnlyModifier;
import com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsToPowerOnlyModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_008_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("leia", "1_17"); put("approach", "5_42"); }},
                new HashMap<>() {{ put("drop", "1262_8"); put("p60", "12_115"); put("droideka", "200_80"); put("trooper", "1_194");
                    put("vader", "1_168"); put("card", "1_194"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void DropVStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("drop").getBlueprint();
        assertEquals("Drop!", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.EPISODE_I); add(Icon.CORUSCANT); add(Icon.LEGACY_BLOCK_6); }});
        assertTrue(bp.getGameText().endsWith("(Immune to Alter.)"));
    }

    private VirtualTableScenario battle(boolean drop) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        if (drop) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("drop"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("p60"), scn.GetDSCard("droideka"), scn.GetDSCard("vader"), scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        return scn;
    }

    @Test
    public void DropVPermanentWeaponCardsDoNotAddBattleDestinyDraws() {
        // Vader draws one; P-60 and Droideka (both [Permanent weapon] cards) each add one.
        var without = battle(false);
        assertEquals(3, without.GetDSBattleDestinyCount());
        var scn = battle(true);
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    private int powerDraws(VirtualTableScenario scn, String player) {
        return scn.game().getModifiersQuerying().getNumDestinyDrawsToTotalPowerOnly(scn.gameState(), player, false, false);
    }

    private int attritionDraws(VirtualTableScenario scn, String player) {
        return scn.game().getModifiersQuerying().getNumDestinyDrawsToAttritionOnly(scn.gameState(), player, false, false);
    }

    private void addDestinies(VirtualTableScenario scn) {
        var source = scn.GetDSCard("vader");
        for (String player : new String[]{scn.DS, scn.LS}) {
            scn.ApplyAdHocModifier(new NumDestinyDrawsToPowerOnlyModifier(source, null, 3, player));
            scn.ApplyAdHocModifier(new NumDestinyDrawsToAttritionOnlyModifier(source, null, 2, player));
        }
    }

    private Integer maxPower(VirtualTableScenario scn, String player) {
        return scn.game().getModifiersQuerying().getMaxDestinyAddedToPower(scn.gameState(), player);
    }

    private Integer maxAttrition(VirtualTableScenario scn, String player) {
        return scn.game().getModifiersQuerying().getMaxDestinyAddedToAttrition(scn.gameState(), player);
    }

    @Test
    public void DropVNeitherPlayerMayAddMoreThanTwoDestinyToPower() {
        var without = battle(false);
        addDestinies(without);
        assertNull(maxPower(without, without.DS));
        assertEquals(Integer.MAX_VALUE, without.game().getModifiersQuerying().getNumDestinyDrawsToTotalPowerOnly(without.gameState(), without.DS, true, false));
        var scn = battle(true);
        addDestinies(scn);
        assertEquals(Integer.valueOf(2), maxPower(scn, scn.DS));
        assertEquals(Integer.valueOf(2), maxPower(scn, scn.LS));
        assertEquals(2, scn.game().getModifiersQuerying().getNumDestinyDrawsToTotalPowerOnly(scn.gameState(), scn.DS, true, false));
    }

    @Test
    public void DropVNeitherPlayerMayAddMoreThanOneDestinyToAttrition() {
        var without = battle(false);
        addDestinies(without);
        assertNull(maxAttrition(without, without.LS));
        var scn = battle(true);
        addDestinies(scn);
        assertEquals(Integer.valueOf(1), maxAttrition(scn, scn.DS));
        assertEquals(Integer.valueOf(1), maxAttrition(scn, scn.LS));
        assertEquals(1, scn.game().getModifiersQuerying().getNumDestinyDrawsToAttritionOnly(scn.gameState(), scn.LS, true, false));
    }

    @Test
    public void DropVDoesNotReduceDestiniesBelowTheLimit() {
        var scn = battle(true);
        var source = scn.GetDSCard("vader");
        scn.ApplyAdHocModifier(new NumDestinyDrawsToPowerOnlyModifier(source, null, 1, scn.DS));
        assertEquals(1, powerDraws(scn, scn.DS));
        assertEquals(0, attritionDraws(scn, scn.DS));
    }

    @Test
    public void DropVAddDestinyActionsBecomeUnavailableAtTheCap() {
        var scn = battle(true);
        var source = scn.GetDSCard("vader");
        assertTrue(com.gempukku.swccgo.cards.GameConditions.canAddDestinyDrawsToPower(scn.game(), scn.DS));
        scn.ApplyAdHocModifier(new NumDestinyDrawsToPowerOnlyModifier(source, null, 2, scn.DS));
        assertFalse(com.gempukku.swccgo.cards.GameConditions.canAddDestinyDrawsToPower(scn.game(), scn.DS));
        assertTrue(com.gempukku.swccgo.cards.GameConditions.canAddDestinyDrawsToPower(scn.game(), scn.LS));
        assertTrue(com.gempukku.swccgo.cards.GameConditions.canAddDestinyDrawsToAttrition(scn.game(), scn.LS));
        scn.ApplyAdHocModifier(new NumDestinyDrawsToAttritionOnlyModifier(source, null, 1, scn.LS));
        assertFalse(com.gempukku.swccgo.cards.GameConditions.canAddDestinyDrawsToAttrition(scn.game(), scn.LS));
    }

    @Test
    public void DropVPlacesCardFromHandInUsedPileOncePerTurn() {
        var scn = scenario();
        var drop = scn.GetDSCard("drop");
        var card = scn.GetDSCard("card");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(drop);
        scn.MoveCardsToDSHand(card, scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        int used = scn.GetDSUsedPileCount();
        assertTrue(scn.DSCardActionAvailable(drop, "Place card from hand in Used Pile"));
        scn.DSUseCardAction(drop, "Place card from hand in Used Pile");
        scn.DSChooseCard(card);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, card);
        assertEquals(used + 1, scn.GetDSUsedPileCount());
        if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(drop, "Place card from hand in Used Pile"));
    }

    /**
     * Light Side responds to the battle with an actual Interrupt that adds one destiny to total power (Dark Approach),
     * then two more destiny to power are added; returns how many destiny Light Side draws to total power.
     */
    private int lightSidePowerDestiniesWithInterrupt(boolean drop) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        var approach = scn.GetLSCard("approach");
        scn.StartGame();
        if (drop) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("drop"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("p60"), scn.GetDSCard("droideka"), scn.GetDSCard("vader"), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(approach);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.EnsureLSForcePile(6);
        scn.DSInitiateBattle(site);
        boolean played = false;
        for (int i = 0; i < 10 && !played; i++) {
            boolean available;
            try {
                available = scn.LSGetDecision() != null && scn.LSCardPlayAvailable(approach, "Add one destiny");
            } catch (IllegalStateException notACardActionDecision) {
                available = false;
            }
            if (available) {
                scn.LSPlayCard(approach, "Add one destiny");
                scn.PassAllResponses();
                played = true;
            } else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        assertTrue("Dark Approach was played", played);
        scn.ApplyAdHocModifier(new NumDestinyDrawsToPowerOnlyModifier(scn.GetDSCard("vader"), null, 2, scn.LS));
        return com.gempukku.swccgo.logic.modifiers.DestinyAddedCapTests.advanceToDamageSegment(scn, false)
                .getNumDestinyToTotalPowerDrawn(scn.LS);
    }

    /** "Neither player may add more than two destiny to power": an Interrupt's added destiny counts toward the cap. */
    @Test
    public void DropVCapCountsDestinyAddedByAnInterrupt() {
        assertEquals(3, lightSidePowerDestiniesWithInterrupt(false));
        assertEquals(2, lightSidePowerDestiniesWithInterrupt(true));
    }
}
