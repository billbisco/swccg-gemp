package com.gempukku.swccgo.cards.set1210.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_090_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("brainiac", "1210_90");
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("vader", "1_168");
                    put("tarkin", "1_191");
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
    public void BrainiacStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("brainiac").getBlueprint();

        assertEquals("Brainiac", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Math.PI, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(Math.PI, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BrainiacPowerEqualsLightHandMinusDarkHand() {
        var scn = GetScenario();
        var brainiac = scn.GetLSCard("brainiac");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, brainiac);
        scn.MoveCardsToLSHand(luke, leia);
        scn.MoveCardsToDSHand(vader);
        assertEquals(1, scn.GetPower(brainiac));
    }

    @Test
    public void BrainiacAddsOneForceGenerationWhenDarkHandIsLarger() {
        var scn = GetScenario();
        var brainiac = scn.GetLSCard("brainiac");
        var vader = scn.GetDSCard("vader");
        var tarkin = scn.GetDSCard("tarkin");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, brainiac);
        float before = scn.game().getModifiersQuerying().getTotalForceGeneration(scn.gameState(), scn.LS);
        scn.MoveCardsToDSHand(vader, tarkin);
        float after = scn.game().getModifiersQuerying().getTotalForceGeneration(scn.gameState(), scn.LS);
        assertEquals(before + 1, after, scn.epsilon);
    }

    @Test
    public void BrainiacAddsOneBattleDestinyWhenHandsAreEqual() {
        var scn = GetScenario();
        var brainiac = scn.GetLSCard("brainiac");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, brainiac, stormtrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void BrainiacMayLoseACardFromHandToResetJustDrawnBattleDestinyToPi() {
        var scn = GetScenario();
        var brainiac = scn.GetLSCard("brainiac");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, brainiac, luke, stormtrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToLSHand(leia);
        scn.PrepareLSDestiny(5);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(brainiac, "Reset destiny to"));
        scn.LSUseCardAction(brainiac, "Reset destiny to");
        scn.PassAllResponses();
        assertEquals(Math.PI, scn.gameState().getBattleState().getTotalBattleDestiny(scn.game(), scn.LS), scn.epsilon);
    }
}
