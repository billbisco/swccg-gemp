package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1261_088_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("first", "1261_88"); put("base", "3_39");
            put("hoth", "3_55"); put("rdv", "7_123"); put("kessel", "1_126");
            put("bay", "3_59"); put("third", "3_56"); put("fourth", "3_62"); put("ordinaryMarker", "3_61");
            put("luminous", "9_77"); put("transport", "8_80"); put("remoteTransport", "5_86"); put("xwing", "1_146");
            put("trooper", "1_28"); put("obi", "1_21"); put("han", "1_11"); put("traffic", "1_66");
        }}, new HashMap<>() {{
            put("virtualMarker", "1282_8"); put("surprise", "5_156"); put("alter", "1_234"); put("control", "4_139");
            put("vader", "1_168");
        }}, 60, 60, StartingSetup.LSStartingLocation("1_129"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void FirstTransportUsesFinalLegacyUtinniPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("first").getBlueprint();
        assertEquals("The First Transport Is Away!", b.getTitle()); assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.UTINNI, b.getCardSubtype()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertTrue(b.hasVirtualSuffix()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.HOTH, Icon.EFFECT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario setup(boolean virtual, boolean includeOtherTargets) {
        var scn = scenario(); scn.StartGame();
        for (var key : List.of("hoth", "rdv", "kessel", "bay", "third", "fourth")) scn.MoveLocationToTable(scn.GetLSCard(key));
        scn.MoveLocationToTable(virtual ? scn.GetDSCard("virtualMarker") : scn.GetLSCard("ordinaryMarker"));
        scn.MoveCardsToLocation(scn.GetLSCard("hoth"), scn.GetLSCard("luminous"), scn.GetLSCard("xwing"));
        scn.MoveCardsToLocation(scn.GetLSCard("kessel"), scn.GetLSCard("remoteTransport"));
        if (includeOtherTargets) scn.MoveCardsToLocation(scn.GetLSCard("hoth"), scn.GetLSCard("transport"));
        scn.MoveCardsToLocation(scn.GetLSCard("third"), scn.GetLSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetLSCard("fourth"), scn.GetLSCard("obi"));
        scn.MoveCardsToLSHand(scn.GetLSCard("first"), scn.GetLSCard("base"));
        scn.MoveCardsToDSHand(scn.GetDSCard("control"), scn.GetDSCard("alter"), scn.GetDSCard("surprise"));
        var darkSite = scn.GetDSStartingLocation();
        if (darkSite.getZone() != Zone.LOCATIONS) scn.MoveLocationToTable(darkSite);
        scn.MoveCardsToLocation(darkSite, scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSActivateForceCheat(8); scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void deploy(VirtualTableScenario scn, String key, PhysicalCardImpl location, PhysicalCardImpl target) {
        var effect = scn.GetLSCard(key);
        assertTrue(scn.LSCardPlayAvailable(effect)); scn.LSPlayCard(effect);
        if (scn.LSHasCardChoiceAvailable(location)) scn.LSChooseCard(location);
        if (scn.LSHasCardChoiceAvailable(target)) scn.LSChooseCard(target);
        scn.PassAllResponses(); assertTrue(scn.IsAttachedTo(location, effect));
        assertEquals(target, effect.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
    }

    @Test public void FirstTransportRequiresVirtualFirstMarkerEvenWithOtherwiseLegalTargets() {
        var scn = setup(false, true);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("first")));
        assertTrue(Filters.First_Marker.accepts(scn.game(), scn.GetLSCard("ordinaryMarker")));
        assertFalse(scn.GetLSCard("ordinaryMarker").getBlueprint().hasVirtualSuffix());
    }

    @Test public void FirstTransportChoosesOnlyRendezvousAndAnyTransportOnHoth() {
        var scn = setup(true, true); var effect = scn.GetLSCard("first"); var rdv = scn.GetLSCard("rdv");
        scn.LSPlayCard(effect);
        if (scn.LSHasCardChoiceAvailable(rdv)) {
            assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("kessel")));
            assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("hoth")));
            scn.LSChooseCard(rdv);
        }
        assertTrue(scn.LSHasCardChoicesAvailable(scn.GetLSCard("luminous"), scn.GetLSCard("transport")));
        assertTrue(scn.LSHasCardChoicesNotAvailable(scn.GetLSCard("xwing"), scn.GetLSCard("remoteTransport")));
        scn.LSChooseCard(scn.GetLSCard("transport"));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(rdv, effect));
        assertEquals(scn.GetLSCard("transport"), effect.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
    }

    private void hyperspeed(VirtualTableScenario scn, PhysicalCardImpl target) {
        var ship = scn.GetLSCard("luminous");
        scn.LSUseCardAction(ship, "hyperspeed");
        scn.LSChooseCard(target); scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(target, ship));
    }

    private int amount(VirtualTableScenario scn, String site, String player) {
        return (int) scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), scn.GetLSCard(site), player);
    }

    private VirtualTableScenario arrive() {
        var scn = setup(true, false); var effect = scn.GetLSCard("first"); var rdv = scn.GetLSCard("rdv");
        var ship = scn.GetLSCard("luminous"); var hoth = scn.GetLSCard("hoth");
        deploy(scn, "first", rdv, ship);
        assertEquals(1, amount(scn, "fourth", scn.LS));
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("traffic")); int lost = scn.GetLSLostPileCount();
        scn.SkipToPhase(Phase.MOVE); hyperspeed(scn, scn.GetLSCard("kessel"));
        assertTrue(scn.IsAttachedTo(rdv, effect));
        scn.SkipToLSTurn(Phase.MOVE); hyperspeed(scn, rdv);
        assertTrue(scn.IsAttachedTo(hoth, effect));
        assertEquals(UtinniEffectStatus.COMPLETED, effect.getUtinniEffectStatus());
        assertEquals(lost, scn.GetLSLostPileCount());
        return scn;
    }

    @Test public void FirstTransportActuallyRelocatesAfterTwoLegalMovesAndBoostsOnlyOwnUnshieldedMarkers() {
        var scn = arrive();
        assertTrue(Filters.underHothEnergyShield().accepts(scn.game(), scn.GetLSCard("third")));
        assertFalse(Filters.underHothEnergyShield().accepts(scn.game(), scn.GetLSCard("fourth")));
        assertEquals(2, amount(scn, "fourth", scn.LS));
        assertEquals(1, amount(scn, "third", scn.LS));
        assertEquals(1, amount(scn, "fourth", scn.DS));
    }

    @Test public void FirstTransportBlocksActualControlOnUnshieldedDrain() {
        var scn = arrive(); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(scn.GetLSCard("fourth"));
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Cancel Force drain")));
        scn.PassAllResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test public void FirstTransportLeavesShieldedDrainCancelableByActualControl() {
        var scn = arrive(); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(scn.GetLSCard("third"));
        scn.DSPlayCard(scn.GetDSCard("control"), "Cancel Force drain"); scn.PassAllResponses();
        assertNull(scn.gameState().getForceDrainState());
    }

    @Test public void FirstTransportIsImmuneToActualSurpriseAndAlterWhileOrdinaryUtinniIsTargetable() {
        var scn = setup(true, false);
        var effect = scn.GetLSCard("first");
        var ship = scn.GetLSCard("luminous"); var other = scn.GetLSCard("remoteTransport");
        scn.MoveCardsToLocation(scn.GetLSCard("hoth"), other);
        scn.LSPass(); scn.DSPass();
        deploy(scn, "first", scn.GetLSCard("rdv"), ship);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Re-target Utinni Effect")));
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("alter")));
        assertNotNull(effect.getAttachedTo());

        var stock = setup(true, false); var stockShip = stock.GetLSCard("luminous");
        stock.MoveCardsToLocation(stock.GetLSCard("bay"), stockShip, stock.GetLSCard("remoteTransport"));
        stock.LSPass(); stock.DSPass();
        deploy(stock, "base", stock.GetLSCard("kessel"), stockShip);
        stock.SkipToDSTurn(Phase.CONTROL);
        assertTrue(stock.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Re-target Utinni Effect")));
        assertTrue(stock.DSCardPlayAvailable(stock.GetDSCard("alter")));
        assertNotNull(stock.GetLSCard("base").getAttachedTo());
    }
}
