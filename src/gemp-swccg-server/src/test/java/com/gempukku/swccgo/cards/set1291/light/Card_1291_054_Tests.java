package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_054_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blueone", "1291_54");
                    put("r2", "2_14"); // R2-D2 (astromech)
                    put("r2x2", "1_24"); // R2-X2 (astromech)
                    put("extinguisher", "2_25"); // Fire Extinguisher
                    put("luke", "1_19"); // Rebel, ability 4
                    put("gold1", "1_141"); // Rebel starfighter
                    put("obi", "11_6"); // not a Rebel
                    put("wokling", "1240_93"); // [Virtual Block 4] Wokling (V)
                    put("woklingovs", "1103_25"); // Wokling (V) without the [Virtual Block 4] icon
                }},
                new HashMap<>() {{
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
    public void WhatAboutThatBlueOneStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blueone").getBlueprint();
        assertEquals("What About That Blue One?", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WhatAboutThatBlueOneDeploysOnTable() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        scn.StartGame();
        scn.MoveCardsToLSHand(blueOne);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(blueOne));
        scn.LSPlayCard(blueOne);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, blueOne);
    }

    @Test
    public void WhatAboutThatBlueOneYourTotalForceGenerationIsPlusOne() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        scn.StartGame();
        var mq = scn.game().getModifiersQuerying();
        float lsBefore = mq.getTotalForceGeneration(scn.gameState(), scn.LS);
        float dsBefore = mq.getTotalForceGeneration(scn.gameState(), scn.DS);
        scn.MoveCardsToLSSideOfTable(blueOne);
        assertEquals(lsBefore + 1, mq.getTotalForceGeneration(scn.gameState(), scn.LS), scn.epsilon);
        assertEquals(dsBefore, mq.getTotalForceGeneration(scn.gameState(), scn.DS), scn.epsilon);
    }

    @Test
    public void WhatAboutThatBlueOneTakesAnAstromechIntoHandOncePerGame() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        var r2 = scn.GetLSCard("r2");
        var r2x2 = scn.GetLSCard("r2x2");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(blueOne);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(r2, r2x2);

        assertTrue(scn.LSCardActionAvailable(blueOne, "Take an astromech into hand"));
        scn.LSUseCardAction(blueOne, "Take an astromech into hand");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoicesAvailable(r2, r2x2));
        scn.LSChooseCard(r2);
        scn.PassAllResponses();
        assertInHand(r2);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(blueOne, "Take an astromech into hand"));
    }

    @Test
    public void WhatAboutThatBlueOneRetrievesARebelOncePerGame() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        var luke = scn.GetLSCard("luke");
        var gold1 = scn.GetLSCard("gold1");
        var obi = scn.GetLSCard("obi");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(blueOne);
        scn.MoveCardsToTopOfLSLostPile(luke, gold1, obi);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(blueOne, "Retrieve a Rebel"));
        scn.LSUseCardAction(blueOne, "Retrieve a Rebel");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(luke));
        assertTrue(scn.LSHasCardChoiceAvailable(gold1));
        // Obi-Wan is shown but not selectable: only Luke and Gold 1 may be chosen
        assertEquals(2, scn.LSGetSelectableCount());
        scn.LSChooseCard(gold1);
        scn.PassAllResponses();
        assertTrue(gold1.getZone() == Zone.USED_PILE || gold1.getZone() == Zone.TOP_OF_USED_PILE);
        assertInZone(Zone.LOST_PILE, luke);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(blueOne, "Retrieve a Rebel"));
    }

    private void SetUpFireExtinguisher(VirtualTableScenario scn) {
        var r2 = scn.GetLSCard("r2");
        var extinguisher = scn.GetLSCard("extinguisher");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, r2, luke, vader);
        scn.AttachCardsTo(r2, extinguisher);
    }

    @Test
    public void FireExtinguisherMayCancelBattleOpponentInitiatesWithoutWhatAboutThatBlueOne() {
        var scn = GetScenario();
        scn.StartGame();
        SetUpFireExtinguisher(scn);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        assertTrue(scn.LSActionAvailable("Cancel battle"));
    }

    @Test
    public void WhatAboutThatBlueOneFireExtinguisherMayNotCancelBattleOpponentInitiates() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(blueOne);
        SetUpFireExtinguisher(scn);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(t -> t.contains("Cancel battle")));
        scn.PassAllResponses();
        assertTrue(scn.IsBattleStarted());
    }

    @Test
    public void WhatAboutThatBlueOneFireExtinguisherMayStillCancelBattleYouInitiate() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(blueOne);
        SetUpFireExtinguisher(scn);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetDSStartingLocation());
        if (!scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.DSPass();
        }
        assertTrue(scn.LSActionAvailable("Cancel battle"));
    }

    @Test
    public void WhatAboutThatBlueOneCancelsVirtualBlock4WoklingOnTable() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        var wokling = scn.GetLSCard("wokling");
        var woklingOvs = scn.GetLSCard("woklingovs");
        assertTrue(wokling.getBlueprint().hasIcon(Icon.LEGACY_BLOCK_4));
        assertFalse(woklingOvs.getBlueprint().hasIcon(Icon.LEGACY_BLOCK_4));
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(wokling);
        scn.MoveCardsToLSSideOfTable(blueOne);
        scn.DSActivateMaxForceAndPass();
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, wokling);
        assertInZone(Zone.SIDE_OF_TABLE, blueOne);
    }

    @Test
    public void WhatAboutThatBlueOneCancelsVirtualBlock4WoklingWhenDeployed() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        var wokling = scn.GetLSCard("wokling");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(blueOne);
        scn.MoveCardsToLSHand(wokling);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(wokling));
        scn.LSPlayCard(wokling);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, wokling);
    }

    @Test
    public void WhatAboutThatBlueOneDoesNotCancelOtherWokling() {
        var scn = GetScenario();
        var blueOne = scn.GetLSCard("blueone");
        var woklingOvs = scn.GetLSCard("woklingovs");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(woklingOvs);
        scn.MoveCardsToLSSideOfTable(blueOne);
        scn.DSActivateMaxForceAndPass();
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, woklingOvs);
    }
}
