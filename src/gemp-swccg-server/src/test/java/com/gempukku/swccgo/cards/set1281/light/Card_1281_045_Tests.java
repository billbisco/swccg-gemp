package com.gempukku.swccgo.cards.set1281.light;

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

public class Card_1281_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("errant", "1281_45");
                    put("corran", "10_5");
                    put("mirax", "10_12"); // Mirax Terrik
                    put("lando", "601_50");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void ErrantVentureStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("errant").getBlueprint();

        assertEquals("Errant Venture", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(6, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        assertEquals(6, card.getPilotCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SHIP_DOCKING_CAPABILITY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.INDEPENDENT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ErrantVentureIsImmuneToAttritionLessThanSevenWhileCorranPiloting() {
        var scn = GetScenario();
        var errant = scn.GetLSCard("errant");
        var corran = scn.GetLSCard("corran");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, errant);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), errant), scn.epsilon);

        scn.MoveCardsToLocation(yavin, corran);
        scn.BoardAsPilot(errant, corran);
        assertEquals(7, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), errant), scn.epsilon);
    }

    @Test
    public void ErrantVentureDeploysANonMaintenanceScoundrelAboardOncePerGame() {
        var scn = GetScenario();
        var errant = scn.GetLSCard("errant");
        var lando = scn.GetLSCard("lando");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        var corran = scn.GetLSCard("corran");
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, errant);
        scn.MoveCardsToLocation(yavin, corran);
        scn.BoardAsPilot(errant, corran);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(lando);

        assertTrue(scn.LSCardActionAvailable(errant, "Deploy a scoundrel aboard"));
        scn.LSUseCardAction(errant, "Deploy a scoundrel aboard");
        assertTrue(scn.LSHasCardChoiceAvailable(lando));
        scn.LSChooseCard(lando);
        scn.PassAllResponses();

        assertEquals(Zone.ATTACHED, lando.getZone());
        assertEquals(errant, lando.getAttachedTo());

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(errant, "Deploy a scoundrel aboard"));
    }

    @Test
    public void ErrantVentureDoesNotDeployAScoundrelWhileYourReflectionsIIObjectiveIsOnTable() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("errant", "1281_45");
                    put("corran", "10_5");
                    put("lando", "601_50");
                    put("yavin", "1_135");
                    put("watch", "10_26");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
        var errant = scn.GetLSCard("errant");
        var lando = scn.GetLSCard("lando");
        var yavin = scn.GetLSCard("yavin");
        var watch = scn.GetLSCard("watch");
        var corran = scn.GetLSCard("corran");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, errant);
        scn.MoveCardsToLocation(yavin, corran);
        scn.BoardAsPilot(errant, corran);
        scn.MoveCardsToLSSideOfTable(watch);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(lando);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(errant, "Deploy a scoundrel aboard"));
    }

    @Test
    public void ErrantVentureIsImmuneToAttritionLessThanSevenWhileATerrikPiloting() {
        var scn = GetScenario();
        var errant = scn.GetLSCard("errant");
        var mirax = scn.GetLSCard("mirax");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, errant);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), errant), scn.epsilon);

        scn.MoveCardsToLocation(yavin, mirax);
        scn.BoardAsPilot(errant, mirax);
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(7, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), errant), scn.epsilon);
    }
}
