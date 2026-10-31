package com.gempukku.swccgo.cards.set1107.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1107_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blues", "1107_8");
                    put("han", "1_11");
                    put("figrin", "1_9");
                    put("dice", "1_84");
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
    public void SmugglersBluesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blues").getBlueprint();

        assertEquals("Smuggler's Blues", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EFFECT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SmugglersBluesDeploysOnTable() {
        var scn = GetScenario();
        var blues = scn.GetLSCard("blues");

        scn.StartGame();
        scn.MoveCardsToLSHand(blues);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(blues));
        scn.LSDeployCard(blues);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, blues);
    }

    @Test
    public void SmugglersBluesPreventsOpponentCancelingForceDrainsWhereYouHaveAMusicianAndASmuggler() {
        var scn = GetScenario();
        var blues = scn.GetLSCard("blues");
        var han = scn.GetLSCard("han");
        var figrin = scn.GetLSCard("figrin");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(blues);
        scn.MoveCardsToLocation(site, han, figrin);
        assertTrue(scn.game().getModifiersQuerying().cantCancelForceDrainAtLocation(
                scn.gameState(), site, null, scn.DS, scn.LS));
        assertTrue(scn.game().getModifiersQuerying().cantModifyForceDrainAtLocation(
                scn.gameState(), site, null, scn.DS, scn.LS));
    }

    @Test
    public void SmugglersBluesTakesHansDiceFromReserveDeck() {
        var scn = GetScenario();
        var blues = scn.GetLSCard("blues");
        var dice = scn.GetLSCard("dice");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(blues);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(dice);
        assertTrue(scn.LSCardActionAvailable(blues, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(blues, "Take card into hand from Reserve Deck");
        int safety = 0;
        while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(dice)) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            }
            else {
                break;
            }
        }
        scn.LSChooseCard(dice);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, dice.getZone());
    }
}
