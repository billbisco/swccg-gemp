package com.gempukku.swccgo.cards.set2272.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Original Imperial Stockpile (Dec 2010 slip: returns the weapon "if it leaves table"), a version of 1272_5. */
public class Card_2272_005_Tests {
    private VirtualTableScenario scenario(String stockpileId) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
            put("levitation", "6_79");
        }}, new HashMap<>() {{
            put("stockpile", stockpileId);
            put("other", "1272_5".equals(stockpileId) ? "2272_5" : "1272_5");
            put("rifle", "1_312");
            put("trooper", "1_194");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalImperialStockpileIsTheCurrentCardWithTheDecember2010Text() {
        var s = scenario("2272_5");
        assertVersion(s.GetDSCard("stockpile").getBlueprint(), s.GetDSCard("other").getBlueprint(), "1272_5", "",
                LocalDate.of(2010, 12, 15), LocalDate.of(2011, 1, 28),
                "Deploy on table; take into hand from Reserve Deck and stack a non-lightsaber weapon (without 'lost' in game text) here; reshuffle. If a battle was just initiated, may deploy weapon here on a participating card. Return weapon here at end of weapons segment (if possible), if it leaves table, or at end of battle. (Immune to Alter.)");
    }

    @Test
    public void OriginalImperialStockpileIsTheSameUniqueCardAsTheCurrentVersion() {
        for (String onTableId : List.of("1272_5", "2272_5")) {
            for (boolean otherOut : List.of(false, true)) {
                var s = scenario(onTableId);
                var onTable = s.GetDSCard("stockpile");
                var inHand = s.GetDSCard("other");
                s.StartGame();
                if (otherOut)
                    s.MoveCardsToDSSideOfTable(onTable);
                s.MoveCardsToDSHand(inHand);
                s.SkipToDSTurn(Phase.DEPLOY);
                s.EnsureDSForcePile(10);
                assertEquals(onTableId + " on table=" + otherOut, !otherOut, s.DSDeployAvailable(inHand));
            }
        }
    }

    private void deployRifleThenLukeStealsIt(VirtualTableScenario s) {
        var stockpile = s.GetDSCard("stockpile");
        var rifle = s.GetDSCard("rifle");
        var trooper = s.GetDSCard("trooper");
        var levitation = s.GetLSCard("levitation");
        var site = s.GetDSStartingLocation();
        s.StartGame();
        s.MoveCardsToDSSideOfTable(stockpile);
        s.StackCardsOn(stockpile, rifle);
        s.MoveCardsToLocation(site, trooper, s.GetLSCard("luke"));
        s.MoveCardsToLSHand(levitation);
        s.EnsureDSForcePile(6);
        s.EnsureLSForcePile(6);
        s.SkipToDSTurn(Phase.BATTLE);
        s.DSInitiateBattle(site);
        for (int i = 0; i < 20 && !(s.GetDecidingPlayer().equals(s.DS)
                && s.DSCardActionAvailable(stockpile, "Deploy weapon on a participating card")); i++)
            s.PlayerPass(s.GetDecidingPlayer());
        s.DSUseCardAction(stockpile, "Deploy weapon on a participating card");
        if (s.DSHasCardChoiceAvailable(rifle)) s.DSChooseCard(rifle);
        if (s.DSHasCardChoiceAvailable(trooper)) s.DSChooseCard(trooper);
        for (int i = 0; i < 20 && rifle.getAttachedTo() != trooper; i++)
            s.PlayerPass(s.GetDecidingPlayer());
        assertTrue(s.IsAttachedTo(trooper, rifle));
        for (int i = 0; i < 20 && !(s.GetDecidingPlayer().equals(s.LS) && s.LSCardPlayAvailable(levitation, "Steal")); i++)
            s.PlayerPass(s.GetDecidingPlayer());
        assertTrue(s.LSCardPlayAvailable(levitation, "Steal"));
        s.LSPlayCard(levitation, "Steal");
        if (s.LSHasCardChoiceAvailable(rifle)) s.LSChooseCard(rifle);
        if (s.LSHasCardChoiceAvailable(s.GetLSCard("luke"))) s.LSChooseCard(s.GetLSCard("luke"));
        s.PassAllResponses();
    }

    @Test
    public void OriginalImperialStockpileDoesNotSaveItsWeaponFromBeingStolen() {
        // "if it leaves table": a stolen weapon stays on table, so the original does not return it (the current card does)
        var s = scenario("2272_5");
        deployRifleThenLukeStealsIt(s);
        assertEquals(s.GetLSCard("luke"), s.GetDSCard("rifle").getAttachedTo());
    }

    @Test
    public void CurrentImperialStockpileReturnsItsWeaponInsteadOfItBeingStolen() {
        var s = scenario("1272_5");
        deployRifleThenLukeStealsIt(s);
        assertTrue(s.IsStackedOn(s.GetDSCard("stockpile"), s.GetDSCard("rifle")));
    }

    @Test
    public void OriginalImperialStockpileReturnsItsWeaponAfterTheWeaponsSegment() {
        var s = scenario("2272_5");
        var stockpile = s.GetDSCard("stockpile");
        var rifle = s.GetDSCard("rifle");
        var trooper = s.GetDSCard("trooper");
        var site = s.GetDSStartingLocation();
        s.StartGame();
        s.MoveCardsToDSSideOfTable(stockpile);
        s.StackCardsOn(stockpile, rifle);
        s.MoveCardsToLocation(site, trooper, s.GetLSCard("luke"));
        s.EnsureDSForcePile(6);
        s.SkipToDSTurn(Phase.BATTLE);
        s.DSInitiateBattle(site);
        s.DSUseCardAction(stockpile, "Deploy weapon on a participating card");
        s.PassAllResponses();
        assertTrue(s.IsAttachedTo(trooper, rifle));
        s.PassWeaponsSegmentActions();
        s.PassAllResponses();
        assertTrue(s.IsStackedOn(stockpile, rifle));
    }
}
