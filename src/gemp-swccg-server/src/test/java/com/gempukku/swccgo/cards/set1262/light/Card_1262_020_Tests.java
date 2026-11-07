package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Proxy;
import java.util.function.BooleanSupplier;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1262_020_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("rider", "1262_20"); put("kaadu", "1262_26"); put("oldKaadu", "14_61"); put("speeder", "3_69");
            put("scurrier", "7_52"); put("nudj", "4_5"); put("nudj2", "4_5"); put("trooper", "1_28"); put("dock", "1_136"); put("swamp", "4_87");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("trooper", "1_194"); put("rockwart", "7_213"); put("snake", "4_109"); put("sleen", "4_111");
            put("mynock", "4_110"); put("vine", "4_113");
        }}, 60, 60, StartingSetup.LSStartingLocation("1_137"), StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void BeastRiderStatsMatchPrintedHyphenAndVirtualSuffix() {
        var scn = scenario(); var b = scn.GetLSCard("rider").getBlueprint();
        assertEquals("Beast-Rider", b.getTitle()); assertTrue(b.hasVirtualSuffix()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.RESTRICTED_3, b.getUniqueness()); assertNull(b.getCardSubtype());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(2, b.getPower(), scn.epsilon); assertEquals(4, b.getAbility(), scn.epsilon); assertEquals(5, b.getForfeit(), scn.epsilon);
        assertEquals("Female.", b.getLore()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.REPUBLIC)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.REPUBLIC, Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.FEMALE)));
    }

    private VirtualTableScenario start() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("dock"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("rider")); return scn;
    }

    @Test public void BeastRiderHasLandspeedTwoAndActuallyMovesBetweenSites() {
        var scn = start(); scn.SkipToLSTurn(Phase.MOVE); scn.LSActivateForceCheat(3);
        assertEquals(2, scn.game().getModifiersQuerying().getLandspeed(scn.gameState(), scn.GetLSCard("rider")), scn.epsilon);
        scn.LSMoveCard(scn.GetLSCard("rider"), scn.GetLSCard("dock")); scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard("dock"), scn.GetLSCard("rider"));
    }

    private void power(String creature, boolean ownCreature, boolean remote, boolean two, int expected) {
        var scn = start(); var site = scn.GetLSStartingLocation(); scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE);
        if (creature != null) scn.MoveCardsToLocation(remote ? scn.GetLSCard("dock") : site,
                ownCreature ? scn.GetLSCard(creature) : scn.GetDSCard(creature));
        if (two) scn.MoveCardsToLocation(site, scn.GetLSCard("nudj2"));
        assertEquals(2, scn.GetPower(scn.GetLSCard("rider")));
        scn.LSInitiateBattle(site); scn.PassAllResponses(); assertEquals(expected, scn.GetPower(scn.GetLSCard("rider")));
        scn.SkipToEndOfPowerSegment(false); assertEquals(expected, scn.GetLSTotalPower());
    }
    @Test public void BeastRiderAddsNoPowerWithoutCreature() { power(null, false, false, false, 2); }
    @Test public void BeastRiderCountsOwnFerociousNudjDespiteDefenseOne() { power("nudj", true, false, false, 6); }
    @Test public void BeastRiderCountsEachNonParasiteCreatureSeparately() { power("nudj", true, false, true, 10); }
    @Test public void BeastRiderCountsOpposingSleenAsFerociousDespiteDefenseTwo() { power("sleen", false, false, false, 6); }
    @Test public void BeastRiderCountsOpposingDefenseThreeCreatureAsFourPower() { power("snake", false, false, false, 6); }
    @Test public void BeastRiderDoesNotCountCreatureAtAnotherSite() { power("sleen", false, true, false, 2); }
    @Test public void BeastRiderDoesNotCountMynockParasiteEvenAtDefenseThree() { power("mynock", false, false, false, 2); }
    @Test public void BeastRiderDoesNotCountVineSnakeParasiteEvenAtDefenseThree() { power("vine", false, false, false, 2); }

    private boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var blueprints = scn.LSGetADParam("blueprintId"); var enabled = scn.LSGetADParam("selectable");
        if (blueprints == null || enabled == null) return scn.LSHasCardChoiceAvailable(card);
        for (int i = 0; i < blueprints.length; i++)
            if (blueprints[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(enabled[i])) return true;
        return false;
    }

    private VirtualTableScenario download(Phase phase, boolean opponentsTurn, int force) {
        var scn = start();
        if (opponentsTurn) { scn.SkipToDSTurn(phase); scn.DSPass(); }
        else scn.SkipToLSTurn(phase);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("kaadu"), scn.GetLSCard("oldKaadu"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSForcePile().toArray(PhysicalCardImpl[]::new));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSFillerRange(force + 3));
        scn.LSActivateForceCheat(force); assertEquals(force, scn.GetLSForcePileCount());
        scn.LSUseCardAction(scn.GetLSCard("rider"), "Deploy Kaadu here"); scn.PassAllResponses(); return scn;
    }

    private void deployChosen(VirtualTableScenario scn, String card) {
        assertTrue(selectable(scn, scn.GetLSCard(card))); scn.LSChooseCard(scn.GetLSCard(card)); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) {
            scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        }
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard(card));
    }
    @Test public void BeastRiderDownloadsLegacyKaaduHereForNormalOneForce() {
        var scn = download(Phase.CONTROL, false, 1); var messages = recordMessages(scn);
        assertFalse(selectable(scn, scn.GetLSCard("speeder")));
        deployChosen(scn, "kaadu"); assertEquals(0, scn.GetLSForcePileCount());
        assertEquals(1, messages.stream().filter(m -> m.startsWith(scn.LS + " shuffles Reserve Deck")).count());
    }
    @Test public void BeastRiderMayDownloadOriginalKaaduInstead() {
        var scn = download(Phase.DEPLOY, false, 1); deployChosen(scn, "oldKaadu"); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void BeastRiderMayDownloadDuringOpponentsMovePhase() {
        var scn = download(Phase.MOVE, true, 1); deployChosen(scn, "kaadu"); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void BeastRiderDoesNotDownloadUnaffordableKaadu() {
        var scn = download(Phase.CONTROL, false, 0); assertFalse(selectable(scn, scn.GetLSCard("kaadu")));
        assertFalse(selectable(scn, scn.GetLSCard("oldKaadu"))); assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("kaadu"));
    }
    @Test public void BeastRiderDownloadLimitIsPerTurnAndResetsNextTurn() {
        var scn = download(Phase.CONTROL, false, 2); deployChosen(scn, "kaadu");
        scn.DSPass(); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("rider"), "Deploy Kaadu here"));
        scn.SkipToDSTurn(Phase.ACTIVATE); scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("rider"), "Deploy Kaadu here"));
    }

    private boolean retrievalAvailable(VirtualTableScenario scn) {
        return scn.LSGetADParam("actionText") != null && scn.LSCardActionAvailable(scn.GetLSCard("rider"), "Retrieve up to 4");
    }
    private boolean reach(VirtualTableScenario scn, BooleanSupplier goal) {
        for (int i = 0; i < 80; i++) {
            if (goal.getAsBoolean()) return true;
            var decision = scn.GetCurrentDecision(); if (decision == null) return false;
            String text = decision.getText().toLowerCase();
            if (text.contains("optional response") || text.contains("weapons segment") || text.contains("power segment"))
                scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
            else return false;
        }
        return goal.getAsBoolean();
    }

    private VirtualTableScenario eaten(boolean sameSite, boolean ownVictim) {
        var scn = start(); var site = scn.GetLSStartingLocation();
        if (!sameSite) { site = scn.GetLSCard("swamp"); scn.MoveLocationToTable(site); }
        scn.MoveCardsToLocation(site, ownVictim ? scn.GetLSCard("trooper") : scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.DEPLOY); scn.MoveCardsToLocation(site, scn.GetDSCard("snake"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSFillerRange(5)); scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(scn.GetDSCard("snake"), "Initiate attack"); return scn;
    }
    private void retrieve(int amount) {
        var scn = eaten(true, false); assertTrue(reach(scn, () -> retrievalAvailable(scn)));
        int lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(scn.GetLSCard("rider"), "Retrieve up to 4"); scn.LSDecided(amount); scn.PassAllResponses();
        assertEquals(lost - amount, scn.GetLSLostPileCount());
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("trooper"));
    }
    @Test public void BeastRiderRetrievesFourWhenOpponentsCardIsActuallyEatenHere() { retrieve(4); }
    @Test public void BeastRiderMayChooseLessThanFourRetrieval() { retrieve(2); }
    @Test public void BeastRiderMayChooseZeroRetrieval() { retrieve(0); }
    @Test public void BeastRiderMayDeclineEatenCardRetrieval() {
        var scn = eaten(true, false); assertTrue(reach(scn, () -> retrievalAvailable(scn))); int lost = scn.GetLSLostPileCount();
        scn.LSPass(); scn.PassAllResponses(); assertEquals(lost, scn.GetLSLostPileCount());
    }
    @Test public void BeastRiderDoesNotRetrieveWhenOwnCardIsEaten() {
        var scn = eaten(true, true); assertFalse(reach(scn, () -> retrievalAvailable(scn)));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("trooper")); assertEquals(6, scn.GetLSLostPileCount());
    }
    @Test public void BeastRiderDoesNotRetrieveWhenOpponentsCardIsEatenElsewhere() {
        var scn = eaten(false, false); assertFalse(reach(scn, () -> retrievalAvailable(scn)));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("trooper")); assertEquals(5, scn.GetLSLostPileCount());
    }
    private List<String> recordMessages(VirtualTableScenario scn) {
        List<String> messages = new ArrayList<>();
        GameStateListener listener = (GameStateListener) Proxy.newProxyInstance(GameStateListener.class.getClassLoader(),
                new Class<?>[]{GameStateListener.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "sendMessage": messages.add((String) args[0]); return null;
                        case "hashCode": return System.identityHashCode(proxy);
                        case "equals": return proxy == args[0];
                        case "toString": return "message recorder";
                    }
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == int.class) return 0;
                    if (method.getReturnType() == long.class) return 0L;
                    return null;
                });
        scn.game().addGameStateListener(scn.LS, listener); messages.clear(); return messages;
    }

    @Test public void BeastRiderCannotBeCreatureAttackTarget() {
        var scn = start(); scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("snake"));
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("snake"), "Initiate attack"));
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("rider"));
    }

    @Test public void BeastRiderDownloadDoesNotOfferAnotherLegalSite() {
        var scn = download(Phase.CONTROL, false, 1); scn.LSChooseCard(scn.GetLSCard("kaadu")); scn.PassAllResponses();
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("dock")));
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) {
            scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        }
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("kaadu"));
    }

    @Test public void BeastRiderDownloadResetsOnImmediatelyFollowingOpponentTurn() {
        var scn = download(Phase.CONTROL, false, 2); deployChosen(scn, "kaadu");
        scn.DSPass(); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("rider"), "Deploy Kaadu here"));
        scn.SkipToDSTurn(Phase.MOVE); scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("rider"), "Deploy Kaadu here"));
        scn.LSUseCardAction(scn.GetLSCard("rider"), "Deploy Kaadu here"); scn.PassAllResponses(); deployChosen(scn, "oldKaadu");
    }

    @Test public void BeastRiderDoesNotSearchAnEmptyReserveDeck() {
        var scn = start(); scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSReserveDeck().toArray(PhysicalCardImpl[]::new));
        assertEquals(0, scn.GetLSReserveDeckCount()); scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("rider"), "Deploy Kaadu here"));
    }

    @Test public void BeastRiderCountsSelectiveLandspeedTwoScurrierAsPackhunterInsteadOfFerocious() {
        power("scurrier", true, false, false, 4);
    }
    @Test public void BeastRiderCountsOpposingSelectiveLandspeedTwoRockWartAsPackhunter() {
        power("rockwart", false, false, false, 4);
    }

}
