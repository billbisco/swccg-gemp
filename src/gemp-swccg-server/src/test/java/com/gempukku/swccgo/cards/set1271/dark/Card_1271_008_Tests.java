package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_008_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("obi", "1_21", "journal", "10_15")),
            new HashMap<>(Map.of("effect", "1271_8")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void ASithsWeaponPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetDSCard("effect").getBlueprint();
        assertEquals("A Sith's Weapon", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(4, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EFFECT));
        s.BlueprintIconCheck(b, List.of(Icon.EFFECT, Icon.EPISODE_I, Icon.SETUP, Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of());
    }

    private VirtualTableScenario board(boolean journalOnTable) {
        var s = scenario();
        s.StartGame();
        var obi = s.GetLSCard("obi");
        var journal = s.GetLSCard("journal");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), obi);
        s.MoveCardsToDSSideOfTable(s.GetDSCard("effect"));
        if (journalOnTable) s.AttachCardsTo(obi, journal);
        else s.MoveCardsToLSHand(journal);
        return s;
    }

    @Test public void ASithsWeaponActuallyPaysItsUsedPileCostToCancelAnAttachedJournal() {
        var s = board(true);
        var effect = s.GetDSCard("effect");
        s.SkipToDSTurn(Phase.CONTROL);
        s.DSUseCardAction(effect, "Cancel Obi-Wan's Journal");
        if (s.DSHasCardChoiceAvailable(s.GetLSCard("journal"))) s.DSChooseCard(s.GetLSCard("journal"));
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("journal")));
        assertTrue(s.GetDSUsedPile().contains(effect));
    }

    @Test public void ASithsWeaponActuallyCancelsAJournalAsItIsDeployed() {
        var s = board(false);
        var effect = s.GetDSCard("effect");
        var journal = s.GetLSCard("journal");
        var obi = s.GetLSCard("obi");
        s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployCard(journal);
        if (s.LSHasCardChoiceAvailable(obi)) s.LSChooseCard(obi);
        if (s.DSGetDecision() == null && s.LSGetDecision() != null) s.LSPass();
        s.DSUseCardAction(effect, "Cancel");
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(journal));
        assertTrue(s.GetDSUsedPile().contains(effect));
    }

    @Test public void ASithsWeaponDoesNotOfferCancellationWhenNoJournalIsInPlay() {
        var s = board(false);
        s.SkipToDSTurn(Phase.CONTROL);
        assertFalse(s.DSCardActionAvailable(s.GetDSCard("effect"), "Cancel Obi-Wan's Journal"));
    }

    // ---- 'Swing' again after failing to 'hit' ----

    private VirtualTableScenario swingScenario() {
        return new VirtualTableScenario(
            new HashMap<>(Map.of("quigon", "11_10", "luke", "601_52", "yoda", "4_2")),
            new HashMap<>(Map.of("effect", "1271_8", "maul", "11_54", "saber", "11_99", "permmaul", "14_77")),
            20, 20,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    /** Puts DS Maul (armed with Maul's Lightsaber) and the given LS character at the DS site, the Effect on table, and starts a DS battle. */
    private VirtualTableScenario swingBoard(String lsTarget, boolean armedMaul, int... destinyTopFirst) {
        var s = swingScenario();
        s.StartGame();
        var site = s.GetDSStartingLocation();
        var target = s.GetLSCard(lsTarget);
        if (armedMaul) {
            var maul = s.GetDSCard("maul");
            s.MoveCardsToLocation(site, maul, target);
            s.AttachCardsTo(maul, s.GetDSCard("saber"));
        } else {
            s.MoveCardsToLocation(site, s.GetDSCard("permmaul"), target);
        }
        s.MoveCardsToDSSideOfTable(s.GetDSCard("effect"));
        s.DSActivateForceCheat(4);
        s.DSStartBattleAndSkipToWeaponsSegment(site);
        for (int i = destinyTopFirst.length - 1; i >= 0; --i) s.PrepareDSDestiny(destinyTopFirst[i]);
        return s;
    }

    private static final String SWING = "'swing'";

    /** Fires the given weapon card's action at the target and passes until the swing-again trigger is offered or the firing is done. */
    private boolean fireAndCheckSwingOffered(VirtualTableScenario s, com.gempukku.swccgo.game.PhysicalCardImpl weaponOrCharacter, com.gempukku.swccgo.game.PhysicalCardImpl target) {
        s.DSUseCardAction(weaponOrCharacter, "Fire");
        if (s.DSHasCardChoiceAvailable(target)) s.DSChooseCard(target);
        return passUntilSwingOrWeaponsSegment(s);
    }

    private static boolean swingOffered(VirtualTableScenario s) {
        if (s.DSGetDecision() == null) return false;
        return s.GetDSAvailableActions().stream().anyMatch(a -> a.contains(SWING));
    }

    private boolean passUntilSwingOrWeaponsSegment(VirtualTableScenario s) {
        for (int i = 0; i < 60; ++i) {
            if (swingOffered(s)) return true;
            if (s.AwaitingDSWeaponsSegmentActions() || s.AwaitingLSWeaponsSegmentActions()) return false;
            var decision = s.GetCurrentDecision();
            if (decision == null) return false;
            s.PlayerPass(s.GetDecidingPlayer());
        }
        return false;
    }

    /** Uses the swing-again trigger and plays out the re-fire, preparing the re-fire's two destiny once the cost is paid. */
    private void swingAgain(VirtualTableScenario s, com.gempukku.swccgo.game.PhysicalCardImpl target, int destiny1, int destiny2) {
        s.DSUseCardAction(s.GetDSCard("effect"), SWING);
        boolean prepared = false;
        for (int i = 0; i < 40; ++i) {
            var decision = s.GetCurrentDecision();
            if (decision == null || s.AwaitingDSWeaponsSegmentActions() || s.AwaitingLSWeaponsSegmentActions()) break;
            if (s.AwaitingDSForceLossPayment()) { s.DSPayForceLossFromReserveDeck(); continue; }
            if (s.DSHasCardChoiceAvailable(target)) {
                assertFalse("only the same target may be swung at", s.DSHasCardChoiceAvailable(s.GetLSCard("yoda")));
                s.DSChooseCard(target);
                continue;
            }
            // Cost paid and target chosen (the only legal target is chosen automatically): stack the re-fire's destiny.
            if (!prepared && decision.getText().startsWith("Fire ")) { s.PrepareDSDestiny(destiny2); s.PrepareDSDestiny(destiny1); prepared = true; }
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertTrue("the lightsaber was fired again", prepared);
    }

    @Test public void ASithsWeaponLetsMaulLoseOneForceToSwingAgainAtAnEpisodeIJediHeFailedToHit() {
        var s = swingBoard("quigon", true, 0, 1);
        var quigon = s.GetLSCard("quigon");
        var yoda = s.GetLSCard("yoda");
        s.MoveCardsToLocation(s.GetDSStartingLocation(), yoda);
        assertTrue(fireAndCheckSwingOffered(s, s.GetDSCard("saber"), quigon));
        assertFalse(quigon.isHit());
        int lostBefore = s.GetDSLostPile().size();
        swingAgain(s, quigon, 6, 7);
        assertTrue(quigon.isHit());
        assertFalse(yoda.isHit());
        assertEquals("lost 1 Force", lostBefore + 1, s.GetDSLostPile().size());
    }

    @Test public void ASithsWeaponIsOncePerTurnSoASecondMissIsNotOfferedAgain() {
        var s = swingBoard("quigon", true, 0, 1);
        var quigon = s.GetLSCard("quigon");
        assertTrue(fireAndCheckSwingOffered(s, s.GetDSCard("saber"), quigon));
        s.DSUseCardAction(s.GetDSCard("effect"), SWING);
        boolean offeredAgain = false;
        boolean prepared = false;
        for (int i = 0; i < 40; ++i) {
            if (swingOffered(s)) { offeredAgain = true; break; }
            var decision = s.GetCurrentDecision();
            if (decision == null || s.AwaitingDSWeaponsSegmentActions() || s.AwaitingLSWeaponsSegmentActions()) break;
            if (s.AwaitingDSForceLossPayment()) { s.DSPayForceLossFromReserveDeck(); continue; }
            if (s.DSHasCardChoiceAvailable(quigon)) { s.DSChooseCard(quigon); continue; }
            if (!prepared && decision.getText().startsWith("Fire ")) { s.PrepareDSDestiny(3); s.PrepareDSDestiny(2); prepared = true; }
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertTrue(prepared);
        assertFalse(quigon.isHit());
        assertFalse(offeredAgain);
    }

    @Test public void ASithsWeaponAlsoWorksAgainstBlock6Luke() {
        var s = swingBoard("luke", true, 0, 1);
        assertTrue(fireAndCheckSwingOffered(s, s.GetDSCard("saber"), s.GetLSCard("luke")));
    }

    @Test public void ASithsWeaponIsNotOfferedAgainstAJediWithoutTheEpisodeIIcon() {
        var s = swingBoard("yoda", true, 0, 1);
        assertFalse(fireAndCheckSwingOffered(s, s.GetDSCard("saber"), s.GetLSCard("yoda")));
    }

    @Test public void ASithsWeaponIsNotOfferedWhenTheLightsaberHits() {
        var s = swingBoard("quigon", true, 6, 7);
        var quigon = s.GetLSCard("quigon");
        assertFalse(fireAndCheckSwingOffered(s, s.GetDSCard("saber"), quigon));
        assertTrue(quigon.isHit());
    }

    @Test public void ASithsWeaponIsNotOfferedWhenTheCharacterHasAbilityFourOrLess() {
        var s = swingBoard("quigon", true, 0, 1);
        var maul = s.GetDSCard("maul");
        s.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.ResetAbilityModifier(maul, maul, 4));
        assertEquals(4, s.game().getModifiersQuerying().getAbility(s.gameState(), maul), s.epsilon);
        assertFalse(fireAndCheckSwingOffered(s, s.GetDSCard("saber"), s.GetLSCard("quigon")));
    }

    @Test public void ASithsWeaponIsNotOfferedForAPermanentWeaponLightsaber() {
        var s = swingBoard("quigon", false, 0, 1);
        assertFalse(fireAndCheckSwingOffered(s, s.GetDSCard("permmaul"), s.GetLSCard("quigon")));
    }
}
