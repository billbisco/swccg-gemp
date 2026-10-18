package com.gempukku.swccgo.cards.set1271.dark;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class Card_1271_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                    put("gold5", "1_142");
                }},
                new HashMap<>() {{
                    put("aces", "1271_26");
                    put("dantooine", "1_282");
                    put("tie", "1_304");
                    put("ds61", "1_173");
                    put("trooper", "1_194");
                    put("tallon", "1_270");
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
    public void ImperialAcesStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("aces").getBlueprint();
        assertEquals("Imperial Aces", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.MISSION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SETUP);
            add(Icon.DEATH_STAR_II);
            add(Icon.MISSION);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialAcesDeploysForFreeDuringDeployPhase() {
        var scn = GetScenario();
        var aces = scn.GetDSCard("aces");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(aces));
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(aces);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, aces);
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void ImperialAcesAddsOneToForceDrainWithAbility2ImperialPiloting() {
        var scn = GetScenario();
        var aces = scn.GetDSCard("aces");
        var dantooine = scn.GetDSCard("dantooine");
        var tie = scn.GetDSCard("tie");
        var ds61 = scn.GetDSCard("ds61");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, tie);
        scn.BoardAsPilot(tie, ds61);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(dantooine));
        scn.DSForceDrainAt(dantooine);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void ImperialAcesDoesNotAddForceDrainWithoutAbility2Pilot() {
        var scn = GetScenario();
        var aces = scn.GetDSCard("aces");
        var dantooine = scn.GetDSCard("dantooine");
        var tie = scn.GetDSCard("tie");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, tie);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(dantooine));
        scn.DSForceDrainAt(dantooine);
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void ImperialAcesSkipsDrainBonusWhenAnotherForceDrainBonusExists() {
        var scn = GetScenario();
        var aces = scn.GetDSCard("aces");
        var dantooine = scn.GetDSCard("dantooine");
        var tie = scn.GetDSCard("tie");
        var ds61 = scn.GetDSCard("ds61");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, tie);
        scn.BoardAsPilot(tie, ds61);
        scn.ApplyAdHocModifier(new ForceDrainModifier(trooper, Filters.sameCardId(dantooine), 1, scn.DS));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(dantooine));
        scn.DSForceDrainAt(dantooine);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void ImperialAcesGrantsAttritionImmunityToPilotedStarfighters() {
        var scn = GetScenario();
        var aces = scn.GetDSCard("aces");
        var dantooine = scn.GetDSCard("dantooine");
        var tie = scn.GetDSCard("tie");
        var ds61 = scn.GetDSCard("ds61");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, tie);
        scn.BoardAsPilot(tie, ds61);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), tie), scn.epsilon);
    }

    @Test
    public void ImperialAcesAddsAstromechCapacityOnlyWhenPrintedCapacityIsZero() {
        var scn = GetScenario();
        var aces = scn.GetDSCard("aces");
        var dantooine = scn.GetDSCard("dantooine");
        var tie = scn.GetDSCard("tie");
        var gold5 = scn.GetLSCard("gold5");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, tie);
        scn.MoveCardsToLocation(dantooine, gold5);
        assertEquals(1, scn.game().getModifiersQuerying().getAstromechCapacity(scn.gameState(), tie));
        assertEquals(1, scn.game().getModifiersQuerying().getAstromechCapacity(scn.gameState(), gold5));
    }

    @Test
    public void ImperialAcesCancelsTallonRollBeingPlayed() {
        var scn = GetScenario();
        var aces = scn.GetDSCard("aces");
        var dantooine = scn.GetDSCard("dantooine");
        var tie = scn.GetDSCard("tie");
        var tallon = scn.GetDSCard("tallon");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        scn.MoveCardsToHand(aces, tallon);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, tie);
        scn.MoveCardsToLocation(dantooine, xwing);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(dantooine);
        scn.PassAllResponses();
        assertTrue(scn.DSCardPlayAvailable(tallon));
        scn.DSPlayCard(tallon);
        scn.DSChooseCard(tie);
        scn.DSChooseCard(xwing);
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, tallon);
    }
}
