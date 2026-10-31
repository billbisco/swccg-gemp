package com.gempukku.swccgo.cards.set1112.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("faithful", "1112_11");
                    put("derlin", "3_13");
                    put("luke", "3_3");
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
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
    public void FaithfulServiceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("faithful").getBlueprint();

        assertEquals("Faithful Service", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PREMIUM);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FaithfulServiceDeploysOnTable() {
        var scn = GetScenario();
        var faithful = scn.GetLSCard("faithful");

        scn.StartGame();
        scn.MoveCardsToLSHand(faithful);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSCardPlayAvailable(faithful));
        scn.LSPlayCard(faithful);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, faithful);
    }

    @Test
    public void FaithfulServiceMakesUniqueHothEndorOrDeathStarIINonPilotWarriorsOfDestinyGreaterThanOneTroopers() {
        var scn = GetScenario();
        var faithful = scn.GetLSCard("faithful");
        var derlin = scn.GetLSCard("derlin");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(faithful);
        scn.MoveCardsToLocation(site, derlin);

        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), derlin, Keyword.TROOPER));
    }

    @Test
    public void FaithfulServiceGivesYourTroopersArmorFour() {
        var scn = GetScenario();
        var faithful = scn.GetLSCard("faithful");
        var derlin = scn.GetLSCard("derlin");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(faithful);
        scn.MoveCardsToLocation(site, derlin, trooper);

        assertEquals(4, scn.GetArmor(derlin));
        assertEquals(4, scn.GetArmor(trooper));
    }

    @Test
    public void FaithfulServiceMayPlaceTroopersInUsedPileWhenOpponentIsAboutToRemoveLastLifeForce() {
        var scn = GetScenario();
        var faithful = scn.GetLSCard("faithful");
        var derlin = scn.GetLSCard("derlin");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(faithful);
        scn.MoveCardsToLocation(site, derlin, trooper);
        scn.SkipToLSTurn(Phase.CONTROL);

        while (scn.GetDSLifeForceRemaining() > 1) {
            if (scn.GetDSForcePileCount() > 0) {
                scn.MoveCardsToTopOfDSLostPile((PhysicalCardImpl) scn.GetDSForcePile().getFirst());
            } else if (scn.GetDSUsedPileCount() > 0) {
                scn.MoveCardsToTopOfDSLostPile((PhysicalCardImpl) scn.GetDSUsedPile().getFirst());
            } else {
                scn.MoveCardsToTopOfDSLostPile(scn.GetTopOfDSReserveDeck());
            }
        }
        assertEquals(1, scn.GetDSLifeForceRemaining());
        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        scn.PassForceDrainStartResponses();

        boolean placed = false;
        for (int i = 0; i < 24; i++) {
            String lsText = scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    ? scn.LSGetDecision().getText().toLowerCase() : "";
            if (lsHasCardActions(scn) && scn.LSCardActionAvailable(faithful, "Place troopers")) {
                scn.LSUseCardAction(faithful, "Place troopers");
                placed = true;
                break;
            }
            if (lsHasCardActions(scn)) {
                var actions = scn.LSGetADParamAsList("actionText");
                if (actions != null) {
                    for (String a : actions) {
                        if (a != null && a.toLowerCase().contains("place troopers")) {
                            scn.LSChooseAction("Place troopers in Used Pile");
                            placed = true;
                            break;
                        }
                    }
                }
            }
            if (placed) {
                break;
            }
            if (lsText.contains("place troopers")) {
                if (scn.LSChoiceAvailable("Yes")) {
                    scn.LSChooseYes();
                } else {
                    scn.LSChooseAction("Place troopers in Used Pile");
                }
                placed = true;
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && !lsHasCardActions(scn) && !lsText.contains("place troopers")) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue(placed);
        for (int i = 0; i < 8; i++) {
            if (scn.GameIsFinished()) {
                break;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(derlin)) {
                scn.LSChooseCard(derlin);
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(trooper)) {
                scn.LSChooseCard(trooper);
                continue;
            }
            break;
        }
        if (!scn.GameIsFinished() && (scn.LSGetDecision() != null || scn.DSGetDecision() != null)) {
            scn.PassAllResponses();
        }
        if (!scn.GameIsFinished() && scn.DSGetDecision() != null) {
            if (scn.GetDSReserveDeckCount() > 0) {
                scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
            } else if (scn.GetDSForcePileCount() > 0) {
                scn.DSChooseCard((PhysicalCardImpl) scn.GetDSForcePile().getFirst());
            }
        }
        if (!scn.GameIsFinished() && (scn.LSGetDecision() != null || scn.DSGetDecision() != null)) {
            scn.PassAllResponses();
        }

        assertTrue(derlin.getZone() == Zone.USED_PILE || derlin.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(trooper.getZone() == Zone.USED_PILE || trooper.getZone() == Zone.TOP_OF_USED_PILE);
        assertEquals(0, scn.GetDSLifeForceRemaining());
        assertTrue(scn.GameIsFinished());
    }

    @Test
    public void FaithfulServiceDoesNotMakeAUniqueHothPilotWarriorATrooper() {
        var scn = GetScenario();
        var faithful = scn.GetLSCard("faithful");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(faithful);
        scn.MoveCardsToLocation(site, luke);

        assertFalse(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), luke, Keyword.TROOPER));
    }

    private boolean lsHasCardActions(VirtualTableScenario scn) {
        return scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null;
    }
}
