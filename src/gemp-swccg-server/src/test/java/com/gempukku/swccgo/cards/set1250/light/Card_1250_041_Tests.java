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
        assertEquals(CardCategory.CHARACTER, bp.getCardCategory());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.ALIEN)));
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
        reachBattle(s, s.DS); assertTrue(s.DSCanInitiateBattle(enemy));
        reachBattle(s, s.LS); assertTrue(s.LSCanInitiateBattle(enemy));
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
        if (s.AwaitingDSWeaponsSegmentActions()) s.DSPass();
        assertTrue(s.AwaitingLSWeaponsSegmentActions());
        assertTrue(s.LSCardActionAvailable(saber, "Fire")); s.LSUseCardAction(saber, "Fire");
        s.LSChooseCard(target);
        s.PassWeaponFireWithDestinyDraw(2); s.PassAllResponses();
        assertTrue(target.isHit()); assertEquals(0, s.GetLSForcePileCount());
    }

    private void toMaintenance(VirtualTableScenario s) {
        if (s.GetCurrentPlayer().equals(s.LS)) s.SkipToPhase(Phase.DRAW);
        else s.SkipToLSTurn(Phase.DRAW);
        for (int i = 0; i < 25 && !s.LSDecisionAvailable("Choose maintenance option"); i++) {
            String prompt = s.GetCurrentDecision().getText();
            assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                    || prompt.contains("RECIRCULATED"));
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertTrue(s.LSDecisionAvailable("Choose maintenance option"));
    }

    private void reachBattle(VirtualTableScenario s, String player) {
        for (int i = 0; i < 60; i++) {
            if (s.GetCurrentPlayer().equals(player)
                    && (player.equals(s.LS) ? s.AwaitingLSBattlePhaseActions() : s.AwaitingDSBattlePhaseActions())) return;
            String prompt = s.GetCurrentDecision().getText();
            if (prompt.equals("You have not activated Force. Do you want to Pass?")) {
                s.PlayerChooseYes(s.GetDecidingPlayer());
            } else {
                assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                        || prompt.contains("RECIRCULATED"));
                s.PlayerPass(s.GetDecidingPlayer());
            }
        }
        fail("Expected next battle phase for " + player);
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
        assertTrue(s.LSChoiceAvailable("to recycle")); s.LSChoose("to recycle");
        s.PassAllResponses(); assertTrue(s.AwaitingLSForceLossPayment()); s.LSChooseCard(payment);
        s.PassAllResponses();
        if (s.LSDecisionAvailable("Choose card to put on Used Pile")) s.LSChooseCard(survivor);
        if (s.LSDecisionAvailable("Choose card to put on Used Pile")) s.LSChooseCard(s.GetLSCard("saber"));
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

    private VirtualTableScenario sabaccScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("survivor", "1250_41"); put("wolfman", "1_30"); put("destiny3", "1_47");
            put("sabacc", "6_67"); put("han", "1_11"); put("chamber", "6_81");
        }}, new HashMap<>() {{
            put("ds5a", "200_120"); put("ds5b", "200_120");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    /**
     * Light plays Jabba's Palace Sabacc (stakes: a character weapon or non-unique alien) with Han and loses
     * 7 to 10 while holding the given alien plus a destiny 3 card. Returns the scenario once Dark is asked to
     * collect its winnings.
     */
    private VirtualTableScenario loseSabaccHolding(String alienName) {
        var s = sabaccScenario(); s.StartGame();
        var alien = s.GetLSCard(alienName); var destiny3 = s.GetLSCard("destiny3");
        var chamber = s.GetLSCard("chamber");
        s.MoveLocationToTable(chamber);
        s.MoveCardsToLocation(chamber, s.GetLSCard("han"));
        s.MoveCardsToHand(s.GetLSCard("sabacc"));
        s.MoveCardsToHand(s.GetLSCard(alienName.equals("survivor") ? "wolfman" : "survivor"));
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSCard("ds5a"), s.GetDSCard("ds5b"));
        s.MoveCardsToTopOfLSReserveDeck(alien, destiny3);

        s.DSPass(); s.DSChoose("yes");
        assertTrue(s.AwaitingLSActivatePhaseActions());
        s.LSPlayCard(s.GetLSCard("sabacc"));
        s.LSChooseCard(s.GetLSCard("han"));
        for (int i = 0; i < 30 && !s.LSDecisionAvailable("set sabacc value"); i++) {
            if (s.GetCurrentDecision().getText().contains("draw another")) s.PlayerChooseNo(s.GetDecidingPlayer());
            else s.PlayerPass(s.GetDecidingPlayer());
        }
        // The alien is a clone card (aliens are clone cards); Han is a gambler, so it may be used as a 4
        assertTrue(s.LSHasCardChoiceAvailable(alien));
        assertFalse(s.LSHasCardChoiceAvailable(destiny3));
        s.LSChooseCard(alien);
        assertTrue(s.LSDecisionAvailable("use 4 as the clone card value"));
        s.LSChoose("Yes");
        assertEquals(7, s.GetLSSabaccTotal());
        assertEquals(10, s.GetDSSabaccTotal());
        s.PassAllResponses(); // total calculated
        s.PassAllResponses(); // winner determined
        return s;
    }

    @Test
    public void ActualJabbasPalaceSabaccNeverWinsSurvivorAsStakes() {
        var s = loseSabaccHolding("survivor");
        var survivor = s.GetLSCard("survivor"); var destiny3 = s.GetLSCard("destiny3");
        // Survivor is a non-unique alien but may never be won: no stake is available, so Dark only chooses a card to be lost
        assertTrue(s.DSDecisionAvailable("to be lost"));
        assertTrue(s.DSHasCardChoiceAvailable(survivor));
        assertTrue(s.DSHasCardChoiceAvailable(destiny3));
        s.DSChooseCard(survivor);
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(survivor));
        assertFalse(s.GetDSUsedPile().contains(survivor));
    }

    @Test
    public void ActualJabbasPalaceSabaccStillWinsAnOrdinaryNonUniqueAlien() {
        var s = loseSabaccHolding("wolfman");
        var wolfman = s.GetLSCard("wolfman"); var destiny3 = s.GetLSCard("destiny3");
        // The Wolfman is the only stake, so it is won (placed in the winner's Used Pile) rather than chosen to be lost
        assertFalse(s.DSDecisionAvailable("to be lost"));
        if (s.DSDecisionAvailable("place in your Used Pile")) {
            assertTrue(s.DSHasCardChoiceAvailable(wolfman));
            assertFalse(s.DSHasCardChoiceAvailable(destiny3));
            s.DSChooseCard(wolfman);
        }
        s.PassAllResponses();
        assertTrue(s.GetDSUsedPile().contains(wolfman));
        assertFalse(s.GetLSLostPile().contains(wolfman));
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // The release sheet text is the latest version: the PC card list only spelled out the icons (audit 2026-11-08), so it is no version. GEMP's wording before r16 matched no published version.
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(java.util.Map.of("c", "1250_41")), new java.util.HashMap<>(), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetLSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("When deployed, you may not initiate battle here this turn. While at opponent's site, adds one [Light Side Force] and one [Dark Side Force]. May 'swing' lightsabers for free. May never be won in sabacc. Immune to attrition < 5. End of your turn: [Maintenance] Use 1 or [Recycle] Lose 1 or [Skull].", text);
        org.junit.Assert.assertFalse(text.contains("End of your turn: Use 1 Force"));
        org.junit.Assert.assertNull(scn.GetLSCard("c").getBlueprint().getLegacyVersion());
    }
}
