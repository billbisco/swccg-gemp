package com.gempukku.swccgo.cards.set1101.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1101_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("top", "1_17");
                }},
                new HashMap<>() {{
                    put("prophetess", "1101_12");
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
    public void ProphetessStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("prophetess").getBlueprint();

        assertEquals("Prophetess", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ProphetessDeploysMinusOneAtASite() {
        var scn = GetScenario();
        var prophetess = scn.GetDSCard("prophetess");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(prophetess);
        float costToSite = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), prophetess, prophetess, site, false, null, false, 0, null, false);
        assertEquals(2, costToSite, scn.epsilon);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        assertTrue(scn.DSDeployAvailable(prophetess));
        scn.DSDeployCard(prophetess);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, prophetess.getAtLocation());
    }

    @Test
    public void ProphetessIsForfeitPlusThreeAtASite() {
        var scn = GetScenario();
        var prophetess = scn.GetDSCard("prophetess");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, prophetess);
        assertEquals(5, scn.GetForfeit(prophetess));
    }

    @Test
    public void ProphetessPeeksAtTopCardOfOpponentsReserveDeckAndMayShuffle() {
        var scn = GetScenario();
        var prophetess = scn.GetDSCard("prophetess");
        var top = scn.GetLSCard("top");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, prophetess);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(top);
        assertTrue(scn.DSCardActionAvailable(prophetess, "Peek at top card of a pile"));
        scn.DSUseCardAction(prophetess, "Peek at top card of a pile");
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(top)) {
            scn.DSChooseCard(top);
        }
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("shuffle") || scn.DSDecisionAvailable("Shuffle"));
        scn.DSChooseYes();
        scn.PassAllResponses();
        assertTrue(top.getZone() == Zone.RESERVE_DECK || top.getZone() == Zone.TOP_OF_RESERVE_DECK);
    }

    @Test
    public void ProphetessIsImmuneToAttritionLessThanThree() {
        var scn = GetScenario();
        var prophetess = scn.GetDSCard("prophetess");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, prophetess);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), prophetess), scn.epsilon);
    }
}
