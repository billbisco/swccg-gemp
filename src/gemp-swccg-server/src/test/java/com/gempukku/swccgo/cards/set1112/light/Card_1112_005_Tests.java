package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1112_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("training", "1112_5");
                    put("tremor", "1103_1");
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
    public void StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("training").getBlueprint();
        assertEquals("Commando Training", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Gameplay() {
        var scn = GetScenario();
        var card = scn.GetLSCard("training");
        scn.StartGame();
        scn.MoveCardsToLSHand(card);
        assertEquals("Commando Training", card.getTitle());
        assertTrue(card.getBlueprint().hasVirtualSuffix());
    }

    @Test
    public void CommandoTrainingDeploysAVirtualSet3EffectFromReserveDeck() {
        var scn = GetScenario();
        var training = scn.GetLSCard("training");
        var tremor = scn.GetLSCard("tremor");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(training);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(tremor);
        assertTrue(scn.LSCardActionAvailable(training, "Deploy a Virtual Set 3 Effect"));
        scn.LSUseCardAction(training, "Deploy a Virtual Set 3 Effect");
        for (int i = 0; i < 12 && tremor.getZone() != Zone.SIDE_OF_TABLE; i++) {
            if (scn.LSHasCardChoiceAvailable(tremor)) {
                scn.LSChooseCard(tremor);
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.SIDE_OF_TABLE, tremor.getZone());
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(training, "Deploy a Virtual Set 3 Effect"));
    }
}
