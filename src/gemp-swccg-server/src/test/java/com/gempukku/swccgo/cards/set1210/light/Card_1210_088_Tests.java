package com.gempukku.swccgo.cards.set1210.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1210_088_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("biggs", "1210_88");
                    put("red3", "1_145");
                    put("canyon", "7_130");
                    put("hobbie", "3_5");
                    put("oldtimes", "7_95");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void BiggsDarklighterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("biggs").getBlueprint();

        assertEquals("Biggs Darklighter", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RED_SQUADRON);
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
    public void BiggsAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var biggs = scn.GetLSCard("biggs");
        var red3 = scn.GetLSCard("red3");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, red3);
        scn.BoardAsPilot(red3, biggs);
        assertEquals(5, scn.GetPower(red3));
    }

    @Test
    public void BiggsDeploysFreeToBeggarsCanyon() {
        var scn = GetScenario();
        var biggs = scn.GetLSCard("biggs");
        var canyon = scn.GetLSCard("canyon");

        scn.StartGame();
        scn.MoveLocationToTable(canyon);
        scn.MoveCardsToLSHand(biggs);
        scn.EnsureLSForcePile(1);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(biggs));
        scn.LSPlayCard(biggs);
        scn.LSChooseCard(canyon);
        scn.PassAllResponses();
        assertEquals(canyon, biggs.getAtLocation());
    }

    @Test
    public void BiggsDeploysFreeToSameLocationAsHobbie() {
        var scn = GetScenario();
        var biggs = scn.GetLSCard("biggs");
        var hobbie = scn.GetLSCard("hobbie");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hobbie);
        scn.MoveCardsToLSHand(biggs);
        scn.EnsureLSForcePile(1);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(biggs));
        scn.LSPlayCard(biggs);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, biggs.getAtLocation());
    }

    @Test
    public void BiggsTakesOldTimesDuringControlPhase() {
        var scn = GetScenario();
        var biggs = scn.GetLSCard("biggs");
        var oldtimes = scn.GetLSCard("oldtimes");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, biggs);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(oldtimes);
        assertTrue(scn.LSCardActionAvailable(biggs, "Take Old Times into hand"));
        scn.LSUseCardAction(biggs, "Take Old Times into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(oldtimes);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, oldtimes.getZone());
    }
}
