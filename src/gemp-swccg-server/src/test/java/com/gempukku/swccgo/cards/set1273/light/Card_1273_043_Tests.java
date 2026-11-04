package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_043_Tests {
    StartingSetup RebelStrikeTeamObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("rst", "1273_43");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("endor", "8_068");
                    put("backdoor", "8_069");
                    put("shieldisdown", "1273_36");
                    put("bunker", "8_070");
                    put("dtsg", "8_043");
                    put("beezer", "8_004");
                    put("orrimaarko", "1230_77");
                    put("padawan", "11_006");
                    put("redone", "1_144");
                    put("rebel", "1_028");
                    put("yavin", "6_087");
                    put("bwing", "7_140");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("dsbunker", "8_160");
                    put("endorshield", "9_124");
                }},
                10, 10,
                RebelStrikeTeamObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static void flip(VirtualTableScenario scn) {
        var rst = scn.GetLSCard("rst");
        if (!Vb81RestTestSupport.holdsDecision(scn, scn.LS, "Choose")) {
            scn.SkipToLSTurn(Phase.CONTROL);
        }
        scn.LSExecuteAdHocEffect(rst, new FlipCardEffect(new TopLevelGameTextAction(rst, rst.getCardId()), rst));
        scn.PassAllResponses();
        assertEquals("Garrison Destroyed", rst.getBlueprint().getTitle());
    }

    @Test
    public void RebelStrikeTeamVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rst").getBlueprint();

        assertEquals(Title.Rebel_Strike_Team, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.ENDOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GarrisonDestroyedVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        scn.StartGame();
        var card = scn.GetLSCard("rst").getOtherSideBlueprint();

        assertEquals("Garrison Destroyed", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.ENDOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
    }

    @Test
    public void RebelStrikeTeamVDeploysEndorBackDoorAndTheShieldIsDown() {
        var scn = GetScenario();
        scn.StartGame();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetLSCard("rst").getZone());
        assertEquals(Zone.LOCATIONS, scn.GetLSCard("endor").getZone());
        assertEquals(Zone.LOCATIONS, scn.GetLSCard("backdoor").getZone());
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetLSCard("shieldisdown").getZone());
    }

    @Test
    public void RebelStrikeTeamVEpisodeICharactersAreLostForRemainderOfGame() {
        var scn = GetScenario();
        var padawan = scn.GetLSCard("padawan");
        scn.StartGame();
        scn.MoveCardsToLSHand(padawan);
        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(padawan);
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("backdoor"))) {
            scn.LSChooseCard(scn.GetLSCard("backdoor"));
        }
        scn.PassAllResponses();
        assertTrue(Vb81RestTestSupport.inZone(padawan, Zone.LOST_PILE));
    }

    @Test
    public void RebelStrikeTeamVForceGenerationPlusOneAtYourEndorSites() {
        var scn = GetScenario();
        var backdoor = scn.GetLSCard("backdoor");
        scn.StartGame();
        assertEquals(backdoor.getBlueprint().getIconCount(Icon.LIGHT_FORCE) + 1,
                scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), backdoor, scn.LS), scn.epsilon);
        flip(scn);
        assertEquals(backdoor.getBlueprint().getIconCount(Icon.LIGHT_FORCE) + 1,
                scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), backdoor, scn.LS), scn.epsilon);
    }

    @Test
    public void RebelStrikeTeamVUsesOneForceToTakeEndorEpicEventIntoHand() {
        var scn = GetScenario();
        var rst = scn.GetLSCard("rst");
        var dtsg = scn.GetLSCard("dtsg");
        scn.StartGame();
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(dtsg);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(rst, "Take [Endor] Epic Event"));
        scn.LSUseCardAction(rst, "Take [Endor] Epic Event");
        if (Vb81RestTestSupport.passUntil(scn, () -> scn.LSHasCardChoiceAvailable(dtsg))) {
            scn.LSChooseCard(dtsg);
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, dtsg.getZone());
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void RebelStrikeTeamVForceDrainsOnEndorMinusOneAndYourBunkerGainsLightSideIcon() {
        var scn = GetScenario();
        var backdoor = scn.GetLSCard("backdoor");
        var bunker = scn.GetLSCard("bunker");
        var rebel = scn.GetLSCard("rebel");
        scn.StartGame();
        scn.MoveLocationToTable(bunker);
        scn.MoveCardsToLocation(backdoor, rebel);
        assertEquals(bunker.getBlueprint().getIconCount(Icon.LIGHT_FORCE) + 1, scn.game().getModifiersQuerying().getIconCount(scn.gameState(), bunker, Icon.LIGHT_FORCE));
        float drain = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), backdoor, scn.LS);
        flip(scn);
        assertEquals(drain + 1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), backdoor, scn.LS), scn.epsilon);
        assertEquals(bunker.getBlueprint().getIconCount(Icon.LIGHT_FORCE), scn.game().getModifiersQuerying().getIconCount(scn.gameState(), bunker, Icon.LIGHT_FORCE));
    }

    @Test
    public void RebelStrikeTeamVAfterWinningBattleAtBunkerMayUseEpicEventToBlowAwayBunkerAndFlips() {
        var scn = GetScenario();
        var rst = scn.GetLSCard("rst");
        var bunker = scn.GetLSCard("bunker");
        var dtsg = scn.GetLSCard("dtsg");
        var orrimaarko = scn.GetLSCard("orrimaarko");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveLocationToTable(bunker);
        scn.AttachCardsTo(bunker, dtsg);
        scn.MoveCardsToLocation(bunker, orrimaarko, trooper);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(bunker);
        scn.SkipToDamageSegment(false);
        for (int i = 0; i < 30 && !Vb73LightTestSupport.holds(() -> scn.LSActionAvailable("Attempt to 'blow away' Bunker")); ++i) {
            if (scn.AwaitingDSAttritionPayment()) {
                scn.DSPayAttritionFromCardInPlay(trooper);
            }
            else if (scn.AwaitingDSBattleDamagePayment()) {
                scn.DSPayRemainingBattleDamageFromReserveDeck();
            }
            else if (scn.GetCurrentDecision() == null) {
                break;
            }
            else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        assertTrue(scn.LSActionAvailable("Attempt to 'blow away' Bunker"));
        // two Epic Event destiny draws: 7 + 6 = 13 > 12
        scn.PrepareLSDestiny(6);
        scn.PrepareLSDestiny(7);
        scn.LSChooseAction("Attempt to 'blow away' Bunker");
        for (int i = 0; i < 100 && !"Garrison Destroyed".equals(rst.getBlueprint().getTitle()); ++i) {
            if (Vb73LightTestSupport.holds(scn::AwaitingDSForceLossPayment)) {
                Vb73LightTestSupport.payDSForceLoss(scn);
            }
            else if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText().contains("relocate")
                    && scn.GetCurrentDecision().getDecisionType() == com.gempukku.swccgo.logic.decisions.AwaitingDecisionType.MULTIPLE_CHOICE) {
                scn.PlayerChooseNo(scn.GetDecidingPlayer());
            }
            else if (scn.GetCurrentDecision() == null) {
                break;
            }
            else if (scn.GetCurrentDecision().getDecisionType() == com.gempukku.swccgo.logic.decisions.AwaitingDecisionType.ARBITRARY_CARDS) {
                Vb73LightTestSupport.chooseFirstSelectable(scn, scn.GetDecidingPlayer());
            }
            else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        assertEquals("decision=" + (scn.GetCurrentDecision() == null ? null : scn.GetCurrentDecision().getText()), "Garrison Destroyed", rst.getBlueprint().getTitle());
    }

    @Test
    public void GarrisonDestroyedVCancelsEndorShield() {
        var scn = GetScenario();
        var dsbunker = scn.GetDSCard("dsbunker");
        var endorshield = scn.GetDSCard("endorshield");
        scn.StartGame();
        scn.MoveLocationToTable(dsbunker);
        scn.AttachCardsTo(dsbunker, endorshield);
        flip(scn);
        assertFalse(endorshield.getZone().isInPlay());
    }

    @Test
    public void GarrisonDestroyedVWhenYouInitiateBattleWithScoutMayRetrieveTwoForce() {
        var scn = GetScenario();
        var beezer = scn.GetLSCard("beezer");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        flip(scn);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("rebel"));
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("redone"));
        scn.MoveCardsToLocation(site, beezer, trooper);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        int lost = scn.GetLSLostPileCount();
        scn.LSInitiateBattle(site);
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.LSActionAvailable("Retrieve 2 Force")));
        scn.LSChooseAction("Retrieve 2 Force");
        Vb81RestTestSupport.passUntil(scn, () -> scn.GetLSLostPileCount() == lost - 2);
        assertEquals(lost - 2, scn.GetLSLostPileCount());
    }

    @Test
    public void GarrisonDestroyedVOncePerGameDeploysRebelStarshipForHalfPrintedDeployCost() {
        var scn = GetScenario();
        var rst = scn.GetLSCard("rst");
        var redone = scn.GetLSCard("bwing");
        var endor = scn.GetLSCard("endor");
        scn.StartGame();
        flip(scn);
        scn.MoveLocationToTable(scn.GetLSCard("yavin"));
        scn.MoveCardsToLSHand(redone);
        scn.LSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        int printed = (int) (float) redone.getBlueprint().getDeployCost();
        assertTrue(scn.LSCardActionAvailable(rst, "Deploy Rebel starship from hand"));
        scn.LSUseCardAction(rst, "Deploy Rebel starship from hand");
        for (int i = 0; i < 10 && !redone.getZone().isInPlay(); ++i) {
            if (scn.LSHasCardChoiceAvailable(redone)) {
                scn.LSChooseCard(redone);
            }
            else if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("yavin"))) {
                scn.LSChooseCard(scn.GetLSCard("yavin"));
            }
            else {
                        scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        scn.PassAllResponses();
        assertTrue(redone.getZone().isInPlay());
        assertEquals(force - printed / 2, scn.GetLSForcePileCount());
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, rst, "Deploy Rebel starship from Reserve Deck"));
    }
}
