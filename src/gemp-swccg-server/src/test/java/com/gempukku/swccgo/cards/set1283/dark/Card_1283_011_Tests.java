package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.InitiatePodraceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("hewins", "1283_11");
                    put("junkyard", "12_178"); // Tatooine: Watto's Junkyard
                    put("sebulba", "11_62");
                    put("tatooine", "1_289"); // Tatooine system
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void HeAlwaysWinsStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("hewins").getBlueprint();
        assertEquals("He Always Wins!", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HeAlwaysWinsDeploysOnAJunkyard() {
        var scn = GetScenario();
        var hewins = scn.GetDSCard("hewins");
        var junkyard = scn.GetDSCard("junkyard");
        scn.StartGame();
        scn.MoveCardsToHand(hewins);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSDeployAvailable(hewins));
        scn.MoveLocationToTable(junkyard);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(hewins));
        scn.DSDeployCard(hewins);
        scn.DSChooseCard(junkyard);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(junkyard, hewins));
    }

    private void heAlwaysWinsOnJunkyard(VirtualTableScenario scn) {
        var hewins = scn.GetDSCard("hewins");
        var junkyard = scn.GetDSCard("junkyard");
        scn.StartGame();
        scn.MoveLocationToTable(junkyard);
        scn.AttachCardsTo(junkyard, hewins);
    }

    @Test
    public void HeAlwaysWinsMayDeploySebulbaFromReserveDeck() {
        var scn = GetScenario();
        var hewins = scn.GetDSCard("hewins");
        var sebulba = scn.GetDSCard("sebulba");
        heAlwaysWinsOnJunkyard(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(sebulba);
        assertTrue(scn.DSCardActionAvailable(hewins, "Deploy Sebulba or Tatooine from Reserve Deck"));
        scn.DSUseCardAction(hewins, "Deploy Sebulba or Tatooine from Reserve Deck");
        scn.DSChooseCard(sebulba);
        if (scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation())) {
            scn.DSChooseCard(scn.GetDSStartingLocation());
        }
        scn.PassAllResponses();
        assertInZone(Zone.AT_LOCATION, sebulba);
    }

    @Test
    public void HeAlwaysWinsMayDeployTatooineSystemFromReserveDeck() {
        var scn = GetScenario();
        var hewins = scn.GetDSCard("hewins");
        var tatooine = scn.GetDSCard("tatooine");
        heAlwaysWinsOnJunkyard(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(tatooine);
        scn.DSUseCardAction(hewins, "Deploy Sebulba or Tatooine from Reserve Deck");
        scn.DSChooseCard(tatooine);
        scn.PassAllResponses();
        assertInZone(Zone.LOCATIONS, tatooine);
    }

    @Test
    public void HeAlwaysWinsMayDrawTwoCardsFromReserveDeckWhenAPodraceWasJustInitiated() {
        var scn = GetScenario();
        var hewins = scn.GetDSCard("hewins");
        heAlwaysWinsOnJunkyard(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int hand = scn.GetDSHandCount();
        scn.DSExecuteAdHocEffect(hewins, new InitiatePodraceEffect(new TopLevelGameTextAction(hewins, hewins.getCardId())));
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(hewins, "Draw two cards from Reserve Deck"));
        scn.DSUseCardAction(hewins, "Draw two cards from Reserve Deck");
        scn.PassAllResponses();
        assertEquals(hand + 2, scn.GetDSHandCount());
    }

    private int dsTotalPowerInBattle(boolean wonPodrace) {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        heAlwaysWinsOnJunkyard(scn);
        if (wonPodrace) {
            scn.game().getGameState().setPodraceWinner(scn.DS);
        }
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        return scn.GetDSTotalPower();
    }

    @Test
    public void HeAlwaysWinsTotalPowerDuringBattleIsPlus3IfYouHaveWonAPodrace() {
        assertEquals(dsTotalPowerInBattle(false) + 3, dsTotalPowerInBattle(true));
    }

}
