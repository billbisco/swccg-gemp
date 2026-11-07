package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_1261_105_Tests {
    private static final String TRANSPORT = "'Transport' characters";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "combo", "1261_105", "luke", "1_19", "leia", "1_17", "jungle", "1_137", "interior", "1_128")),
                new HashMap<>(Map.of("dining", "5_168", "vader", "1_168")),
                40, 40, StartingSetup.LSStartingLocation("5_80"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void firstAidUsesTheExactFinalLegacyComboPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Odin Nesloor & First Aid", b.getTitle());
        assertEquals(4, b.getDestiny(), scn.epsilon); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.USED, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario transport(String destination, int destiny, boolean both, boolean fund) {
        var scn = scenario(); var source = scn.GetLSStartingLocation();
        var first = scn.GetLSCard("luke"); var second = scn.GetLSCard("leia");
        var opposing = scn.GetDSCard("vader");
        scn.MoveLocationToTable(scn.GetLSCard("jungle"));
        scn.MoveLocationToTable(scn.GetLSCard("interior"));
        scn.MoveLocationToTable(scn.GetDSCard("dining"));
        scn.MoveCardsToLocation(source, first, second, opposing);
        var combo = scn.GetLSCard("combo");
        scn.MoveCardsToLSHand(combo); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(TRANSPORT)));
        scn.SkipToPhase(Phase.MOVE);
        if (fund) scn.EnsureLSForcePile(5);
        else scn.LSUseForceCheat(scn.GetLSForcePileCount());
        scn.PrepareLSDestiny(destiny);
        scn.LSPlayCard(combo, TRANSPORT);
        if (scn.LSHasCardChoiceAvailable(source)) scn.LSChooseCard(source);
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("dining")));
        scn.LSChooseCard(scn.GetLSCard(destination));
        assertFalse(scn.LSHasCardChoiceAvailable(opposing));
        if (both) scn.LSChooseCards(first, second); else scn.LSChooseCard(first);
        scn.PassAllResponses(); scn.PassDestinyDrawResponses();
        return scn;
    }

    private void resolve(VirtualTableScenario scn, boolean pay) {
        assertTrue(scn.LSDecisionAvailable("transport"));
        if (pay) scn.LSChooseYes(); else scn.LSChooseNo();
        scn.PassForceUseResponses(); scn.PassAllResponses();
    }

    @Test public void paysActualDestinyToTransportBothFromAnInteriorSiteToAnExteriorSite() {
        var scn = transport("jungle", 2, true, true);
        int before = scn.GetLSForcePileCount();
        resolve(scn, true);
        assertEquals(before - 2, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSCard("jungle"), scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("combo")));
    }

    @Test public void zeroDestinyMayTransportOneForFreeToAnInteriorBattleground() {
        var scn = transport("interior", 0, false, true);
        int before = scn.GetLSForcePileCount();
        resolve(scn, true);
        assertEquals(before, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSCard("interior"), scn.GetLSCard("luke"));
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"));
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("combo")));
    }

    @Test public void decliningAnAffordableTransportMakesOnlyTheInterruptLost() {
        var scn = transport("jungle", 2, true, true);
        int before = scn.GetLSForcePileCount();
        resolve(scn, false);
        assertEquals(before, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("combo")));
    }

    @Test public void anUnaffordableTransportMakesTheInterruptLostWithoutMovingAnyone() {
        var scn = transport("jungle", 2, false, false);
        scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("combo")));
    }

    // ---- Mode 1: restore forfeit just reduced to 0 ----
    private static final String RESTORE = "Restore";
    private static final String CANCEL_OPERATE = "Cancel attempt to 'operate'";

    private VirtualTableScenario combat() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "combo", "1261_105", "luke", "1_19", "leia", "1_17")),
                new HashMap<>(Map.of("vaderpw", "108_006", "vader", "1_168", "saber", "1_324",
                        "sense", "1_267", "evazan", "1_172")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    private boolean lsHas(VirtualTableScenario scn, String text) {
        return scn.LSGetDecision() != null && scn.GetLSAvailableActions().stream().anyMatch(x -> x != null && x.contains(text));
    }

    private boolean dsHas(VirtualTableScenario scn, String text) {
        return scn.DSGetDecision() != null && scn.GetDSAvailableActions().stream().anyMatch(x -> x != null && x.contains(text));
    }

    /** Passes response windows until the condition holds (or no more response windows remain). */
    private void passUntil(VirtualTableScenario scn, java.util.function.BooleanSupplier ready) {
        for (int i = 0; i < 40 && !ready.getAsBoolean(); i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) return;
            String text = decision.getText().toLowerCase();
            if (!text.contains("response") && !text.contains("optional") && !text.contains("required")) return;
            scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
        }
    }

    /** DS battles at the LS site with the given shooter and fires the given weapon at Leia (destiny 6 + 6 hits). */
    private VirtualTableScenario hitLeia(boolean permanentWeapon, int lsForce, boolean withEvazan) {
        var scn = combat();
        var site = scn.GetLSStartingLocation();
        var leia = scn.GetLSCard("leia");
        var shooter = scn.GetDSCard(permanentWeapon ? "vaderpw" : "vader");
        scn.MoveCardsToLocation(site, leia, scn.GetLSCard("luke"), shooter);
        if (withEvazan) scn.MoveCardsToLocation(site, scn.GetDSCard("evazan"));
        if (!permanentWeapon) scn.AttachCardsTo(shooter, scn.GetDSCard("saber"));
        scn.MoveCardsToLSHand(scn.GetLSCard("combo"));
        scn.MoveCardsToDSHand(scn.GetDSCard("sense"));
        scn.SkipToDSTurn(Phase.BATTLE);
        assertFalse("No forfeit has been reduced yet", lsHas(scn, RESTORE));
        scn.EnsureDSForcePile(6);
        if (lsForce > scn.GetLSForcePileCount()) scn.EnsureLSForcePile(lsForce);
        else scn.LSUseForceCheat(scn.GetLSForcePileCount() - lsForce);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(6);
        scn.PrepareDSDestiny(6);
        var weapon = permanentWeapon ? shooter : scn.GetDSCard("saber");
        assertTrue(scn.GetDSAvailableActions().toString(), scn.DSCardActionAvailable(weapon, "Fire"));
        scn.DSUseCardAction(weapon, "Fire");
        if (scn.DSHasCardChoiceAvailable(leia)) scn.DSChooseCard(leia);
        return scn;
    }

    private void restoreLeia(VirtualTableScenario scn) {
        var leia = scn.GetLSCard("leia");
        passUntil(scn, () -> lsHas(scn, RESTORE));
        assertTrue("Leia was hit: " + leia.isHit(), leia.isHit());
        assertEquals(0, scn.GetForfeit(leia), scn.epsilon);
        assertTrue(scn.GetLSAvailableActions().toString(), scn.LSCardPlayAvailable(scn.GetLSCard("combo"), RESTORE));
        scn.LSPlayCard(scn.GetLSCard("combo"), RESTORE);
        if (scn.LSHasCardChoiceAvailable(leia)) scn.LSChooseCard(leia);
    }

    private boolean dsSenseOffered(VirtualTableScenario scn) {
        for (int i = 0; i < 12; i++) {
            if (scn.DSGetDecision() != null && scn.DSCardPlayAvailable(scn.GetDSCard("sense"))) return true;
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("response")) return false;
            scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
        }
        return false;
    }

    @Test public void permanentWeaponForfeitReductionIsRestoredForFreeAndImmuneToSense() {
        var scn = hitLeia(true, 3, false);
        var leia = scn.GetLSCard("leia");
        restoreLeia(scn);
        int force = scn.GetLSForcePileCount();
        assertFalse("Restore mode is immune to Sense", dsSenseOffered(scn));
        scn.PassAllResponses();
        assertEquals("free when reduced by a [Permanent Weapon] weapon", force, scn.GetLSForcePileCount());
        assertEquals(leia.getBlueprint().getForfeit(), scn.GetForfeit(leia), scn.epsilon);
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("combo")));
    }

    @Test public void weaponCardForfeitReductionCostsOneForceToRestore() {
        var scn = hitLeia(false, 3, false);
        var leia = scn.GetLSCard("leia");
        int force = scn.GetLSForcePileCount();
        restoreLeia(scn);
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertEquals(leia.getBlueprint().getForfeit(), scn.GetForfeit(leia), scn.epsilon);
    }

    @Test public void weaponCardForfeitReductionIsNotRestorableWithoutForce() {
        var scn = hitLeia(false, 0, false);
        var leia = scn.GetLSCard("leia");
        passUntil(scn, () -> lsHas(scn, RESTORE));
        assertTrue(leia.isHit());
        assertEquals(0, scn.GetForfeit(leia), scn.epsilon);
        assertFalse(lsHas(scn, RESTORE));
    }

    @Test public void permanentWeaponForfeitReductionIsRestorableWithoutForce() {
        var scn = hitLeia(true, 0, false);
        restoreLeia(scn);
        scn.PassAllResponses();
        assertEquals(scn.GetLSCard("leia").getBlueprint().getForfeit(), scn.GetForfeit(scn.GetLSCard("leia")), scn.epsilon);
    }

    // ---- Mode 2: cancel an attempt to 'operate' ----

    private boolean operable(VirtualTableScenario scn, String cardKey) {
        return scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), scn.GetLSCard(cardKey), scn.GetDSCard("evazan"),
                Set.of(TargetingReason.TO_BE_OPERATED_ON));
    }

    private VirtualTableScenario evazanOperatesOnLeia() {
        var scn = hitLeia(true, 3, true);
        passUntil(scn, () -> dsHas(scn, "'Operate'"));
        assertTrue(scn.GetDSAvailableActions().toString(), dsHas(scn, "'Operate'"));
        scn.DSUseCardAction(scn.GetDSCard("evazan"), "'Operate'");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("leia"))) scn.DSChooseCard(scn.GetLSCard("leia"));
        return scn;
    }

    @Test public void cancelsEvazansAttemptToOperateAndProtectsOnlyThatCharacter() {
        var scn = evazanOperatesOnLeia();
        var leia = scn.GetLSCard("leia");
        passUntil(scn, () -> lsHas(scn, CANCEL_OPERATE));
        assertTrue(scn.GetLSAvailableActions().toString(), scn.LSCardPlayAvailable(scn.GetLSCard("combo"), CANCEL_OPERATE));
        assertTrue(operable(scn, "leia"));
        scn.LSPlayCard(scn.GetLSCard("combo"), CANCEL_OPERATE);
        scn.PassAllResponses();
        assertTrue("The 'patient' is not lost", leia.getZone().isInPlay());
        assertFalse("Protected from further attempts", operable(scn, "leia"));
        assertTrue("Only the patient is protected", operable(scn, "luke"));
        // Protection is specific to 'operate': Leia may still be targeted for other reasons.
        assertTrue(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), leia, scn.GetDSCard("evazan"),
                Set.of(TargetingReason.TO_BE_LOST)));
    }

    @Test public void evazansOperateLosesThePatientWhenNotCanceled() {
        var scn = evazanOperatesOnLeia();
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("leia")));
    }

    @Test public void cancelOperateIsNotOfferedOutsideAnOperateAttempt() {
        var scn = hitLeia(true, 3, false);
        for (int i = 0; i < 40; i++) {
            assertFalse(lsHas(scn, CANCEL_OPERATE));
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("response")) break;
            scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
        }
    }
}
