package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_049_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("barrier", "1_105"); put("barrier2", "1_105"); put("reinforcements", "1_106");
            put("signal", "6_77"); put("planners", "1_60"); put("revolution", "1_62");
            put("trooper", "1_28"); put("luke", "1_19"); put("warroom", "1_139");
        }}, new HashMap<>() {{
            put("academy", "1240_49"); put("janus", "9_110"); put("sim", "9_120");
            put("trooper", "1_194"); put("trooper2", "1_194"); put("scum", "8_155");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CaridaAcademyPrintedIdentityBothSidesAndIconsAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("academy").getBlueprint();
        assertEquals("Carida: ISB Training Academy", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        assertEquals(0, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("Once per game, if opponent just played an Interrupt with 'Rebel' in title, may stack it here.", bp.getLocationDarkSideGameText());
        assertEquals("Imperial Council Members are forfeit +2 here. Immune to Revolution.", bp.getLocationLightSideGameText());
        assertEquals(1, bp.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, bp.getIconCount(Icon.DARK_FORCE));
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.DARK_FORCE,
                Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.GRABBER, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
    }

    private VirtualTableScenario Ready(boolean light, Phase phase) {
        var scn = GetScenario();
        scn.StartGame();
        if (light) scn.SkipToLSTurn(Phase.ACTIVATE);
        else scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("academy"));
        scn.EnsureDSForcePile(10);
        scn.EnsureLSForcePile(10);
        scn.MoveCardsToLSHand(scn.GetLSCard("barrier"), scn.GetLSCard("barrier2"));
        scn.SkipToPhase(phase);
        return scn;
    }

    private void Reach(VirtualTableScenario scn, boolean light, PhysicalCardImpl card, String action) {
        for (int i = 0; i < 40; i++) {
            if (light && scn.LSAnyActionsAvailable() && scn.LSCardActionAvailable(card, action)) return;
            if (!light && scn.DSAnyActionsAvailable() && scn.DSCardActionAvailable(card, action)) return;
            assertNotNull(scn.GetCurrentDecision());
            assertTrue(scn.GetCurrentDecision().getText(), scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Expected card action was not offered");
    }

    private void PassWithoutStackOffer(VirtualTableScenario scn) {
        for (int i = 0; i < 40; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional response")) return;
            if (scn.DSAnyActionsAvailable()) assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("academy"), "Stack"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Response windows did not finish");
    }

    private void PlayBarrier(VirtualTableScenario scn, String trooper, String barrier) {
        scn.MoveCardsToDSHand(scn.GetDSCard(trooper));
        scn.DSDeployCard(scn.GetDSCard(trooper));
        scn.DSChooseCard(scn.GetDSCard("academy"));
        Reach(scn, true, scn.GetLSCard(barrier), "Prevent");
        scn.LSPlayCard(scn.GetLSCard(barrier));
    }

    private void StackBarrier(VirtualTableScenario scn, String barrier) {
        Reach(scn, false, scn.GetDSCard("academy"), "Stack");
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(scn.GetDSCard("academy"), "Stack");
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(scn.GetDSCard("academy"), scn.GetLSCard(barrier)));
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void CaridaAcademyAddsTwoForfeitToCouncilMembersHereButNotOtherCharacters() {
        var scn = Ready(false, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetDSCard("academy"), scn.GetDSCard("janus"), scn.GetDSCard("sim"), scn.GetDSCard("trooper"));
        assertEquals(7, scn.GetForfeit(scn.GetDSCard("janus")));
        assertEquals(8, scn.GetForfeit(scn.GetDSCard("sim")));
        assertEquals(2, scn.GetForfeit(scn.GetDSCard("trooper")));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("sim"));
        assertEquals(6, scn.GetForfeit(scn.GetDSCard("sim")));
        scn.MoveCardsToLocation(scn.GetDSCard("academy"), scn.GetDSCard("sim"));
        assertEquals(8, scn.GetForfeit(scn.GetDSCard("sim")));
    }

    @Test
    public void CaridaAcademyBoostedForfeitPaysSevenActualBattleDamageAndAttrition() {
        var scn = Ready(false, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetDSCard("academy"), scn.GetDSCard("janus"), scn.GetLSCard("luke"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetDSCard("academy"));
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(7);
        scn.SkipToDamageSegment(true);
        int damage = scn.GetUnpaidDSBattleDamage();
        assertTrue(damage > 7);
        assertEquals(7, scn.GetUnpaidDSAttrition());
        scn.DSPayBattleDamageFromCardInPlay(scn.GetDSCard("janus"));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("janus"));
        assertEquals(damage - 7, scn.GetUnpaidDSBattleDamage());
        assertEquals(0, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void CaridaAcademyStacksOpponentUsedInterruptWithoutCancelingItsEffect() {
        var scn = Ready(false, Phase.DEPLOY);
        scn.MoveCardsToLocation(scn.GetDSCard("academy"), scn.GetLSCard("trooper"));
        PlayBarrier(scn, "trooper", "barrier");
        StackBarrier(scn, "barrier");
        assertEquals(List.of(scn.GetLSCard("barrier")), scn.GetStackedCards(scn.GetDSCard("academy")));
        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(scn.DSCanInitiateBattle(scn.GetDSCard("academy")));
        assertAtLocation(scn.GetDSCard("academy"), scn.GetDSCard("trooper"));
    }

    @Test
    public void CaridaAcademyMayDeclineAndStillStackALaterMatchingInterrupt() {
        var scn = Ready(false, Phase.DEPLOY);
        PlayBarrier(scn, "trooper", "barrier");
        Reach(scn, false, scn.GetDSCard("academy"), "Stack");
        scn.DSPass();
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetLSCard("barrier"));
        assertTrue(scn.GetStackedCards(scn.GetDSCard("academy")).isEmpty());
        PlayBarrier(scn, "trooper2", "barrier2");
        StackBarrier(scn, "barrier2");
    }

    @Test
    public void CaridaAcademyCannotStackAgainEvenAfterTheTurnChanges() {
        var scn = Ready(false, Phase.DEPLOY);
        PlayBarrier(scn, "trooper", "barrier");
        StackBarrier(scn, "barrier");
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.DEPLOY);
        PlayBarrier(scn, "trooper2", "barrier2");
        PassWithoutStackOffer(scn);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetLSCard("barrier2"));
        assertEquals(List.of(scn.GetLSCard("barrier")), scn.GetStackedCards(scn.GetDSCard("academy")));
    }

    @Test
    public void CaridaAcademyStacksLostInterruptAndItStillDrawsItsDestiny() {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"), scn.GetDSCard("trooper2"));
        scn.MoveCardsToLSHand(scn.GetLSCard("reinforcements"));
        scn.PrepareLSDestiny(0);
        scn.LSPlayCard(scn.GetLSCard("reinforcements"));
        Reach(scn, false, scn.GetDSCard("academy"), "Stack");
        int used = scn.GetLSUsedPileCount();
        int reserve = scn.GetLSReserveDeckCount();
        scn.DSUseCardAction(scn.GetDSCard("academy"), "Stack");
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(scn.GetDSCard("academy"), scn.GetLSCard("reinforcements")));
        assertEquals(reserve - 1, scn.GetLSReserveDeckCount());
        assertEquals(used + 1, scn.GetLSUsedPileCount());
    }

    @Test
    public void CaridaAcademyDoesNotStackOwnersOwnRebelTitledInterrupt() {
        var scn = Ready(false, Phase.CONTROL);
        scn.MoveCardsToLocation(scn.GetDSCard("academy"), scn.GetLSCard("trooper"));
        scn.MoveCardsToDSHand(scn.GetDSCard("scum"));
        scn.DSPlayCard(scn.GetDSCard("scum"), "Cancel a non-unique Rebel's game text");
        scn.DSChooseCard(scn.GetLSCard("trooper"));
        PassWithoutStackOffer(scn);
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(scn.GetDSCard("academy")).isEmpty());
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("scum"));
    }

    @Test
    public void CaridaAcademyDoesNotStackAnInterruptWithoutRebelInItsTitle() {
        var scn = Ready(true, Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSCard("signal"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("planners"));
        scn.LSPlayCard(scn.GetLSCard("signal"), "Take card into hand");
        PassWithoutStackOffer(scn);
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetLSCard("planners"));
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(scn.GetDSCard("academy")).isEmpty());
        assertInZone(Zone.USED_PILE, scn.GetLSCard("signal"));
        assertInZone(Zone.HAND, scn.GetLSCard("planners"));
    }

    @Test
    public void CaridaAcademyDoesNotStackAnEffectWithRebelInItsTitle() {
        var scn = Ready(true, Phase.DEPLOY);
        scn.MoveLocationToTable(scn.GetLSCard("warroom"));
        scn.MoveCardsToLSHand(scn.GetLSCard("planners"));
        scn.LSDeployCard(scn.GetLSCard("planners"));
        scn.LSChooseCard(scn.GetLSCard("warroom"));
        PassWithoutStackOffer(scn);
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(scn.GetDSCard("academy")).isEmpty());
        assertEquals(scn.GetLSCard("warroom"), scn.GetLSCard("planners").getAttachedTo());
    }

    @Test
    public void CaridaAcademyIsNotASelectableRevolutionTargetWhileOrdinarySiteIs() {
        var scn = Ready(true, Phase.DEPLOY);
        scn.MoveCardsToLSHand(scn.GetLSCard("revolution"));
        scn.LSDeployCard(scn.GetLSCard("revolution"));
        String[] ids = scn.GetADParam(scn.LS, "cardId");
        String[] selectable = scn.GetADParam(scn.LS, "selectable");
        assertNotNull(ids);
        assertNotNull(selectable);
        boolean ordinarySelectable = false;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(String.valueOf(scn.GetDSCard("academy").getCardId()))) assertFalse(Boolean.parseBoolean(selectable[i]));
            if (ids[i].equals(String.valueOf(scn.GetLSStartingLocation().getCardId()))) ordinarySelectable = Boolean.parseBoolean(selectable[i]);
        }
        assertTrue(ordinarySelectable);
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("revolution").getAttachedTo());
    }
}
