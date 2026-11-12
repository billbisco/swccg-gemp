package com.gempukku.swccgo.cards.set3.light;


import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Printed text states (docs/legacy-versions.md, "Text states") of Hoth and Dagobah cards whose printed text plays
 * differently from the current card: Imperial Gunner (3_88o) and Wes Janson (3_25o) add to weapon destiny only
 * aboard a starship, K-3PO (3_12o) counts himself among "your droids", and Shot In The Dark (4_131o) has no
 * once-per-turn limit. Printed texts from the plain wiki.swccg.com articles (Decipher archive scans).
 */
public class Card_3_4_Behaviour_TextStates_Tests {
    private static final String GUNNER = "If aboard a starship as a passenger, adds 1 to starship's weapon destiny draws.";
    private static final String WES = "If aboard a starship as a passenger, adds 1 to starship's weapon destiny draws (adds 3 if aboard Rogue 3 or with Wedge or Jek).";
    private static final String K3PO = "May initiate battle and be battled. K-3PO is power +1 for each of your droids at same Hoth or Yavin 4 site. Functions as a leader if present with at least one of your other droids.";
    private static final String SHOT = "Deploy on your side of table. At any time, you may lose 1 Force to draw the top card of your Reserve Deck into your hand. If that card is a space creature, you may immediately deploy it for free.";

    private static VirtualTableScenario scenario(HashMap<String, String> ls, HashMap<String, String> ds, StartingSetup lsLocation, StartingSetup dsLocation) {
        return new VirtualTableScenario(ls, ds, 10, 10, lsLocation, dsLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    private static void assertPrinted(PhysicalCardImpl card, String bareId, String title, String text, LocalDate printedOn) {
        var bp = card.getBlueprint();
        assertEquals(text, bp.getGameText());
        TextState state = bp.getTextState();
        assertEquals(bareId, state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(printedOn, state.printedOn());
        assertEquals(title + " [Printed]", GameUtils.getFullName(bp));
    }

    /** Weapon destiny added by the passenger's modifiers when the ship he is aboard fires a weapon. */
    private static float passengerWeaponDestinyBonus(String passengerId, boolean light, boolean vehicle) {
        HashMap<String, String> mine = new HashMap<>() {{
            put("passenger", passengerId);
            put("ship", vehicle ? (light ? "3_69" : "3_157") : (light ? "1_146" : "1_305"));
        }};
        HashMap<String, String> none = new HashMap<>();
        var scn = scenario(light ? mine : none, light ? none : mine,
                light ? StartingSetup.DefaultLSGroundLocation : StartingSetup.DefaultLSSpaceSystem,
                light ? StartingSetup.DSStartingLocation("1_288") : StartingSetup.DefaultDSGroundLocation);
        scn.StartGame();
        var ship = light ? scn.GetLSCard("ship") : scn.GetDSCard("ship");
        var passenger = light ? scn.GetLSCard("passenger") : scn.GetDSCard("passenger");
        scn.MoveCardsToLocation(vehicle == light ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation(), ship);
        scn.BoardAsPassenger(ship, passenger);
        float total = 0;
        for (Modifier m : scn.game().getModifiersQuerying().getModifiers(scn.gameState(), ModifierType.EACH_WEAPON_DESTINY)) {
            if (m.getSource(scn.gameState()) != null && m.getSource(scn.gameState()).getCardId() == passenger.getCardId())
                total += m.getWeaponDestinyModifier(scn.gameState(), scn.game().getModifiersQuerying(), ship, ship, null, Collections.emptyList());
        }
        return total;
    }

    @Test
    public void ImperialGunnerPrintedAddsWeaponDestinyAboardAStarshipOnly() {
        var scn = scenario(new HashMap<>(), new HashMap<>() {{ put("gunner", "3_88o"); }}, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation);
        assertPrinted(scn.GetDSCard("gunner"), "3_88", "Imperial Gunner", GUNNER, LocalDate.of(1996, 11, 1));
        assertEquals(1, passengerWeaponDestinyBonus("3_88o", false, false), 0.01f);
        assertEquals(1, passengerWeaponDestinyBonus("3_88", false, false), 0.01f);
        assertEquals(0, passengerWeaponDestinyBonus("3_88o", false, true), 0.01f);
        assertEquals(1, passengerWeaponDestinyBonus("3_88", false, true), 0.01f);
    }

    @Test
    public void WesJansonPrintedAddsWeaponDestinyAboardAStarshipOnly() {
        var scn = scenario(new HashMap<>() {{ put("wes", "3_25o"); }}, new HashMap<>(), StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation);
        assertPrinted(scn.GetLSCard("wes"), "3_25", "Wes Janson", WES, LocalDate.of(1996, 11, 1));
        assertEquals(1, passengerWeaponDestinyBonus("3_25o", true, false), 0.01f);
        assertEquals(1, passengerWeaponDestinyBonus("3_25", true, false), 0.01f);
        assertEquals(0, passengerWeaponDestinyBonus("3_25o", true, true), 0.01f);
        assertEquals(1, passengerWeaponDestinyBonus("3_25", true, true), 0.01f);
    }

    private static int k3poPowerAloneAtEchoDockingBay(String id) {
        var scn = scenario(new HashMap<>() {{ put("k3po", id); }}, new HashMap<>(),
                StartingSetup.LSStartingLocation("3_59"), StartingSetup.DefaultDSGroundLocation);
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("k3po"));
        return scn.GetPower(scn.GetLSCard("k3po"));
    }

    @Test
    public void K3POPrintedCountsHimselfAmongYourDroids() {
        var scn = scenario(new HashMap<>() {{ put("k3po", "3_12o"); }}, new HashMap<>(), StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation);
        assertPrinted(scn.GetLSCard("k3po"), "3_12", "K-3PO (Kay-Threepio)", K3PO, LocalDate.of(1996, 11, 1));
        assertEquals(k3poPowerAloneAtEchoDockingBay("3_12") + 1, k3poPowerAloneAtEchoDockingBay("3_12o"));
    }

    private static boolean shotUsableTwiceInOneTurn(String id) {
        var scn = scenario(new HashMap<>(), new HashMap<>() {{ put("shot", id); }}, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation);
        var shot = scn.GetDSCard("shot");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(shot);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(shot));
        int hand = scn.GetDSHandCount();
        scn.DSUseCardAction(shot);
        scn.PassAllResponses();
        // Cost: lose 1 Force (paid from Reserve Deck), then the top card of Reserve Deck is drawn
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(hand + 1, scn.GetDSHandCount());
        // Light Side has the next control action; it passes back to Dark Side
        if (scn.LSDecisionAvailable("Choose Control action"))
            scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("Choose Control action"));
        return scn.DSCardActionAvailable(shot);
    }

    @Test
    public void ShotInTheDarkPrintedHasNoOncePerTurnLimit() {
        var scn = scenario(new HashMap<>(), new HashMap<>() {{ put("shot", "4_131o"); }}, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation);
        assertPrinted(scn.GetDSCard("shot"), "4_131", "Shot In The Dark", SHOT, LocalDate.of(1997, 4, 1));
        assertTrue(shotUsableTwiceInOneTurn("4_131o"));
        assertFalse(shotUsableTwiceInOneTurn("4_131"));
    }
}
