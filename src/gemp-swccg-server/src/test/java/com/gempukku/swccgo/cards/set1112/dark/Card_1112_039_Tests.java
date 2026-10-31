package com.gempukku.swccgo.cards.set1112.dark;

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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_039_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("obsidian", "1112_39");
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
    public void Obsidian10StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("obsidian").getBlueprint();

        assertEquals("Obsidian 10", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.OBSIDIAN_SQUADRON);
            add(Keyword.NO_HYPERDRIVE);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.DEATH_STAR_II);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Obsidian10OS7210IsMatchingPilotAndDeploysMinusTwoAboard() {
        var scn = GetPilotScenario();
        var obsidian = scn.GetDSCard("obsidian");
        var os72 = scn.GetDSCard("os72");
        var endor = scn.GetDSCard("endor");

        scn.StartGame();
        scn.MoveLocationToTable(endor);
        scn.MoveCardsToLocation(endor, obsidian);

        assertTrue(scn.IsMatchingPilot(obsidian, os72));
        assertEquals(3, scn.GetDeployCost(os72));
        assertEquals(1, deployCostTo(scn, os72, obsidian), scn.epsilon);
    }

    @Test
    public void Obsidian10LostFromEndorMayPlaceStarshipAndCardsAboardInUsedPile() {
        var scn = GetPilotScenario();
        var obsidian = scn.GetDSCard("obsidian");
        var os72 = scn.GetDSCard("os72");
        var endor = scn.GetDSCard("endor");
        var corvette = scn.GetLSCard("corvette");

        scn.StartGame();
        scn.MoveLocationToTable(endor);
        scn.MoveCardsToLocation(endor, obsidian, corvette);
        scn.BoardAsPilot(obsidian, os72);
        scn.SkipToPhase(Phase.BATTLE);
        assertTrue("DS must be able to battle at Endor; phase=" + scn.GetCurrentPhase()
                        + " currentPlayer=" + scn.gameState().getCurrentPlayerId()
                        + " dsForce=" + scn.GetDSForcePileCount()
                        + " actions=" + scn.GetDSAvailableActions(),
                scn.DSCanInitiateBattle(endor));
        scn.DSInitiateBattle(endor);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue("DS must pay battle damage; dsPower=" + scn.GetPower(obsidian)
                        + " lsPower=" + scn.GetPower(corvette)
                        + " unpaidDS=" + (scn.gameState().getBattleState() == null ? "none" : scn.GetUnpaidDSBattleDamage())
                        + " unpaidLS=" + (scn.gameState().getBattleState() == null ? "none" : scn.GetUnpaidLSBattleDamage())
                        + " dsPay=" + scn.AwaitingDSBattleDamagePayment()
                        + " lsPay=" + scn.AwaitingLSBattleDamagePayment()
                        + " decision=" + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText()),
                scn.AwaitingDSBattleDamagePayment());
        scn.DSChooseCard(obsidian);
        assertTrue("DS must confirm forfeit of piloted Obsidian 10; decision="
                        + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText()),
                scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();
        assertTrue("Used Pile must be offered when lost from Endor; decision="
                        + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText())
                        + " actions=" + scn.GetDSAvailableActions(),
                scn.GetDSAvailableActions().toString().contains("Used Pile"));
        scn.DSChooseAction("Used Pile");
        scn.PassAllResponses();

        assertTrue(obsidian.getZone() == Zone.USED_PILE || obsidian.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(os72.getZone() == Zone.USED_PILE || os72.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void Obsidian10LostFromARegularSystemDoesNotOfferUsedPile() {
        var scn = GetPilotScenario();
        var obsidian = scn.GetDSCard("obsidian");
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, obsidian);
        scn.SkipToDSTurn(Phase.BATTLE);

        scn.DSExecuteAdHocEffect(obsidian, new LoseCardFromTableEffect(
                new TopLevelGameTextAction(obsidian, obsidian.getCardId()), obsidian));
        assertFalse("Used Pile must not be offered at a regular system; actions=" + scn.GetDSAvailableActions(),
                scn.GetDSAvailableActions().toString().contains("Used Pile"));
        scn.PassAllResponses();
        assertTrue(obsidian.getZone() == Zone.LOST_PILE || obsidian.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    private VirtualTableScenario GetPilotScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("corvette", "1_140");
                }},
                new HashMap<>() {{
                    put("obsidian", "1112_39");
                    put("os72", "7_194");
                    put("endor", "8_157");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card,
                               com.gempukku.swccgo.game.PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
