package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_011_Tests {
    private static final String GUARD = "Deploy a Royal Guard from Reserve Deck";
    private static final String LIGHTNING = "Force Lightning: Draw destiny to reduce power and cancel immunity";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("luke", "601_52", "ben", "1_21", "alter", "1_71")),
                new HashMap<>(Map.of("effect", "1263_11", "emperor", "9_109", "guard", "9_119",
                        "anotherGuard", "9_118", "trooper", "1_194", "lightning", "9_136")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void TheEmperorIsComingHerePrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("effect").getBlueprint();
        assertEquals("The Emperor Is Coming Here?", b.getTitle());
        assertEquals("Though reluctant to leave Coruscant, Emperor Palpatine occasionally finds it necessary to personally put lagging Imperial operations back on schedule.", b.getLore());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
    }

    @Test public void TheEmperorIsComingHereActuallyDeploysOnTableAndIsImmuneToAlter() {
        var scn = scenario();
        var effect = scn.GetDSCard("effect");
        scn.MoveCardsToDSHand(effect);
        scn.MoveCardsToLSHand(scn.GetLSCard("alter"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("ben"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(effect);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("alter")));
    }

    @Test public void TheEmperorIsComingHereDeploysAndReshufflesOneActualRoyalGuardPerGame() {
        for (boolean emperorOnTable : List.of(false, true)) {
            var scn = scenario();
            var effect = scn.GetDSCard("effect");
            var guard = scn.GetDSCard("guard");
            var anotherGuard = scn.GetDSCard("anotherGuard");
            var trooper = scn.GetDSCard("trooper");
            var site = scn.GetDSStartingLocation();
            scn.MoveCardsToDSSideOfTable(effect);
            if (emperorOnTable) scn.MoveCardsToLocation(site, scn.GetDSCard("emperor"));
            scn.SkipToPhase(Phase.CONTROL);
            scn.EnsureDSForcePile(8);
            scn.MoveCardsToTopOfDSReserveDeck(guard, anotherGuard, trooper);
            scn.SkipToPhase(Phase.DEPLOY);
            assertEquals(emperorOnTable, scn.DSCardActionAvailable(effect, GUARD));
            if (!emperorOnTable) {
                assertInZone(Zone.RESERVE_DECK, guard);
                continue;
            }
            var remaining = new ArrayList<>(scn.GetDSReserveDeck());
            remaining.remove(guard);
            scn.gameState().getRandom().setSeed(4096);
            int force = scn.GetDSForcePileCount();
            scn.DSUseCardAction(effect, GUARD);
            assertTrue(scn.DSHasCardChoiceAvailable(guard));
            int trooperIndex = scn.DSGetBPChoices().indexOf(trooper.getBlueprintId(true));
            assertTrue("Trooper is visible in the Reserve search", trooperIndex >= 0);
            assertEquals("false", scn.GetADParam(scn.DS, "selectable")[trooperIndex]);
            scn.DSChooseCard(guard);
            if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
            scn.PassAllResponses();
            assertSame(site, guard.getAtLocation());
            assertInZone(Zone.RESERVE_DECK, anotherGuard);
            assertEquals(force - (int) (float) guard.getBlueprint().getDeployCost(), scn.GetDSForcePileCount());
            assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
            assertNotEquals(remaining, scn.GetDSReserveDeck());
            for (int i = 0; i < 20 && !scn.AwaitingDSDeployPhaseActions(); i++) {
                if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected deployment response: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(scn.AwaitingDSDeployPhaseActions());
            assertFalse(scn.DSCardActionAvailable(effect, GUARD));
            scn.SkipToDSTurn(Phase.DEPLOY);
            assertFalse(scn.DSCardActionAvailable(effect, GUARD));
        }
    }

    @Test public void TheEmperorIsComingHereGrantsAnActualUsedPlayOnlyDuringBattleWithAnEligibleTarget() {
        for (boolean effectOnTable : List.of(false, true)) {
            var scn = scenario();
            var site = scn.GetDSStartingLocation();
            var lightning = scn.GetDSCard("lightning");
            scn.MoveCardsToLocation(site, scn.GetDSCard("emperor"), scn.GetLSCard("luke"));
            if (effectOnTable) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
            scn.MoveCardsToDSHand(lightning);
            scn.EnsureDSForcePile(3);
            scn.SkipToPhase(Phase.BATTLE);
            assertFalse(scn.DSCardPlayAvailable(lightning, LIGHTNING));
            scn.DSInitiateBattle(site);
            scn.PassAllResponses();
            assertTrue(scn.AwaitingDSWeaponsSegmentActions());
            assertEquals(effectOnTable, scn.DSCardPlayAvailable(lightning, LIGHTNING));
            assertTrue(scn.DSCardPlayAvailable(lightning, "Reduce target's defense value"));
        }
    }

    @Test public void TheEmperorIsComingHereUsesThePrintedStrictAbilityBoundaryForBothResults() {
        for (int destiny : List.of(1, 2, 3)) {
            var scn = scenario();
            var site = scn.GetDSStartingLocation();
            var luke = scn.GetLSCard("luke");
            var ben = scn.GetLSCard("ben");
            var lightning = scn.GetDSCard("lightning");
            scn.MoveCardsToLocation(site, scn.GetDSCard("emperor"), luke);
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ben);
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
            scn.MoveCardsToDSHand(lightning);
            scn.EnsureDSForcePile(3);
            scn.SkipToPhase(Phase.BATTLE);
            scn.DSInitiateBattle(site);
            scn.PassAllResponses();
            scn.PrepareDSDestiny(destiny);
            var mods = scn.game().getModifiersQuerying();
            int power = scn.GetPower(luke);
            float defense = mods.getDefenseValue(scn.gameState(), luke);
            float immunity = mods.getImmunityToAttritionLessThan(scn.gameState(), luke);
            assertEquals(5, mods.getAbility(scn.gameState(), luke), scn.epsilon);
            assertTrue(immunity > 0);
            scn.DSPlayCard(lightning, LIGHTNING);
            assertTrue(scn.DSHasCardChoiceAvailable(luke));
            assertFalse(scn.DSHasCardChoiceAvailable(ben));
            scn.DSChooseCard(luke);
            for (int i = 0; i < 40 && !scn.AwaitingDSWeaponsSegmentActions(); i++) {
                if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Force Lightning decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(scn.AwaitingDSWeaponsSegmentActions());
            assertInZone(Zone.USED_PILE, lightning);
            assertEquals(defense, mods.getDefenseValue(scn.gameState(), luke), scn.epsilon);
            boolean succeeds = destiny > 2;
            assertEquals(succeeds ? power - 2 : power, scn.GetPower(luke));
            assertEquals(succeeds ? 0 : immunity, mods.getImmunityToAttritionLessThan(scn.gameState(), luke), scn.epsilon);
            if (succeeds) {
                scn.SkipToDamageSegment(false);
                if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
                scn.SkipToPhase(Phase.MOVE);
                assertInZone(Zone.AT_LOCATION, luke);
                assertEquals(power, scn.GetPower(luke));
                assertEquals(immunity, mods.getImmunityToAttritionLessThan(scn.gameState(), luke), scn.epsilon);
            }
        }
    }
}
