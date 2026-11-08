package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard;
import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.*;

/** Original Vader's Mask (first printing, 2010-06-18), a version of 1271_17: any move away from Vader costs opponent 1 Force. */
public class Card_2271_017_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("forest", "1116_16");
            put("haven", "1116_17");
            put("system", "1116_15");
            put("ship", "1115_26");
            put("rebel", "1_28");
        }}, new HashMap<>() {{
            put("vader", "1271_2");
            put("rise", "1271_19");
            put("version", "2271_17");
            put("current", "1271_17");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalVadersMaskIsTheCurrentCardWithItsOwnText() {
        assertVersion(VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2271_17"),
                VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_17"), "1271_17", "",
                LocalDate.of(2010, 6, 18), LocalDate.of(2010, 6, 28),
                "Deploy on [Vader] Vader. During your battle phase, may cancel this Effect to allow Vader to make a regular move. In order for opponent's character to move away from here, opponent must first lose 1 Force. While defending a battle at opponent's site, Vader is power +2 and immune to attrition. (Immune to Alter.)");
    }

    // Uniqueness: [Vader] Effects deploy only from under Rise Of The Dark Lord, so no deploy-from-hand check here;
    // LegacyVersionsTests checks the version has the current card's title and uniqueness.

    private int lightLossForMoveAway(String maskKey, boolean shuttle) {
        var s = scenario();
        var forest = s.GetLSCard("forest");
        var haven = s.GetLSCard("haven");
        var system = s.GetLSCard("system");
        var ship = s.GetLSCard("ship");
        var rebel = s.GetLSCard("rebel");
        s.StartGame();
        s.MoveLocationToTable(system);
        s.MoveLocationToTable(forest);
        s.MoveLocationToTable(haven);
        s.MoveCardsToLocation(system, ship);
        s.MoveCardsToDSSideOfTable(s.GetDSCard("rise"));
        s.MoveCardsToLocation(forest, s.GetDSCard("vader"), rebel);
        s.AttachCardsTo(s.GetDSCard("vader"), s.GetDSCard(maskKey));
        s.SkipToLSTurn(Phase.MOVE);
        s.EnsureLSForcePile(10);
        int lost = s.GetLSLostPile().size();
        if (shuttle) {
            s.LSUseCardAction(rebel, "Shuttle");
            s.LSChooseCard(ship);
        } else {
            s.LSMoveCard(rebel, haven);
        }
        s.PassAllResponses();
        if (s.AwaitingLSForceLossPayment()) {
            s.LSPayForceLossFromReserveDeck();
            s.PassAllResponses();
        }
        if (shuttle)
            assertTrue(s.IsAboardAsPassenger(ship, rebel));
        else
            assertTrue(s.CardsAtLocation(haven, rebel));
        return s.GetLSLostPile().size() - lost;
    }

    @Test
    public void OriginalVadersMaskChargesOneForceForAShuttleAwayFromVader() {
        assertEquals(1, lightLossForMoveAway("version", true));
    }

    @Test
    public void CurrentVadersMaskDoesNotChargeAShuttleAwayFromVader() {
        assertEquals(0, lightLossForMoveAway("current", true));
    }

    @Test
    public void OriginalVadersMaskStillChargesOneForceForALandspeedMoveAway() {
        assertEquals(1, lightLossForMoveAway("version", false));
    }
}
