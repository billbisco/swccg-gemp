package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("transport", "1111_10");
                }},
                new HashMap<>() {{
                    put("flagship", "1272_10");
                    put("decipher", "14_114");
                    put("pilot", "12_111");
                    put("pilot2", "12_111");
                    put("dantooine", "1_282");
                }},
                10,
                10,
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
    public void BlockadeFlagshipStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("flagship").getBlueprint();

        assertEquals("Blockade Flagship", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(7, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(7, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertEquals(4, card.getPilotCapacity());
        assertEquals(4, card.getPassengerCapacity());
        assertEquals(2, card.getVehicleCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.TRADE_FEDERATION_BATTLESHIP);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BLOCKADE_FLAGSHIP);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DROID_CONTROL_SHIP);
            add(Keyword.SHIP_DOCKING_CAPABILITY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.TRADE_FEDERATION);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.EPISODE_I);
            add(Icon.THEED_PALACE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BlockadeFlagshipPermanentPilotProvidesAbilityOfTwo() {
        var scn = GetScenario();
        var flagship = scn.GetDSCard("flagship");
        var dantooine = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, flagship);
        assertEquals(2, scn.GetDSAbilityAtLocation(dantooine));
    }

    @Test
    public void BlockadeFlagshipDeploysNeimoidianPilotAboardFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var flagship = scn.GetDSCard("flagship");
        var pilot = scn.GetDSCard("pilot");
        var pilot2 = scn.GetDSCard("pilot2");
        var dantooine = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, flagship);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);
        scn.MoveCardsToTopOfDSReserveDeck(pilot, pilot2);

        assertTrue(scn.DSCardActionAvailable(flagship, "Deploy card from Reserve Deck"));
        scn.DSUseCardAction(flagship, "Deploy card from Reserve Deck");
        scn.DSChooseCard(pilot);
        scn.DSChoose("Pilot");
        scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(flagship, pilot) || scn.IsAboardAsPilot(flagship, pilot2));

        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(flagship, "Deploy card from Reserve Deck"));
    }

    @Test
    public void BlockadeFlagshipIsImmuneToMediumTransport() {
        var scn = GetScenario();
        var flagship = scn.GetDSCard("flagship");
        var decipher = scn.GetDSCard("decipher");
        var transport = scn.GetLSCard("transport");

        assertTrue(flagship.getBlueprint().isImmuneToCardTitle("Medium Transport"));
        assertFalse(decipher.getBlueprint().isImmuneToCardTitle("Medium Transport"));
        assertTrue(scn.game() != null && transport != null);
    }
}
