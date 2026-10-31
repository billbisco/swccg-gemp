package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1106_033_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("c3po", "1_5");
                    put("r2", "2_14");
                }},
                new HashMap<>() {{
                    put("e3po", "1106_33");
                    put("echuta", "5_138");
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
    public void E3POStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("e3po").getBlueprint();

        assertEquals("E-3PO", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.CLOUD_CITY);        }});
        assertTrue(card.getModelTypes().contains(ModelType.PROTOCOL));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void E3POCancelsC3POGameTextWhileWithC3PO() {
        var scn = GetScenario();
        var e3po = scn.GetDSCard("e3po");
        var c3po = scn.GetLSCard("c3po");
        var r2 = scn.GetLSCard("r2");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, c3po, r2);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        assertFalse(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), c3po));
        scn.MoveCardsToLocation(site, e3po);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), c3po));
    }

    @Test
    public void E3POTakesEChuTaDuringAnyControlPhase() {
        var scn = GetScenario();
        var e3po = scn.GetDSCard("e3po");
        var echuta = scn.GetDSCard("echuta");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, e3po);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        scn.MoveCardsToTopOfDSReserveDeck(echuta);
        assertTrue(scn.DSCardActionAvailable(e3po, "Take E Chu Ta into hand from Reserve Deck"));
        scn.DSUseCardAction(e3po, "Take E Chu Ta into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(echuta);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, echuta.getZone());
    }
}
