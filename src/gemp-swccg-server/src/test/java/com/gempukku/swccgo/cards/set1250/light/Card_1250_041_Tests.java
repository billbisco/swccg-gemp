package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_041_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("survivor", "1250_41"); put("guard", "1_26"); put("luke", "1_19");
            put("saber", "1_155"); put("blaster", "1_152"); put("payment", "1_109");
        }}, new HashMap<>() {{
            put("trooper", "1_194"); put("other", "1_194");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalSurvivorHasThreeDotAlienAndMaintenanceCharacteristics() {
        var s = scenario(); var bp = s.GetLSCard("survivor").getBlueprint();
        assertEquals("Jedi Survivor", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals("Female.", bp.getLore()); assertEquals(3, bp.getDestiny(), s.epsilon);
        assertEquals(5, bp.getDeployCost(), s.epsilon); assertEquals(5, bp.getPower(), s.epsilon);
        assertEquals(6, bp.getAbility(), s.epsilon); assertEquals(5, bp.getForfeit(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.CHARACTER, CardType.ALIEN)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.ALIEN, Icon.SPECIAL_EDITION,
                Icon.EPISODE_I, Icon.WARRIOR, Icon.MAINTENANCE, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.FEMALE)));
    }

    @Test
    public void ActualDeploymentBarsBattleOnlyAtThatSiteUntilNextTurn() {
        var s = scenario(); s.StartGame(); var survivor = s.GetLSCard("survivor");
        var enemy = s.GetDSStartingLocation(); var friendly = s.GetLSStartingLocation();
        s.MoveCardsToLocation(enemy, s.GetDSCard("trooper"));
        s.MoveCardsToLocation(friendly, s.GetDSCard("other"), s.GetLSCard("guard"));
        s.MoveCardsToHand(survivor); s.LSActivateForceCheat(9); s.SkipToLSTurn(Phase.DEPLOY);
        int force = s.GetLSForcePileCount(); s.LSDeployCard(survivor);
        s.LSChooseCard(enemy); s.PassAllResponses();
        assertEquals(enemy, survivor.getAtLocation()); assertEquals(force - 5, s.GetLSForcePileCount());
        s.SkipToPhase(Phase.BATTLE);
        assertFalse(s.LSCanInitiateBattle(enemy)); assertTrue(s.LSCanInitiateBattle(friendly));
        toMaintenance(s); s.LSChoose("to maintain"); s.PassAllResponses();
        s.SkipToDSTurn(Phase.BATTLE); assertTrue(s.DSCanInitiateBattle(enemy));
        s.SkipToLSTurn(Phase.BATTLE); assertTrue(s.LSCanInitiateBattle(enemy));
    }

    @Test
    public void BothForceIconsFollowOnlyTheOpposingSiteAndAttritionThresholdIsFive() {
        var s = scenario(); s.StartGame(); var survivor = s.GetLSCard("survivor");
        var enemy = s.GetDSStartingLocation(); var friendly = s.GetLSStartingLocation();
        int enemyLight = s.GetIconCount(enemy, Icon.LIGHT_FORCE);
        int enemyDark = s.GetIconCount(enemy, Icon.DARK_FORCE);
        int ownLight = s.GetIconCount(friendly, Icon.LIGHT_FORCE);
        int ownDark = s.GetIconCount(friendly, Icon.DARK_FORCE);
        s.MoveCardsToLocation(enemy, survivor);
        assertEquals(enemyLight + 1, s.GetIconCount(enemy, Icon.LIGHT_FORCE));
        assertEquals(enemyDark + 1, s.GetIconCount(enemy, Icon.DARK_FORCE));
        assertEquals(5, s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), survivor), s.epsilon);
        s.MoveCardsToLocation(friendly, survivor);
        assertEquals(enemyLight, s.GetIconCount(enemy, Icon.LIGHT_FORCE));
        assertEquals(enemyDark, s.GetIconCount(enemy, Icon.DARK_FORCE));
        assertEquals(ownLight, s.GetIconCount(friendly, Icon.LIGHT_FORCE));
        assertEquals(ownDark, s.GetIconCount(friendly, Icon.DARK_FORCE));
    }

    @Test
    public void ActualLightsaberHitsWithoutForceAndBlasterCostIsUnchanged() {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        var survivor = s.GetLSCard("survivor"); var saber = s.GetLSCard("saber"); var blaster = s.GetLSCard("blaster");
        var target = s.GetDSCard("trooper");
        s.MoveCardsToLocation(site, survivor, target, s.GetDSCard("other"));
        s.AttachCardsTo(survivor, saber, blaster); s.DSActivateForceCheat(4);
        s.SkipToPhase(Phase.BATTLE); s.PrepareLSDestiny(6); s.PrepareLSDestiny(5);
        var query = s.game().getModifiersQuerying();
        assertEquals(1, query.getFireWeaponCost(s.gameState(), blaster, survivor, target, 1), s.epsilon);
        assertEquals(0, s.GetLSForcePileCount());
        s.DSInitiateBattle(site);
        assertTrue(s.LSCardActionAvailable(survivor)); s.LSUseCardAction(survivor);
        if (s.LSHasCardChoiceAvailable(saber)) s.LSChooseCard(saber);
        if (s.LSHasCardChoiceAvailable(target)) s.LSChooseCard(target);
        s.PassDestinyDrawResponses(); s.PassAllResponses();
        assertTrue(target.isHit()); assertEquals(0, s.GetLSForcePileCount());
    }

    private void toMaintenance(VirtualTableScenario s) {
        s.SkipToLSTurn(Phase.DRAW);
        for (int i = 0; i < 25 && !s.LSDecisionAvailable("Choose maintenance option"); i++) {
            String prompt = s.GetCurrentDecision().getText();
            assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                    || prompt.contains("RECIRCULATED"));
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertTrue(s.LSDecisionAvailable("Choose maintenance option"));
    }

    private VirtualTableScenario maintenanceSetup() {
        var s = scenario(); s.StartGame();
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("survivor"));
        s.MoveCardsToHand(s.GetLSCard("payment")); s.LSActivateForceCheat(4);
        toMaintenance(s); return s;
    }

    @Test
    public void MaintainUsesExactlyOneAndSurvivorStaysOnTable() {
        var s = maintenanceSetup(); int before = s.GetLSForcePileCount();
        assertTrue(s.LSChoiceAvailable("Use 1 Force to maintain")); s.LSChoose("to maintain"); s.PassAllResponses();
        assertEquals(before - 1, s.GetLSForcePileCount());
        assertEquals(s.GetLSStartingLocation(), s.GetLSCard("survivor").getAtLocation());
    }

    @Test
    public void RecycleLosesOneAndPutsSurvivorAndAttachmentInUsed() {
        var s = scenario(); s.StartGame(); var survivor = s.GetLSCard("survivor");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), survivor); s.AttachCardsTo(survivor, s.GetLSCard("saber"));
        var payment = s.GetLSCard("payment"); s.MoveCardsToHand(payment); s.LSActivateForceCheat(4); toMaintenance(s);
        assertTrue(s.LSChoiceAvailable("Lose 1 Force to recycle")); s.LSChoose("to recycle");
        s.PassAllResponses(); assertTrue(s.AwaitingLSForceLossPayment()); s.LSChooseCard(payment);
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(payment));
        assertTrue(s.GetLSUsedPile().containsAll(List.of(survivor, s.GetLSCard("saber"))));
    }

    @Test
    public void SacrificePlacesTheSurvivorOutOfPlayWithoutForcePayment() {
        var s = maintenanceSetup(); int before = s.GetLSForcePileCount();
        s.LSChoose("Sacrifice"); s.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, s.GetLSCard("survivor").getZone());
        assertEquals(before, s.GetLSForcePileCount());
    }
}
