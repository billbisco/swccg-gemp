package com.gempukku.swccgo.cards.set1210.dark;

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

public class Card_1210_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("hyper", "1_088");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("fgst", "1210_28");
                    put("avenger", "4_166");
                    put("black2", "1_299");
                    put("tieln", "1_304");
                    put("db94", "1_291");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetGroundScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("homeone", "9_057");
                }},
                new HashMap<>() {{
                    put("fgst", "1210_28");
                    put("db94", "1_291");
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
    public void FusionGeneratorSupplyTanksStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("fgst").getBlueprint();

        assertEquals("Fusion Generator Supply Tanks", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FusionGeneratorSupplyTanksDeploysOnADockingBay() {
        var scn = GetGroundScenario();
        var fgst = scn.GetDSCard("fgst");
        var db94 = scn.GetDSCard("db94");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToDSHand(fgst);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(fgst));
        scn.DSPlayCard(fgst);
        scn.DSChooseCard(db94);
        scn.PassAllResponses();
        assertEquals(db94, fgst.getAttachedTo());
    }

    @Test
    public void FusionGeneratorSupplyTanksMayNotDeployOnMarketplace() {
        var scn = GetGroundScenario();
        var fgst = scn.GetDSCard("fgst");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(fgst);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(fgst));
        assertFalse(scn.DSHasCardChoiceAvailable(site));
    }

    @Test
    public void FusionGeneratorSupplyTanksMakesStarDestroyersAndUniqueTiesImmuneToAttritionLessThanThree() {
        var scn = GetScenario();
        var fgst = scn.GetDSCard("fgst");
        var avenger = scn.GetDSCard("avenger");
        var black2 = scn.GetDSCard("black2");
        var tieln = scn.GetDSCard("tieln");
        var db94 = scn.GetDSCard("db94");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(system, avenger, black2, tieln);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), avenger), scn.epsilon);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), black2), scn.epsilon);
        scn.AttachCardsTo(db94, fgst);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), avenger), scn.epsilon);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), black2), scn.epsilon);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), tieln), scn.epsilon);
    }

    @Test
    public void FusionGeneratorSupplyTanksCancelsHyperEscapeBeingPlayedByGoingToLostPile() {
        var scn = GetScenario();
        var fgst = scn.GetDSCard("fgst");
        var avenger = scn.GetDSCard("avenger");
        var db94 = scn.GetDSCard("db94");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var hyper = scn.GetLSCard("hyper");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.AttachCardsTo(db94, fgst);
        scn.MoveCardsToLocation(system, avenger, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.MoveCardsToLSHand(hyper);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(system));
        scn.DSUseCardAction(system, "Initiate battle");
        scn.PassForceUseResponses();
        boolean playedHyper = false;
        if (scn.LSPlayUsedInterruptAvailable(hyper)) {
            scn.LSPlayUsedInterrupt(hyper);
            playedHyper = true;
        } else if (scn.LSActionAvailable("Move starships")) {
            scn.LSChooseAction("Move starships");
            playedHyper = true;
        } else if (scn.LSActionAvailable("Hyper Escape")) {
            scn.LSChooseAction("Hyper Escape");
            playedHyper = true;
        }
        assertTrue(playedHyper);
        assertTrue(scn.DSActionAvailable("Cancel Hyper Escape") || scn.DSDecisionAvailable("optional"));
        if (scn.DSActionAvailable("Cancel Hyper Escape")) {
            scn.DSChooseAction("Cancel Hyper Escape");
        } else {
            scn.DSChooseAction("Cancel");
        }
        scn.PassAllResponses();
        assertTrue(fgst.getZone() == Zone.LOST_PILE || fgst.getZone() == Zone.TOP_OF_LOST_PILE);
        assertEquals(system, falcon.getAtLocation());
    }

    @Test
    public void FusionGeneratorSupplyTanksGoesToUsedPileWhenOpponentControlsThisLocation() {
        var scn = GetGroundScenario();
        var fgst = scn.GetDSCard("fgst");
        var homeone = scn.GetLSCard("homeone");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(homeone);
        scn.AttachCardsTo(homeone, fgst);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(Zone.ATTACHED, fgst.getZone());
        scn.MoveCardsToLocation(homeone, luke);
        scn.LSPass();
        scn.DSPass();
        if (scn.GetLSAvailableActions().stream().anyMatch(a -> a != null && a.contains("Place in Used Pile"))) {
            scn.LSChooseAction("Place in Used Pile");
        } else if (scn.GetDSAvailableActions().stream().anyMatch(a -> a != null && a.contains("Place in Used Pile"))) {
            scn.DSChooseAction("Place in Used Pile");
        }
        scn.PassAllResponses();
        assertTrue(fgst.getZone() == Zone.USED_PILE || fgst.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
