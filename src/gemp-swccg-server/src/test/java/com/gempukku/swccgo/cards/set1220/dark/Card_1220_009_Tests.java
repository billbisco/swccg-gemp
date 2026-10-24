package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

public class Card_1220_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("smuggler", "7_38");
                }},
                new HashMap<>() {{
                    put("vandolay", "1220_9");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
                    put("spice", "2_125");
                    put("immediate", "5_116");
                    put("prison", "1_284");
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
    public void CorporalVandolayStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("vandolay").getBlueprint();

        assertEquals("Corporal Vandolay", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.DAGOBAH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CorporalVandolayAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var vandolay = scn.GetDSCard("vandolay");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, vandolay);
        assertEquals(11, scn.GetPower(devastator));
    }

    @Test
    public void CorporalVandolayReducesOpponentsSmugglersForfeitByTwo() {
        var scn = GetScenario();
        var vandolay = scn.GetDSCard("vandolay");
        var smuggler = scn.GetLSCard("smuggler");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vandolay, smuggler);
        assertEquals(1, scn.GetForfeit(smuggler));
    }

    @Test
    public void CorporalVandolayTakesSpiceMinesOfKesselDuringAnyControlPhase() {
        var scn = GetScenario();
        var vandolay = scn.GetDSCard("vandolay");
        var spice = scn.GetDSCard("spice");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vandolay);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        scn.MoveCardsToTopOfDSReserveDeck(spice);
        assertTrue(scn.DSCardActionAvailable(vandolay, "Take Spice Mines Of Kessel into hand"));
        scn.DSUseCardAction(vandolay, "Take Spice Mines Of Kessel into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(spice);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, spice.getZone());
    }

    @Test
    public void CorporalVandolayTakesImmediateEffectAndOrPrisonOncePerGame() {
        var scn = GetScenario();
        var vandolay = scn.GetDSCard("vandolay");
        var immediate = scn.GetDSCard("immediate");
        var prison = scn.GetDSCard("prison");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vandolay);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(immediate, prison);
        assertTrue(scn.DSCardActionAvailable(vandolay, "Take Immediate Effect and/or prison into hand"));
        scn.DSUseCardAction(vandolay, "Take Immediate Effect and/or prison into hand");
        for (int i = 0; i < 20; i++) {
            if (immediate.getZone() == Zone.HAND || prison.getZone() == Zone.HAND) {
                break;
            }
            if (immediate.getZone() != Zone.HAND && prison.getZone() != Zone.HAND
                    && scn.DSHasCardChoiceAvailable(immediate) && scn.DSHasCardChoiceAvailable(prison)) {
                scn.DSChooseCards(immediate, prison);
                continue;
            }
            if (immediate.getZone() != Zone.HAND && scn.DSHasCardChoiceAvailable(immediate)) {
                scn.DSChooseCard(immediate);
                continue;
            }
            if (prison.getZone() != Zone.HAND && scn.DSHasCardChoiceAvailable(prison)) {
                scn.DSChooseCard(prison);
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
            scn.PassAllResponses();
            scn.PassCardAndForceUseResponses();
        }
        assertTrue(immediate.getZone() == Zone.HAND || prison.getZone() == Zone.HAND);
    }
}
