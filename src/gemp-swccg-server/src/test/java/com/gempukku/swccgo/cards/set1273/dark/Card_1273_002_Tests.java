package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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

public class Card_1273_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127"); // Tatooine (parsec 7)
                    put("falcon", "1_143"); // Millennium Falcon
                    put("han", "1_11"); // pilot
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("pellaeon", "1273_2");
                    put("kessel", "1_288"); // Kessel (parsec 8)
                    put("tie", "1_304"); // TIE Fighter
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
    public void AdmiralPellaeonStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("pellaeon").getBlueprint();
        assertEquals("Admiral Pellaeon", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ADMIRAL);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AdmiralPellaeonAddsTwoToPowerOfWhatHePilots() {
        var scn = GetScenario();
        var pellaeon = scn.GetDSCard("pellaeon");
        var tie = scn.GetDSCard("tie");
        var kessel = scn.GetDSCard("kessel");
        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, tie);
        int unpiloted = scn.GetPower(tie);
        scn.BoardAsPilot(tie, pellaeon);
        assertEquals(unpiloted + 2, scn.GetPower(tie));
    }

    @Test
    public void AdmiralPellaeonRevealsFromHandToDeployWithJustifierForMinusOneEach() {
        var scn = GetScenario();
        var pellaeon = scn.GetDSCard("pellaeon");
        var kessel = scn.GetDSCard("kessel");
        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToDSHand(pellaeon);
        scn.SkipToDSTurn(Phase.DEPLOY);
        var justifier = MissionTestSupport.putInReserve(scn, 7302, scn.DS, LegacyVb73DarkTestSupport.justifier());
        int forceBefore = scn.GetDSForcePileCount();
        var qs = scn.game().getModifiersQuerying();
        float fullCost = qs.getDeployCost(scn.gameState(), justifier, justifier, kessel, false, null, false, 0, null, false) + qs.getDeployCost(scn.gameState(), pellaeon);
        assertTrue(scn.DSCardActionAvailable(pellaeon, "Reveal to deploy with Justifier"));
        scn.DSUseCardAction(pellaeon, "Reveal to deploy with Justifier");
        for (int i = 0; i < 10 && pellaeon.getZone() == Zone.HAND; i++) {
            if (scn.DSHasCardChoiceAvailable(justifier)) {
                scn.DSChooseCard(justifier);
            } else if (scn.DSHasCardChoiceAvailable(kessel)) {
                scn.DSChooseCard(kessel);
            } else if (scn.DSChoiceAvailable("Pilot")) {
                scn.DSChoose("Pilot");
            } else {
                scn.PassResponses();
            }
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAboard(justifier, pellaeon));
        // each deploys for -1 Force
        assertEquals(forceBefore - (fullCost - 2), scn.GetDSForcePileCount(), scn.epsilon);
    }

    @Test
    public void AdmiralPellaeonMakesOpponentsPassengersHereForfeitZero() {
        var scn = GetScenario();
        var pellaeon = scn.GetDSCard("pellaeon");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.BoardAsPassenger(falcon, luke);
        int lukeForfeit = scn.GetForfeit(luke);
        assertTrue(lukeForfeit > 0);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, pellaeon);
        assertEquals(0, scn.GetForfeit(luke));
        assertTrue(scn.GetForfeit(han) > 0);
    }

    @Test
    public void AdmiralPellaeonMovesJustifierAwayFromBattleAsReactOncePerGame() {
        var scn = GetScenario();
        var pellaeon = scn.GetDSCard("pellaeon");
        var tatooine = scn.GetLSCard("tatooine");
        var kessel = scn.GetDSCard("kessel");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(kessel);
        var justifier = MissionTestSupport.putInHand(scn, 7303, scn.DS, LegacyVb73DarkTestSupport.justifier());
        scn.MoveCardsToLocation(tatooine, justifier, falcon);
        scn.BoardAsPilot(justifier, pellaeon);
        scn.BoardAsPilot(falcon, han);
        scn.SkipToLSTurn(Phase.BATTLE);
        int lostBefore = scn.GetDSLostPileCount();
        scn.LSInitiateBattle(tatooine);
        assertTrue(scn.DSActionAvailable("Move Justifier away"));
        scn.DSChooseAction("Move Justifier away");
        for (int i = 0; i < 10 && scn.IsAboard(justifier, pellaeon) && justifier.getAtLocation() != kessel; i++) {
            if (scn.DSHasCardChoiceAvailable(kessel)) {
                scn.DSChooseCard(kessel);
            } else if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayForceLossFromForcePile();
            } else {
                scn.PassResponses();
            }
        }
        assertEquals(kessel, justifier.getAtLocation());
        assertEquals(lostBefore + 1, scn.GetDSLostPileCount());
    }
}
