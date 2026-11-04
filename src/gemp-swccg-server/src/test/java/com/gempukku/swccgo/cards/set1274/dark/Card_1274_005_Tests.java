package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leadership", "1112_19");  // Rebel Leadership (V): Used Interrupt, take card into hand from Reserve Deck
                    put("admiral", "9_6");
                    put("reservecard", "1_19");
                }},
                new HashMap<>() {{
                    put("counter", "1274_5");
                    put("propaganda", "601_88");   // Imperial Propaganda (V)
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static boolean dsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean lsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.LSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean passUntil(VirtualTableScenario scn, BooleanSupplier condition) {
        for (int i = 0; i < 30; i++) {
            if (condition.getAsBoolean()) {
                return true;
            }
            if (scn.DSDecisionAvailable("Choose Force to lose") || scn.LSDecisionAvailable("Choose Force to lose")) {
                return false;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                return false;
            }
        }
        return condition.getAsBoolean();
    }

    @Test
    public void DesperateCounterVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("counter").getBlueprint();

        assertEquals("Desperate Counter", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.GRABBER);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DesperateCounterVDeploysToReduceForceLossByFourAndOpponentMayTakeCardIntoHand() {
        var scn = GetScenario();
        var counter = scn.GetDSCard("counter");
        var luke = scn.GetLSCard("luke");
        var reservecard = scn.GetLSCard("reservecard");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToDSHand(counter);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToBottomOfLSReserveDeck(reservecard);
        scn.SkipToLSTurn(Phase.CONTROL);
        int dsLostBefore = scn.GetDSLostPileCount();
        int dsForcePileBefore = scn.GetDSForcePileCount();
        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        assertTrue(passUntil(scn, () -> dsCan(scn, counter, null)));
        scn.DSPlayCard(counter);
        assertTrue(passUntil(scn, () -> scn.LSDecisionAvailable("take any one card")));
        scn.LSChooseYes();
        scn.LSChooseCard(reservecard);
        scn.PassAllResponses();

        assertTrue(counter.getZone().isInPlay());
        assertFalse(scn.DSDecisionAvailable("Choose Force to lose"));
        assertFalse(scn.IsActiveForceDrain());
        assertInZone(Zone.HAND, reservecard);
        assertEquals(dsLostBefore, scn.GetDSLostPileCount());
        assertEquals(dsForcePileBefore, scn.GetDSForcePileCount());
    }

    @Test
    public void DesperateCounterVMayNotDeployToReduceForceLossIfImperialPropagandaOnTable() {
        var scn = GetScenario();
        var counter = scn.GetDSCard("counter");
        var propaganda = scn.GetDSCard("propaganda");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToDSSideOfTable(propaganda);
        scn.MoveCardsToDSHand(counter);
        scn.EnsureDSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSForceDrainAt(site);
        assertFalse(passUntil(scn, () -> dsCan(scn, counter, null)));
    }

    @Test
    public void DesperateCounterVUsesTwoForceToDeployAndGrabJustPlayedInterrupt() {
        var scn = GetScenario();
        var counter = scn.GetDSCard("counter");
        var leadership = scn.GetLSCard("leadership");
        var admiral = scn.GetLSCard("admiral");

        scn.StartGame();
        scn.MoveCardsToDSHand(counter);
        scn.MoveCardsToLSHand(leadership);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToBottomOfLSReserveDeck(admiral);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int dsForceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(leadership, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(leadership, "Take card into hand from Reserve Deck");
        assertTrue(passUntil(scn, () -> dsCan(scn, counter, null)));
        scn.DSPlayCard(counter);
        scn.PassAllResponses();

        assertTrue(counter.getZone().isInPlay());
        assertEquals(dsForceBefore - 2, scn.GetDSForcePileCount());
        assertTrue(scn.IsStackedOn(counter, leadership));
        assertFalse(admiral.getZone() == Zone.HAND);
    }
}
