package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_065_Tests {
    private static final String DEPLOY = "Deploy up to two clones";
    private static final String RETRIEVE = "Retrieve any one card into hand";
    private static final String DESTINY = "Add one battle destiny";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("ambush", "1250_65"); put("clone1", "1116_8"); put("clone2", "1116_9");
            put("clone3", "200_6"); put("nonClone", "1_28"); put("luke", "1_19");
            put("forest", "8_74"); put("dense", "8_72"); put("door", "8_69"); put("hut", "8_71");
            put("dock", "12_74"); put("club", "1116_30"); put("council", "1116_29");
            put("older", "1_60"); put("newer", "1_5");
        }}, new HashMap<>() {{
            put("trooper", "1_194"); put("clone", "1116_43");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void AmbushMetadataMatchesPrintedLostInterrupt() {
        var s = scenario(); var bp = s.GetLSCard("ambush").getBlueprint();
        assertEquals(Title.Ambush, bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(3, bp.getDestiny(), s.epsilon);
        assertEquals(CardSubtype.LOST, bp.getCardSubtype()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals("\"Well done. Hold them in the security tower, and keep it quiet. Move.\"", bp.getLore());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.CLOUD_CITY, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    private VirtualTableScenario download(Phase phase, boolean darkTurn, int force) {
        var s = scenario(); s.StartGame(); s.MoveCardsToHand(s.GetLSCard("ambush"));
        reachPhaseWithForce(s, darkTurn, phase, force, () ->
                s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("clone1"), s.GetLSCard("clone2"),
                        s.GetLSCard("clone3"), s.GetLSCard("nonClone")));
        return s;
    }

    private void reachPhaseWithForce(VirtualTableScenario s, boolean darkTurn, Phase phase, int force, Runnable setup) {
        if (darkTurn) s.SkipToDSTurn(Phase.ACTIVATE); else s.SkipToLSTurn(Phase.ACTIVATE);
        leaveForce(s, force);
        setup.run();
        // Set the Force fixture before the tested action decision is constructed.
        for (int i = 0; i < 20 && s.GetCurrentPhase() == Phase.ACTIVATE; i++) {
            String prompt = s.GetCurrentDecision().getText();
            if (prompt.equals("You have not activated Force. Do you want to Pass?")) {
                if (s.DSDecisionAvailable(prompt)) s.DSChooseYes(); else s.LSChooseYes();
            } else {
                assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses"));
                if (s.DSDecisionAvailable(prompt)) s.DSPass(); else s.LSPass();
            }
        }
        assertEquals(Phase.CONTROL, s.GetCurrentPhase());
        s.SkipToPhase(phase);
        if (darkTurn && s.DSAnyDecisionsAvailable()) s.DSPass();
        assertEquals(force, s.GetLSForcePileCount());
    }

    private void leaveForce(VirtualTableScenario s, int force) {
        int current = s.GetLSForcePileCount();
        if (current > force) s.UseForceCheat(s.LS, current - force);
        if (current < force) s.LSActivateForceCheat(force - current);
        assertEquals(force, s.GetLSForcePileCount());
    }

    private boolean mode(VirtualTableScenario s, String action) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("ambush"), action);
    }

    private List<String> recordMessages(VirtualTableScenario s) {
        List<String> messages = new ArrayList<>();
        GameStateListener listener = (GameStateListener) Proxy.newProxyInstance(GameStateListener.class.getClassLoader(),
                new Class<?>[]{GameStateListener.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "sendMessage": messages.add((String) args[0]); return null;
                        case "hashCode": return System.identityHashCode(proxy);
                        case "equals": return proxy == args[0];
                        case "toString": return "Ambush message recorder";
                    }
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                });
        s.game().addGameStateListener(s.LS, listener); messages.clear(); return messages;
    }

    private long shuffles(VirtualTableScenario s, List<String> messages) {
        return messages.stream().filter(m -> m.startsWith(s.LS + " shuffles Reserve Deck")).count();
    }

    private void play(VirtualTableScenario s, String action) {
        assertTrue("Expected Ambush mode: " + action, mode(s, action));
        s.LSPlayCard(s.GetLSCard("ambush"), action); s.PassAllResponses();
    }

    private void chooseClone(VirtualTableScenario s, String clone) {
        assertTrue(s.LSDecisionAvailable("to deploy from Reserve Deck"));
        int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard(clone).getBlueprintId(true));
        assertTrue("Missing " + clone + " in the Reserve Deck choices", index >= 0);
        assertEquals("true", s.LSGetADParam("selectable")[index]);
        s.LSChooseCard(s.GetLSCard(clone)); s.PassAllResponses();
        if (s.LSAnyDecisionsAvailable() && s.LSHasCardChoicesAvailable(s.GetLSStartingLocation()))
            s.LSChooseCard(s.GetLSStartingLocation());
        s.PassAllResponses();
        assertEquals("Deploying " + clone + "; zone=" + s.GetLSCard(clone).getZone()
                        + "; decision=" + s.GetCurrentDecision().getText(), s.GetLSStartingLocation(),
                s.game().getModifiersQuerying().getLocationThatCardIsAt(s.gameState(), s.GetLSCard(clone)));
    }

    @Test
    public void AmbushDownloadsExactlyTwoClonesAtNormalCostsAndReshuffles() {
        var s = download(Phase.DEPLOY, false, 7); var messages = recordMessages(s);
        play(s, DEPLOY);
        int other = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard("nonClone").getBlueprintId(true));
        assertTrue(other >= 0); assertEquals("false", s.LSGetADParam("selectable")[other]);
        chooseClone(s, "clone1"); chooseClone(s, "clone2");
        assertFalse(s.LSDecisionAvailable("to deploy from Reserve Deck"));
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("clone3")));
        assertEquals(2, s.GetLSForcePileCount());
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("ambush")));
        assertEquals(1, shuffles(s, messages));
    }

    @Test
    public void AmbushMayDeployOnlyOneEvenWhenSecondIsAffordable() {
        var s = download(Phase.DEPLOY, false, 5); var messages = recordMessages(s);
        play(s, DEPLOY); chooseClone(s, "clone1");
        assertTrue(s.LSDecisionAvailable("to deploy from Reserve Deck")); assertEquals(0, s.LSGetChoiceMin());
        s.LSChooseCards(); s.PassAllResponses();
        assertEquals(2, s.GetLSForcePileCount());
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("clone2")));
        assertEquals(1, shuffles(s, messages));
    }

    @Test
    public void AmbushDownloadUnavailableWithZeroForce() {
        var s = download(Phase.DEPLOY, false, 0); assertFalse(mode(s, DEPLOY));
    }

    @Test
    public void AmbushOneForceSearchDoesNotMakeUnaffordableCloneFree() {
        var s = download(Phase.DEPLOY, false, 1);
        play(s, DEPLOY);
        if (s.LSDecisionAvailable("to deploy from Reserve Deck")) {
            int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard("clone1").getBlueprintId(true));
            assertTrue(index >= 0); assertEquals("false", s.LSGetADParam("selectable")[index]);
            s.LSChooseCards(); s.PassAllResponses();
        }
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("clone1")));
        assertEquals(0, s.GetLSForcePileCount());
    }

    @Test
    public void AmbushCanDownloadDuringOpponentsControlPhase() {
        var s = download(Phase.CONTROL, true, 3);
        play(s, DEPLOY); chooseClone(s, "clone1");
        assertEquals(0, s.GetLSForcePileCount());
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("clone2")));
    }

    private VirtualTableScenario retrieval(String third, boolean coruscant, boolean darkTurn, int force, boolean withLost) {
        var s = scenario(); s.StartGame(); s.MoveCardsToHand(s.GetLSCard("ambush"));
        String[] sites = {coruscant ? "dock" : "forest", coruscant ? "club" : "dense", third};
        for (int i = 0; i < sites.length; i++) {
            PhysicalCardImpl location = sites[i].equals("elsewhere") ? s.GetLSStartingLocation() : s.GetLSCard(sites[i]);
            if (location.getZone() != Zone.LOCATIONS) s.MoveLocationToTable(location);
            s.MoveCardsToLocation(location, s.GetLSCard("clone" + (i + 1)));
        }
        if (third.equals("door")) s.MoveCardsToLocation(s.GetLSCard("door"), s.GetDSCard("trooper"));
        if (withLost) s.MoveCardsToTopOfLSLostPile(s.GetLSCard("older"), s.GetLSCard("newer"));
        reachPhaseWithForce(s, darkTurn, Phase.CONTROL, force, () -> {});
        return s;
    }

    private void chooseOlder(VirtualTableScenario s, int expectedForce) {
        play(s, RETRIEVE);
        assertTrue(s.LSHasCardChoicesAvailable(s.GetLSCard("older"), s.GetLSCard("newer")));
        s.LSChooseCard(s.GetLSCard("older")); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(s.GetLSCard("older")));
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("newer")));
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("ambush")));
        assertEquals(expectedForce, s.GetLSForcePileCount());
    }

    @Test
    public void AmbushRetrievesEitherLostCardForTwoForceEvenWhenBattlegroundIsContested() {
        var s = retrieval("door", false, false, 2, true);
        assertTrue(Filters.occupies(s.LS).accepts(s.gameState(), s.game().getModifiersQuerying(), s.GetLSCard("door")));
        assertFalse(Filters.controls(s.LS).accepts(s.gameState(), s.game().getModifiersQuerying(), s.GetLSCard("door")));
        chooseOlder(s, 0);
    }

    @Test
    public void AmbushRetrievalIsFreeAtThreeRelatedCoruscantBattlegrounds() {
        chooseOlder(retrieval("council", true, false, 0, true), 0);
    }

    @Test
    public void AmbushRetrievalCostsTwoOutsideCoruscant() {
        assertFalse(mode(retrieval("door", false, false, 1, true), RETRIEVE));
    }

    @Test
    public void AmbushRetrievalRequiresThreeDistinctBattlegrounds() {
        assertFalse(mode(retrieval("forest", false, false, 2, true), RETRIEVE));
    }

    @Test
    public void AmbushRetrievalDoesNotCountUnrelatedBattleground() {
        assertFalse(mode(retrieval("elsewhere", false, false, 2, true), RETRIEVE));
    }

    @Test
    public void AmbushRetrievalDoesNotCountThirdNonBattlegroundSite() {
        assertFalse(mode(retrieval("hut", false, false, 2, true), RETRIEVE));
    }

    @Test
    public void AmbushRetrievalRequiresYourControlPhaseAndNonemptyLostPile() {
        assertFalse(mode(retrieval("door", false, true, 2, true), RETRIEVE));
        assertFalse(mode(retrieval("door", false, false, 2, false), RETRIEVE));
    }

    private VirtualTableScenario battle(boolean ownCloneParticipates) {
        var s = scenario(); s.StartGame(); var site = s.GetLSStartingLocation();
        s.MoveCardsToHand(s.GetLSCard("ambush"));
        s.MoveCardsToLocation(site, s.GetLSCard("luke"), s.GetDSCard("clone"));
        s.MoveCardsToLocation(ownCloneParticipates ? site : s.GetDSStartingLocation(), s.GetLSCard("clone1"));
        s.LSStartBattleAndSkipToWeaponsSegment(site);
        return s;
    }

    @Test
    public void AmbushAddsOneActualBattleDestinyWithParticipatingClone() {
        var s = battle(true); int force = s.GetLSForcePileCount();
        assertEquals(1, s.GetLSBattleDestinyCount());
        play(s, DESTINY);
        s.PrepareLSDestiny(3); s.PrepareLSDestiny(4); s.SkipToPowerSegment();
        assertEquals(2, s.GetLSBattleDestinyCount());
        assertTrue(s.LSDecisionAvailable("battle destiny?")); s.LSChooseYes();
        for (int i = 0; i < 2; i++) s.PassDestinyDrawResponses();
        s.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertEquals(7, s.GetLSTotalDestiny()); assertEquals(force, s.GetLSForcePileCount());
    }

    @Test
    public void AmbushCannotAddDestinyForEnemyCloneOrYourCloneElsewhere() {
        var s = battle(false); assertFalse(mode(s, DESTINY));
        assertEquals(1, s.GetLSBattleDestinyCount());
    }
}
