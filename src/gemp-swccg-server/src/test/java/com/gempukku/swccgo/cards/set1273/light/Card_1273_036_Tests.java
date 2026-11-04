package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shieldisdown", "1273_36");
                    put("endor", "8_068");
                    put("bunker", "8_070");
                    put("dtsg", "8_043");
                    put("beezer", "8_004");
                    put("beezer2", "8_001");
                    put("obiwan", "1_021");
                    put("obiwan", "1_021");
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
    public void TheShieldIsDownVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("shieldisdown").getBlueprint();

        assertEquals("The Shield Is Down!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheShieldIsDownVDeploysOnlyIfVirtualRebelStrikeTeamOnTable() {
        var scn = GetScenario();
        var shieldisdown = scn.GetLSCard("shieldisdown");

        scn.StartGame();
        scn.MoveCardsToLSHand(shieldisdown);
        scn.LSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(shieldisdown));
        Vb73LightTestSupport.putOnTable(scn, scn.LS, "1273_43");
        scn.SkipToNextTurn(scn.LS);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(shieldisdown));
    }

    @Test
    public void TheShieldIsDownVEpicEventTotalPlusOneForEachNonJediScoutAtBunker() {
        var scn = GetScenario();
        var shieldisdown = scn.GetLSCard("shieldisdown");
        var endor = scn.GetLSCard("endor");
        var bunker = scn.GetLSCard("bunker");
        var dtsg = scn.GetLSCard("dtsg");
        var beezer = scn.GetLSCard("beezer");
        var beezer2 = scn.GetLSCard("beezer2");

        scn.StartGame();
        scn.MoveLocationToTable(endor);
        scn.MoveLocationToTable(bunker);
        scn.AttachCardsTo(bunker, dtsg);
        scn.MoveCardsToLocation(bunker, beezer, beezer2);
        assertEquals(10, scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), dtsg, 10), scn.epsilon);
        scn.MoveCardsToLSSideOfTable(shieldisdown);
        assertEquals(12, scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), dtsg, 10), scn.epsilon);
    }

    @Test
    public void TheShieldIsDownVBunkerMayNotBeConvertedWhileOpponentsEndorObjectiveOnTable() {
        var scn = GetScenario();
        var shieldisdown = scn.GetLSCard("shieldisdown");
        var endor = scn.GetLSCard("endor");
        var bunker = scn.GetLSCard("bunker");

        scn.StartGame();
        scn.MoveLocationToTable(endor);
        scn.MoveLocationToTable(bunker);
        scn.MoveCardsToLSSideOfTable(shieldisdown);
        assertFalse(scn.game().getModifiersQuerying().cannotBeConverted(scn.gameState(), bunker));
        Vb73LightTestSupport.putOnTable(scn, scn.DS, "8_167");
        assertTrue(scn.game().getModifiersQuerying().cannotBeConverted(scn.gameState(), bunker));
    }

    @Test
    public void TheShieldIsDownVMayTakeBunkerIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var shieldisdown = scn.GetLSCard("shieldisdown");
        var bunker = scn.GetLSCard("bunker");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(shieldisdown);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(shieldisdown, "Take Bunker into hand from Reserve Deck"));
        scn.LSUseCardAction(shieldisdown, "Take Bunker into hand from Reserve Deck");
        if (Vb81RestTestSupport.passUntil(scn, () -> scn.LSHasCardChoiceAvailable(bunker))) {
            scn.LSChooseCard(bunker);
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, bunker.getZone());
    }

    @Test
    public void TheShieldIsDownVAttritionAgainstOpponentPlusOneForEachOfYourNonJediScoutsInBattle() {
        var scn = GetScenario();
        var shieldisdown = scn.GetLSCard("shieldisdown");
        var beezer = scn.GetLSCard("beezer");
        var beezer2 = scn.GetLSCard("beezer2");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(shieldisdown);
        // Obi-Wan (a Jedi, so not counted) lets Light Side draw battle destiny so attrition is calculated
        scn.MoveCardsToLocation(site, beezer, beezer2, scn.GetLSCard("obiwan"), trooper);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(3);
        scn.SkipToDamageSegment(true);
        assertEquals(scn.GetLSTotalDestiny() + 2, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.DS), scn.epsilon);
    }
}
