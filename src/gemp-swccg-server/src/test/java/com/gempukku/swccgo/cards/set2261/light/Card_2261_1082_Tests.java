package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ThrowCardEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** Batch R8-P: the card-list version 2261_1082 of 1261_82 keeps the behaviour the current card had before the Master List text (2013) became current; these are the former Card_1261_082_Tests tests run on 2261_1082. */
public class Card_2261_1082_Tests {
    private static final String DEPLOY = "Reveal pilot or unpiloted combat vehicle";
    private static final String DRAW = "Draw top card of Reserve Deck";

    private String flapsId = "2261_1082";

    private VirtualTableScenario fixture() {
        var ls = new HashMap<>(Map.of("flaps", flapsId, "luke", "3_3", "rogue", "3_66", "differentPilot", "3_27",
                "differentVehicle", "3_68", "artillery", "3_72", "tactics", "7_86", "top", "2_18", "controlEffect", "1_66"));
        ls.put("hoth", "3_63");
        ls.putAll(Map.of("system", "3_55", "capital", "9_74", "turbolaser", "9_89",
                "xwing", "1_146", "xwingCannon", "7_162", "rapidFire", "7_100",
                "onTarget", "7_96", "luckyShot", "3_45", "steadyAim", "7_105", "speederCannon", "3_74"));
        ls.put("lightAlter", "1_71");
        ls.put("dack", "3_4");
        ls.put("leia", "1_17");
        var ds = new HashMap<>(Map.of("darkEffect", "4_116", "vader", "1_168", "alter", "1_234", "capital", "1_301", "walker", "3_157"));
        return new VirtualTableScenario(ls, ds, 40, 40, StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void exactLegacyComboRetainsBothTitlesAndOneDestiny() {
        var scn = fixture(); var card = scn.GetLSCard("flaps"); var b = card.getBlueprint();
        assertEquals("Maneuvering Flaps & Nick Of Time", b.getTitle());
        assertEquals("Deploy on table. Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.' During your deploy phase, may reveal an unpiloted combat vehicle from hand to take its matching pilot character (or vice versa) from Reserve Deck and deploy both simultaneously; reshuffle. Whenever you play Desperate Tactics, Lucky Shot, On Target, Rapid Fire, or Steady Aim, may draw top card from Reserve Deck. (Immune to Alter.)", b.getGameText());
        assertFalse(b.getGameText().contains("May not be suspended"));
        assertTrue(Filters.Maneuvering_Flaps.accepts(scn.game(), card));
        assertTrue(Filters.title("Nick Of Time").accepts(scn.game(), card));
        assertEquals(1, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private void pair(boolean pilotFromHand) {
        var scn = fixture(); scn.StartGame();
        var flaps = scn.GetLSCard("flaps");
        var hand = scn.GetLSCard(pilotFromHand ? "luke" : "rogue");
        var reserve = scn.GetLSCard(pilotFromHand ? "rogue" : "luke");
        var wrong = scn.GetLSCard(pilotFromHand ? "differentVehicle" : "differentPilot");
        var site = scn.GetLSCard("hoth");
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLSSideOfTable(flaps);
        scn.MoveCardsToLSHand(hand);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(DEPLOY)));
        scn.SkipToPhase(Phase.DEPLOY); scn.EnsureLSForcePile(10);
        scn.MoveCardsToTopOfLSReserveDeck(reserve, wrong);
        scn.gameState().markCardsRevealedFromPile(List.of(wrong));
        scn.LSUseCardAction(flaps, DEPLOY);
        if (scn.LSDecisionAvailable("Choose card from hand")) scn.LSChooseCard(hand);
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(reserve)) {
            int idx = scn.LSGetBPChoices().indexOf(wrong.getBlueprintId(true));
            assertTrue(idx < 0 || !Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(idx)));
            scn.LSChooseCard(reserve);
        }
        if (scn.LSHasCardChoiceAvailable(site)) scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertTrue("hand=" + hand.getZone() + " reserve=" + reserve.getZone(), scn.IsCardActive(hand));
        assertTrue(scn.IsCardActive(reserve));
        assertEquals(Zone.RESERVE_DECK, wrong.getZone());
        assertFalse(scn.gameState().isCardRevealedFromPile(wrong));
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(DEPLOY)));
    }

    @Test public void revealsACombatVehicleAndDeploysItsReservePilot() { pair(false); }
    @Test public void revealsAPilotAndDeploysItsReserveCombatVehicle() { pair(true); }

    @Test public void originalVersionDeploysAndMayNotBeSuspendedWhileAnotherEffectIs() {
        // 2261_82 (6.1 sheet / Master List): "May not be suspended." The current PC card-list text drops it.
        flapsId = "2261_82";
        var scn = fixture(); scn.StartGame();
        var flaps = scn.GetLSCard("flaps"); var ordinary = scn.GetLSCard("controlEffect");
        scn.MoveCardsToLSHand(flaps); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(flaps); scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, flaps.getZone());
        assertTrue(flaps.getBlueprint().getGameText().contains("May not be suspended."));
        scn.MoveCardsToLSSideOfTable(ordinary);
        scn.SkipToDSTurn(Phase.CONTROL);
        var action = new TopLevelGameTextAction(scn.GetDSStartingLocation(), scn.DS, scn.GetDSStartingLocation().getCardId());
        action.appendEffect(new SuspendCardUntilEndOfTurnEffect(action, ordinary));
        action.appendEffect(new SuspendCardUntilEndOfTurnEffect(action, flaps));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        scn.PassAllResponses();
        assertFalse(scn.IsCardActive(ordinary));
        assertTrue(scn.IsCardActive(flaps));
        assertFalse(flaps.isSuspended());
    }

    @Test public void flapsMayNowBeSuspendedLikeAnotherEffect() {
        // PC card list (PC-E1, the latest text) has no "May not be suspended."
        var scn = fixture(); scn.StartGame();
        var flaps = scn.GetLSCard("flaps"); var ordinary = scn.GetLSCard("controlEffect");
        scn.MoveCardsToLSHand(flaps); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(flaps); scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, flaps.getZone());
        assertFalse(flaps.getBlueprint().getGameText().contains("May not be suspended."));
        scn.MoveCardsToLSSideOfTable(ordinary);
        scn.SkipToDSTurn(Phase.CONTROL);
        var action = new TopLevelGameTextAction(scn.GetDSStartingLocation(), scn.DS, scn.GetDSStartingLocation().getCardId());
        action.appendEffect(new SuspendCardUntilEndOfTurnEffect(action, ordinary));
        action.appendEffect(new SuspendCardUntilEndOfTurnEffect(action, flaps));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        scn.PassAllResponses();
        assertFalse(scn.IsCardActive(ordinary));
        assertFalse(scn.IsCardActive(flaps));
        assertTrue(flaps.isSuspended());
    }

    // ---- "Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.'" ----

    /** Vader 'throws' the vehicle (shared ThrowCardEffect) during DS's control phase; returns whether it was 'hit'. */
    private boolean throwVehicle(boolean flapsOnTable, String vehicleKey, boolean dsVehicle, String pilotKey, String passengerKey) {
        var scn = fixture(); scn.StartGame();
        var site = scn.GetLSCard("hoth");
        scn.MoveLocationToTable(site);
        var vader = scn.GetDSCard("vader");
        var vehicle = dsVehicle ? scn.GetDSCard(vehicleKey) : scn.GetLSCard(vehicleKey);
        scn.MoveCardsToLocation(site, vader, vehicle);
        if (pilotKey != null) scn.BoardAsPilot(vehicle, scn.GetLSCard(pilotKey));
        if (passengerKey != null) scn.BoardAsPassenger(vehicle, scn.GetLSCard(passengerKey));
        if (flapsOnTable) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("flaps"));
        scn.SkipToDSTurn(Phase.CONTROL);
        var action = new TopLevelGameTextAction(vader, scn.DS, vader.getCardId());
        action.appendEffect(new ThrowCardEffect(action, vehicle, vader));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        boolean aboutToBeHitWindow = scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText().contains("ABOUT_TO_BE_HIT");
        scn.PassAllResponses();
        // A vehicle 'hit' outside of battle is lost right away, so 'hit' shows as having left the site.
        boolean hit = vehicle.isHit() || vehicle.getZone() != Zone.AT_LOCATION;
        assertEquals("the 'hit' goes through the normal about-to-be-hit window", hit, aboutToBeHitWindow);
        return hit;
    }

    @Test public void yourVehicleWithAPilotCharacterAboardIsNotHitWhenThrown() {
        assertFalse(throwVehicle(true, "rogue", false, "luke", null));
    }

    @Test public void yourVehicleWithAGunnerAboardIsNotHitWhenThrown() {
        assertFalse(throwVehicle(true, "differentVehicle", false, null, "dack"));
    }

    @Test public void withoutFlapsTheSamePilotedVehicleIsHitWhenThrown() {
        assertTrue(throwVehicle(false, "rogue", false, "luke", null));
    }

    @Test public void yourEmptyVehicleIsStillHitWhenThrown() {
        assertTrue(throwVehicle(true, "differentVehicle", false, null, null));
    }

    @Test public void yourVehicleWithOnlyANonPilotNonGunnerAboardIsStillHitWhenThrown() {
        assertTrue(throwVehicle(true, "differentVehicle", false, null, "leia"));
    }

    @Test public void opponentsVehiclesAreStillHitWhenThrown() {
        assertTrue(throwVehicle(true, "walker", true, null, null));
    }

    private void tacticsDraw(boolean accept) {
        var scn = fixture(); scn.StartGame();
        var flaps = scn.GetLSCard("flaps"); var tactics = scn.GetLSCard("tactics");
        var site = scn.GetLSStartingLocation(); var top = scn.GetLSCard("top");
        scn.MoveCardsToLSSideOfTable(flaps);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("artillery"), scn.GetDSCard("vader"));
        scn.MoveCardsToLSHand(tactics); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.MoveCardsToTopOfLSReserveDeck(top);
        scn.LSPlayCard(tactics, "Cancel immunity to attrition");
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(flaps, DRAW));
        if (accept) scn.LSUseCardAction(flaps, DRAW); else scn.LSPass();
        scn.PassAllResponses();
        if (accept) assertEquals(Zone.HAND, top.getZone()); else assertEquals(top, scn.GetTopOfLSReserveDeck());
    }

    @Test public void actualDesperateTacticsOffersTheTopReserveCard() { tacticsDraw(true); }
    @Test public void actualDesperateTacticsDrawMayBeDeclined() { tacticsDraw(false); }
    private void takeFlapsDraw(VirtualTableScenario scn) {
        var flaps = scn.GetLSCard("flaps");
        var top = scn.GetLSCard("top");
        var trace = new ArrayList<String>();
        for (int i = 0; i < 12 && scn.GetCurrentDecision() != null; i++) {
            var prompt = scn.GetCurrentDecision().getText();
            trace.add(prompt + " " + scn.GetLSAvailableActions());
            if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(DRAW))) {
                assertEquals(top, scn.GetTopOfLSReserveDeck());
                scn.LSUseCardAction(flaps, DRAW);
                assertEquals(Zone.HAND, top.getZone());
                return;
            }
            if (!prompt.toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Flaps did not offer its printed draw: " + trace);
    }

    @Test public void actualOnTargetOffersTheTopReserveCard() {
        var scn = fixture(); scn.StartGame();
        var system = scn.GetLSCard("system"); var capital = scn.GetLSCard("capital");
        var opponent = scn.GetDSCard("capital"); var top = scn.GetLSCard("top");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, capital, opponent);
        scn.AttachCardsTo(capital, scn.GetLSCard("turbolaser"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("flaps"));
        scn.MoveCardsToLSHand(scn.GetLSCard("onTarget"));
        scn.LSActivateForceCheat(10); scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(top);
        scn.LSPlayCard(scn.GetLSCard("onTarget"), "Target starship");
        if (scn.LSHasCardChoiceAvailable(opponent)) scn.LSChooseCard(opponent);
        scn.PassForceUseResponses();
        takeFlapsDraw(scn);
    }

    @Test public void actualRapidFireOffersTheTopReserveCard() {
        var scn = fixture(); scn.StartGame();
        var system = scn.GetLSCard("system"); var ship = scn.GetLSCard("xwing");
        var cannon = scn.GetLSCard("xwingCannon"); var top = scn.GetLSCard("top");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship, scn.GetDSCard("capital"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("flaps"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rapidFire"), cannon);
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.MoveCardsToTopOfLSReserveDeck(top);
        scn.LSPlayCard(scn.GetLSCard("rapidFire"), "Deploy weapon from hand");
        scn.PassResponses("PLAYING_CARD_ACTION");
        if (scn.LSHasCardChoiceAvailable(cannon)) scn.LSChooseCard(cannon);
        if (scn.LSHasCardChoiceAvailable(ship)) scn.LSChooseCard(ship);
        takeFlapsDraw(scn);
    }

    private void weaponResponse(boolean starship) {
        var scn = fixture(); scn.StartGame();
        var location = starship ? scn.GetLSCard("system") : scn.GetLSCard("hoth");
        var gun = scn.GetLSCard(starship ? "turbolaser" : "speederCannon");
        var opponent = scn.GetDSCard(starship ? "capital" : "walker");
        var response = scn.GetLSCard(starship ? "steadyAim" : "luckyShot");
        scn.MoveLocationToTable(location);
        if (starship) {
            var capital = scn.GetLSCard("capital");
            scn.MoveCardsToLocation(location, capital, opponent);
            scn.AttachCardsTo(capital, gun);
        } else {
            var speeder = scn.GetLSCard("rogue");
            scn.MoveCardsToLocation(location, speeder, opponent);
            scn.BoardAsPilot(speeder, scn.GetLSCard("luke"));
            scn.AttachCardsTo(speeder, gun);
            scn.BoardAsPilot(opponent, scn.GetDSCard("vader"));
        }
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("flaps"));
        scn.MoveCardsToLSHand(response);
        scn.LSActivateForceCheat(12); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(location);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("top"));
        scn.LSUseCardAction(gun, "Fire");
        if (scn.LSHasCardChoiceAvailable(opponent)) scn.LSChooseCard(opponent);
        scn.PassForceUseResponses();
        String mode = starship ? "Add 2 to weapon destiny" : "Add 1 to weapon destiny";
        var trace = new ArrayList<String>();
        for (int i = 0; i < 12 && scn.GetCurrentDecision() != null
                && scn.GetLSAvailableActions().stream().noneMatch(x -> x.contains(mode)); i++) {
            var prompt = scn.GetCurrentDecision().getText();
            trace.add(prompt + " " + scn.GetLSAvailableActions());
            if (!prompt.toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(trace.toString(), scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(mode)));
        scn.LSPlayCard(response, mode);
        takeFlapsDraw(scn);
    }

    @Test public void actualLuckyShotOffersTheTopReserveCard() { weaponResponse(false); }
    @Test public void actualSteadyAimOffersTheTopReserveCard() { weaponResponse(true); }
    @Test public void actualAlterCanChooseOrdinaryEffectButNotFlaps() {
        var scn = fixture(); scn.StartGame();
        var flaps = scn.GetLSCard("flaps"); var other = scn.GetLSCard("controlEffect");
        scn.MoveCardsToLSSideOfTable(flaps, other);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("alter"));
        scn.DSActivateForceCheat(8); scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(scn.GetDSCard("alter"), "Draw destiny to cancel Effect");
        assertFalse(scn.DSHasCardChoiceAvailable(flaps));
        assertTrue(scn.DSHasCardChoiceAvailable(other));
    }
    @Test public void playingAnUnlistedLightInterruptDoesNotOfferTheDraw() {
        var scn = fixture(); scn.StartGame();
        var flaps = scn.GetLSCard("flaps"); var alter = scn.GetLSCard("lightAlter");
        var effect = scn.GetDSCard("darkEffect"); var luke = scn.GetLSCard("luke");
        var top = scn.GetLSCard("top");
        scn.MoveCardsToLSSideOfTable(flaps, effect);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.MoveCardsToLSHand(alter); scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(top);
        scn.LSPlayCard(alter, "Draw destiny to cancel Effect");
        if (scn.LSHasCardChoiceAvailable(effect)) scn.LSChooseCard(effect);
        if (scn.LSHasCardChoiceAvailable(luke)) scn.LSChooseCard(luke);
        boolean sawDraw = false;
        for (int i = 0; i < 22 && scn.GetCurrentDecision() != null; i++) {
            sawDraw |= scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(DRAW));
            if (!scn.GetCurrentDecision().getText().toLowerCase().contains("optional response")) break;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertFalse(sawDraw);
        assertTrue(scn.GetLSUsedPile().contains(alter));
    }
}
