package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.layout.LocationPlacement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_071_Tests {
    private static final String TAKE = "Take Speak With The Jedi Council into hand";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("archives", "1250_71"); put("council", "12_76"); put("dock", "12_74");
            put("nightclub", "1116_30"); put("speak", "12_69"); put("other", "1_19");
        }}, new HashMap<>() {{
            put("trooper", "1_194");
        }}, 25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void JediArchivesMetadataMatchesPrintedLightSiteWithNoVirtualSuffix() {
        var s = scenario(); var bp = s.GetLSCard("archives").getBlueprint();
        assertEquals("Coruscant: Jedi Archives", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Title.Coruscant, bp.getSystemName()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.LOCATION)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.INTERIOR_SITE,
                Icon.PLANET, Icon.EPISODE_I, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertEquals(1, bp.getIconCount(Icon.LIGHT_FORCE)); assertEquals(0, bp.getIconCount(Icon.DARK_FORCE));
    }

    private VirtualTableScenario searchSetup(boolean council, boolean darkTurn) {
        var s = scenario(); s.StartGame();
        s.MoveLocationToTable(s.GetLSCard("archives")); s.MoveLocationToTable(s.GetLSCard("dock"));
        if (council) s.MoveLocationToTable(s.GetLSCard("council"));
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("speak"), s.GetLSCard("other"));
        if (darkTurn) s.SkipToDSTurn(Phase.CONTROL); else s.SkipToLSTurn(Phase.CONTROL);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("speak"), s.GetLSCard("other"));
        return s;
    }

    private boolean searchAvailable(VirtualTableScenario s, String player) {
        if (s.GetADParam(player, "cardId") == null || s.GetADParam(player, "actionText") == null) return false;
        return player.equals(s.LS) ? s.LSCardActionAvailable(s.GetLSCard("archives"), TAKE)
                : s.DSCardActionAvailable(s.GetLSCard("archives"), TAKE);
    }

    private void takeSpeak(VirtualTableScenario s) {
        var speak = s.GetLSCard("speak"); var other = s.GetLSCard("other");
        int reserve = s.GetLSReserveDeckCount();
        assertTrue(searchAvailable(s, s.LS));
        s.LSUseCardAction(s.GetLSCard("archives"), TAKE); s.PassAllResponses();
        var options = s.LSGetADParamAsList("blueprintId");
        int correct = options.indexOf(speak.getBlueprintId(true));
        int incorrect = options.indexOf(other.getBlueprintId(true));
        assertTrue(correct >= 0 && incorrect >= 0);
        assertEquals("true", s.LSGetADParam("selectable")[correct]);
        assertEquals("false", s.LSGetADParam("selectable")[incorrect]);
        s.LSChooseCard(speak); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(speak));
        assertTrue(s.GetLSReserveDeck().contains(other));
        assertEquals(reserve - 1, s.GetLSReserveDeckCount());
    }

    @Test
    public void JediArchivesTakesActualSpeakWithCouncilAndReshuffles() {
        var s = searchSetup(true, false); var reveal = s.GetLSCard("other");
        s.gameState().markCardsRevealedFromPile(List.of(reveal));
        assertTrue(s.gameState().isCardRevealedFromPile(reveal));
        takeSpeak(s);
        assertFalse(s.gameState().isCardRevealedFromPile(reveal));
    }

    @Test
    public void JediArchivesCannotSearchWithOnlyAnotherCoruscantSiteOnTable() {
        var s = searchSetup(false, false); assertFalse(searchAvailable(s, s.LS));
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("speak")));
    }

    @Test
    public void JediArchivesLightAbilityRemainsLightDuringDarkControl() {
        var s = searchSetup(true, true); assertFalse(searchAvailable(s, s.DS));
        s.DSPass(); takeSpeak(s);
    }

    @Test
    public void JediArchivesSearchDoesNotResetOnLaterTurn() {
        var s = searchSetup(true, false); takeSpeak(s);
        int oldTurn = s.gameState().getPlayersLatestTurnNumber(s.LS);
        for (int i = 0; i < 200; i++) {
            if (s.GetCurrentPlayer().equals(s.LS) && s.gameState().getPlayersLatestTurnNumber(s.LS) == oldTurn + 1
                    && s.GetCurrentPhase() == Phase.CONTROL) break;
            String prompt = s.GetCurrentDecision().getText();
            if (prompt.equals("You have not activated Force. Do you want to Pass?")) {
                if (s.DSDecisionAvailable(prompt)) s.DSChooseYes(); else s.LSChooseYes();
            } else {
                assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                        || prompt.contains("RECIRCULATED"));
                if (s.DSDecisionAvailable(prompt)) s.DSPass(); else s.LSPass();
            }
        }
        assertEquals(oldTurn + 1, s.gameState().getPlayersLatestTurnNumber(s.LS));
        assertEquals(Phase.CONTROL, s.GetCurrentPhase()); assertTrue(s.AwaitingLSControlPhaseActions());
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("speak"));
        assertFalse(searchAvailable(s, s.LS));
    }

    private LocationPlacement rightOf(PhysicalCardImpl card) {
        return new LocationPlacement(Title.Coruscant, null, null, card, LocationPlacementDirection.RIGHT_OF);
    }

    private boolean adjacent(VirtualTableScenario s) {
        return s.game().getModifiersQuerying().isAdjacentSites(s.gameState(), s.GetLSCard("archives"), s.GetLSCard("council"));
    }

    private List<LocationPlacement> placements(VirtualTableScenario s, PhysicalCardImpl card) {
        return s.gameState().getLocationPlacement(s.game(), card, null, null);
    }

    private void deploy(VirtualTableScenario s, PhysicalCardImpl card, PhysicalCardImpl nextTo) {
        s.MoveCardsToHand(card); s.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(s.LSCardPlayAvailable(card)); s.LSDeployCard(card);
        if (s.LSDecisionAvailable("Choose a location to deploy")) {
            assertTrue(s.LSHasCardChoiceAvailable(nextTo)); s.LSChooseCard(nextTo);
        }
        if (s.LSDecisionAvailable("On which side")) s.LSChooseOption("Left");
        s.PassAllResponses(); assertEquals(Zone.LOCATIONS, card.getZone());
    }

    private void checkSecondSite(boolean archiveSecond) {
        var s = scenario(); s.StartGame(); var archives = s.GetLSCard("archives"); var council = s.GetLSCard("council");
        var first = archiveSecond ? council : archives;
        var second = archiveSecond ? archives : council;
        var dock = s.GetLSCard("dock"); var nightclub = s.GetLSCard("nightclub");
        s.MoveLocationToTable(first); s.MoveLocationToTable(dock, rightOf(first));
        s.MoveLocationToTable(nightclub, rightOf(dock));
        assertTrue(placements(s, second).stream().noneMatch(p -> p.getOtherCard() == nightclub));
        deploy(s, second, first); assertTrue(adjacent(s));
    }

    @Test
    public void JediArchivesMustActuallyDeployAdjacentToExistingCouncilChamber() { checkSecondSite(true); }

    @Test
    public void JediCouncilChamberMustActuallyDeployAdjacentToExistingArchives() { checkSecondSite(false); }

    @Test
    public void JediArchivesCannotBeSeparatedByThirdSiteButOutsideDeploymentIsLegal() {
        var s = scenario(); s.StartGame(); var archives = s.GetLSCard("archives"); var council = s.GetLSCard("council");
        var dock = s.GetLSCard("dock"); s.MoveLocationToTable(archives); s.MoveLocationToTable(council, rightOf(archives));
        assertTrue(adjacent(s));
        var choices = placements(s, dock);
        assertFalse(choices.stream().anyMatch(p -> p.getOtherCard() == archives && p.getDirection().isRightOf()));
        assertFalse(choices.stream().anyMatch(p -> p.getOtherCard() == council && p.getDirection().isLeftOf()));
        assertTrue(choices.stream().anyMatch(p -> p.getOtherCard() == archives && p.getDirection().isLeftOf()));
        deploy(s, dock, archives); assertTrue(adjacent(s));
    }

    @Test
    public void JediArchivesCanDeployWithoutCouncilChamber() {
        var s = scenario(); s.StartGame(); var dock = s.GetLSCard("dock"); s.MoveLocationToTable(dock);
        deploy(s, s.GetLSCard("archives"), dock);
        assertNotEquals(Zone.LOCATIONS, s.GetLSCard("council").getZone());
    }

    private void checkDarkDrain(boolean atArchives, int expected) {
        var s = scenario(); s.StartGame(); var archives = s.GetLSCard("archives"); var dock = s.GetLSCard("dock");
        s.MoveLocationToTable(archives); s.MoveLocationToTable(dock);
        var location = atArchives ? archives : dock;
        s.MoveCardsToLocation(location, s.GetDSCard("trooper"));
        s.SkipToDSTurn(Phase.CONTROL);
        int life = s.GetLSLifeForceRemaining();
        assertTrue(s.DSForceDrainAvailable(location)); s.DSForceDrainAt(location); s.PassForceDrainStartResponses();
        assertEquals(expected, s.GetForceDrainTotal());
        for (int i = 0; i < expected; i++) {
            assertTrue(s.LSAnyDecisionsAvailable()); s.LSChooseCard(s.GetTopOfLSReserveDeck()); s.PassAllResponses();
        }
        assertEquals(life - expected, s.GetLSLifeForceRemaining());
    }

    @Test
    public void JediArchivesAddsOneToActualDarkDrainHereWithoutCouncilChamber() { checkDarkDrain(true, 2); }

    @Test
    public void JediArchivesDoesNotAddToDarkDrainAtOtherCoruscantSite() { checkDarkDrain(false, 1); }

    @Test
    public void JediArchivesDoesNotIncreaseLightDrainAmount() {
        var s = scenario(); s.StartGame(); var archives = s.GetLSCard("archives");
        s.MoveLocationToTable(archives); s.MoveCardsToLocation(archives, s.GetLSCard("other"));
        s.SkipToLSTurn(Phase.CONTROL);
        assertEquals(0, s.game().getModifiersQuerying().getForceDrainAmount(s.gameState(), archives, s.LS), s.epsilon);
    }
}
