package com.gempukku.swccgo.cards.set1240.dark;

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

public class Card_1240_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("gate", "1240_18");
                    put("corridor", "1_284");
                    put("military", "1_286");
                    put("warroom", "1_287");
                    put("trooper", "1_194");
                }},
                20,
                20,
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
    public void LaserGateVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("gate").getBlueprint();

        assertEquals("Laser Gate", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LaserGateVDeploysBetweenTwoInteriorDeathStarSites() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("gate");
        var corridor = scn.GetDSCard("corridor");
        var military = scn.GetDSCard("military");

        scn.StartGame();
        scn.MoveLocationToTable(corridor);
        scn.MoveLocationToTable(military);
        scn.MoveCardsToHand(gate);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);

        assertTrue(scn.DSDeployAvailable(gate));
        scn.DSDeployCard(gate);
        scn.DSChooseCard(corridor);
        scn.DSChooseCard(military);
        scn.PassAllResponses();

        assertEquals(Zone.BETWEEN_SITES, gate.getZone());
    }

    @Test
    public void LaserGateVOpponentsCharactersDeployPlusTwoToAdjacentSitesOnly() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("gate");
        var corridor = scn.GetDSCard("corridor");
        var military = scn.GetDSCard("military");
        var warroom = scn.GetDSCard("warroom");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(corridor);
        scn.MoveLocationToTable(military);
        scn.MoveLocationToTable(warroom);

        int base = (int) scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), luke, luke, corridor, false, null, false, 0, null, false);
        scn.PlaceBetweenSites(corridor, military, gate);
        int withGate = (int) scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), luke, luke, corridor, false, null, false, 0, null, false);
        assertEquals(base + 2, withGate);
        int withGateMilitary = (int) scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), luke, luke, military, false, null, false, 0, null, false);
        assertEquals(base + 2, withGateMilitary);
        int warroomCost = (int) scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), luke, luke, warroom, false, null, false, 0, null, false);
        assertEquals(base, warroomCost);
    }

    @Test
    public void LaserGateVOpponentLosesOneForceToPassCharacterAcrossGate() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("gate");
        var corridor = scn.GetDSCard("corridor");
        var military = scn.GetDSCard("military");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(corridor);
        scn.MoveLocationToTable(military);
        scn.PlaceBetweenSites(corridor, military, gate);
        scn.MoveCardsToLocation(corridor, luke);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.EnsureLSForcePile(5);
        int lostBefore = scn.GetLSLostPileCount();
        scn.LSMoveCard(luke, military);
        scn.PassAllResponses();
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
        }
        scn.PassAllResponses();

        assertEquals(military.getCardId(), scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), luke).getCardId());
        assertEquals(lostBefore + 1, scn.GetLSLostPileCount());
    }

    @Test
    public void LaserGateVDoesNotChargeYourOwnCharacters() {
        var scn = GetScenario();
        var gate = scn.GetDSCard("gate");
        var corridor = scn.GetDSCard("corridor");
        var military = scn.GetDSCard("military");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(corridor);
        scn.MoveLocationToTable(military);
        scn.PlaceBetweenSites(corridor, military, gate);
        scn.MoveCardsToLocation(corridor, trooper);

        scn.SkipToPhase(Phase.MOVE);
        scn.EnsureDSForcePile(5);
        int lostBefore = scn.GetDSLostPileCount();
        int lsLostBefore = scn.GetLSLostPileCount();
        scn.DSMoveCard(trooper, military);
        scn.PassAllResponses();

        assertFalse(scn.AwaitingLSForceLossPayment());
        assertEquals(lostBefore, scn.GetDSLostPileCount());
        assertEquals(lsLostBefore, scn.GetLSLostPileCount());
    }
}
