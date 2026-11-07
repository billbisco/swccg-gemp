package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1261_093_Tests {
    private static final String FROM_RESERVE = "Deploy a character weapon from Reserve Deck";
    private static final String FROM_HAND = "Deploy a character weapon from hand";
    private static final String PROTECT = "Protect Obi-Wan's forfeit";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("combo", "1261_93"); put("obi", "1_21"); put("ref3", "13_33"); put("luke", "1_19");
            put("pistol", "1_154"); put("saber", "1_157"); put("traffic", "1_66"); put("remote", "1_136");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("dsSaber", "1_324"); put("stunning", "2_140"); put("disarmed", "1_214");
        }}, 60, 60, StartingSetup.LSStartingLocation("5_79"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void ArmedComboMatchesBothPrintedUniqueTitles() {
        var scn = scenario(); var b = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Armed And Dangerous & Krayt Dragon Howl", b.getTitle()); assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(List.of(new TitleComponent("Armed And Dangerous", Uniqueness.UNIQUE), new TitleComponent("Krayt Dragon Howl", Uniqueness.UNIQUE)), b.getTitleComponents());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Rarity.V, b.getRarity()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        assertEquals(CardSubtype.USED_OR_LOST, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario started() {
        var scn = scenario(); scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("remote")); scn.MoveCardsToLSHand(scn.GetLSCard("combo")); return scn;
    }

    private void moveCombatants(VirtualTableScenario scn, String obi) {
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard(obi), scn.GetDSCard("vader"));
    }

    private VirtualTableScenario weapons(boolean hand) {
        var scn = started(); moveCombatants(scn, "obi");
        scn.MoveCardsToLocation(scn.GetLSCard("remote"), scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSActivateForceCheat(8);
        if (hand) scn.MoveCardsToLSHand(scn.GetLSCard("pistol"));
        else scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("pistol"));
        scn.SkipToPhase(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSStartingLocation()); return scn;
    }

    private boolean hasAction(VirtualTableScenario scn, String text) {
        return scn.GetLSAvailableActions().stream().anyMatch(label -> label.contains(text));
    }

    private void equip(VirtualTableScenario scn, String source) {
        var weapon = scn.GetLSCard("pistol"); var obi = scn.GetLSCard("obi"); int force = scn.GetLSForcePileCount();
        assertTrue(hasAction(scn, source)); scn.LSPlayCard(scn.GetLSCard("combo"), source); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(weapon)) scn.LSChooseCard(weapon);
        scn.PassAllResponses(); assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke")));
        if (scn.LSHasCardChoiceAvailable(obi)) scn.LSChooseCard(obi);
        scn.PassAllResponses(); assertEquals(obi, weapon.getAttachedTo()); assertEquals(force - 3, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSCard("combo"));
    }

    @Test public void ArmedDeploysCharacterWeaponOnActualParticipantFromReserveAndReshuffles() {
        var scn = weapons(false); var marker = scn.GetLSCard("traffic"); scn.MoveCardsToTopOfLSReserveDeck(marker);
        scn.gameState().markCardsRevealedFromPile(List.of(marker));
        equip(scn, FROM_RESERVE); assertFalse(scn.gameState().isCardRevealedFromPile(marker));
    }
    @Test public void ArmedDeploysFromHandWithoutSearchingReserve() {
        var scn = weapons(true); var marker = scn.GetTopOfLSReserveDeck();
        scn.gameState().markCardsRevealedFromPile(List.of(marker));
        equip(scn, FROM_HAND); assertTrue(scn.gameState().isCardRevealedFromPile(marker));
    }
    @Test public void ArmedWeaponDeploymentWindowClosesAfterTheInitiationResponse() {
        var scn = weapons(true); assertTrue(hasAction(scn, FROM_HAND));
        scn.LSPass(); scn.PassAllResponses(); assertNotNull(scn.gameState().getBattleState());
        assertFalse(hasAction(scn, FROM_HAND)); assertFalse(hasAction(scn, FROM_RESERVE));
    }

    @Test public void ArmedCancelsActualStunningLeaderBeforeItsExclusion() {
        var scn = started(); moveCombatants(scn, "obi"); scn.MoveCardsToDSHand(scn.GetDSCard("stunning"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSActivateForceCheat(8); scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetLSStartingLocation());
        if (scn.GetAwaitingDecision(scn.LS) != null) scn.LSPass();
        scn.DSPlayCard(scn.GetDSCard("stunning"), "Exclude characters");
        for (int i = 0; i < 12 && !hasAction(scn, "Cancel"); i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("response")) break;
            scn.PlayerPass(scn.GetAwaitingDecision(scn.LS) != null ? scn.LS : scn.DS);
        }
        assertTrue("Expected real Stunning Leader cancel window: " + scn.GetCurrentDecision().getText() + " " + scn.GetLSAvailableActions(),
                hasAction(scn, "Cancel"));
        scn.LSPlayCard(scn.GetLSCard("combo"), "Cancel"); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("stunning")); assertInZone(Zone.USED_PILE, scn.GetLSCard("combo"));
        assertTrue(Filters.participatingInBattle.accepts(scn.game(), scn.GetLSCard("obi")));
    }

    private VirtualTableScenario disarmed() {
        var scn = started(); moveCombatants(scn, "obi");
        scn.AttachCardsTo(scn.GetLSCard("obi"), scn.GetLSCard("saber"));
        scn.AttachCardsTo(scn.GetDSCard("vader"), scn.GetDSCard("dsSaber"));
        scn.MoveCardsToDSHand(scn.GetDSCard("disarmed")); scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(scn.GetDSCard("disarmed"));
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("obi"))) scn.DSChooseCard(scn.GetLSCard("obi"));
        return scn;
    }

    @Test public void ArmedCanCancelDisarmedBeforeTheWeaponIsLost() {
        var scn = disarmed(); scn.LSPlayCard(scn.GetLSCard("combo"), "Cancel"); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("disarmed"));
        assertEquals(scn.GetLSCard("obi"), scn.GetLSCard("saber").getAttachedTo());
        assertFalse(Filters.Disarmed.accepts(scn.game(), scn.GetLSCard("obi")));
    }

    @Test public void ArmedCanCancelAnExistingDisarmedCondition() {
        var scn = disarmed(); scn.PassAllResponses();
        assertTrue(Filters.Disarmed.accepts(scn.game(), scn.GetLSCard("obi")));
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSPlayCard(scn.GetLSCard("combo"), "Cancel Disarmed");
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("obi"))) scn.LSChooseCard(scn.GetLSCard("obi"));
        scn.PassAllResponses(); assertFalse(Filters.Disarmed.accepts(scn.game(), scn.GetLSCard("obi")));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("saber"));
    }

    private boolean selectable(VirtualTableScenario scn, String key) {
        int i = scn.LSGetBPChoices().indexOf(scn.GetLSCard(key).getBlueprintId(true));
        return i >= 0 && Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(i));
    }
    @Test public void KraytActuallyUsesOnePlusDeployCostAndReshufflesEligibleObiWan() {
        var scn = started(); scn.SkipToLSTurn(Phase.CONTROL); scn.LSActivateForceCheat(10);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("obi"), scn.GetLSCard("ref3"), scn.GetLSCard("traffic"));
        scn.SkipToPhase(Phase.DEPLOY); int force = scn.GetLSForcePileCount();
        var marker = scn.GetLSCard("traffic"); scn.gameState().markCardsRevealedFromPile(List.of(marker));
        scn.LSPlayCard(scn.GetLSCard("combo"), "Deploy Obi-Wan"); scn.PassAllResponses();
        assertTrue(selectable(scn, "obi")); assertFalse(selectable(scn, "ref3"));
        scn.LSChooseCard(scn.GetLSCard("obi"));
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses(); assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("obi"));
        assertEquals(force - 6, scn.GetLSForcePileCount()); assertFalse(scn.gameState().isCardRevealedFromPile(marker));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("combo"));
    }

    @Test public void KraytActuallyRetrievesEligibleObiWanToUsedForOne() {
        var scn = started(); scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("obi"), scn.GetLSCard("ref3"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSActivateForceCheat(2); scn.LSPass(); scn.DSPass();
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(scn.GetLSCard("combo"), "Retrieve Obi-Wan"); scn.PassAllResponses();
        if (scn.LSGetBPChoices().contains(scn.GetLSCard("obi").getBlueprintId(true))) {
            assertTrue(selectable(scn, "obi")); assertFalse(selectable(scn, "ref3")); scn.LSChooseCard(scn.GetLSCard("obi"));
        }
        scn.PassAllResponses(); assertInZone(Zone.USED_PILE, scn.GetLSCard("obi"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("ref3"), scn.GetLSCard("combo")); assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    private VirtualTableScenario opponentBattle(String obi, boolean alone) {
        var scn = started(); moveCombatants(scn, obi);
        if (!alone) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetDSCard("vader"), scn.GetDSCard("dsSaber")); scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetLSStartingLocation()); return scn;
    }

    private void fireAtObi(VirtualTableScenario scn, boolean protection) {
        var obi = scn.GetLSCard("obi"); var saber = scn.GetDSCard("dsSaber"); int forfeit = scn.GetForfeit(obi);
        assertTrue(hasAction(scn, PROTECT));
        if (protection) {
            scn.LSPlayCard(scn.GetLSCard("combo"), PROTECT);
            if (scn.LSHasCardChoiceAvailable(obi)) scn.LSChooseCard(obi);
        }
        scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(4));
        scn.DSUseCardAction(saber, "Fire");
        if (scn.DSHasCardChoiceAvailable(obi)) scn.DSChooseCard(obi);
        scn.PassAllResponses(); scn.PassWeaponFireWithDestinyDraw(2);
        assertTrue(obi.isHit()); assertEquals(protection ? forfeit : 0, scn.GetForfeit(obi));
    }
    @Test public void KraytProtectsAloneDefenderFromActualLightsaberForfeitReduction() { fireAtObi(opponentBattle("obi", true), true); }
    @Test public void KraytMayDeclineAndActualLightsaberReducesForfeitToZero() { fireAtObi(opponentBattle("obi", true), false); }
    @Test public void KraytCannotProtectReflectionsThreeOrACharacterWhoIsNotAlone() {
        assertFalse(hasAction(opponentBattle("ref3", true), PROTECT));
        assertFalse(hasAction(opponentBattle("obi", false), PROTECT));
    }
    @Test public void KraytCannotProtectAnAttackingObiWan() { assertFalse(hasAction(weapons(true), PROTECT)); }
}
