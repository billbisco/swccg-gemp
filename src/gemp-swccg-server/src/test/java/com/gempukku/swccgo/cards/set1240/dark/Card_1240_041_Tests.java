package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_1240_041_Tests {
    private static final String THROW = "USED: Throw a vehicle or starship";
    private static final String CHARACTER = "LOST: Choke or drain an opponent's character";
    private static final String ORDINARY_CONFLICT = "LOST: Subtract 2 from battle destiny draws";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("han", "1_11"), Map.entry("remote", "1_28"), Map.entry("luke", "1_19"),
                Map.entry("crawler", "1_150"), Map.entry("speeder", "1_149"), Map.entry("redThree", "1_145"),
                Map.entry("computer", "1_39"), Map.entry("sense", "1_109"))),
                new HashMap<>(Map.ofEntries(
                        Map.entry("effect", "1240_41"), Map.entry("vader", "1_168"), Map.entry("galen", "601_81"),
                        Map.entry("trooper", "1_181"), Map.entry("parry", "11_83"), Map.entry("orders", "106_17"),
                        Map.entry("conflict", "11_88"), Map.entry("other", "1_249"))),
                12, 12, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.DeclineLSStartingInterrupt, StartingSetup.DeclineDSStartingInterrupt,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TheForceUnleashedHasPrintedStatsIconsAndKeywords() {
        var scn = scenario();
        var card = scn.GetDSCard("effect").getBlueprint();
        assertEquals("The Force Unleashed", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.getCardTypes().contains(CardType.EFFECT));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.EFFECT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void TheForceUnleashedDeploysOnTableAndDoesNotGrantPlaysOutsideBattle() {
        var scn = scenario();
        var effect = scn.GetDSCard("effect");
        scn.StartGame();
        scn.MoveCardsToDSHand(effect, scn.GetDSCard("parry"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"), scn.GetLSCard("han"),
                scn.GetLSCard("crawler"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPlayCardAndPassResponses(effect);
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        assertNoGrant(scn, "parry");
        scn.SkipToPhase(Phase.BATTLE);
        assertNoGrant(scn, "parry");
    }

    @Test
    public void TheForceUnleashedGrantsBothModesToAllThreeOriginalInterrupts() {
        for (String caster : List.of("vader", "galen")) {
            var scn = battle(caster, true, false);
            for (String key : List.of("parry", "orders", "conflict")) {
                var interrupt = scn.GetDSCard(key);
                assertTrue(caster + " / " + key, scn.DSCardActionAvailable(interrupt, THROW));
                assertTrue(caster + " / " + key, scn.DSCardActionAvailable(interrupt, CHARACTER));
            }
            assertNoGrant(scn, "other");
        }
    }

    @Test
    public void TheForceUnleashedNeedsAnActiveEffectAndVaderOrGalenPresentAtThisBattle() {
        var absent = battle("remote", true, false);
        var noEffect = battle("vader", false, false);
        for (var scn : List.of(absent, noEffect)) {
            for (String key : List.of("parry", "orders", "conflict")) {
                assertNoGrant(scn, key);
            }
        }
        assertTrue(noEffect.DSCardActionAvailable(noEffect.GetDSCard("conflict"), ORDINARY_CONFLICT));
    }

    @Test
    public void TheForceUnleashedThrowsVehicleAtThreeButNotAtTwo() {
        verifyVehicleThrow(2, false);
        verifyVehicleThrow(3, true);
    }

    private void verifyVehicleThrow(int destiny, boolean succeeds) {
        var scn = battle("vader", true, false);
        var parry = scn.GetDSCard("parry");
        var target = scn.GetLSCard("crawler");
        int power = scn.GetDSTotalPower();
        scn.PrepareDSDestiny(destiny);
        scn.DSUseCardAction(parry, THROW);
        assertTrue(scn.DSHasCardChoiceAvailable(target));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("han")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("speeder")));
        scn.DSChooseCard(target);
        scn.PassAllResponses();

        assertEquals(succeeds, target.isHit());
        assertEquals(power + (succeeds ? 3 : 0), scn.GetDSTotalPower());
        assertTrue(scn.GetDSUsedPile().contains(parry));
    }

    @Test
    public void TheForceUnleashedUsesPrintedManeuverRatherThanModifiedDefenseOnStarship() {
        var scn = battle("galen", true, true);
        var orders = scn.GetDSCard("orders");
        var ship = scn.GetLSCard("redThree");
        assertEquals(5, scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), ship), scn.epsilon);
        int power = scn.GetDSTotalPower();
        scn.PrepareDSDestiny(3);
        scn.DSUseCardAction(orders, THROW);
        scn.DSChooseCard(ship);
        scn.PassAllResponses();

        assertTrue(ship.isHit());
        assertEquals(power + 4, scn.GetDSTotalPower());
        assertTrue(scn.GetDSUsedPile().contains(orders));
    }

    @Test
    public void TheForceUnleashedChokesOnlyWhenDestinyExceedsAbility() {
        verifyChoke(3, false);
        verifyChoke(4, true);
    }

    private void verifyChoke(int destiny, boolean succeeds) {
        var scn = battle("galen", true, false);
        var orders = scn.GetDSCard("orders");
        var han = scn.GetLSCard("han");
        int lost = scn.GetLSLostPileCount();
        chooseCharacterMode(scn, orders, han, destiny, "Choke");
        scn.PassAllResponses();

        assertFalse(han.isHit());
        assertInZone(succeeds ? Zone.LOST_PILE : Zone.AT_LOCATION, han);
        assertEquals(lost + (succeeds ? 1 : 0), scn.GetLSLostPileCount());
        assertInZone(Zone.LOST_PILE, orders);
    }

    @Test
    public void TheForceUnleashedDrainsAtThreeButNotAtTwoAgainstAbilityThree() {
        verifyDrain(2, false);
        verifyDrain(3, true);
    }

    private void verifyDrain(int destiny, boolean succeeds) {
        var scn = battle("vader", true, false);
        var conflict = scn.GetDSCard("conflict");
        var han = scn.GetLSCard("han");
        int lost = scn.GetLSLostPileCount();
        chooseCharacterMode(scn, conflict, han, destiny, "Drain of life");
        scn.PassAllResponses();

        assertEquals(succeeds, han.isHit());
        assertInZone(Zone.AT_LOCATION, han);
        if (succeeds) {
            assertTrue(scn.AwaitingLSForceLossPayment());
            scn.LSPayRemainingForceLossFromReserveDeck();
        } else {
            assertFalse(scn.AwaitingLSForceLossPayment());
        }
        assertEquals(lost + (succeeds ? 2 : 0), scn.GetLSLostPileCount());
        assertInZone(Zone.LOST_PILE, conflict);
    }

    @Test
    public void TheForceUnleashedKeepsTheOriginalThereIsNoConflictPlay() {
        var scn = battle("vader", true, false);
        var conflict = scn.GetDSCard("conflict");
        assertTrue(scn.DSCardActionAvailable(conflict, CHARACTER));
        assertTrue(scn.DSCardActionAvailable(conflict, ORDINARY_CONFLICT));
        scn.DSUseCardAction(conflict, ORDINARY_CONFLICT);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, conflict);
        scn.PrepareLSDestiny(3);
        scn.PrepareDSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(1, scn.GetLSTotalDestiny());
    }

    @Test
    public void TheForceUnleashedGrantedUsedPlayCanBeCanceledByOriginalSense() {
        var scn = battle("vader", true, false);
        var conflict = scn.GetDSCard("conflict");
        var target = scn.GetLSCard("crawler");
        var sense = scn.GetLSCard("sense");
        scn.MoveCardsToLSHand(sense);
        var destiny = scn.GetDSDestiny(3);
        scn.MoveCardsToTopOfDSReserveDeck(destiny);
        scn.PrepareLSDestiny(1);
        int power = scn.GetDSTotalPower();

        scn.DSUseCardAction(conflict, THROW);
        scn.DSChooseCard(target);
        if (scn.DSDecisionAvailable("optional response")) {
            scn.DSPass();
        }
        assertTrue(scn.LSCardActionAvailable(sense, "Draw destiny to cancel"));
        scn.LSUseCardAction(sense, "Draw destiny to cancel");
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke"))) {
            scn.LSChooseCard(scn.GetLSCard("luke"));
        }
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, conflict);
        assertFalse(target.isHit());
        assertEquals(power, scn.GetDSTotalPower());
        assertSame(destiny, scn.GetTopOfDSReserveDeck());
    }

    private void chooseCharacterMode(VirtualTableScenario scn, PhysicalCardImpl interrupt,
                                     PhysicalCardImpl target, int destiny, String mode) {
        var draw = scn.GetDSDestiny(destiny);
        scn.MoveCardsToTopOfDSReserveDeck(draw);
        scn.DSUseCardAction(interrupt, CHARACTER);
        assertTrue(scn.DSHasCardChoiceAvailable(target));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("trooper")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("remote")));
        scn.DSChooseCard(target);
        assertTrue(scn.DSDecisionAvailable("Choose before drawing destiny"));
        assertTrue(scn.DSChoiceAvailable("Choke"));
        assertTrue(scn.DSChoiceAvailable("Drain of life"));
        assertSame(draw, scn.GetTopOfDSReserveDeck());
        scn.DSChoose(mode);
    }

    private static void assertNoGrant(VirtualTableScenario scn, String key) {
        var interrupt = scn.GetDSCard(key);
        assertFalse(key, scn.DSCardActionAvailable(interrupt, THROW));
        assertFalse(key, scn.DSCardActionAvailable(interrupt, CHARACTER));
    }

    private VirtualTableScenario battle(String caster, boolean withEffect, boolean withShip) {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var away = scn.GetLSStartingLocation();
        if (withEffect) {
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
        }
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("han"), scn.GetLSCard("crawler"));
        scn.MoveCardsToLocation(away, scn.GetLSCard("remote"), scn.GetLSCard("speeder"));
        if (caster.equals("remote")) {
            scn.MoveCardsToLocation(away, scn.GetDSCard("vader"), scn.GetDSCard("galen"));
        } else {
            scn.MoveCardsToLocation(site, scn.GetDSCard(caster));
        }
        if (withShip) {
            var ship = scn.GetLSCard("redThree");
            var luke = scn.GetLSCard("luke");
            scn.MoveCardsToLocation(site, ship);
            scn.RemoveCardZone(luke);
            scn.gameState().attachCardInPilotCapacitySlot(luke, ship);
            scn.AttachCardsTo(ship, scn.GetLSCard("computer"));
        } else {
            scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        }
        scn.MoveCardsToDSHand(scn.GetDSCard("parry"), scn.GetDSCard("orders"),
                scn.GetDSCard("conflict"), scn.GetDSCard("other"));
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        return scn;
    }
}
