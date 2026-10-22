package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import static org.junit.Assert.assertTrue;

public class Card_1210_112_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("redleader", "1210_112");
                    put("red1", "1_144");
                    put("dockingbay", "601_256");
                }},
                new HashMap<>() {{
                    put("dantooine", "1_282");
                    put("stormtrooper", "1_194");
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
    public void RedLeaderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("redleader").getBlueprint();

        assertEquals("Red Leader", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertTrue(card.getPersonas().contains(Persona.RED_LEADER));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RED_SQUADRON);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RedLeaderAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var redleader = scn.GetLSCard("redleader");
        var red1 = scn.GetLSCard("red1");
        var dantooine = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, red1);
        scn.BoardAsPilot(red1, redleader);
        assertEquals(5, scn.GetPower(red1));
    }

    @Test
    public void RedLeaderMakesOpponentLoseOneForceWhenTheyDeployToARelatedSite() {
        var scn = GetScenario();
        var redleader = scn.GetLSCard("redleader");
        var red1 = scn.GetLSCard("red1");
        var dockingbay = scn.GetLSCard("dockingbay");
        var dantooine = scn.GetDSCard("dantooine");
        var stormtrooper = scn.GetDSCard("stormtrooper");

        scn.StartGame();
        scn.MoveLocationToTable(dantooine);
        scn.MoveLocationToTable(dockingbay);
        scn.MoveCardsToLocation(dantooine, red1);
        scn.BoardAsPilot(red1, redleader);
        scn.MoveCardsToDSHand(stormtrooper);
        scn.EnsureDSForcePile(4);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int lifeBefore = scn.GetDSLifeForceRemaining();
        assertTrue(scn.DSCardPlayAvailable(stormtrooper));
        scn.DSPlayCard(stormtrooper);
        scn.DSChooseCard(dockingbay);
        scn.PassCardAndForceUseResponses();
        assertTrue(scn.DSDecisionAvailable("Choose effect to be performed"));
        scn.DSChooseOption("Dark Side Player loses 1 Force");
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("FORCE_LOSS") || scn.DSDecisionAvailable("ABOUT_TO_LOSE")) {
            scn.PassResponses("FORCE_LOSS_INITIATED");
            scn.PassResponses("ABOUT_TO_LOSE");
        }
        if (scn.GetDSForcePileCount() > 0 && scn.DSHasCardChoiceAvailable(scn.GetTopOfDSForcePile())) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile());
        }
        else if (scn.GetDSReserveDeckCount() > 0 && scn.DSHasCardChoiceAvailable(scn.GetTopOfDSReserveDeck())) {
            scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        }
        scn.PassAllResponses();
        assertEquals(dockingbay, stormtrooper.getAtLocation());
        assertEquals(lifeBefore - 1, scn.GetDSLifeForceRemaining());
    }
}
