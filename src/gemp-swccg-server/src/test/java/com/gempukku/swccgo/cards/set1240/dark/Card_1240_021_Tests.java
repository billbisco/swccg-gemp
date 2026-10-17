package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.common.Zone;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1240_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ywing", "1_147");
                }},
                new HashMap<>() {{
                    put("blown", "1240_21");
                    put("evax", "1240_12");
                    put("motti", "1_164");
                    put("room", "1240_51");
                    put("tatooine", "1_289");
                }},
                10,
                10,
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
    public void BlownClearStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("blown").getBlueprint();

        assertEquals("Blown Clear", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SPECIAL_EDITION);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BlownClearStacksCouncilMemberAndSubtractsStarshipForfeit() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var blown = scn.GetDSCard("blown");
        var evax = scn.GetDSCard("evax");
        var motti = scn.GetDSCard("motti");
        var room = scn.GetDSCard("room");
        var ywing = scn.GetLSCard("ywing");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToDSSideOfTable(blown);
        scn.MoveCardsToLocation(room, evax);
        scn.MoveCardsToDSSideOfTable(ywing);
        scn.StackCardsOn(blown, motti);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.CONTROL);

        assertEquals(Zone.STACKED, motti.getZone());
        assertTrue(scn.HasKeyword(motti, Keyword.ADMIRAL));
        int forfeitBefore = scn.GetForfeit(ywing);
        assertTrue(scn.DSCardActionAvailable(blown, "Subtract influence from a starship"));
        scn.DSUseCardAction(blown, "Subtract influence from a starship");
        scn.DSChooseCard(ywing);
        scn.PassAllResponses();
        assertEquals(forfeitBefore - 1, scn.GetForfeit(ywing));
    }
}
