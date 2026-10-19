package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class Card_1291_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                    put("alderaan", "1_122");
                    put("yavin", "1_129");
                }},
                new HashMap<>() {{
                    put("rally", "1291_28");
                    put("dooku", "200_76");
                    put("trooper", "1_194");
                    put("junk", "1_304");
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
    public void RallyToOurCauseStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("rally").getBlueprint();
        assertEquals("Rally To Our Cause", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.MISSION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.SEPARATIST);
            add(Icon.MISSION);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RallyToOurCauseDeploysOnWarHasBegun() {
        var scn = GetScenario();
        var rally = scn.GetDSCard("rally");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9600, scn.DS, MissionTestSupport.warHasBegun());
        scn.MoveCardsToHand(rally);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(rally));
        scn.DSDeployCard(rally);
        scn.DSChooseCard(host);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(host, rally));
    }

    @Test
    public void RallyToOurCauseAlignsASystemDuringControlPhase() {
        var scn = GetScenario();
        var rally = scn.GetDSCard("rally");
        var tatooine = scn.GetLSCard("tatooine");
        var dooku = scn.GetDSCard("dooku");
        var junk = scn.GetDSCard("junk");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9600, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, rally);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper, dooku);
        scn.MoveCardsToTopOfOwnLostPile(junk);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(7);
        assertTrue(scn.DSActionAvailable("Draw destiny to align a system"));
        scn.DSUseCardAction(rally, "Draw destiny to align a system");
        scn.DSChooseCard(tatooine);
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(tatooine).isEmpty());
    }

    @Test
    public void RallyToOurCauseTurnTheTideOnOpponentDrainAtAlignedSystem() {
        var scn = GetScenario();
        var rally = scn.GetDSCard("rally");
        var tatooine = scn.GetLSCard("tatooine");
        var xwing = scn.GetLSCard("xwing");
        var junk = scn.GetDSCard("junk");
        var dooku = scn.GetDSCard("dooku");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9600, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, rally);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper, dooku);
        scn.MoveCardsToTopOfOwnLostPile(junk);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(7);
        assertTrue(scn.DSActionAvailable("Draw destiny to align a system"));
        scn.DSUseCardAction(rally, "Draw destiny to align a system");
        scn.DSChooseCard(tatooine);
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(tatooine).isEmpty());
        scn.DSPass();
        scn.PassAllResponses();
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(tatooine));
        scn.LSForceDrainAt(tatooine);
        scn.PassForceDrainStartResponses();
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(tatooine).isEmpty());
        assertInZone(Zone.USED_PILE, rally);
    }

    @Test
    public void RallyToOurCauseCannotRedrawTheSameSystemThisControlPhase() {
        var scn = GetScenario();
        var rally = scn.GetDSCard("rally");
        var tatooine = scn.GetLSCard("tatooine");
        var dooku = scn.GetDSCard("dooku");
        var junk = scn.GetDSCard("junk");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9600, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, rally);
        scn.MoveLocationToTable(tatooine);
        var muunilinst = MissionTestSupport.putLocationOnTable(scn, 9601, scn.DS, MissionTestSupport.muunilinstSystem());
        var plaza = MissionTestSupport.putLocationOnTable(scn, 9602, scn.DS, MissionTestSupport.muunilinstSite("Muunilinst: Plaza"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dooku);
        scn.MoveCardsToLocation(plaza, trooper);
        scn.MoveCardsToTopOfOwnLostPile(junk);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(1);
        assertTrue(scn.DSActionAvailable("Draw destiny to align a system"));
        scn.DSUseCardAction(rally, "Draw destiny to align a system");
        scn.DSChooseCard(tatooine);
        scn.PassAllResponses();
        assertTrue(scn.GetStackedCards(tatooine).isEmpty());
        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSActionAvailable("Draw destiny to align a system"));
        scn.DSUseCardAction(rally, "Draw destiny to align a system");
        assertTrue(scn.DSHasCardChoiceAvailable(muunilinst));
        assertFalse(scn.DSHasCardChoiceAvailable(tatooine));
        scn.DSChooseCard(muunilinst);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSActionAvailable("Draw destiny to align a system"));
    }

    @Test
    public void RallyToOurCauseRewardAddsBattleDestinyLimitedTo2() {
        var scn = GetScenario();
        var rally = scn.GetDSCard("rally");
        var tatooine = scn.GetLSCard("tatooine");
        var dooku = scn.GetDSCard("dooku");
        var junk = scn.GetDSCard("junk");
        var trooper = scn.GetDSCard("trooper");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9600, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, rally);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper, dooku);
        scn.MoveCardsToTopOfOwnLostPile(junk);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(7);
        scn.DSUseCardAction(rally, "Draw destiny to align a system");
        scn.DSChooseCard(tatooine);
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(tatooine).isEmpty());
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), xwing);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.PrepareDSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetDSTotalDestiny());
    }

    @Test
    public void RallyToOurCauseRewardBattleDestinyBonusIsCappedAt2() {
        var scn = GetScenario();
        var rally = scn.GetDSCard("rally");
        var dooku = scn.GetDSCard("dooku");
        var trooper = scn.GetDSCard("trooper");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        var host = MissionTestSupport.putOnTable(scn, 9600, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, rally);
        var s1 = MissionTestSupport.putLocationOnTable(scn, 9610, scn.DS, MissionTestSupport.namedSystem("Align One", 1));
        var s2 = MissionTestSupport.putLocationOnTable(scn, 9611, scn.DS, MissionTestSupport.namedSystem("Align Two", 2));
        var s3 = MissionTestSupport.putLocationOnTable(scn, 9612, scn.DS, MissionTestSupport.namedSystem("Align Three", 3));
        assertEquals(Zone.LOCATIONS, s1.getZone());
        assertEquals(Zone.LOCATIONS, s2.getZone());
        assertEquals(Zone.LOCATIONS, s3.getZone());
        rally.setWhileInPlayData(new com.gempukku.swccgo.game.state.WhileInPlayData());
        rally.getWhileInPlayData().getTextValues().add(String.valueOf(s1.getCardId()));
        rally.getWhileInPlayData().getTextValues().add(String.valueOf(s2.getCardId()));
        rally.getWhileInPlayData().getTextValues().add(String.valueOf(s3.getCardId()));
        assertEquals(3, rally.getWhileInPlayData().getTextValues().size());
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper, dooku);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), xwing);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.PrepareDSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(3, scn.GetDSTotalDestiny());
    }
}
