package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("webb", "1112_14");
                }},
                new HashMap<>() {{
                    put("bay", "1_285");
                    put("deathstar", "2_143");
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
    public void JeroenWebbStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("webb").getBlueprint();

        assertEquals(Title.Jeroen_Webb, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.HOTH);
            add(Icon.PILOT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.mayDeployAsUndercoverSpy(scn.game(), scn.GetLSCard("webb")));
    }

    @Test
    public void JeroenWebbDeploysMinusTwoToAnOpponentsMobileSiteAndLowersInfluence() {
        var scn = GetScenario();
        var webb = scn.GetLSCard("webb");
        var bay = scn.GetDSCard("bay");
        var deathstar = scn.GetDSCard("deathstar");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLSHand(webb);
        float mobile = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), webb, webb, bay, false, null, false, 0, null, false);
        float ground = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), webb, webb, site, false, null, false, 0, null, false);
        assertEquals(0, mobile, scn.epsilon);
        assertEquals(2, ground, scn.epsilon);

        float before = scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, bay);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(2);
        scn.LSPlayCard(webb);
        scn.LSChooseCard(bay);
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertEquals(bay, webb.getAtLocation());
        assertTrue(webb.isUndercover());
        float undercover = scn.game().getModifiersQuerying().getTotalInfluenceAtLocation(scn.gameState(), scn.DS, bay);
        assertEquals(before, undercover, scn.epsilon);
        scn.MoveCardsToLocation(site, webb);
        if (webb.isUndercover()) {
            scn.gameState().breakCover(webb);
        }
        assertEquals(site, webb.getAtLocation());
        assertFalse(webb.isUndercover());
        float influenceChange = 0;
        for (var modifier : scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), com.gempukku.swccgo.logic.modifiers.ModifierType.TOTAL_INFLUENCE_AT_LOCATION, site)) {
            if (modifier instanceof com.gempukku.swccgo.logic.modifiers.TotalInfluenceAtLocationModifier influenceModifier) {
                influenceChange += influenceModifier.getAmount(scn.DS, scn.gameState(), scn.game().getModifiersQuerying(), site);
            }
        }
        assertEquals(-1, influenceChange, scn.epsilon);
    }
}
