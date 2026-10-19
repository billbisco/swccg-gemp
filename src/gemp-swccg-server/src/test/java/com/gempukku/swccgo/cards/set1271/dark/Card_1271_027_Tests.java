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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class Card_1271_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("yoda", "4_2");
                }},
                new HashMap<>() {{
                    put("isb", "1271_27");
                    put("trooper", "1_194");
                    put("isard", "200_92");
                    put("bay", "12_166");
                    put("storm2", "1_179");
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

    private PhysicalCardImpl officeOnTable(VirtualTableScenario scn) {
        return MissionTestSupport.putLocationOnTable(scn, 9201, scn.DS, MissionTestSupport.isbCentralOffice());
    }

    private PhysicalCardImpl isbAgentOnTable(VirtualTableScenario scn) {
        return MissionTestSupport.putInHand(scn, 9202, scn.DS, MissionTestSupport.isbAgent());
    }

    @Test
    public void InTheHandsOfTheISBStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("isb").getBlueprint();
        assertEquals("In The Hands Of The ISB", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.MISSION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>() {{ add(Keyword.DEPLOYS_ON_SITE); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SETUP);
            add(Icon.SPECIAL_EDITION);
            add(Icon.MISSION);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void InTheHandsOfTheISBDeploysOnISBCentralOffice() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        scn.StartGame();
        var office = officeOnTable(scn);
        scn.MoveCardsToHand(isb);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(isb));
        scn.DSDeployCard(isb);
        scn.DSChooseCard(office);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(office, isb));
    }

    @Test
    public void InTheHandsOfTheISBMakesTheSiteAPrison() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        scn.StartGame();
        var office = officeOnTable(scn);
        scn.AttachCardsTo(office, isb);
        assertTrue(scn.HasKeyword(office, Keyword.PRISON));
    }

    @Test
    public void InTheHandsOfTheISBRequiresAbility6ForOpponentBattleDestiny() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var office = officeOnTable(scn);
        scn.AttachCardsTo(office, isb);
        scn.MoveCardsToLocation(office, trooper);
        scn.MoveCardsToLocation(office, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(office);
        scn.PassAllResponses();
        assertEquals(0, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void InTheHandsOfTheISBAllowsBattleDestinyWhenAbilityIs6() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        var vader = scn.GetDSCard("isard");
        var luke = scn.GetLSCard("luke");
        var yoda = scn.GetLSCard("yoda");
        scn.StartGame();
        var office = officeOnTable(scn);
        scn.AttachCardsTo(office, isb);
        scn.MoveCardsToLocation(office, vader);
        scn.MoveCardsToLocation(office, luke, yoda);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(office);
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void InTheHandsOfTheISBReleasesCaptivesWhenOpponentControls() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        scn.StartGame();
        var office = officeOnTable(scn);
        scn.AttachCardsTo(office, isb);
        scn.gameState().imprisonCharacter(scn.game(), leia, office);
        scn.MoveCardsToLocation(office, luke);
        scn.EnterControlOrKeepRequired();
        scn.PassAllResponses();
        assertFalse(leia.isCaptive());
    }

    @Test
    public void InTheHandsOfTheISBAgentsMayEscortCaptives() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        var trooper = scn.GetDSCard("trooper");
        var leia = scn.GetLSCard("leia");
        scn.StartGame();
        var office = officeOnTable(scn);
        scn.AttachCardsTo(office, isb);
        scn.ApplyAdHocModifier(new KeywordModifier(isb, trooper, Keyword.ISB_AGENT));
        scn.MoveCardsToLocation(office, trooper);
        scn.CaptureCardWith(trooper, leia);
        assertTrue(leia.isCaptive());
        assertEquals(trooper, leia.getEscort());
    }

    @Test
    public void InTheHandsOfTheISBMakesOpponentLose4ForceOncePerCaptivityOnDelivery() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        var leia = scn.GetLSCard("leia");
        scn.StartGame();
        var office = officeOnTable(scn);
        scn.AttachCardsTo(office, isb);
        var agent = isbAgentOnTable(scn);
        scn.MoveCardsToLocation(office, agent);
        assertTrue(scn.HasKeyword(agent, Keyword.ISB_AGENT));
        scn.CaptureCardWith(agent, leia);
        int lsForce = scn.GetLSLifeForceRemaining();
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.DSActionAvailable("Deliver captive to prison"));
        scn.DSChooseAction("Deliver captive to prison");
        if (scn.DSDecisionAvailable("Make opponent lose 4")) {
            scn.DSChooseAction("Make opponent lose 4 Force");
        }
        scn.PassAllResponses();
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
        }
        assertTrue(leia.isImprisoned());
        assertEquals(lsForce - 4, scn.GetLSLifeForceRemaining());
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        scn.SkipToDSTurn(Phase.MOVE);
        assertFalse(scn.DSActionAvailable("Deliver captive to prison"));
    }

    @Test
    public void InTheHandsOfTheISBRelocatesEscortAndCaptiveToRelatedDockingBay() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        var leia = scn.GetLSCard("leia");
        var bay = scn.GetDSCard("bay");
        scn.StartGame();
        scn.MoveLocationToTable(bay);
        var office = officeOnTable(scn);
        scn.AttachCardsTo(office, isb);
        var agent = isbAgentOnTable(scn);
        scn.MoveCardsToLocation(office, agent);
        assertTrue(scn.HasKeyword(agent, Keyword.ISB_AGENT));
        scn.CaptureCardWith(agent, leia);
        scn.SkipToDSTurn(Phase.MOVE);
        if (scn.AwaitingLSMovePhaseActions()) {
            scn.LSPass();
        }
        scn.DSActivateForceCheat(2);
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.DSActionAvailable("Relocate escort and captive to related docking bay"));
        scn.DSUseCardAction(isb, "Relocate escort and captive to related docking bay");
        scn.DSChooseCard(agent);
        scn.DSChooseCard(bay);
        scn.PassAllResponses();
        assertAtLocation(bay, agent);
        assertTrue(leia.isCaptive());
        assertEquals(agent, leia.getEscort());
    }

    @Test
    public void InTheHandsOfTheISBRelocatesIsardPresentAgentToASiteYouControl() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        var isard = scn.GetDSCard("isard");
        var storm2 = scn.GetDSCard("storm2");
        scn.StartGame();
        var office = officeOnTable(scn);
        scn.AttachCardsTo(office, isb);
        var agent = isbAgentOnTable(scn);
        scn.MoveCardsToLocation(office, agent, isard);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), storm2);
        assertTrue(scn.HasKeyword(agent, Keyword.ISB_AGENT));
        scn.SkipToDSTurn(Phase.MOVE);
        if (scn.AwaitingLSMovePhaseActions()) {
            scn.LSPass();
        }
        scn.DSActivateForceCheat(3);
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.DSActionAvailable("Relocate ISB agent to a site you control"));
        scn.DSUseCardAction(isb, "Relocate ISB agent to a site you control");
        scn.DSChooseCard(agent);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), agent);
    }
}
