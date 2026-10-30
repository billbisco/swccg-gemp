package com.gempukku.swccgo.cards.set1103.dark;

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

public class Card_1103_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("reegesk", "1103_43");
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
    public void ReegeskStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("reegesk").getBlueprint();

        assertEquals("Reegesk", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.THIEF);
            add(Keyword.SCAVENGER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ReegeskStealsFromOpponentsLostPileDuringOpponentsTurn() {
        var scn = GetScenario();
        var reegesk = scn.GetDSCard("reegesk");
        var xwing = scn.GetLSCard("xwing");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, reegesk);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();

        assertTrue(scn.DSCardActionAvailable(reegesk, "Steal card from Lost Pile"));
        scn.DSUseCardAction(reegesk, "Steal card from Lost Pile");
        scn.PassAllResponses();
        scn.DSChooseCard(xwing);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, xwing.getZone());
        assertEquals(scn.DS, xwing.getOwner());
    }
}
