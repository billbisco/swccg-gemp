package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Original Hindsight (V) (Dec 2011 slip: "reduce loss by 4"), a version of 1274_18. */
public class Card_2274_018_Tests {
    private static final String DEPLOY = "Deploy to reduce Force loss by 4";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("hindsight", "2274_18");
            put("current", "1274_18");
        }}, new HashMap<>() {{
            put("trooper", "1_194");
            put("vader", "1_168");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private static void passUntil(VirtualTableScenario s, BooleanSupplier done) {
        for (int i = 0; i < 30; ++i) {
            if (done.getAsBoolean())
                return;
            if (s.LSAnyDecisionsAvailable())
                s.LSPass();
            else if (s.DSAnyDecisionsAvailable())
                s.DSPass();
            else
                return;
        }
    }

    private static boolean lsHasAction(VirtualTableScenario s, PhysicalCardImpl card, String text) {
        return s.LSAnyDecisionsAvailable() && !s.GetLSAvailableActions().isEmpty() && s.LSCardActionAvailable(card, text);
    }

    @Test
    public void OriginalHindsightIsTheCurrentCardWithTheDecember2011Text() {
        var s = scenario();
        assertVersion(s.GetLSCard("hindsight").getBlueprint(), s.GetLSCard("current").getBlueprint(), "1274_18", "",
                LocalDate.of(2011, 12, 16), LocalDate.of(2012, 8, 27),
                "Unless Imperial Atrocity on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may take any one card into hand from Reserve Deck; reshuffle. Immune to Control. OR Use 2 Force to deploy on table and stack one just-played Interrupt here.");
    }

    @Test
    public void OriginalHindsightIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            var s = scenario();
            var onTable = s.GetLSCard(currentOnTable ? "current" : "hindsight");
            var inHand = s.GetLSCard(currentOnTable ? "hindsight" : "current");
            var trooper = s.GetDSCard("trooper");
            var site = s.GetDSStartingLocation();
            s.StartGame();
            s.MoveCardsToLSSideOfTable(onTable);
            s.MoveCardsToLSHand(inHand);
            s.MoveCardsToLocation(site, trooper);
            s.SkipToDSTurn(Phase.CONTROL);
            s.ApplyAdHocModifier(new ForceDrainModifier(trooper, Filters.sameCardId(site), 8 - s.GetDSIconsOnLocation(site), s.DS));
            s.DSForceDrainAt(site);
            boolean offered = false;
            for (int i = 0; i < 30 && !offered; i++) {
                offered = s.LSAnyDecisionsAvailable() && !s.GetLSAvailableActions().isEmpty()
                        && s.LSCardActionAvailable(inHand, "Deploy to reduce Force loss");
                if (offered || !(s.LSAnyDecisionsAvailable() || s.DSAnyDecisionsAvailable()))
                    break;
                if (s.AwaitingLSForceLossPayment())
                    break;
                if (s.LSAnyDecisionsAvailable()) s.LSPass();
                else s.DSPass();
            }
            assertEquals("current on table=" + currentOnTable, false, offered);
        }
    }

    @Test
    public void OriginalHindsightReducesADrainOfEightByFourLeavingFour() {
        var s = scenario();
        var hindsight = s.GetLSCard("hindsight");
        var vader = s.GetDSCard("vader");
        var trooper = s.GetDSCard("trooper");
        var site = s.GetDSStartingLocation();
        s.StartGame();
        s.MoveCardsToHand(hindsight);
        s.MoveCardsToLocation(site, trooper);
        s.SkipToDSTurn(Phase.CONTROL);
        s.ApplyAdHocModifier(new ForceDrainModifier(trooper, Filters.sameCardId(site), 8 - s.GetDSIconsOnLocation(site), s.DS));
        s.DSForceDrainAt(site);
        passUntil(s, () -> lsHasAction(s, hindsight, DEPLOY));
        assertTrue(lsHasAction(s, hindsight, DEPLOY));
        int lifeForceBefore = s.GetLSLifeForceRemaining();
        s.LSUseCardAction(hindsight, DEPLOY);
        s.MoveCardsToTopOfOwnReserveDeck(vader);
        passUntil(s, () -> s.DSDecisionAvailable("take any one card into hand from Reserve Deck"));
        assertInZone(Zone.SIDE_OF_TABLE, hindsight);
        s.DSChooseYes();
        s.DSChooseCard(vader);
        passUntil(s, () -> s.AwaitingLSForceLossPayment() || s.AwaitingDSControlPhaseActions());
        if (s.AwaitingLSForceLossPayment()) {
            s.LSPayRemainingForceLossFromReserveDeck();
            s.PassAllResponses();
        }
        assertInZone(Zone.HAND, vader);
        assertEquals(4, lifeForceBefore - s.GetLSLifeForceRemaining());
    }
}
