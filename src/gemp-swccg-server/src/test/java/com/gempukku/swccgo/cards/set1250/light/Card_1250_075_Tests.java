package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_075_Tests {
    private static final String DRAW = "Draw from Reserve Deck for Wookiee sites";
    private static final String YARNA = "Deploy Yarna d'al' Gargan from Reserve Deck";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("forest", "1250_75"); put("haven", "1116_17"); put("wookiee", "7_50"); put("chewie", "2_3");
            put("luke", "1_19"); put("droid", "1_18"); put("yarna", "6_59"); put("other", "1_60");
            put("nudj", "4_5"); put("second", "4_5"); put("vine", "4_7"); put("bog", "4_85"); put("plains", "12_79");
        }}, new HashMap<>() {{ put("trooper", "1_194"); }}, 35, 35,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalSacredForestHasTwoIconsForEachSideAndForestKeyword() {
        var s = scenario(); var bp = s.GetLSCard("forest").getBlueprint();
        assertEquals("Kashyyyk: Sacred Forest", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Title.Kashyyyk, bp.getSystemName()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.LOCATION)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.DARK_FORCE, Icon.EXTERIOR_SITE,
                Icon.PLANET, Icon.EPISODE_I, Icon.ENDOR, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.FOREST)));
        assertEquals(2, bp.getIconCount(Icon.LIGHT_FORCE)); assertEquals(2, bp.getIconCount(Icon.DARK_FORCE));
    }

    private boolean offered(VirtualTableScenario s, String name) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("forest"), name);
    }

    private VirtualTableScenario drawSetup(boolean forestAbility, boolean mixedHaven) {
        var s = scenario(); s.StartGame();
        var forest = s.GetLSCard("forest"); var haven = s.GetLSCard("haven");
        s.MoveLocationToTable(forest); s.MoveLocationToTable(haven);
        if (forestAbility) s.MoveCardsToLocation(forest, s.GetLSCard("wookiee"));
        s.MoveCardsToLocation(forest, s.GetLSCard("droid"), s.GetDSCard("trooper"));
        s.MoveCardsToLocation(haven, s.GetLSCard("chewie"));
        s.MoveCardsToLocation(mixedHaven ? haven : s.GetLSStartingLocation(), s.GetLSCard("luke"));
        s.SkipToPhase(Phase.DRAW);
        assertFalse(offered(s, DRAW));
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("other"), s.GetLSCard("yarna"));
        for (int i = 0; i < 30 && !offered(s, DRAW)
                && !(s.GetCurrentPlayer().equals(s.LS) && s.GetCurrentPhase() == Phase.ACTIVATE); i++) {
            String prompt = s.GetCurrentDecision().getText();
            assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                    || prompt.contains("RECIRCULATED"));
            s.PlayerPass(s.GetDecidingPlayer());
        }
        return s;
    }

    @Test
    public void ActualStartOfTurnDrawTakesTheTopTwoForTwoWookieeSites() {
        var s = drawSetup(true, false); var expected = s.GetLSReserveDeck().stream().limit(2).toList();
        int initial = s.GetLSHandCount(); assertTrue(offered(s, DRAW));
        s.LSUseCardAction(s.GetLSCard("forest"), DRAW); s.PassAllResponses();
        assertTrue(s.GetLSHand().containsAll(expected)); assertEquals(initial + 2, s.GetLSHandCount());
    }

    @Test
    public void OneMixedAbilitySiteLeavesOnlyOneDraw() {
        var s = drawSetup(true, true); var expected = s.GetTopOfLSReserveDeck();
        int initial = s.GetLSHandCount(); assertTrue(offered(s, DRAW));
        s.LSUseCardAction(s.GetLSCard("forest"), DRAW); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(expected)); assertEquals(initial + 1, s.GetLSHandCount());
    }

    @Test
    public void ZeroAbilityAndMixedAbilitySitesDoNotOfferDraw() {
        var s = drawSetup(false, true);
        assertEquals(s.LS, s.GetCurrentPlayer()); assertFalse(offered(s, DRAW)); assertEquals(0, s.GetLSHandCount());
    }

    @Test
    public void ActualYarnaDownloadSelectsOnlyNamedEffectAndReshuffles() {
        var s = scenario(); s.StartGame(); var forest = s.GetLSCard("forest");
        s.MoveLocationToTable(forest); s.SkipToLSTurn(Phase.CONTROL);
        assertFalse(offered(s, YARNA));
        s.SkipToPhase(Phase.DEPLOY); var yarna = s.GetLSCard("yarna"); var other = s.GetLSCard("other");
        s.MoveCardsToTopOfLSReserveDeck(yarna, other);
        s.gameState().markCardsRevealedFromPile(List.of(other));
        assertTrue(offered(s, YARNA)); s.LSUseCardAction(forest, YARNA); s.PassAllResponses();
        var options = s.LSGetADParamAsList("blueprintId");
        if (options != null && options.contains(yarna.getBlueprintId(true))) {
            assertEquals("true", s.LSGetADParam("selectable")[options.indexOf(yarna.getBlueprintId(true))]);
            assertEquals("false", s.LSGetADParam("selectable")[options.indexOf(other.getBlueprintId(true))]);
            s.LSChooseCard(yarna);
        }
        s.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, yarna.getZone());
        assertTrue(s.GetLSReserveDeck().contains(other)); assertFalse(s.gameState().isCardRevealedFromPile(other));
        if (s.AwaitingDSDeployPhaseActions()) s.DSPass();
        assertTrue(s.AwaitingLSDeployPhaseActions()); assertFalse(offered(s, YARNA));
    }

    @Test
    public void ActualForestDeploymentLosesNudjsAtBothOtherSitesButNotAnotherCreature() {
        var s = scenario(); s.StartGame(); var plains = s.GetLSCard("plains"); var bog = s.GetLSCard("bog");
        var forest = s.GetLSCard("forest"); s.MoveCardsToHand(forest); s.SkipToLSTurn(Phase.DEPLOY);
        s.MoveLocationToTable(plains); s.MoveLocationToTable(bog);
        s.MoveCardsToLocation(plains, s.GetLSCard("nudj")); s.MoveCardsToLocation(bog, s.GetLSCard("second"), s.GetLSCard("vine"));
        var nudjs = List.of(s.GetLSCard("nudj"), s.GetLSCard("second"));
        s.LSDeployLocation(forest);
        for (int i = 0; i < 70 && !s.GetLSLostPile().containsAll(nudjs); i++) {
            String prompt = s.GetCurrentDecision().getText();
            if (s.LSDecisionAvailable("Choose card to put on Lost Pile")) {
                var choice = nudjs.stream().filter(s::LSHasCardChoiceAvailable).findFirst().orElseThrow();
                s.LSChooseCard(choice);
            } else if (prompt.contains("Required responses")) {
                var player = s.GetDecidingPlayer();
                var choices = s.GetADParamAsList(player, "actionText");
                s.ChooseAction(player, choices.contains("All Nudjs are lost") ? "All Nudjs are lost" : choices.getFirst());
            } else if (prompt.contains("Optional responses")) {
                s.PlayerPass(s.GetDecidingPlayer());
            } else {
                break;
            }
        }
        assertEquals(Zone.LOCATIONS, forest.getZone());
        assertTrue(s.GetCurrentDecision().getText() + "; Nudj zones " + nudjs.stream().map(c -> c.getZone()).toList(),
                s.GetLSLostPile().containsAll(nudjs));
        assertEquals(bog, s.GetLSCard("vine").getAtLocation());
    }
}
