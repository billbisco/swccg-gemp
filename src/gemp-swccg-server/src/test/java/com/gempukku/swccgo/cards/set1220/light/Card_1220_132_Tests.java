package com.gempukku.swccgo.cards.set1220.light;

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

public class Card_1220_132_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("polarized", "1220_132");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("barrier", "1_249");
                    put("crew", "1_266");
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
    public void PolarizedNegativePowerCouplingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("polarized").getBlueprint();

        assertEquals("Polarized Negative Power Coupling", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.GRABBER);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PolarizedNegativePowerCouplingDeploysOnTable() {
        var scn = GetScenario();
        var polarized = scn.GetLSCard("polarized");

        scn.StartGame();
        scn.MoveCardsToLSHand(polarized);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(polarized));
        scn.LSDeployCard(polarized);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, polarized);
    }

    @Test
    public void PolarizedNegativePowerCouplingGrabsUsedInterruptJustPlayed() {
        var scn = GetScenario();
        var polarized = scn.GetLSCard("polarized");
        var luke = scn.GetLSCard("luke");
        var crew = scn.GetDSCard("crew");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(polarized);
        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToDSHand(crew);
        scn.DSActivateForceCheat(2);
        scn.SkipToPhase(Phase.CONTROL);

        String lsText = scn.LSGetDecision() == null ? "none" : scn.LSGetDecision().getText();
        String dsText = scn.DSGetDecision() == null ? "none" : scn.DSGetDecision().getText();
        assertTrue("DS has no actions after skip; LS=" + scn.GetLSAvailableActions()
                        + " DS=" + scn.GetDSAvailableActions()
                        + " LSdec=" + lsText + " DSdec=" + dsText
                        + " phase=" + scn.gameState().getCurrentPhase()
                        + " player=" + scn.gameState().getCurrentPlayerId(),
                scn.DSAnyActionsAvailable());
        assertTrue(scn.DSCardPlayAvailable(crew));
        scn.DSPlayCard(crew);
        scn.PassForceUseResponses();

        lsText = scn.LSGetDecision() == null ? "none" : scn.LSGetDecision().getText();
        dsText = scn.DSGetDecision() == null ? "none" : scn.DSGetDecision().getText();
        assertTrue("LS grab missing; LS=" + scn.GetLSAvailableActions()
                        + " DS=" + scn.GetDSAvailableActions()
                        + " LSdec=" + lsText + " DSdec=" + dsText,
                scn.GetLSAvailableActions().stream()
                        .anyMatch(text -> text != null && text.toLowerCase().contains("grab")));
        scn.LSChooseAction("Grab");
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(polarized, crew));
        assertInZone(Zone.STACKED, crew);
    }

    @Test
    public void PolarizedNegativePowerCouplingPlacesStackedCardInUsedPileForTwoForce() {
        var scn = GetScenario();
        var polarized = scn.GetLSCard("polarized");
        var barrier = scn.GetDSCard("barrier");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(polarized);
        scn.StackCardsOn(polarized, barrier);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(polarized, "Place a card here in Used Pile"));
        scn.LSUseCardAction(polarized, "Place a card here in Used Pile");
        if (scn.LSHasCardChoiceAvailable(barrier)) {
            scn.LSChooseCard(barrier);
        }
        scn.PassAllResponses();
        assertTrue(barrier.getZone() == Zone.USED_PILE || barrier.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
