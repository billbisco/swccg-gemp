package com.gempukku.swccgo.cards.set1106.dark;

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

public class Card_1106_031_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("bewil", "1106_31");
                    put("access", "5_122");
                    put("interrupt", "1_239");
                    put("docking", "1_291");
                    put("farm", "1_292");
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
    public void CaptainBewilStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bewil").getBlueprint();

        assertEquals("Captain Bewil", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
            add(Keyword.CAPTAIN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.CLOUD_CITY);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CaptainBewilStopsOpponentCharactersMovingAwayDuringYourTurn() {
        var scn = GetScenario();
        var bewil = scn.GetDSCard("bewil");
        var luke = scn.GetLSCard("luke");
        var docking = scn.GetDSCard("docking");
        var farm = scn.GetDSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(docking, bewil, luke);
        scn.SkipToDSTurn(Phase.MOVE);
        assertTrue(scn.game().getModifiersQuerying().mayNotMoveFromLocationToLocationUsingLandspeed(
                scn.gameState(), luke, docking, farm, false));
    }

    @Test
    public void CaptainBewilDeploysRestrictedAccessFromReserveDeck() {
        var scn = GetScenario();
        var bewil = scn.GetDSCard("bewil");
        var access = scn.GetDSCard("access");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bewil);
        scn.MoveCardsToTopOfDSReserveDeck(access);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(bewil, "Deploy Restricted Access from Reserve Deck"));
    }

    @Test
    public void CaptainBewilTakesInterruptFromForcePileDuringControlPhase() {
        var scn = GetScenario();
        var bewil = scn.GetDSCard("bewil");
        var interrupt = scn.GetDSCard("interrupt");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bewil);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSForcePile(interrupt);
        assertTrue(scn.DSCardActionAvailable(bewil, "Take Interrupt into hand from Force Pile"));
        scn.DSUseCardAction(bewil, "Take Interrupt into hand from Force Pile");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        if (scn.DSHasCardChoiceAvailable(interrupt)) {
            scn.DSChooseCard(interrupt);
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, interrupt.getZone());
    }
}
