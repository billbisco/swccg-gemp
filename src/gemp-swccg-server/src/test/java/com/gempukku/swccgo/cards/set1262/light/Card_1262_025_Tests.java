package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Card_1262_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("approach", "1262_25");
                    put("luke", "1_19");
                    put("starting", "13_5");
                    put("shield", "13_1");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("emperor", "9_109");
                    put("lightning", "9_136");
                    put("trooper", "1_194");
                    put("tie", "1271_29");
                    put("pilot", "1271_6");
                    put("tatooine", "1_289");
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
    public void DarkApproachStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("approach").getBlueprint();

        assertEquals("Dark Approach", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_6);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals("'The Force is with you, young Skywalker. But you are not a Jedi yet.'", card.getLore());
        assertEquals("During the weapons segment, if your character is about to be lost, it is instead lost at end of battle (forfeit for 0). OR Play a Defensive Shield from under your Starting Effect. OR During the power segment, cancel a just drawn destiny (except battle destiny).",
                card.getGameText());
    }

    @Test
    public void DarkApproachDuringWeaponsSegmentMakesCharacterLostAtEndOfBattleForForfeitZero() {
        var scn = GetScenario();
        var approach = scn.GetLSCard("approach");
        var luke = scn.GetLSCard("luke");
        var emperor = scn.GetDSCard("emperor");
        var lightning = scn.GetDSCard("lightning");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, emperor);
        scn.MoveCardsToLSHand(approach);
        scn.MoveCardsToDSHand(lightning);
        scn.EnsureDSForcePile(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(7);

        playForceLightningAtLuke(scn, lightning, emperor, luke);
        scn.PassResponses("ATTRIBUTE");
        scn.PassCardPlayResponses();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("DESTINY");
        scn.PassResponses("ATTRIBUTE");
        scn.PassResponses("ABOUT_TO_BE_LOST");
        assertTrue(scn.LSCardPlayAvailable(approach, "lost at end of battle"));
        scn.LSPlayCard(approach, "lost at end of battle");
        scn.PassAllResponses();
        assertTrue(luke.getZone().isInPlay());

        scn.SkipToDamageSegment(false);
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.PassDamageSegmentActions();
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, luke);
    }

    @Test
    public void DarkApproachPlaysDefensiveShieldFromStartingEffect() {
        var scn = GetScenario();
        var approach = scn.GetLSCard("approach");
        var starting = scn.GetLSCard("starting");
        var shield = scn.GetLSCard("shield");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(starting);
        scn.StackCardsOn(starting, shield);
        scn.MoveCardsToLSHand(approach);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(approach, "Defensive Shield"));
        scn.LSPlayCard(approach, "Defensive Shield");
        assertTrue(scn.LSHasCardChoiceAvailable(shield));
        scn.LSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());
    }

    @Test
    public void DarkApproachDuringPowerSegmentCancelsJustDrawnDestinyExceptBattleDestiny() {
        var scn = GetScenario();
        var approach = scn.GetLSCard("approach");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var pilot = scn.GetDSCard("pilot");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing, tie);
        scn.BoardAsPilot(tie, pilot);
        scn.MoveCardsToLSHand(approach);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(3);
        scn.DSInitiateBattle(tatooine);
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("optional");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        assertTrue(scn.LSCardPlayAvailable(approach, "Cancel destiny"));
        scn.LSPlayCard(approach, "Cancel destiny");
        scn.PassAllResponses();
        scn.SkipBattleDestinyDraws(false);
        assertTrue(scn.GetDSTotalPower() < 9);
    }

    @Test
    public void DarkApproachDoesNotCancelBattleDestinyDuringPowerSegment() {
        var scn = GetScenario();
        var approach = scn.GetLSCard("approach");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.MoveCardsToLSHand(approach);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(3);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.PassPowerSegmentActions();
        scn.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.DSDecisionAvailable("DESTINY_DRAWN"));
        scn.DSPass();
        assertNotNull(scn.LSGetDecision());
        assertFalse(scn.LSCardPlayAvailable(approach, "Cancel destiny"));
    }

    private void playForceLightningAtLuke(VirtualTableScenario scn, PhysicalCardImpl lightning,
                                          PhysicalCardImpl emperor, PhysicalCardImpl luke) {
        assertTrue(scn.DSCardPlayAvailable(lightning, "Make target lost"));
        scn.DSPlayCard(lightning, "Make target lost");
        assertTrue(scn.DSDecisionAvailable("Choose Emperor"));
        scn.DSChooseCard(emperor);
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        scn.DSChooseCard(luke);
    }
}
