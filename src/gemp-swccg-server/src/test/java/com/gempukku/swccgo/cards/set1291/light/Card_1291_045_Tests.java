package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leebo", "1291_45");
                    put("outrider", "10_17"); // Outrider
                    put("xwing", "1_146"); // Red 5
                    put("han", "1_11"); // Han Solo: pilot, ability 3
                    put("luke", "1_19"); // Luke Skywalker: ability 4
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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

    @Test
    public void LeeboStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("leebo").getBlueprint();
        assertEquals("Leebo", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.MAINTENANCE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SMUGGLER);
            add(Keyword.INFORMATION_BROKER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.PILOT);
            add(Icon.PRESENCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void battleAtLSSystem(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
    }

    @Test
    public void LeeboAddsPowerAndDrawsBattleDestinyWhilePiloting() {
        var scn = GetScenario();
        var leebo = scn.GetLSCard("leebo");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, xwing, tie);
        int power = scn.GetPower(xwing);
        scn.BoardAsPilot(xwing, leebo);
        assertEquals(power + 2, scn.GetPower(xwing));
        battleAtLSSystem(scn);
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void LeeboAsPassengerDrawsNoBattleDestiny() {
        var scn = GetScenario();
        var leebo = scn.GetLSCard("leebo");
        var han = scn.GetLSCard("han");
        var outrider = scn.GetLSCard("outrider");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, outrider, tie);
        scn.BoardAsPilot(outrider, han);
        scn.BoardAsPassenger(outrider, leebo);
        battleAtLSSystem(scn);
        assertEquals(0, scn.GetLSBattleDestinyCount());
    }

    private int totalBattleDestinyWithReductions(boolean leeboPiloting) {
        var scn = GetScenario();
        var leebo = scn.GetLSCard("leebo");
        var han = scn.GetLSCard("han");
        var luke = scn.GetLSCard("luke");
        var outrider = scn.GetLSCard("outrider");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, outrider, tie);
        if (leeboPiloting) {
            scn.BoardAsPilot(outrider, leebo);
            scn.BoardAsPassenger(outrider, han);
        } else {
            scn.BoardAsPilot(outrider, luke);
            scn.BoardAsPassenger(outrider, leebo);
        }
        // opponent's reductions: each battle destiny -1 and total battle destiny -2
        scn.ApplyAdHocModifier(new EachBattleDestinyModifier(tie, -1, scn.LS));
        scn.ApplyAdHocModifier(new TotalBattleDestinyModifier(tie, -2, scn.LS));
        battleAtLSSystem(scn);
        assertEquals(1, scn.GetLSBattleDestinyCount());
        scn.PrepareLSDestiny(5);
        Vb91LightTestSupport.reach(scn, scn::IsAttritionCalculated);
        return scn.GetLSTotalDestiny();
    }

    @Test
    public void LeeboWhilePilotingBattleDestinyAndTotalMayNotBeReduced() {
        assertEquals(5, totalBattleDestinyWithReductions(true));
    }

    @Test
    public void LeeboNotPilotingLetsBattleDestinyBeReduced() {
        assertEquals(2, totalBattleDestinyWithReductions(false));
    }

    @Test
    public void LeeboAboardOutriderMayUseForceToLimitImmunityToAttrition() {
        var scn = GetScenario();
        var leebo = scn.GetLSCard("leebo");
        var luke = scn.GetLSCard("luke");
        var outrider = scn.GetLSCard("outrider");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, outrider, tie);
        scn.BoardAsPilot(outrider, leebo);
        scn.BoardAsPassenger(outrider, luke);
        scn.ApplyAdHocModifier(new ImmuneToAttritionLessThanModifier(tie, tie, 8));
        var mq = scn.game().getModifiersQuerying();
        assertEquals(8, mq.getImmunityToAttritionLessThan(scn.gameState(), tie), scn.epsilon);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertFalse(scn.LSCardActionAvailable(leebo, "Limit immunity"));
        scn.LSInitiateBattle(system);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, leebo, "Limit immunity"));
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(leebo, "Limit immunity");
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertEquals(5, mq.getImmunityToAttritionLessThan(scn.gameState(), tie), scn.epsilon);
    }

    @Test
    public void LeeboNotAboardOutriderMayNotLimitImmunityToAttrition() {
        var scn = GetScenario();
        var leebo = scn.GetLSCard("leebo");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var outrider = scn.GetLSCard("outrider");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, xwing, outrider, tie);
        scn.BoardAsPilot(xwing, leebo);
        scn.BoardAsPilot(outrider, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        assertFalse(Vb91LightTestSupport.advanceUntil(scn, () -> Vb91LightTestSupport.lsAction(scn, leebo, "Limit immunity")));
    }
}
