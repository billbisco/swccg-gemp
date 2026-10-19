package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1271_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("pilot", "1271_6");
                    put("tie", "1271_29");
                    put("combat", "601_76");
                    put("set0combat", "200_105");
                    put("tatooine", "1_289");
                    put("filler", "1_304");
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
    public void SaberSquadronPilotStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("pilot").getBlueprint();

        assertEquals("Saber Squadron Pilot", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.DEATH_STAR_II);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SaberSquadronPilotAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, pilot);
        assertEquals(5, scn.GetPower(tie));
    }

    @Test
    public void SaberSquadronPilotIsMatchingPilotForSaberSquadronTIE() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, pilot);
        assertTrue(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), pilot, tie));
    }

    @Test
    public void SaberSquadronPilotWhilePilotingMayRelocateUsingCombatResponse() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var tie = scn.GetDSCard("tie");
        var combat = scn.GetDSCard("combat");
        var tatooine = scn.GetDSCard("tatooine");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToDSSideOfTable(combat);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, pilot);
        scn.SkipToDSTurn(Phase.MOVE);
        scn.EnsureDSForcePile(2);

        assertTrue(scn.DSCardActionAvailable(pilot, "Relocate Saber Squadron TIE"));
        scn.DSUseCardAction(pilot, "Relocate Saber Squadron TIE");
        assertTrue(scn.DSHasCardChoiceAvailable(yavin));
        scn.DSChooseCard(yavin);
        scn.PassAllResponses();
        assertAtLocation(yavin, tie);
        assertTrue(pilot.isPilotOf());
    }

    @Test
    public void SaberSquadronPilotDoesNotOfferRelocateWithoutCombatResponse() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetDSCard("tatooine");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, pilot);
        scn.DSActivateForceCheat(2);
        scn.SkipToDSTurn(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(pilot, "Relocate Saber Squadron TIE"));
    }

    @Test
    public void SaberSquadronPilotDoesNotOfferRelocateWithSet0CombatResponse() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var tie = scn.GetDSCard("tie");
        var set0combat = scn.GetDSCard("set0combat");
        var tatooine = scn.GetDSCard("tatooine");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToDSSideOfTable(set0combat);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, pilot);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(pilot, "Relocate Saber Squadron TIE"));
    }

    @Test
    public void SaberSquadronPilotWhilePilotingAddsOneDestinyToTotalPowerOnly() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var tie = scn.GetDSCard("tie");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie, xwing);
        scn.BoardAsPilot(tie, pilot);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(3);
        scn.DSInitiateBattle(tatooine);
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("optional");
        scn.PassDestinyDrawResponses();
        scn.SkipBattleDestinyDraws(false);
        assertTrue(scn.GetDSTotalPower() >= 8);
        assertEquals(0, scn.GetDSTotalDestiny(), scn.epsilon);
    }

    @Test
    public void SaberSquadronPilotMayAddOneToForceDrainHereWithCapOfTwo() {
        var scn = GetScenario();
        var pilot = scn.GetDSCard("pilot");
        var tie = scn.GetDSCard("tie");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, tie);
        scn.BoardAsPilot(tie, pilot);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        int beforeLost = scn.GetLSLostPileCount();
        assertTrue(scn.DSForceDrainAvailable(yavin));
        scn.DSForceDrainAt(yavin);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(pilot, "Add 1 to Force drain"));
        scn.DSUseCardAction(pilot, "Add 1 to Force drain");
        scn.PassForceDrainStartResponses();
        assertEquals(3, scn.GetForceDrainTotal());
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassForceDrainPendingResponses();
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassForceDrainPendingResponses();
        assertFalse(scn.LSDecisionAvailable("Choose Force to lose"));
        int lost = scn.GetLSLostPileCount() - beforeLost;
        assertEquals(2, lost);
    }
}
