package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_1103_038_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("ket", "1103_38");
                    put("greedo", "2_89");
                    put("bay", "1_285");
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
    public void KetMalissStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ket").getBlueprint();

        assertEquals("Ket Maliss", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KetMalissDeploysOnTable() {
        var scn = GetScenario();
        var ket = scn.GetDSCard("ket");

        scn.StartGame();
        scn.MoveCardsToDSHand(ket);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(ket) || scn.DSDeployAvailable(ket));
        scn.DSPlayCard(ket);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, ket);
    }

    @Test
    public void KetMalissTakesGreedoIntoHandOncePerTurn() {
        var scn = GetScenario();
        var ket = scn.GetDSCard("ket");
        var greedo = scn.GetDSCard("greedo");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(ket);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(greedo);
        assertTrue(scn.DSCardActionAvailable(ket, "Take alien into hand from Reserve Deck"));
        scn.DSUseCardAction(ket, "Take alien into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(greedo);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, greedo.getZone());
    }

    @Test
    public void KetMalissDeploysDockingBayDuringDeployPhase() {
        var scn = GetScenario();
        var ket = scn.GetDSCard("ket");
        var bay = scn.GetDSCard("bay");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(ket);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(bay);
        assertTrue(scn.DSCardActionAvailable(ket, "Deploy docking bay from Reserve Deck"));
        scn.DSUseCardAction(ket, "Deploy docking bay from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(bay);
        scn.PassAllResponses();
        assertTrue(bay.getZone() == Zone.LOCATIONS || bay.getZone() == Zone.AT_LOCATION);
    }
}
