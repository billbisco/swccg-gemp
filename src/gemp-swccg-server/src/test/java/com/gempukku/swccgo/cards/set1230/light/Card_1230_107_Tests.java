package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.communication.GameStateListener;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1230_107_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("hope", "1230_107"); put("objective", "9_61"); put("otherObjective", "7_136");
            put("luke", "1_19"); put("han", "1_11"); put("chewie", "2_3"); put("biggs", "1_3");
            put("take", "1_30"); put("lost1", "1_31"); put("lost2", "1_32");
            put("patience", "1230_99");
            for (int i = 1; i <= 6; i++) put("force" + i, "1_30");
        }}, new HashMap<>() {{ put("tech", "2_105"); put("maul", "11_54"); }},
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OurOnlyHopeStatsIconsKeywordsAndHostLoreAreCorrect() {
        var s = scenario(); var c = s.GetLSCard("hope").getBlueprint();
        assertEquals("Our Only Hope", c.getTitle()); assertTrue(c.hasVirtualSuffix());
        assertEquals(4, c.getDestiny(), s.epsilon); assertEquals(Side.LIGHT, c.getSide());
        assertEquals(Uniqueness.UNIQUE, c.getUniqueness()); assertEquals(CardSubtype.USED_OR_LOST, c.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, c.getExpansionSet()); assertEquals(Rarity.V, c.getRarity());
        assertEquals("'The Emperor knew, as I did, if Anakin were to have any offspring, they would be a threat to him.'", c.getLore());
        s.BlueprintCardTypeCheck(c, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        s.BlueprintIconCheck(c, new ArrayList<>() {{ add(Icon.INTERRUPT); add(Icon.DEATH_STAR_II); add(Icon.LEGACY_BLOCK_3); }});
        s.BlueprintKeywordCheck(c, new ArrayList<Keyword>());
    }

    private void setup(VirtualTableScenario s, String objective) {
        s.MoveOutOfPlay(s.GetLSCard("objective")); s.MoveOutOfPlay(s.GetLSCard("otherObjective"));
        s.StartGame();
        s.MoveCardsToLSHand(s.GetLSCard("hope"), s.GetLSCard("patience"));
        for (String key : new String[]{"objective", "otherObjective", "luke", "han", "chewie", "biggs", "take", "lost1", "lost2"})
            s.MoveOutOfPlay(s.GetLSCard(key));
        s.MoveOutOfPlay(s.GetDSCard("tech")); s.MoveOutOfPlay(s.GetDSCard("maul"));
        if (objective != null) s.MoveCardsToLSSideOfTable(s.GetLSCard(objective));
        for (int i = 1; i <= 6; i++) s.MoveCardsToTopOfLSForcePile(s.GetLSCard("force" + i));
    }

    @Test
    public void OurOnlyHopeUsedRequiresYourDeathStarIIObjective() {
        for (String objective : new String[]{null, "otherObjective"}) {
            var s = scenario(); setup(s, objective); s.SkipToLSTurn(Phase.CONTROL);
            assertFalse(s.LSCardPlayAvailable(s.GetLSCard("hope"), "Deploy Luke from Reserve Deck"));
        }
    }

    @Test
    public void OurOnlyHopeUsedDeploysLukeDuringControlAndPaysNormalCost() {
        var s = scenario(); setup(s, "objective"); s.SkipToLSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("luke"), s.GetLSCard("take"));
        var messages = recordMessages(s);
        int force = s.GetLSForcePileCount();
        s.LSPlayCard(s.GetLSCard("hope"), "Deploy Luke from Reserve Deck"); s.PassAllResponses();
        assertTrue(s.LSHasCardChoiceAvailable(s.GetLSCard("luke")));
        assertDownloadSelectable(s, "luke", true); assertDownloadSelectable(s, "take", false);
        s.LSChooseCard(s.GetLSCard("luke")); s.LSChooseCard(s.GetLSStartingLocation()); s.PassAllResponses();
        assertAtLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"));
        assertEquals(force - 3, s.GetLSForcePileCount()); assertInZone(Zone.USED_PILE, s.GetLSCard("hope"));
        assertEquals(1, countShuffles(s, messages));
    }

    @Test
    public void OurOnlyHopeRelocatesLukeOnlyDuringYourMovePhaseToAnOccupiedSite() {
        var s = scenario(); setup(s, "objective");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("tech"));
        s.SkipToLSTurn(Phase.CONTROL);
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("hope"), "Relocate Luke"));
        s.SkipToPhase(Phase.MOVE);
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("hope"), "Relocate Luke"));
        int force = s.GetLSForcePileCount();
        s.LSPlayCard(s.GetLSCard("hope"), "Relocate Luke");
        if (s.LSHasCardChoiceAvailable(s.GetLSCard("luke"))) s.LSChooseCard(s.GetLSCard("luke"));
        if (s.LSHasCardChoiceAvailable(s.GetDSStartingLocation())) s.LSChooseCard(s.GetDSStartingLocation());
        s.PassAllResponses();
        assertAtLocation(s.GetDSStartingLocation(), s.GetLSCard("luke"));
        assertEquals(force, s.GetLSForcePileCount()); assertInZone(Zone.USED_PILE, s.GetLSCard("hope"));
    }

    @Test
    public void OurOnlyHopeDoesNotOfferRelocationWithoutALegalOccupiedDestination() {
        var s = scenario(); setup(s, "objective");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"));
        s.SkipToLSTurn(Phase.MOVE);
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("hope"), "Relocate Luke"));
    }

    private VirtualTableScenario battleReady(String rebel, String objective, boolean lukeElsewhere) {
        var s = scenario(); setup(s, objective);
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard(rebel), s.GetDSCard("tech"));
        if (lukeElsewhere) s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("luke"));
        s.MoveCardsToTopOfLSLostPile(s.GetLSCard("lost1"), s.GetLSCard("lost2"));
        s.LSStartBattleAndSkipToWeaponsSegment(s.GetLSStartingLocation());
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("take"));
        s.SkipToEndOfPowerSegment(false);
        return s;
    }

    private void reachWinningResponse(VirtualTableScenario s) {
        for (int i = 0; i < 20; i++) {
            if (s.LSDecisionAvailable("optional response") && s.LSCardPlayAvailable(s.GetLSCard("hope"), "Take a card into hand")) return;
            if (s.LSAnyDecisionsAvailable()) s.LSPass(); else s.DSPass();
        }
        fail("Our Only Hope winning response was not offered");
    }

    private void takeCard(VirtualTableScenario s) {
        reachWinningResponse(s);
        var messages = recordMessages(s);
        s.LSPlayCard(s.GetLSCard("hope"), "Take a card into hand"); s.PassAllResponses();
        assertTrue(s.LSHasCardChoiceAvailable(s.GetLSCard("take")));
        s.LSChooseCard(s.GetLSCard("take")); s.PassAllResponses();
        assertInHand(s.GetLSCard("take"));
        assertEquals(1, countShuffles(s, messages));
    }

    private void assertDownloadSelectable(VirtualTableScenario s, String key, boolean expected) {
        int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard(key).getBlueprintId(true));
        assertTrue(index >= 0);
        assertEquals(expected, Boolean.parseBoolean(s.LSGetADParam("selectable")[index]));
    }

    private List<String> recordMessages(VirtualTableScenario s) {
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
        s.game().addGameStateListener(s.LS, listener); messages.clear(); return messages;
    }

    private long countShuffles(VirtualTableScenario s, List<String> messages) {
        return messages.stream().filter(m -> m.startsWith(s.LS + " shuffles Reserve Deck")).count();
    }

    @Test
    public void OurOnlyHopeUsedCannotDeployLukeWithoutEnoughForce() {
        var s = scenario(); setup(s, "objective"); s.SkipToLSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("luke"));
        s.UseForceCheat(s.LS, s.GetLSForcePileCount());
        s.LSPlayCard(s.GetLSCard("hope"), "Deploy Luke from Reserve Deck"); s.PassAllResponses();
        assertDownloadSelectable(s, "luke", false);
    }

    @Test
    public void OurOnlyHopeUsedDeploysLukeDuringOpponentsControlPhase() {
        var s = scenario(); setup(s, "objective"); s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("luke"));
        if (s.DSAnyDecisionsAvailable()) s.DSPass();
        int force = s.GetLSForcePileCount();
        s.LSPlayCard(s.GetLSCard("hope"), "Deploy Luke from Reserve Deck"); s.PassAllResponses();
        s.LSChooseCard(s.GetLSCard("luke")); s.LSChooseCard(s.GetLSStartingLocation()); s.PassAllResponses();
        assertAtLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"));
        assertEquals(force - 3, s.GetLSForcePileCount());
    }

    @Test
    public void OurOnlyHopeWinningLukeMayRetrieveAnyLostCardToUsedPile() {
        var s = battleReady("luke", "objective", false); takeCard(s);
        assertTrue(s.LSDecisionAvailable("Retrieve any one card?")); s.LSChooseYes(); s.PassAllResponses();
        assertTrue(s.LSHasCardChoiceAvailable(s.GetLSCard("lost1")));
        assertTrue(s.LSHasCardChoiceAvailable(s.GetLSCard("lost2")));
        s.LSChooseCard(s.GetLSCard("lost1")); s.PassAllResponses();
        assertInZone(Zone.USED_PILE, s.GetLSCard("lost1")); assertInZone(Zone.LOST_PILE, s.GetLSCard("lost2"));
        assertInZone(Zone.LOST_PILE, s.GetLSCard("hope"));
    }

    @Test
    public void OurOnlyHopeWinningLukeMayDeclineRetrieval() {
        var s = battleReady("luke", "objective", false); takeCard(s);
        s.LSChooseNo(); s.PassAllResponses();
        assertInZone(Zone.LOST_PILE, s.GetLSCard("lost1")); assertInZone(Zone.LOST_PILE, s.GetLSCard("lost2"));
        assertInZone(Zone.LOST_PILE, s.GetLSCard("hope"));
    }

    @Test
    public void OurOnlyHopeWinningHanDoesNotRetrieveBecauseLukeIsElsewhere() {
        var s = battleReady("han", "objective", true); takeCard(s);
        assertFalse(s.LSDecisionAvailable("Retrieve any one card?"));
        assertInZone(Zone.LOST_PILE, s.GetLSCard("lost1")); assertInZone(Zone.LOST_PILE, s.GetLSCard("hope"));
    }

    private void assertNoWinningResponse(VirtualTableScenario s) {
        for (int i = 0; i < 24; i++) {
            if (!s.GetCurrentDecision().getText().toLowerCase().contains("optional response")) break;
            if (s.LSAnyDecisionsAvailable()) {
                assertFalse(s.LSCardPlayAvailable(s.GetLSCard("hope"), "Take a card into hand")); s.LSPass();
            } else s.DSPass();
        }
        assertTrue(s.gameState().getBattleState().isWinner(s.LS));
        assertInHand(s.GetLSCard("hope"));
    }

    @Test
    public void OurOnlyHopeLostRequiresYourDeathStarIIObjectiveEvenAfterLukeWins() {
        for (String objective : new String[]{null, "otherObjective"}) {
            var s = battleReady("luke", objective, false); assertNoWinningResponse(s);
        }
    }

    @Test
    public void OurOnlyHopeLostDoesNotTriggerForAWinningRebelOfExactlyTwoAbility() {
        var s = battleReady("biggs", "objective", false); assertNoWinningResponse(s);
        assertEquals(2, s.GetAbility(s.GetLSCard("biggs")), s.epsilon);
    }

    @Test
    public void OurOnlyHopeLostAfterActualDuelTakesACardAndOffersLukeRetrieval() {
        var s = scenario(); setup(s, "objective");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"), s.GetDSCard("maul"));
        s.MoveCardsToTopOfLSLostPile(s.GetLSCard("lost1"), s.GetLSCard("lost2"));
        s.SkipToLSTurn(Phase.CONTROL); s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("take"));
        s.PrepareLSDestiny(5); s.PrepareLSDestiny(6); s.PrepareDSDestiny(2); s.PrepareDSDestiny(1);
        s.LSPlayCard(s.GetLSCard("patience"), "Initiate duel");
        boolean offered = false;
        for (int i = 0; i < 120; i++) {
            if (s.LSHasCardChoiceAvailable(s.GetLSCard("luke"))) s.LSChooseCard(s.GetLSCard("luke"));
            else if (s.LSHasCardChoiceAvailable(s.GetDSCard("maul"))) s.LSChooseCard(s.GetDSCard("maul"));
            else if (s.LSDecisionAvailable("optional response") && s.LSCardPlayAvailable(s.GetLSCard("hope"), "Take a card into hand")) {
                offered = true; break;
            }
            else if (s.GetLSCard("patience").getZone() == Zone.LOST_PILE && s.gameState().getDuelState() == null) break;
            else if (s.LSDecisionAvailable("optional response")) s.LSPass();
            else if (s.DSDecisionAvailable("optional response")) s.DSPass();
            else break;
        }
        assertTrue(offered); assertEquals(s.GetLSCard("luke"), s.gameState().getDuelState().getWinningCharacter()); takeCard(s); s.LSChooseYes(); s.PassAllResponses();
        s.LSChooseCard(s.GetLSCard("lost1")); s.PassAllResponses();
        assertInHand(s.GetLSCard("take")); assertInZone(Zone.USED_PILE, s.GetLSCard("lost1"));
        assertInZone(Zone.LOST_PILE, s.GetLSCard("hope"));
        assertInZone(Zone.LOST_PILE, s.GetLSCard("patience"));
    }
}
