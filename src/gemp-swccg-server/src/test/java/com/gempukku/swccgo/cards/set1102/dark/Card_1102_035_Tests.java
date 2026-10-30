package com.gempukku.swccgo.cards.set1102.dark;

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

public class Card_1102_035_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("naboo", "12_83");
                    put("obi", "1_21");
                    put("quigon", "11_10");
                }},
                new HashMap<>() {{
                    put("lack", "1102_35");
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
    public void IFindYourLackOfFaithDisturbingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("lack").getBlueprint();

        assertEquals("I Find Your Lack Of Faith Disturbing", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IFindYourLackOfFaithDisturbingCapsForceLossFromDrainsAtOpponentsNabooSitesWhenTwoJediAreOnNaboo() {
        var scn = GetScenario();
        var lack = scn.GetDSCard("lack");
        var naboo = scn.GetLSCard("naboo");
        var obi = scn.GetLSCard("obi");
        var quigon = scn.GetLSCard("quigon");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(lack);
        scn.MoveLocationToTable(naboo);
        scn.MoveCardsToLocation(naboo, obi, quigon);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertEquals(2, scn.game().getModifiersQuerying().getForceToLoseFromForceDrainLimit(scn.gameState(), scn.DS, naboo), scn.epsilon);
        assertTrue(scn.LSForceDrainAvailable(naboo));
    }
}
