package com.gempukku.swccgo.cards.set1115.dark;

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

public class Card_1115_048_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("dune", "1_130");
                }},
                new HashMap<>() {{
                    put("gravel", "1115_48");
                    put("vader", "1210_7");
                    put("cantina", "1_290");
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
    public void GravelStormStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("gravel").getBlueprint();

        assertEquals("Gravel Storm", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GravelStormTargetsDuneSeaAndCancelsImmunityToAttritionThereThisTurn() {
        var scn = GetScenario();
        var gravel = scn.GetDSCard("gravel");
        var vader = scn.GetDSCard("vader");
        var dune = scn.GetLSCard("dune");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(dune);
        scn.MoveCardsToLocation(dune, vader);
        scn.MoveCardsToDSHand(gravel);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
        assertTrue(scn.DSCardPlayAvailable(gravel));
        scn.DSPlayCard(gravel);
        assertTrue(scn.DSHasCardChoiceAvailable(dune));
        assertTrue(scn.DSHasCardChoiceAvailable(marketplace));
        scn.DSChooseCard(dune);
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose Force to lose")) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile() != null ? scn.GetTopOfDSForcePile() : scn.GetTopOfDSReserveDeck());
            scn.PassAllResponses();
        }

        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
        assertInZone(Zone.USED_PILE, gravel);
    }

    @Test
    public void GravelStormTargetsExteriorTatooineMarketplace() {
        var scn = GetScenario();
        var gravel = scn.GetDSCard("gravel");
        var vader = scn.GetDSCard("vader");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(marketplace, vader);
        scn.MoveCardsToDSHand(gravel);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
        assertTrue(scn.DSCardPlayAvailable(gravel));
        scn.DSPlayCard(gravel);
        assertTrue(scn.DSHasCardChoiceAvailable(marketplace));
        scn.DSChooseCard(marketplace);
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose Force to lose")) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile() != null ? scn.GetTopOfDSForcePile() : scn.GetTopOfDSReserveDeck());
            scn.PassAllResponses();
        }

        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
        assertInZone(Zone.USED_PILE, gravel);
    }

    @Test
    public void GravelStormDoesNotTargetInteriorTatooineUnlessAlsoDesert() {
        var scn = GetScenario();
        var gravel = scn.GetDSCard("gravel");
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToDSHand(gravel);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(gravel));
        scn.DSPlayCard(gravel);
        assertTrue(scn.DSHasCardChoiceAvailable(marketplace));
        assertFalse(scn.DSHasCardChoiceAvailable(cantina));
        scn.DSChooseCard(marketplace);
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose Force to lose")) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile() != null ? scn.GetTopOfDSForcePile() : scn.GetTopOfDSReserveDeck());
            scn.PassAllResponses();
        }
        assertInZone(Zone.USED_PILE, gravel);
    }
}
