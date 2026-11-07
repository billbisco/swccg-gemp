package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_044_Tests {
    private static final String TRANSPORT = "Transport' characters";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("rebel", "1_19")),
                new HashMap<>(Map.of("lana", "1261_44", "vader", "1_168", "trooper", "1_194",
                        "elsewhere", "1_177", "battleground", "5_166", "interior", "5_168", "exterior", "7_277")),
                25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedUsedComboIdentity() {
        var scn = scenario();
        var bp = scn.GetDSCard("lana").getBlueprint();
        assertEquals("Lana Dobreed & Sacrifice", bp.getTitle());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(4, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertFalse(bp.hasVirtualSuffix());
        assertTrue(bp.getTitles().containsAll(List.of(Title.Lana_Dobreed, Title.Sacrifice)));
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.EPISODE_I,
                Icon.CORUSCANT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test(timeout = 10000)
    public void mayPayOneDestinyAmountToMoveTwoFromOneSiteToInteriorBattlegroundOrExteriorNonBattleground() {
        for (String destination : List.of("battleground", "exterior")) {
            var scn = scenario();
            setup(scn, Phase.MOVE);
            scn.EnsureDSForcePile(7);
            scn.PrepareDSDestiny(3);
            int before = scn.GetDSForcePileCount();
            select(scn, destination, true);
            scn.PassAllResponses();
            scn.DSChooseYes();
            scn.PassAllResponses();
            assertEquals(before - 3, scn.GetDSForcePileCount());
            assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("lana")));
            assertSame(scn.GetDSCard(destination), location(scn, scn.GetDSCard("vader")));
            assertSame(scn.GetDSCard(destination), location(scn, scn.GetDSCard("trooper")));
            assertSame(scn.GetLSStartingLocation(), location(scn, scn.GetDSCard("elsewhere")));
            assertSame(scn.GetDSStartingLocation(), location(scn, scn.GetLSCard("rebel")));
        }
    }

    @Test(timeout = 10000)
    public void canDeclineAnAffordableTransportOrFailAnUnaffordableOneAndLoseOnlyTheInterrupt() {
        for (boolean affordable : List.of(true, false)) {
            var scn = scenario();
            setup(scn, Phase.MOVE);
            scn.EnsureDSForcePile(5);
            if (!affordable)
                scn.DrainDSForcePileTo(1);
            scn.PrepareDSDestiny(3);
            int before = scn.GetDSForcePileCount();
            select(scn, "battleground", false);
            scn.PassAllResponses();
            if (affordable)
                scn.DSChooseNo();
            scn.PassAllResponses();
            assertEquals(before, scn.GetDSForcePileCount());
            assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("lana")));
            assertSame(scn.GetDSStartingLocation(), location(scn, scn.GetDSCard("vader")));
            assertSame(scn.GetDSStartingLocation(), location(scn, scn.GetDSCard("trooper")));
        }
    }

    @Test
    public void transportIsNotOfferedInDeployPhase() {
        var scn = scenario();
        setup(scn, Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("lana"), TRANSPORT));
    }

    private void setup(VirtualTableScenario scn, Phase phase) {
        scn.StartGame();
        for (String key : List.of("battleground", "interior", "exterior"))
            scn.MoveLocationToTable(scn.GetDSCard(key));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),
                scn.GetDSCard("vader"), scn.GetDSCard("trooper"), scn.GetLSCard("rebel"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("elsewhere"));
        scn.MoveCardsToDSHand(scn.GetDSCard("lana"));
        scn.SkipToDSTurn(phase);
    }

    private void select(VirtualTableScenario scn, String destination, boolean both) {
        scn.DSUseCardAction(scn.GetDSCard("lana"), TRANSPORT);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("interior")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        scn.DSChooseCard(scn.GetDSCard(destination));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("rebel")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("elsewhere")));
        if (both)
            scn.DSChooseCards(scn.GetDSCard("vader"), scn.GetDSCard("trooper"));
        else
            scn.DSChooseCard(scn.GetDSCard("vader"));
    }

    // ---- Mode 1: restore a forfeit just reduced to 0 ----

    private static boolean dsHas(VirtualTableScenario scn, PhysicalCard card, String text) {
        try {
            return scn.DSCardActionAvailable((com.gempukku.swccgo.game.PhysicalCardImpl) card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean lsHas(VirtualTableScenario scn, PhysicalCard card, String text) {
        try {
            return scn.LSCardActionAvailable((com.gempukku.swccgo.game.PhysicalCardImpl) card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static final String RESTORE = "forfeit to normal";

    private VirtualTableScenario combatScenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("debnoli", "1210_99", "obiwan", "1_21", "obisaber", "1_157",
                        "yoda", "1114_15", "obijk", "1111_11", "rebel", "1_19")),
                new HashMap<>(Map.of("lana", "1261_44", "lana2", "1261_44", "trooper", "1_194",
                        "vader", "1_168", "vsaber", "1_324")),
                25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private void passUntilDSAction(VirtualTableScenario scn, PhysicalCard card, String text) {
        for (int i = 0; i < 60; i++) {
            if (dsHas(scn, card, text))
                return;
            assertTrue("no pending optional response: " + scn.GetCurrentDecision().getText(),
                    scn.GetCurrentDecision().getText().toLowerCase().contains("optional"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("action never offered: " + text);
    }

    private void lsFireAtTrooper(VirtualTableScenario scn, String shooter) {
        var site = scn.GetLSStartingLocation();
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard(shooter), trooper);
        if (shooter.equals("obiwan"))
            scn.AttachCardsTo(scn.GetLSCard("obiwan"), scn.GetLSCard("obisaber"));
        scn.MoveCardsToDSHand(scn.GetDSCard("lana"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(6);
        scn.PrepareLSDestiny(6);
        var weapon = shooter.equals("obiwan") ? scn.GetLSCard("obisaber") : scn.GetLSCard(shooter);
        scn.LSUseCardAction(weapon, "Fire");
        scn.LSChooseCard(trooper);
    }

    @Test(timeout = 20000)
    public void restoresForfeitForFreeWhenReducedByAPermanentWeapon() {
        var scn = combatScenario();
        lsFireAtTrooper(scn, "debnoli");
        var trooper = scn.GetDSCard("trooper");
        var lana = scn.GetDSCard("lana");
        passUntilDSAction(scn, lana, RESTORE);
        assertTrue(trooper.isHit());
        assertEquals(0, scn.GetForfeit(trooper));
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(lana, RESTORE);
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();
        assertEquals(2, scn.GetForfeit(trooper));
        assertEquals(force, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSUsedPile().contains(lana));
    }

    @Test(timeout = 20000)
    public void restoringForfeitReducedByANonPermanentWeaponCostsOneForce() {
        var scn = combatScenario();
        lsFireAtTrooper(scn, "obiwan");
        var trooper = scn.GetDSCard("trooper");
        var lana = scn.GetDSCard("lana");
        passUntilDSAction(scn, lana, RESTORE);
        assertEquals(0, scn.GetForfeit(trooper));
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(lana, RESTORE);
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();
        assertEquals(2, scn.GetForfeit(trooper));
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }

    @Test(timeout = 20000)
    public void restoreNeedsOneForceUnlessFreeAndIsImmuneToSense() {
        var scn = combatScenario();
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        var trooper = scn.GetDSCard("trooper");
        var lana = scn.GetDSCard("lana");
        scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan"), trooper);
        scn.AttachCardsTo(scn.GetLSCard("obiwan"), scn.GetLSCard("obisaber"));
        scn.MoveCardsToDSHand(lana);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.DrainDSForcePileTo(0);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(6);
        scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(scn.GetLSCard("obisaber"), "Fire");
        scn.LSChooseCard(trooper);
        for (int i = 0; i < 40 && scn.GetCurrentDecision().getText().toLowerCase().contains("optional"); i++) {
            assertFalse(dsHas(scn, lana, RESTORE));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertEquals(0, scn.GetForfeit(trooper));
        assertTrue(scn.GetDSHand().contains(lana));
    }

    // ---- Mode 2: cancel Yoda's / Obi-Wan's attempt to cancel ----

    private static final String CANCEL_ATTEMPT = "attempt to cancel your";

    /** Passes responses and pays Force losses until the predicate holds or no optional window remains. */
    private void drive(VirtualTableScenario scn, java.util.function.BooleanSupplier stop) {
        for (int i = 0; i < 80; i++) {
            if (stop.getAsBoolean())
                return;
            if (scn.DSDecisionAvailable("Choose Force to lose")) {
                scn.DSChooseCard(scn.GetTopOfDSForcePile());
            } else if (scn.LSDecisionAvailable("Choose Force to lose")) {
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
            } else if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            } else {
                return;
            }
        }
    }

    private void transportWithYodaReady(VirtualTableScenario scn) {
        scn.StartGame();
        for (String key : List.of("battleground", "interior", "exterior"))
            scn.MoveLocationToTable(scn.GetDSCard(key));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("yoda"));
        scn.MoveCardsToDSHand(scn.GetDSCard("elis"), scn.GetDSCard("lana"));
        scn.SkipToDSTurn(Phase.MOVE);
        scn.EnsureDSForcePile(10);
        scn.EnsureLSForcePile(10);
        scn.PrepareDSDestiny(2);
        scn.DSUseCardAction(scn.GetDSCard("elis"), TRANSPORT);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.DSChooseCard(scn.GetDSCard("battleground"));
        scn.DSChooseCard(scn.GetDSCard("vader"));
        scn.PassAllResponses();
        scn.DSChooseYes();
        var yoda = scn.GetLSCard("yoda");
        drive(scn, () -> lsHas(scn, yoda, "Cancel"));
        assertTrue("Yoda's cancel not offered", lsHas(scn, yoda, "Cancel"));
        scn.LSUseCardAction(yoda, "Cancel");
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(scn.GetDSCard("elis")))
            scn.LSChooseCard(scn.GetDSCard("elis"));
    }

    private VirtualTableScenario yodaScenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("yoda", "1114_15")),
                new HashMap<>(Map.of("lana", "1261_44", "elis", "1_243", "vader", "1_168", "trooper", "1_194",
                        "battleground", "5_166", "interior", "5_168", "exterior", "7_277")),
                25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test(timeout = 20000)
    public void loseTwoForceToCancelYodasAttemptToCancelYourInterrupt() {
        var scn = yodaScenario();
        transportWithYodaReady(scn);
        var lana = scn.GetDSCard("lana");
        drive(scn, () -> dsHas(scn, lana, CANCEL_ATTEMPT));
        assertTrue(String.join("\n", scn.gameState().getLastMessages()), dsHas(scn, lana, CANCEL_ATTEMPT));
        int dsLost = scn.GetDSLostPileCount();
        scn.DSUseCardAction(lana, CANCEL_ATTEMPT);
        drive(scn, () -> false);
        assertEquals(dsLost + 2, scn.GetDSLostPileCount());
        assertSame(scn.GetDSCard("battleground"), location(scn, scn.GetDSCard("vader")));
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("elis")));
        assertTrue(scn.GetDSUsedPile().contains(lana));
    }

    @Test(timeout = 20000)
    public void withoutTheResponseYodaCancelsTheInterrupt() {
        var scn = yodaScenario();
        transportWithYodaReady(scn);
        drive(scn, () -> false);
        assertSame(scn.GetDSStartingLocation(), location(scn, scn.GetDSCard("vader")));
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("elis")));
        assertTrue(scn.GetDSHand().contains(scn.GetDSCard("lana")));
    }

    private void fireAtLukeWithObiWanReady(VirtualTableScenario scn) {
        var site = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("rebel");
        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke, scn.GetLSCard("obijk"));
        scn.AttachCardsTo(vader, scn.GetDSCard("vsaber"));
        scn.MoveCardsToDSHand(scn.GetDSCard("lana"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(10);
        scn.EnsureLSForcePile(10);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(6);
        scn.DSUseCardAction(scn.GetDSCard("vsaber"), "Fire");
        scn.DSChooseCard(luke);
        var obi = scn.GetLSCard("obijk");
        drive(scn, () -> lsHas(scn, obi, "Cancel opponent's destiny"));
        assertTrue("Obi-Wan's cancel not offered", lsHas(scn, obi, "Cancel opponent's destiny"));
        scn.LSUseCardAction(obi, "Cancel opponent's destiny");
    }

    @Test(timeout = 20000)
    public void loseTwoForceToCancelObiWansAttemptToCancelYourWeaponDestiny() {
        var scn = combatScenario();
        fireAtLukeWithObiWanReady(scn);
        var lana = scn.GetDSCard("lana");
        drive(scn, () -> dsHas(scn, lana, CANCEL_ATTEMPT));
        assertTrue(dsHas(scn, lana, CANCEL_ATTEMPT));
        int dsLost = scn.GetDSLostPileCount();
        scn.DSUseCardAction(lana, CANCEL_ATTEMPT);
        drive(scn, () -> scn.GetLSCard("rebel").isHit());
        assertTrue(String.join("\n", scn.gameState().getLastMessages()), scn.GetLSCard("rebel").isHit());
        assertEquals(dsLost + 2, scn.GetDSLostPileCount());
    }

    @Test(timeout = 20000)
    public void withoutTheResponseObiWanCancelsTheWeaponDestiny() {
        var scn = combatScenario();
        fireAtLukeWithObiWanReady(scn);
        for (int i = 0; i < 80 && scn.GetCurrentDecision().getText().toLowerCase().contains("optional"); i++)
            scn.PlayerPass(scn.GetDecidingPlayer());
        assertFalse(scn.GetLSCard("rebel").isHit());
    }

    private PhysicalCard location(VirtualTableScenario scn, PhysicalCard card) {
        return scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), card);
    }
}
