package com.gempukku.swccgo.cards.set1274.light;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hindsight", "1274_18");
                    put("atrocity", "7_65");
                    put("luke", "1_19");
                    put("luckyshot", "1274_21");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("shuffle", "1_262");
                    put("vader", "1_168");
                    put("codeclearance", "12_131");
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

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 30; ++i) {
            if (done.getAsBoolean())
                return;
            try {
                if (scn.LSAnyDecisionsAvailable())
                    scn.LSPass();
                else if (scn.DSAnyDecisionsAvailable())
                    scn.DSPass();
                else
                    return;
            } catch (Throwable t) {
                String ls = scn.GetAwaitingDecision(scn.LS) == null ? null : scn.GetAwaitingDecision(scn.LS).getText();
                String ds = scn.GetAwaitingDecision(scn.DS) == null ? null : scn.GetAwaitingDecision(scn.DS).getText();
                throw new AssertionError("Could not pass: LS=" + ls + " DS=" + ds, t);
            }
        }
    }

    private static boolean lsHasAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        return scn.LSAnyDecisionsAvailable() && !scn.GetLSAvailableActions().isEmpty() && scn.LSCardActionAvailable(card, text);
    }

    @Test
    public void HindsightVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hindsight").getBlueprint();

        assertEquals("Hindsight", card.getTitle());
        assertEquals("Unless Imperial Atrocity on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 6 (ignore other modifiers); opponent may take any one card into hand from Reserve Deck; reshuffle. Immune to Control. OR Use 2 Force to deploy on table and stack one just-played Interrupt here.", card.getGameText());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.GRABBER);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    /** Sets up a Dark Side Force drain of the given size at the Dark Side starting site during Dark Side's control phase. */
    private void startDrainOf(VirtualTableScenario scn, int drain) {
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(scn.GetLSCard("hindsight"));
        scn.MoveCardsToLocation(site, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.ApplyAdHocModifier(new ForceDrainModifier(trooper, Filters.sameCardId(site), drain - scn.GetDSIconsOnLocation(site), scn.DS));
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.DSForceDrainAt(site);
    }

    private int lifeForceLostToDrainWithHindsight(VirtualTableScenario scn, int drain) {
        var hindsight = scn.GetLSCard("hindsight");
        var vader = scn.GetDSCard("vader");
        startDrainOf(scn, drain);

        passUntil(scn, () -> lsHasAction(scn, hindsight, "Deploy to reduce Force loss by 6"));
        assertTrue(lsHasAction(scn, hindsight, "Deploy to reduce Force loss by 6"));
        int lifeForceBefore = scn.GetLSLifeForceRemaining();
        scn.LSUseCardAction(hindsight, "Deploy to reduce Force loss by 6");
        scn.MoveCardsToTopOfOwnReserveDeck(vader);
        passUntil(scn, () -> scn.DSDecisionAvailable("take any one card into hand from Reserve Deck"));

        assertInZone(Zone.SIDE_OF_TABLE, hindsight);
        assertTrue(scn.DSDecisionAvailable("take any one card into hand from Reserve Deck"));
        scn.DSChooseYes();
        scn.DSChooseCard(vader);
        passUntil(scn, () -> scn.AwaitingLSForceLossPayment() || scn.AwaitingDSControlPhaseActions());
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
            scn.PassAllResponses();
        }
        assertInZone(Zone.HAND, vader);
        return lifeForceBefore - scn.GetLSLifeForceRemaining();
    }

    @Test
    public void HindsightVReducesADrainOfEightBySixLeavingTwo() {
        assertEquals(2, lifeForceLostToDrainWithHindsight(GetScenario(), 8));
    }

    @Test
    public void HindsightVReducesADrainOfSixToNothing() {
        // The 2011 slip's "reduce loss by 4" would leave 2 here.
        assertEquals(0, lifeForceLostToDrainWithHindsight(GetScenario(), 6));
    }

    @Test
    public void HindsightVMayNotDeployToReduceForceLossWhileImperialAtrocityOnTable() {
        var scn = GetScenario();
        var hindsight = scn.GetLSCard("hindsight");
        var atrocity = scn.GetLSCard("atrocity");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(atrocity);
        scn.MoveCardsToHand(hindsight);
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(site);

        final boolean[] offered = {false};
        passUntil(scn, () -> {
            if (lsHasAction(scn, hindsight, "Deploy to reduce Force loss by 6"))
                offered[0] = true;
            return offered[0] || scn.AwaitingDSControlPhaseActions() || scn.AwaitingLSForceLossPayment();
        });
        assertFalse(offered[0]);
        assertInZone(Zone.HAND, hindsight);
    }

    @Test
    public void HindsightVUsesTwoForceToDeployAndStackAJustPlayedInterrupt() {
        var scn = GetScenario();
        var hindsight = scn.GetLSCard("hindsight");
        var luckyShot = scn.GetLSCard("luckyshot");
        var codeClearance = scn.GetDSCard("codeclearance");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(codeClearance);
        scn.MoveCardsToHand(hindsight, luckyShot);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);
        assertTrue(scn.LSCardPlayAvailable(luckyShot));
        scn.LSPlayCard(luckyShot);
        passUntil(scn, () -> lsHasAction(scn, hindsight, "Use 2 Force to deploy and stack"));
        assertTrue(lsHasAction(scn, hindsight, "Use 2 Force to deploy and stack"));
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSUseCardAction(hindsight, "Use 2 Force to deploy and stack");
        passUntil(scn, () -> scn.AwaitingLSDeployPhaseActions());

        assertInZone(Zone.SIDE_OF_TABLE, hindsight);
        assertTrue(scn.IsStackedOn(hindsight, luckyShot));
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }
}
