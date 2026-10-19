package com.gempukku.swccgo.cards.set1271.light;

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


public class Card_1271_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("aces", "1271_45");
                    put("sandwhirl", "6_57");
                    put("yavin", "1_135");
                    put("xwing", "1_146");
                    put("jek", "1_013");
                    put("gold5", "1_142");
                    put("luke", "1_19");
                    put("desert", "6_085");
                    put("falcon", "1_143");
                    put("red5", "111_2");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("tie", "1_304");
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
    public void RebelAcesStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("aces").getBlueprint();
        assertEquals("Rebel Aces", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
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
    public void RebelAcesDeploysForFreeDuringDeployPhase() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(aces));
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, aces);
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void RebelAcesCancelsSandwhirl() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        var sandwhirl = scn.GetLSCard("sandwhirl");
        var desert = scn.GetLSCard("desert");
        scn.StartGame();
        scn.MoveLocationToTable(desert);
        scn.MoveCardsToHand(aces, sandwhirl);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(sandwhirl));
        scn.LSDeployCard(sandwhirl);
        scn.LSChooseCard(desert);
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, sandwhirl);
    }

    @Test
    public void RebelAcesAddsOneToForceDrainWithAbility2RebelPiloting() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        var yavin = scn.GetLSCard("yavin");
        var xwing = scn.GetLSCard("xwing");
        var jek = scn.GetLSCard("jek");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, xwing);
        scn.BoardAsPilot(xwing, jek);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(yavin));
        scn.LSForceDrainAt(yavin);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void RebelAcesSkipsDrainBonusWhenAnotherForceDrainBonusExists() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        var yavin = scn.GetLSCard("yavin");
        var xwing = scn.GetLSCard("xwing");
        var jek = scn.GetLSCard("jek");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, xwing);
        scn.BoardAsPilot(xwing, jek);
        scn.MoveCardsToLocation(yavin, luke);
        scn.ApplyAdHocModifier(new ForceDrainModifier(luke, Filters.sameCardId(yavin), 1, scn.LS));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(yavin));
        scn.LSForceDrainAt(yavin);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void RebelAcesGrantsAttritionImmunityToPilotedStarfighters() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        var yavin = scn.GetLSCard("yavin");
        var xwing = scn.GetLSCard("xwing");
        var jek = scn.GetLSCard("jek");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, xwing);
        scn.BoardAsPilot(xwing, jek);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), xwing), scn.epsilon);
    }

    @Test
    public void RebelAcesAddsAstromechCapacityOnlyWhenPrintedCapacityIsZero() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        var yavin = scn.GetLSCard("yavin");
        var xwing = scn.GetLSCard("xwing");
        var gold5 = scn.GetLSCard("gold5");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, xwing, gold5);
        assertEquals(1, scn.game().getModifiersQuerying().getAstromechCapacity(scn.gameState(), xwing));
        assertEquals(1, scn.game().getModifiersQuerying().getAstromechCapacity(scn.gameState(), gold5));
    }

    @Test
    public void RebelAcesKeepsDrainBonusWhenOpponentHasANegativeDrainModifier() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        var yavin = scn.GetLSCard("yavin");
        var xwing = scn.GetLSCard("xwing");
        var jek = scn.GetLSCard("jek");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, xwing);
        scn.BoardAsPilot(xwing, jek);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper);
        scn.ApplyAdHocModifier(new ForceDrainModifier(trooper, Filters.sameCardId(yavin), -1, scn.LS));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(yavin));
        scn.LSForceDrainAt(yavin);
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void RebelAcesDoesNotAddAstromechCapacityToFalconOrArtooDetooInRed5() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        var yavin = scn.GetLSCard("yavin");
        var falcon = scn.GetLSCard("falcon");
        var red5 = scn.GetLSCard("red5");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, falcon, red5);
        assertEquals(0, scn.game().getModifiersQuerying().getAstromechCapacity(scn.gameState(), falcon));
        assertEquals(0, scn.game().getModifiersQuerying().getAstromechCapacity(scn.gameState(), red5));
    }

    @Test
    public void RebelAcesDeploysCloseAirSupportFromReserve() {
        var scn = GetScenario();
        var aces = scn.GetLSCard("aces");
        scn.StartGame();
        scn.MoveCardsToHand(aces);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(aces);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        scn.PassAllResponses();
        var close = MissionTestSupport.putInReserve(scn, 9145, scn.LS, MissionTestSupport.closeAirSupport());
        scn.LSActivateForceCheat(5);
        assertTrue(scn.LSActionAvailable("Deploy Close Air Support from Reserve Deck"));
        scn.LSUseCardAction(aces, "Deploy Close Air Support from Reserve Deck");
        assertTrue(scn.LSHasCardChoiceAvailable(close));
        scn.LSChooseCard(close);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, close);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSActionAvailable("Deploy Close Air Support from Reserve Deck"));
    }
}
