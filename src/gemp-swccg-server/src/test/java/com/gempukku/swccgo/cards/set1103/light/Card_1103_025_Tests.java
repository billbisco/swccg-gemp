package com.gempukku.swccgo.cards.set1103.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("wokling", "1103_25");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void WoklingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("wokling").getBlueprint();

        assertEquals("Wokling", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WoklingDeploysOnTable() {
        var scn = GetScenario();
        var wokling = scn.GetLSCard("wokling");

        scn.StartGame();
        scn.MoveCardsToLSHand(wokling);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(wokling) || scn.LSDeployAvailable(wokling));
        scn.LSPlayCard(wokling);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, wokling);
    }

    @Test
    public void WoklingRetrievesOneForceWhenPlacedOutOfPlay() {
        var scn = GetScenario();
        var wokling = scn.GetLSCard("wokling");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(wokling);
        scn.MoveCardsToTopOfLSLostPile(luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(wokling, "Place out of play to retrieve 1 Force"));
        scn.LSUseCardAction(wokling, "Place out of play to retrieve 1 Force");
        scn.PassAllResponses();
        assertTrue(wokling.getZone() == Zone.OUT_OF_PLAY);
        assertTrue(luke.getZone() == Zone.USED_PILE || luke.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
