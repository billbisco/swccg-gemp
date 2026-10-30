package com.gempukku.swccgo.cards.set1103.dark;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1103_035_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("leader", "1103_35");
                    put("vader", "1210_7");
                    put("tatooine", "1_289");
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
    public void ImOnTheLeaderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("leader").getBlueprint();

        assertEquals("I'm On The Leader", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImOnTheLeaderCancelsGameTextOfCharacterAtSameSystemAsVader() {
        var scn = GetScenario();
        var leader = scn.GetDSCard("leader");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, vader, luke);
        scn.MoveCardsToDSHand(leader);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(leader));
        scn.DSPlayCard(leader);
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        scn.DSChooseCard(luke);
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), luke));
        assertInZone(Zone.LOST_PILE, leader);
    }

    @Test
    public void ImOnTheLeaderIsNotOfferedIfVaderIsNotAtASystem() {
        var scn = GetScenario();
        var leader = scn.GetDSCard("leader");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.MoveCardsToDSHand(leader);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertFalse(scn.DSCardPlayAvailable(leader));
    }
}
