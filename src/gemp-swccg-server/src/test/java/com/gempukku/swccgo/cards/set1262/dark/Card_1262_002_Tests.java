package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBattleModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_002_Tests {
    private static final String DOWNLOAD = "Deploy Rancor Mount from Reserve Deck";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.ofEntries(Map.entry("witch", "1262_2"), Map.entry("witch2", "1262_2"),
                        Map.entry("witch3", "1262_2"), Map.entry("witch4", "1262_2"),
                        Map.entry("mount", "1262_16"), Map.entry("mount2", "1262_16"),
                        Map.entry("wampa", "3_93"), Map.entry("wampa2", "3_93"),
                        Map.entry("rockwart", "7_213"), Map.entry("mynock", "4_110"),
                        Map.entry("bubo", "6_138"), Map.entry("sarlacc", "107_5"),
                        Map.entry("cave", "3_150"), Map.entry("pit", "6_170"),
                        Map.entry("camp", "1_292"), Map.entry("wastes", "1_293"),
                        Map.entry("farm", "1_294"), Map.entry("town", "1_295"),
                        Map.entry("trooper", "1_194"), Map.entry("walker", "3_155"))),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void DathomirWitchStatsIconsAndKeywordsMatchPrintedCard() {
        var scn = scenario();
        var b = scn.GetDSCard("witch").getBlueprint();
        assertEquals("Dathomir Witch", b.getTitle());
        assertEquals("Female.", b.getLore());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.RESTRICTED_3, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(2, b.getDestiny(), scn.epsilon);
        assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(2, b.getPower(), scn.epsilon);
        assertEquals(4, b.getAbility(), scn.epsilon);
        assertEquals(5, b.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REPUBLIC)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REPUBLIC, Icon.WARRIOR, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.FEMALE)));
    }

    private void setForce(VirtualTableScenario scn, int amount) {
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getForcePile(scn.DS).toArray(PhysicalCardImpl[]::new));
        scn.EnsureDSForcePile(amount);
    }

    @Test public void DathomirWitchThirdCopyCanDeployButFourthCannot() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("witch"), scn.GetDSCard("witch2"));
        var third = scn.GetDSCard("witch3");
        var fourth = scn.GetDSCard("witch4");
        scn.MoveCardsToDSHand(third, fourth);
        scn.SkipToDSTurn(Phase.CONTROL);
        setForce(scn, 6);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(third));
        scn.DSPlayCard(third);
        if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
        scn.PassAllResponses();
        if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
        assertEquals(site, third.getAtLocation());
        assertEquals(3, scn.GetDSForcePileCount());
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(fourth));
    }

    @Test public void DathomirWitchCanMoveTwoSitesButNotThree() {
        var scn = scenario();
        var sites = List.of(scn.GetDSStartingLocation(), scn.GetDSCard("camp"), scn.GetDSCard("wastes"),
                scn.GetDSCard("farm"), scn.GetDSCard("town"));
        for (int i = 1; i < sites.size(); i++) scn.MoveLocationToTable(sites.get(i));
        for (var from : sites) {
            var q = scn.game().getModifiersQuerying();
            var to2 = sites.stream().filter(to -> Integer.valueOf(2).equals(q.getDistanceBetweenSites(scn.gameState(), from, to))).findFirst();
            var to3 = sites.stream().filter(to -> Integer.valueOf(3).equals(q.getDistanceBetweenSites(scn.gameState(), from, to))).findFirst();
            if (to2.isEmpty() || to3.isEmpty()) continue;
            var witch = scn.GetDSCard("witch");
            scn.MoveCardsToLocation(from, witch);
            scn.SkipToDSTurn(Phase.MOVE);
            assertEquals(2, scn.GetLandspeed(witch));
            int before = scn.GetDSForcePileCount();
            scn.DSUseCardAction(witch, "Move");
            assertFalse(scn.DSHasCardChoiceAvailable(to3.get()));
            scn.DSChooseCard(to2.get());
            scn.PassAllResponses();
            assertEquals(to2.get(), witch.getAtLocation());
            assertEquals(before - 1, scn.GetDSForcePileCount());
            return;
        }
        fail("Need actual Tatooine sites at distances two and three from the same origin");
    }

    private void battlePower(String siteName, boolean remote, boolean excluded, int expected, String... creatures) {
        var scn = scenario();
        var site = scn.GetDSCard(siteName);
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, scn.GetDSCard("witch"), scn.GetLSCard("luke"));
        for (String name : creatures) {
            var creature = scn.GetDSCard(name);
            scn.MoveCardsToLocation(remote ? scn.GetDSStartingLocation() : site, creature);
            if (excluded) scn.ApplyAdHocModifier(new ExcludedFromBattleModifier(null, creature));
        }
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        assertEquals(expected, scn.GetDSTotalPower());
        assertEquals(3, scn.GetLSTotalPower());
    }

    @Test public void DathomirWitchAddsDoubleOnePresentFerociousCreaturesDefenseToOwnBattlePower() {
        battlePower("cave", false, false, 8, "wampa");
    }
    @Test public void DathomirWitchIncludesOneFerociousCreatureEvenIfExcluded() {
        battlePower("cave", false, true, 8, "wampa");
    }
    @Test public void DathomirWitchNoFerociousCreatureMeansNoBonus() {
        battlePower("cave", false, false, 2);
    }
    @Test public void DathomirWitchTwoFerociousCreaturesHereMeanNoBonus() {
        battlePower("cave", false, false, 2, "wampa", "wampa2");
    }
    @Test public void DathomirWitchCreatureAtDifferentSiteMeansNoBonus() {
        battlePower("cave", true, false, 2, "wampa");
    }
    @Test public void DathomirWitchExcludesParasitesAndPackhunters() {
        battlePower("cave", false, false, 2, "mynock");
        battlePower("cave", false, false, 2, "rockwart");
        battlePower("cave", false, false, 8, "wampa", "mynock", "rockwart");
    }
    @Test public void DathomirWitchExplicitlyExcludesBuboAndSarlacc() {
        battlePower("pit", false, false, 2, "bubo");
        battlePower("pit", false, false, 2, "sarlacc");
    }

    private VirtualTableScenario hunt(boolean withTrooper) {
        var scn = scenario();
        var site = scn.GetDSCard("cave");
        scn.MoveLocationToTable(site);
        var wampa = scn.GetDSCard("wampa");
        var witch = scn.GetDSCard("witch");
        scn.MoveCardsToLocation(site, wampa, witch);
        if (withTrooper) scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.BATTLE);
        return scn;
    }

    @Test public void DathomirWitchIsNotAValidTargetOfRealCreatureAttack() {
        var onlyWitch = hunt(false);
        assertFalse(onlyWitch.DSCardActionAvailable(onlyWitch.GetDSCard("wampa"), "Initiate attack"));
        var scn = hunt(true);
        var wampa = scn.GetDSCard("wampa");
        var witch = scn.GetDSCard("witch");
        var trooper = scn.GetDSCard("trooper");
        assertTrue(scn.DSCardActionAvailable(wampa, "Initiate attack"));
        scn.DSUseCardAction(wampa, "Initiate attack");
        assertFalse(scn.DSHasCardChoiceAvailable(witch));
        if (scn.DSHasCardChoiceAvailable(trooper)) scn.DSChooseCard(trooper);
        scn.PassAllResponses();
        assertTrue(scn.gameState().isParticipatingInAttack(trooper));
        assertFalse(scn.gameState().isParticipatingInAttack(witch));
    }

    private VirtualTableScenario downloadSetup(int force) {
        var scn = scenario();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("witch"));
        scn.MoveLocationToTable(scn.GetDSCard("town"));
        scn.MoveCardsToLocation(scn.GetDSCard("town"), scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.CONTROL);
        setForce(scn, force);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("walker"), scn.GetDSCard("mount"));
        scn.MoveCardsToDSHand(scn.GetDSCard("mount2"));
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void download(VirtualTableScenario scn, PhysicalCardImpl mount) {
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("witch"), DOWNLOAD));
        scn.DSUseCardAction(scn.GetDSCard("witch"), DOWNLOAD);
        assertTrue(scn.DSHasCardChoiceAvailable(mount));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("walker")));
        scn.DSChooseCard(mount);
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("town")));
        if (scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation())) scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertEquals(scn.GetDSStartingLocation(), mount.getAtLocation());
    }

    @Test public void DathomirWitchDeploysOnlyMountHereForNormalCostAndReshuffles() {
        var scn = downloadSetup(3);
        var mount = scn.GetDSCard("mount");
        var original = new ArrayList<>(scn.gameState().getReserveDeck(scn.DS));
        assertTrue(original.remove(mount));
        scn.gameState().setRandomSeed(913L);
        download(scn, mount);
        var shuffled = scn.gameState().getReserveDeck(scn.DS);
        assertEquals(original.size(), shuffled.size());
        assertTrue(shuffled.containsAll(original));
        assertNotEquals(original, new ArrayList<>(shuffled));
        assertEquals(0, scn.GetDSForcePileCount());
    }

    @Test public void DathomirWitchCannotDeployMountWithTwoForce() {
        var scn = downloadSetup(2);
        var mount = scn.GetDSCard("mount");
        if (scn.DSCardActionAvailable(scn.GetDSCard("witch"), DOWNLOAD)) {
            scn.DSUseCardAction(scn.GetDSCard("witch"), DOWNLOAD);
            assertFalse(scn.DSHasCardChoiceAvailable(mount));
        }
        assertEquals(2, scn.GetDSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, mount);
    }

    @Test public void DathomirWitchDownloadIsOncePerTurnAndRenewsOnNextOwnTurn() {
        var scn = downloadSetup(6);
        download(scn, scn.GetDSCard("mount"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("mount2"));
        if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(3, scn.GetDSForcePileCount());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("witch"), DOWNLOAD));
        scn.SkipToDSTurn(Phase.DEPLOY);
        download(scn, scn.GetDSCard("mount2"));
    }

    @Test public void DathomirWitchCannotDownloadInMovePhaseOrFromOffTable() {
        var scn = downloadSetup(3);
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("witch"), DOWNLOAD));
        scn.MoveCardsToDSHand(scn.GetDSCard("witch"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("witch"), DOWNLOAD));
    }
}
