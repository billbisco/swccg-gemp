package com.gempukku.swccgo.cards.set1282.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("jabbanow", "1282_25");
                    put("seeking", "112_7"); // Seeking An Audience
                    put("chamber", "6_81"); // Jabba's Palace: Audience Chamber
                    put("ithorian", "6_20"); // alien, deploy 2
                    put("ithorian2", "6_20");
                    put("devaronian", "6_10");
                    put("laugh", "1282_21"); // I Know That Laugh
                }},
                new HashMap<>() {{
                }},
                30, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetAgentsScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("jabbanow", "1282_25");
                    put("devaronian", "6_10");
                    put("agents", "112_1"); // Agents In The Court
                    put("rep", "6_20"); // Rep (an Ithorian)
                    put("repcopy", "6_20");
                }},
                new HashMap<>() {{
                }},
                30, 10,
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
    public void YouWillTakeMeToJabbaNowVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("jabbanow").getBlueprint();

        assertEquals("You Will Take Me To Jabba Now", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals("'Et tu taka bu Jabba now.'", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void YouWillTakeMeToJabbaNowVDeploysSeekingAnAudienceFromReserveDeck() {
        var scn = GetScenario();
        var jabbanow = scn.GetLSCard("jabbanow");
        var seeking = scn.GetLSCard("seeking");

        scn.StartGame();
        scn.MoveCardsToLSHand(jabbanow);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToBottomOfLSReserveDeck(seeking);

        assertTrue(scn.LSCardPlayAvailable(jabbanow, "Seeking An Audience"));
        scn.LSPlayCard(jabbanow, "Seeking An Audience");
        scn.PassAllResponses();
        scn.LSChooseCard(seeking);
        scn.PassAllResponses();

        assertEquals(Zone.SIDE_OF_TABLE, seeking.getZone());
    }

    @Test
    public void YouWillTakeMeToJabbaNowVDeploysAnAlienToAudienceChamberForMinus1Force() {
        var scn = GetScenario();
        var jabbanow = scn.GetLSCard("jabbanow");
        var chamber = scn.GetLSCard("chamber");
        var ithorian = scn.GetLSCard("ithorian");

        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.MoveOutOfPlay(scn.GetLSCard("ithorian2")); // the only Ithorian left in Reserve Deck is ithorian
        scn.MoveCardsToLSHand(jabbanow);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(4);
        scn.MoveCardsToBottomOfLSReserveDeck(ithorian);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(jabbanow, "alien to Audience Chamber"));
        scn.LSPlayCard(jabbanow, "alien to Audience Chamber");
        scn.PassAllResponses();
        scn.LSChooseCard(ithorian);
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(chamber)) {
            scn.LSChooseCard(chamber);
        }
        scn.PassAllResponses();

        assertEquals(chamber, ithorian.getAtLocation());
        // Ithorian deploys for 2 - 1
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void YouWillTakeMeToJabbaNowVRevealsAliensFromHandAndIKnowThatLaughToActivateForce() {
        var scn = GetScenario();
        var jabbanow = scn.GetLSCard("jabbanow");
        var laugh = scn.GetLSCard("laugh");
        var ithorian = scn.GetLSCard("ithorian");
        var ithorian2 = scn.GetLSCard("ithorian2");
        var devaronian = scn.GetLSCard("devaronian");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.StackCardsFaceDownOn(laugh, ithorian2);
        scn.MoveCardsToLSHand(jabbanow, ithorian, devaronian);
        scn.SkipToLSTurn(Phase.CONTROL);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(jabbanow, "Reveal aliens"));
        scn.LSPlayCard(jabbanow, "Reveal aliens");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("up to 3 aliens"));
        scn.LSChooseCards(ithorian, devaronian, ithorian2);
        scn.PassAllResponses();

        assertEquals(force + 3, scn.GetLSForcePileCount());
        assertEquals(Zone.HAND, ithorian.getZone());
        assertTrue(scn.IsStackedOn(laugh, ithorian2));
    }

    @Test
    public void YouWillTakeMeToJabbaNowVMayPlaceARepStackedOnYourObjectiveInUsedPile() {
        var scn = GetAgentsScenario();
        var jabbanow = scn.GetLSCard("jabbanow");
        var agents = scn.GetLSCard("agents");
        var rep = scn.GetLSCard("rep");
        var repcopy = scn.GetLSCard("repcopy");
        var devaronian = scn.GetLSCard("devaronian");

        scn.StartGame();
        scn.MoveCardsToLSHand(jabbanow, devaronian);
        scn.SkipToLSTurn(Phase.CONTROL);
        // Agents In The Court on table with your Rep revealed and a copy of it stacked there
        scn.MoveCardsToLSSideOfTable(agents);
        scn.gameState().setRep(scn.LS, rep);
        scn.StackCardsOn(agents, repcopy);
        scn.LSPass();
        scn.DSPass();

        scn.LSPlayCard(jabbanow, "Reveal aliens");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Rep"));
        scn.LSChooseCards(repcopy);
        scn.PassAllResponses();
        scn.LSChooseCards(devaronian);
        scn.PassAllResponses();

        assertEquals(Zone.USED_PILE, repcopy.getZone() == Zone.TOP_OF_USED_PILE ? Zone.USED_PILE : repcopy.getZone());
    }
}
