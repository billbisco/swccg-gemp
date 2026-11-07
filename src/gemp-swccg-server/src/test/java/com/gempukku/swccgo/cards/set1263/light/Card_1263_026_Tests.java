package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1263_026_Tests {
    private VirtualTableScenario fixture() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("phylo", "1263_26", "local", "1_28", "remote", "1_30",
                        "ship", "1_145", "system", "3_55", "obi", "1_21")),
                new HashMap<>(Map.of("vader", "1_168", "saber", "1_324", "beaten", "5_163", "otherDark", "1_173", "secondDark", "1_179", "blockSix", "1261_21")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void printedIdentityAndStats() {
        var scn = fixture(); var bp = scn.GetLSCard("phylo").getBlueprint();
        assertEquals("Phylo Gandish", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.LIGHT, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon); assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(1, bp.getPower(), scn.epsilon); assertEquals(4, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(Rarity.V, bp.getRarity()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.ALIEN)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.ALIEN, Icon.PILOT, Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.FEMALE)));
    }

    @Test public void addsTwoPowerOnlyToTheShipShePilots() {
        var scn = fixture(); scn.StartGame();
        var system = scn.GetLSCard("system"); var ship = scn.GetLSCard("ship"); var phylo = scn.GetLSCard("phylo");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship);
        int without = scn.GetPower(ship);
        assertEquals(0, without);
        scn.BoardAsPilot(ship, phylo);
        assertEquals(ship.getBlueprint().getPower() + 2, scn.GetPower(ship), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), phylo);
        assertEquals(without, scn.GetPower(ship));
    }

    @Test public void attritionThresholdAppliesToPhyloAndOwnCharactersHereAndFollowsHer() {
        var scn = fixture(); scn.StartGame();
        var here = scn.GetLSStartingLocation(); var away = scn.GetDSStartingLocation();
        var phylo = scn.GetLSCard("phylo"); var local = scn.GetLSCard("local"); var remote = scn.GetLSCard("remote");
        var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(here, phylo, local, vader); scn.MoveCardsToLocation(away, remote);
        var mods = scn.game().getModifiersQuerying(); var state = scn.gameState();
        var opposingBaseline = mods.getImmunityToAttritionLessThan(state, vader);
        assertEquals(3, mods.getImmunityToAttritionLessThan(state, phylo), scn.epsilon);
        assertEquals(3, mods.getImmunityToAttritionLessThan(state, local), scn.epsilon);
        assertEquals(0, mods.getImmunityToAttritionLessThan(state, remote), scn.epsilon);
        scn.MoveCardsToLocation(away, phylo);
        assertEquals(0, mods.getImmunityToAttritionLessThan(state, local), scn.epsilon);
        assertEquals(3, mods.getImmunityToAttritionLessThan(state, remote), scn.epsilon);
        assertEquals(opposingBaseline, mods.getImmunityToAttritionLessThan(state, vader), scn.epsilon);
    }
    private void playYouAreBeaten(boolean protectedHere) {
        var scn = fixture(); scn.StartGame();
        var here = scn.GetLSStartingLocation(); var away = scn.GetDSStartingLocation();
        var phylo = scn.GetLSCard("phylo"); var local = scn.GetLSCard("local");
        var vader = scn.GetDSCard("vader"); var saber = scn.GetDSCard("saber"); var beaten = scn.GetDSCard("beaten");
        var otherDark = scn.GetDSCard("otherDark"); var secondDark = scn.GetDSCard("secondDark");
        scn.MoveCardsToLocation(here, local, vader, otherDark, secondDark); scn.AttachCardsTo(vader, saber);
        scn.MoveCardsToLocation(protectedHere ? here : away, phylo);
        scn.MoveCardsToDSHand(beaten); scn.DSActivateForceCheat(8); scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(beaten, "Prevent character from moving or battling");
        assertEquals(scn.GetCurrentDecision().getText(), !protectedHere, scn.DSHasCardChoiceAvailable(local));
        assertFalse(scn.DSHasCardChoiceAvailable(phylo));
        assertTrue(scn.DSHasCardChoiceAvailable(otherDark));
        assertTrue(scn.DSHasCardChoiceAvailable(secondDark));
        if (!protectedHere) {
            scn.DSChooseCard(local);
            scn.PassForceUseResponses(); scn.PassAllResponses();
            assertTrue(scn.GetDSLostPile().contains(beaten));
        }
    }

    @Test public void actualYouAreBeatenCannotTargetOwnCharactersAtPhylosSite() { playYouAreBeaten(true); }
    @Test public void actualYouAreBeatenCanTargetAnAllyAtADifferentSiteFromPhylo() { playYouAreBeaten(false); }
    private void actualAttrition(int attrition) {
        var scn = fixture(); scn.StartGame();
        var site = scn.GetLSStartingLocation();
        var phylo = scn.GetLSCard("phylo"); var local = scn.GetLSCard("local");
        scn.MoveCardsToLocation(site, phylo, local, scn.GetLSCard("obi"), scn.GetDSCard("vader"));
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(3);
        scn.PrepareDSDestiny(attrition - 1); // Premiere Vader adds +1 to the actual draw.
        scn.SkipToDamageSegment(true);
        scn.PassDamageSegmentActions();
        assertEquals(0, scn.GetUnpaidLSBattleDamage());
        assertEquals(attrition, scn.GetUnpaidLSAttrition());
        assertEquals(attrition == 2 ? "Choose a card from battle to forfeit (if desired)"
                : "Choose a card from battle to forfeit", scn.GetCurrentDecision().getText());
        if (attrition == 2) {
            scn.LSPass();
            assertTrue(phylo.getZone().isInPlay());
            assertTrue(local.getZone().isInPlay());
        } else {
            scn.LSPayAttritionFromCardInPlay(phylo);
            assertTrue(scn.GetLSLostPile().contains(phylo));
        }
    }

    @Test public void canDeclineForfeitAtActualAttritionTwo() { actualAttrition(2); }
    @Test public void mustForfeitAtActualAttritionThree() { actualAttrition(3); }

    /**
     * Dark battles at Light's starting site while a Dark card's modifier makes Dark draw one destiny to total power.
     * Returns whether Phylo offered her response; if offered, uses it and checks Dark's total power drops.
     */
    private boolean phyloRespondsToAddedDestiny(String sourceName, boolean phyloHere) {
        var scn = fixture(); scn.StartGame();
        var here = scn.GetLSStartingLocation(); var away = scn.GetDSStartingLocation();
        var phylo = scn.GetLSCard("phylo"); var local = scn.GetLSCard("local");
        var vader = scn.GetDSCard("vader"); var source = scn.GetDSCard(sourceName);
        scn.MoveCardsToDSSideOfTable(source);
        scn.MoveCardsToLocation(here, local, vader);
        scn.MoveCardsToLocation(phyloHere ? here : away, phylo);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsToPowerOnlyModifier(source, 1, scn.DS, com.gempukku.swccgo.filters.Filters.any));
        scn.DSActivateForceCheat(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(here);
        var trace = new ArrayList<String>();
        for (int i = 0; i < 60 && scn.GetCurrentDecision() != null; i++) {
            var text = scn.GetCurrentDecision().getText(); var decider = scn.GetDecidingPlayer();
            trace.add(decider + ":" + text);
            if (text.contains("INITIAL_ATTRITION_CALCULATED") || text.toLowerCase().contains("damage segment")) break;
            if (text.contains("battle destiny?")) { scn.PlayerChooseYes(decider); continue; }
            if (decider.equals(scn.LS) && lsMayUse(scn, phylo, "Draw two destiny")) {
                int before = scn.GetDSTotalPower();
                scn.PrepareLSDestiny(3); scn.PrepareLSDestiny(2);
                scn.LSUseCardAction(phylo, "Draw two destiny");
                for (int j = 0; j < 30 && scn.GetCurrentDecision() != null; j++) {
                    var t = scn.GetCurrentDecision().getText(); var d = scn.GetDecidingPlayer();
                    if (scn.gameState().getBattleState() == null || t.contains("INITIAL_ATTRITION_CALCULATED")) break;
                    if (d.equals(scn.LS) && t.contains("destiny") && t.startsWith("Choose 1")) {
                        scn.PlayerDecided(scn.LS, scn.LSGetCardChoices().get(0));
                        continue;
                    }
                    if (scn.GetDSTotalPower() < before) break;
                    if (t.contains("battle destiny?")) { scn.PlayerChooseYes(d); continue; }
                    scn.PlayerPass(d);
                }
                assertTrue(trace.toString(), scn.GetDSTotalPower() < before);
                return true;
            }
            scn.PlayerPass(decider);
        }
        assertTrue(trace.toString(), trace.stream().anyMatch(t -> t.contains("BATTLE_DESTINY_DRAWS") || t.contains("DESTINY_DRAWN")));
        return false;
    }

    private static boolean lsMayUse(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        try {
            return scn.LSCardActionAvailable(card, text);
        } catch (IllegalStateException notACardActionDecision) {
            return false;
        }
    }

    @Test public void respondsWhenOpponentsBlockSixCardAddsDestinyToTotalPowerHere() {
        assertTrue(phyloRespondsToAddedDestiny("blockSix", true));
    }

    @Test public void doesNotRespondToADestinyAddedByANonBlockSixCard() {
        assertFalse(phyloRespondsToAddedDestiny("vader", true));
    }

    @Test public void doesNotRespondToADestinyAddedElsewhere() {
        assertFalse(phyloRespondsToAddedDestiny("blockSix", false));
    }
}
