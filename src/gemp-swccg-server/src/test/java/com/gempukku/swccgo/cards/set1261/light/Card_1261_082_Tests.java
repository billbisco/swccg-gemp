package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_082_Tests {
    private static final String DEPLOY = "Reveal pilot or unpiloted combat vehicle";
    private static final String DRAW = "Draw top card of Reserve Deck";

    private VirtualTableScenario fixture() {
        var ls = new HashMap<>(Map.of("flaps", "1261_82", "luke", "3_3", "rogue", "3_66", "differentPilot", "3_27",
                "differentVehicle", "3_68", "artillery", "3_72", "tactics", "7_86", "top", "2_18", "controlEffect", "1_66"));
        ls.put("hoth", "3_63");
        var ds = new HashMap<>(Map.of("vader", "1_168", "alter", "1_234"));
        return new VirtualTableScenario(ls, ds, 40, 40, StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void exactLegacyComboRetainsBothTitlesAndOneDestiny() {
        var scn = fixture(); var card = scn.GetLSCard("flaps"); var b = card.getBlueprint();
        assertEquals("Maneuvering Flaps & Nick Of Time", b.getTitle());
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

    @Test public void flapsDeploysAndNativeSuspensionCannotDisableIt() {
        var scn = fixture(); scn.StartGame();
        var flaps = scn.GetLSCard("flaps"); var ordinary = scn.GetLSCard("controlEffect");
        scn.MoveCardsToLSHand(flaps); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(flaps); scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, flaps.getZone());
        scn.MoveCardsToLSSideOfTable(ordinary);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("alter"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.EnsureDSForcePile(8);
        assertTrue(flaps.getBlueprint().isImmuneToCardTitle(Title.Alter));
        var action = new TopLevelGameTextAction(scn.GetDSStartingLocation(), scn.DS, scn.GetDSStartingLocation().getCardId());
        action.appendEffect(new SuspendCardUntilEndOfTurnEffect(action, ordinary));
        action.appendEffect(new SuspendCardUntilEndOfTurnEffect(action, flaps));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        scn.PassAllResponses();
        assertFalse(scn.IsCardActive(ordinary));
        assertTrue(scn.IsCardActive(flaps));
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
}
