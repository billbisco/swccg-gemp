package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

public class Card_1106_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("levitation", "1106_43");
                    put("starting", "13_69");
                    put("belt", "1_207");
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
    public void LevitationAttackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("levitation").getBlueprint();

        assertEquals("Levitation Attack", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LevitationAttackDeploysDevicesFromUnderStartingEffect() {
        var scn = GetScenario();
        var levitation = scn.GetDSCard("levitation");
        var starting = scn.GetDSCard("starting");
        var belt = scn.GetDSCard("belt");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(starting);
        scn.StackCardsOn(starting, belt);
        scn.MoveCardsToLocation(site, trooper);
        scn.MoveCardsToDSHand(levitation);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(levitation, "Deploy devices from under Starting Effect"));
        scn.DSPlayCard(levitation, "Deploy devices from under Starting Effect");
        for (int i = 0; i < 24; i++) {
            if (belt.getAttachedTo() == trooper) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(belt)) {
                scn.DSChooseCard(belt);
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(trooper)) {
                scn.DSChooseCard(trooper);
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertEquals(trooper, belt.getAttachedTo());
    }
}
