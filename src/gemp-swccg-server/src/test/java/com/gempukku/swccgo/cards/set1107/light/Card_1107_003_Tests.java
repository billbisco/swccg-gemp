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

public class Card_1107_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luck", "1107_3");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void GamblersLuckStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("luck").getBlueprint();

        assertEquals("Gambler's Luck", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GamblersLuckPlaysDuringOpponentsControlPhaseWhenYourGamblerIsOnTable() {
        var scn = GetScenario();
        var luck = scn.GetLSCard("luck");
        var han = scn.GetLSCard("han");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, stormtrooper);
        scn.MoveCardsToLSHand(luck);
        scn.EnsureLSForcePile(2);
        scn.SkipToDSTurn(Phase.CONTROL);
        for (int i = 0; i < 16; i++) {
            if (scn.LSAnyActionsAvailable() && scn.LSCardPlayAvailable(luck, "Draw destiny against opponent")) {
                break;
            }
            if (scn.LSGetDecision() != null && !scn.LSAnyActionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }

        assertTrue("ls=" + scn.GetLSAvailableActions() + " dsDec=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : null),
                scn.LSAnyActionsAvailable() && scn.LSCardPlayAvailable(luck, "Draw destiny against opponent"));
        scn.LSPlayCard(luck, "Draw destiny against opponent");
        for (int i = 0; i < 30; i++) {
            if (luck.getZone() == Zone.LOST_PILE || luck.getZone() == Zone.TOP_OF_LOST_PILE) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(stormtrooper)) {
                scn.LSChooseCard(stormtrooper);
                continue;
            }
            scn.PassAllResponses();
        }
        assertInZone(Zone.LOST_PILE, luck);
    }
}
