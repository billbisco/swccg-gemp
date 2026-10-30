package com.gempukku.swccgo.cards.set1104.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1104_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bones", "1104_19");
                    put("tauntaun", "3_70");
                    put("bogwing", "4_3");
                    put("starting", "13_5");
                    put("ridge", "3_62");
                    put("jungle", "4_86");
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
    public void TauntaunBonesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bones").getBlueprint();

        assertEquals("Tauntaun Bones", card.getTitle());
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
            add(Icon.HOTH);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TauntaunBonesYourFerociousCreaturesAreDeployMinusTwoAndFerocityPlusTwo() {
        var scn = GetScenario();
        var bones = scn.GetLSCard("bones");
        var bogwing = scn.GetLSCard("bogwing");
        var jungle = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveCardsToLSHand(bogwing);
        int printed = scn.GetDeployCost(bogwing);
        scn.MoveCardsToLSSideOfTable(bones);
        assertEquals(printed - 2, scn.GetDeployCost(bogwing), scn.epsilon);

        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, bogwing);
        assertEquals(1f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), bogwing, 0f), scn.epsilon);
    }

    @Test
    public void TauntaunBonesTakesATauntaunIntoHandOncePerTurn() {
        var scn = GetScenario();
        var bones = scn.GetLSCard("bones");
        var tauntaun = scn.GetLSCard("tauntaun");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(bones);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(tauntaun);
        assertTrue(scn.LSCardActionAvailable(bones, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(bones, "Take card into hand from Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if (tauntaun.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(tauntaun)) {
                scn.LSChooseCard(tauntaun);
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, tauntaun.getZone());
    }

    @Test
    public void TauntaunBonesDeploysATauntaunFromUnderStartingEffect() {
        var scn = GetScenario();
        var bones = scn.GetLSCard("bones");
        var starting = scn.GetLSCard("starting");
        var tauntaun = scn.GetLSCard("tauntaun");
        var ridge = scn.GetLSCard("ridge");

        scn.StartGame();
        scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLSSideOfTable(starting, bones);
        scn.StackCardsOn(starting, tauntaun);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(bones, "Deploy card from under Starting Effect"));
        scn.LSUseCardAction(bones, "Deploy card from under Starting Effect");
        for (int i = 0; i < 24; i++) {
            if (tauntaun.getAtLocation() == ridge) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(tauntaun)) {
                scn.LSChooseCard(tauntaun);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(ridge)) {
                scn.LSChooseCard(ridge);
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertAtLocation(ridge, tauntaun);
    }
}
