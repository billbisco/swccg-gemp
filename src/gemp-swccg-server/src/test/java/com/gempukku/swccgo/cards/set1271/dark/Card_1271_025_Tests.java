package com.gempukku.swccgo.cards.set1271.dark;

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
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class Card_1271_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return GetScenario(StartingSetup.DefaultDSGroundLocation);
    }

    protected VirtualTableScenario GetScenario(StartingSetup dsStart) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("domination", "1271_25");
                    put("dantooine", "1_282");
                    put("endor", "8_157");
                    put("flagship", "4_122");
                    put("shield", "9_124");
                    put("executor", "9_157");
                    put("ops", "9_125");
                    put("tie", "1_304");
                    put("slave", "5_177");
                    put("docking", "7_282");
                    put("trooper", "1_194");
                    put("bunker", "8_160");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                dsStart,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void GalacticDominationStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("domination").getBlueprint();
        assertEquals("Galactic Domination", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CRITICAL, card.getCardSubtype());
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
    public void GalacticDominationDeploysForFreeAsCriticalMission() {
        var scn = GetScenario();
        var domination = scn.GetDSCard("domination");
        scn.StartGame();
        scn.MoveCardsToHand(domination);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(domination));
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(domination);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, domination);
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void GalacticDominationNotOfferedWithObjectiveStart() {
        var scn = GetScenario(StartingSetup.CarbonChamberObjective);
        var domination = scn.GetDSCard("domination");
        scn.StartGame();
        scn.MoveCardsToHand(domination);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSDeployAvailable(domination));
    }

    @Test
    public void GalacticDominationBlocksASecondCritical() {
        var scn = GetScenario();
        var domination = scn.GetDSCard("domination");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToHand(domination, trooper);
        var second = MissionTestSupport.putInHand(scn, 9101, scn.DS, MissionTestSupport.testCritical(Side.DARK, "Test DS Critical"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(domination);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        scn.LSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSDeployAvailable(trooper));
        assertFalse(scn.DSDeployAvailable(second));
    }

    @Test
    public void GalacticDominationCancelsFlagshipAndEndorShield() {
        var scn = GetScenario();
        var domination = scn.GetDSCard("domination");
        var flagship = scn.GetDSCard("flagship");
        var shield = scn.GetDSCard("shield");
        var executor = scn.GetDSCard("executor");
        var dantooine = scn.GetDSCard("dantooine");
        var bunker = scn.GetDSCard("bunker");
        scn.StartGame();
        scn.MoveLocationToTable(dantooine);
        scn.MoveLocationToTable(bunker);
        scn.MoveCardsToLocation(dantooine, executor);
        scn.MoveCardsToHand(domination, flagship, shield);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(domination);
        scn.PassAllResponses();
        scn.AttachCardsTo(executor, flagship);
        scn.AttachCardsTo(bunker, shield);
        scn.SkipToPhase(Phase.BATTLE);
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, flagship);
        assertInZone(Zone.LOST_PILE, shield);
    }

    @Test
    public void GalacticDominationForbidsIndependentStarships() {
        var scn = GetScenario();
        var domination = scn.GetDSCard("domination");
        var slave = scn.GetDSCard("slave");
        scn.StartGame();
        scn.MoveCardsToHand(domination, slave);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(domination);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        scn.LSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSDeployAvailable(slave));
    }

    @Test
    public void GalacticDominationDeploysBattlegroundSystemFromReserve() {
        var scn = GetScenario();
        var domination = scn.GetDSCard("domination");
        var endor = scn.GetDSCard("endor");
        scn.StartGame();
        scn.MoveCardsToHand(domination);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(domination);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        scn.LSPass();
        scn.PassAllResponses();
        scn.MoveCardsToTopOfOwnReserveDeck(endor);
        assertTrue(scn.DSActionAvailable("Deploy Executor site or battleground system"));
        scn.DSUseCardAction(domination, "Deploy Executor site or battleground system");
        scn.PassAllResponses();
        scn.DSChooseCard(endor);
        if (scn.DSDecisionAvailable("On which side")) {
            scn.DSChoose("Left");
        }
        scn.PassAllResponses();
        assertInZone(Zone.LOCATIONS, endor);
    }

    @Test
    public void GalacticDominationResetsForceDrainsTo3WithFlagshipOperations() {
        var scn = GetScenario();
        var domination = scn.GetDSCard("domination");
        var dantooine = scn.GetDSCard("dantooine");
        var executor = scn.GetDSCard("executor");
        var ops = scn.GetDSCard("ops");
        var tie = scn.GetDSCard("tie");
        scn.StartGame();
        scn.MoveCardsToHand(domination);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(domination);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, executor, tie);
        scn.AttachCardsTo(executor, ops);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(dantooine));
        scn.DSForceDrainAt(dantooine);
        scn.PassForceDrainStartResponses();
        assertEquals(3, scn.GetForceDrainTotal());
    }

    @Test
    public void GalacticDominationStopsExecutorPowerReductionWithFlagshipOperations() {
        var scn = GetScenario();
        var domination = scn.GetDSCard("domination");
        var dantooine = scn.GetDSCard("dantooine");
        var executor = scn.GetDSCard("executor");
        var ops = scn.GetDSCard("ops");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToHand(domination);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(domination);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, executor);
        scn.AttachCardsTo(executor, ops);
        int printed = scn.GetPower(executor);
        scn.ApplyAdHocModifier(new PowerModifier(luke, Filters.Executor, -5));
        assertEquals(printed, scn.GetPower(executor));
    }

    @Test
    public void GalacticDominationAddsBattleDestinyWithTIEAndCapital() {
        var scn = GetScenario();
        var domination = scn.GetDSCard("domination");
        var dantooine = scn.GetDSCard("dantooine");
        var executor = scn.GetDSCard("executor");
        var ops = scn.GetDSCard("ops");
        var tie = scn.GetDSCard("tie");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        scn.MoveCardsToHand(domination);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(domination);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, executor, tie);
        scn.MoveCardsToLocation(dantooine, xwing);
        scn.AttachCardsTo(executor, ops);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(dantooine);
        scn.PassAllResponses();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }
}
