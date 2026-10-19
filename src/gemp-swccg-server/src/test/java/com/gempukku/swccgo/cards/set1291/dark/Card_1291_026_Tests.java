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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class Card_1291_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("arena", "1291_26");
                    put("petranaki", "227_10");
                    put("trooper", "1_194");
                    put("vader", "1_168");
                    put("delivery", "5_123");
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

    private PhysicalCardImpl hostOnTable(VirtualTableScenario scn) {
        return MissionTestSupport.putOnTable(scn, 9301, scn.DS, MissionTestSupport.warHasBegun());
    }

    @Test
    public void ArenaExecutionStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("arena").getBlueprint();
        assertEquals("Arena Execution", card.getTitle());
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
    public void ArenaExecutionDeploysOnWarHasBegun() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.MoveCardsToHand(mission);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(mission));
        scn.DSDeployCard(mission);
        scn.DSChooseCard(host);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(host, mission));
    }

    @Test
    public void ArenaExecutionDoesNotCancelPremiereSpecialDelivery() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        var delivery = scn.GetDSCard("delivery");
        var petranaki = scn.GetDSCard("petranaki");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToHand(delivery);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(delivery));
        scn.DSDeployCard(delivery);
        scn.DSChooseCard(petranaki);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(petranaki, delivery));
    }

    @Test
    public void ArenaExecutionMakesPetranakiAPrison() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        var petranaki = scn.GetDSCard("petranaki");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(petranaki);
        assertTrue(scn.HasKeyword(petranaki, Keyword.PRISON));
    }

    @Test
    public void ArenaExecutionStacksExecutedCaptiveAndCompletes() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.gameState().imprisonCharacter(scn.game(), luke, petranaki);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSExecuteAdHocEffect(mission, new LoseCardFromTableEffect(new TopLevelGameTextAction(mission, mission.getCardId()), luke));
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertFalse(scn.GetStackedCards(mission).isEmpty());
    }

    @Test
    public void ArenaExecutionTurnTheTideWhenOpponentControlsPetranaki() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, mission);
    }

    @Test
    public void ArenaExecutionCancelsVirtualBlock2SpecialDelivery() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        var petranaki = scn.GetDSCard("petranaki");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(petranaki);
        var vb2 = MissionTestSupport.putInHand(scn, 9326, scn.DS, MissionTestSupport.vb2SpecialDelivery());
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(vb2));
        scn.DSDeployCard(vb2);
        scn.DSChooseCard(petranaki);
        scn.PassResponses("required");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, vb2);
    }

    @Test
    public void ArenaExecutionDeploysArenaPillarsFromReserve() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        var pillars = MissionTestSupport.putInReserve(scn, 9327, scn.DS, MissionTestSupport.arenaPillars());
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(5);
        assertTrue(scn.DSActionAvailable("Deploy Arena Pillars from Reserve Deck"));
        scn.DSUseCardAction(mission, "Deploy Arena Pillars from Reserve Deck");
        if (scn.DSHasCardChoiceAvailable(pillars)) {
            scn.DSChooseCard(pillars);
        }
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, pillars);
    }

    @Test
    public void ArenaExecutionMayImprisonInsteadOfLost() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSExecuteAdHocEffect(luke, new LoseCardFromTableEffect(new TopLevelGameTextAction(luke, luke.getCardId()), luke));
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        if (scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional")) {
            scn.LSPass();
        }
        if (scn.DSHasCardChoiceAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        assertTrue(scn.DSActionAvailable("Imprison"));
        scn.DSChooseAction("Imprison");
        scn.PassAllResponses();
        assertTrue(luke.isCaptive());
        assertTrue(luke.isImprisoned());
    }

    @Test
    public void ArenaExecutionOpponentMayReleaseChosenCaptives() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(petranaki);
        scn.gameState().imprisonCharacter(scn.game(), leia, petranaki);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        if (scn.LSActionAvailable("Release any number of captives")) {
            scn.LSUseCardAction(mission, "Release any number of captives");
            scn.LSChooseCard(luke);
            scn.PassAllResponses();
            assertFalse(luke.isCaptive());
        }
        else {
            scn.PassAllResponses();
            assertInZone(Zone.USED_PILE, mission);
        }
    }

    @Test
    public void ArenaExecutionRewardAdds1ToAttritionAgainstOpponent() {
        var scn = GetScenario();
        var mission = scn.GetDSCard("arena");
        var petranaki = scn.GetDSCard("petranaki");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        scn.StartGame();
        var host = hostOnTable(scn);
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(petranaki);
        scn.StackCardsFaceDownOn(mission, leia);
        scn.MoveCardsToLocation(petranaki, vader);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(petranaki);
        scn.PrepareDSDestiny(1);
        scn.SkipToDamageSegment(true);
        assertTrue(scn.GetUnpaidLSAttrition() >= 1);
    }
}
