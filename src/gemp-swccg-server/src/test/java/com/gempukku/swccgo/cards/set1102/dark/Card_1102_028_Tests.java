package com.gempukku.swccgo.cards.set1102.dark;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1102_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("praji", "1102_28");
                    put("pilot1", "1210_13");
                    put("pilot2", "1_167");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
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
    public void CommanderPrajiStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("praji").getBlueprint();

        assertEquals("Commander Praji", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.COMMANDER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CommanderPrajiAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var praji = scn.GetDSCard("praji");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, praji);
        assertEquals(11, scn.GetPower(devastator));
    }

    @Test
    public void CommanderPrajiTakesImperialPilotOnceDuringControlPhase() {
        var scn = GetScenario();
        var praji = scn.GetDSCard("praji");
        var pilot1 = scn.GetDSCard("pilot1");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, praji);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(pilot1);

        assertTrue(scn.DSCardActionAvailable(praji, "Take Imperial pilot into hand"));
        scn.DSUseCardAction(praji, "Take Imperial pilot into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(pilot1);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, pilot1.getZone());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Take Imperial pilot into hand"));
    }

    @Test
    public void CommanderPrajiTakesImperialPilotTwiceWhileOnDevastator() {
        var scn = GetScenario();
        var praji = scn.GetDSCard("praji");
        var pilot1 = scn.GetDSCard("pilot1");
        var pilot2 = scn.GetDSCard("pilot2");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, praji);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(3);
        scn.MoveCardsToTopOfDSReserveDeck(pilot1, pilot2);

        assertTrue(scn.DSCardActionAvailable(praji, "Take Imperial pilot into hand"));
        scn.DSUseCardAction(praji, "Take Imperial pilot into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(pilot1);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, pilot1.getZone());
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(praji, "Take Imperial pilot into hand"));
        scn.DSUseCardAction(praji, "Take Imperial pilot into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(pilot2);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, pilot1.getZone());
        assertEquals(Zone.HAND, pilot2.getZone());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Take Imperial pilot into hand"));
    }
}
