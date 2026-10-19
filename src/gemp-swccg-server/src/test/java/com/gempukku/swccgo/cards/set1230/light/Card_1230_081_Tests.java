package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_081_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tanus", "1230_81");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("storm2", "1_179");
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
    public void TanusSpijekStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tanus").getBlueprint();

        assertEquals("Tanus Spijek", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TanusSpijekOpponentLosesForceWhenDeployedToASite() {
        var scn = GetScenario();
        var tanus = scn.GetLSCard("tanus");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.MoveCardsToLSHand(tanus);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());

        int dsForceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(tanus));
        scn.LSPlayCard(tanus);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();

        assertAtLocation(site, tanus);
        assertEquals(dsForceBefore - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void TanusSpijekOpponentLosesOneForcePerDarkSideCharacterPresent() {
        var scn = GetScenario();
        var tanus = scn.GetLSCard("tanus");
        var trooper = scn.GetDSCard("trooper");
        var storm2 = scn.GetDSCard("storm2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, storm2);
        scn.MoveCardsToLSHand(tanus);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int dsForceBefore = scn.GetDSForcePileCount();
        scn.LSPlayCard(tanus);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose Force to lose")) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile());
            scn.PassAllResponses();
        }

        assertEquals(dsForceBefore - 2, scn.GetDSForcePileCount());
    }

    @Test
    public void TanusSpijekLoseForceIsOncePerGame() {
        var scn = GetScenario();
        var tanus = scn.GetLSCard("tanus");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.MoveCardsToLSHand(tanus);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(tanus);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();

        scn.MoveCardsToTopOfLSLostPile(tanus);
        scn.MoveCardsToLSHand(tanus);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(tanus);
        scn.LSChooseCard(site);
        scn.PassAllResponses();

        assertFalse(scn.DSDecisionAvailable("Choose Force to lose"));
        assertAtLocation(site, tanus);
    }

    @Test
    public void TanusSpijekDrawsOneBattleDestinyIfUnableToOtherwise() {
        var scn = GetScenario();
        var tanus = scn.GetLSCard("tanus");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tanus, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();

        assertEquals(1, scn.GetLSBattleDestinyCount());
    }
}
