package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hut", "1_134"); // Tatooine: Obi-Wan's Hut (non-battleground)
                    put("tatooine", "1_127"); // Tatooine system
                    put("xwing", "1_146"); // Red 1? any X-wing
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("onthehunt", "1281_12");
                    put("deathmark", "1281_10");
                    put("bane", "1281_2"); // assassin, ability 4
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private com.gempukku.swccgo.game.PhysicalCardImpl putContractKillers(VirtualTableScenario scn, int id) {
        return MissionTestSupport.putOnTable(scn, id, scn.DS, Card_1281_010_Tests.contractKillers());
    }

    @Test
    public void OnTheHuntStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("onthehunt").getBlueprint();
        assertEquals("On The Hunt", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void OnTheHuntDeploysOnContractKillers() {
        var scn = GetScenario();
        var onthehunt = scn.GetDSCard("onthehunt");
        scn.StartGame();
        var host = putContractKillers(scn, 98201);
        scn.MoveCardsToHand(onthehunt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(onthehunt));
        scn.DSDeployCard(onthehunt);
        if (scn.DSHasCardChoiceAvailable(host)) {
            scn.DSChooseCard(host);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(host, onthehunt));
    }

    @Test
    public void OnTheHuntMayNotDeployWithoutContractKillers() {
        var scn = GetScenario();
        var onthehunt = scn.GetDSCard("onthehunt");
        scn.StartGame();
        scn.MoveCardsToHand(onthehunt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(onthehunt));
    }

    @Test
    public void OnTheHuntAssassinsAreDefenseValuePlusOne() {
        var scn = GetScenario();
        var onthehunt = scn.GetDSCard("onthehunt");
        var bane = scn.GetDSCard("bane");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), bane, luke);
        assertEquals(4, scn.GetDefense(bane));
        var host = putContractKillers(scn, 98202);
        scn.AttachCardsTo(host, onthehunt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(5, scn.GetDefense(bane));
        assertEquals(4, scn.GetDefense(luke));
    }

    @Test
    public void OnTheHuntRetrievesDeathMarkOncePerGame() {
        var scn = GetScenario();
        var onthehunt = scn.GetDSCard("onthehunt");
        var deathmark = scn.GetDSCard("deathmark");
        scn.StartGame();
        var host = putContractKillers(scn, 98203);
        scn.AttachCardsTo(host, onthehunt);
        scn.MoveCardsToTopOfOwnLostPile(deathmark);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(onthehunt, "Retrieve Death Mark"));
        scn.DSUseCardAction(onthehunt, "Retrieve Death Mark");
        for (int i = 0; i < 20 && !scn.AwaitingLSDeployPhaseActions() && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(deathmark)) {
                scn.DSChooseCard(deathmark);
            }
            else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            }
        }
        assertInZone(Zone.USED_PILE, deathmark);
        scn.MoveCardsToTopOfOwnLostPile(deathmark);
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertFalse(scn.DSCardActionAvailable(onthehunt, "Retrieve Death Mark"));
    }

    private int LifeForceLostAtEndOfOpponentsTurn(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.CONTROL);
        int before = scn.GetLSLostPile().size();
        scn.SkipToPhase(Phase.DRAW);
        for (int i = 0; i < 60 && !scn.AwaitingDSActivatePhaseActions(); i++) {
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayRemainingForceLossFromReserveDeck();
            }
            else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            }
            else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
        }
        return scn.GetLSLostPile().size() - before;
    }

    @Test
    public void OnTheHuntOpponentLosesThreeForceIfTheirMarkIsAtANonBattleground() {
        var scn = GetScenario();
        var onthehunt = scn.GetDSCard("onthehunt");
        var deathmark = scn.GetDSCard("deathmark");
        var luke = scn.GetLSCard("luke");
        var hut = scn.GetLSCard("hut");
        scn.StartGame();
        var host = putContractKillers(scn, 98204);
        scn.AttachCardsTo(host, onthehunt);
        scn.MoveLocationToTable(hut);
        scn.MoveCardsToLocation(hut, luke);
        scn.AttachCardsTo(luke, deathmark);
        assertEquals(3, LifeForceLostAtEndOfOpponentsTurn(scn));
    }

    @Test
    public void OnTheHuntOpponentLosesThreeForceIfTheirMarkIsAboardTheirStarship() {
        var scn = GetScenario();
        var onthehunt = scn.GetDSCard("onthehunt");
        var deathmark = scn.GetDSCard("deathmark");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetLSCard("tatooine");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        var host = putContractKillers(scn, 98205);
        scn.AttachCardsTo(host, onthehunt);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, luke);
        scn.AttachCardsTo(luke, deathmark);
        assertEquals(3, LifeForceLostAtEndOfOpponentsTurn(scn));
    }

    @Test
    public void OnTheHuntNoForceLossWhileMarkIsAtABattlegroundSite() {
        var scn = GetScenario();
        var onthehunt = scn.GetDSCard("onthehunt");
        var deathmark = scn.GetDSCard("deathmark");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var host = putContractKillers(scn, 98206);
        scn.AttachCardsTo(host, onthehunt);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.AttachCardsTo(luke, deathmark);
        assertEquals(0, LifeForceLostAtEndOfOpponentsTurn(scn));
    }
}
