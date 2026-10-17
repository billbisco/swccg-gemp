package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.common.Zone;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1272_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("collab", "1272_8");
                    put("evax", "1240_12");
                    put("room", "1240_51");
                    put("office", "1240_53");
                    put("site", "1_291");
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
    public void DarkCollaborationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("collab").getBlueprint();

        assertEquals("Dark Collaboration", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DarkCollaborationTakesCouncilMemberAndAddsBattlePower() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var collab = scn.GetDSCard("collab");
        var evax = scn.GetDSCard("evax");
        var room = scn.GetDSCard("room");
        var office = scn.GetDSCard("office");
        var site = scn.GetDSCard("site");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(room, evax);
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToHand(collab);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue("Take action missing. " + scn.DSGetDecision(), scn.DSCardActionAvailable(collab, "Take Council Member"));
        assertTrue("Deploy ISB site missing", scn.DSCardActionAvailable(collab, "Deploy an ISB site"));
        scn.MoveCardsToLocation(site, evax, luke);
        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(site));
    }
}
