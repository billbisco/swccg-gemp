package com.gempukku.swccgo.cards.set1104.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1104_041_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("bones", "1104_41");
                    put("wampa", "3_93");
                    put("cave", "3_150");
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
    public void KraytDragonBonesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bones").getBlueprint();

        assertEquals("Krayt Dragon Bones", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KraytDragonBonesYourFerociousCreaturesAreDeployMinusTwoAndFerocityPlusTwo() {
        var scn = GetScenario();
        var bones = scn.GetDSCard("bones");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("cave");

        scn.StartGame();
        scn.MoveCardsToDSHand(wampa);
        int printed = scn.GetDeployCost(wampa);
        scn.MoveCardsToDSSideOfTable(bones);
        assertEquals(printed - 2, scn.GetDeployCost(wampa), scn.epsilon);

        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, wampa);
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void KraytDragonBonesTakesAFerociousCreatureIntoHandOncePerTurn() {
        var scn = GetScenario();
        var bones = scn.GetDSCard("bones");
        var wampa = scn.GetDSCard("wampa");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(bones);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(wampa);
        assertTrue(scn.DSCardActionAvailable(bones, "Take card into hand from Reserve Deck"));
        scn.DSUseCardAction(bones, "Take card into hand from Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if (wampa.getZone() == Zone.HAND) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(wampa)) {
                scn.DSChooseCard(wampa);
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, wampa.getZone());
    }
}
