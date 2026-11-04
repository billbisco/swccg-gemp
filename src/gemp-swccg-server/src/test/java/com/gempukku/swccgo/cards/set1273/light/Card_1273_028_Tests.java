package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ten", "1273_28");
                    put("bwing", "7_140");
                    put("corvette", "1_140");
                    put("outrider", "10_017");
                }},
                new HashMap<>() {{
                    put("black2", "1_299");
                    put("vader", "1_168");
                    put("bast", "1_165");
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
    public void TenNumbVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("ten").getBlueprint();

        assertEquals(Title.Ten_Numb, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ROGUE_SQUADRON);
            add(Keyword.GUNNER);
            add(Keyword.BLUE_SQUADRON);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TenNumbVAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var ten = scn.GetLSCard("ten");
        var bwing = scn.GetLSCard("bwing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, bwing);
        int before = (int) (float) bwing.getBlueprint().getPower();
        scn.BoardAsPilot(bwing, ten);
        assertEquals(before + 2, scn.GetPower(bwing));
    }

    @Test
    public void TenNumbVDrawsOneBattleDestinyIfUnableToOtherwiseOnlyWhilePilotingBwingOrXwing() {
        var scn = GetScenario();
        var ten = scn.GetLSCard("ten");
        var bwing = scn.GetLSCard("bwing");
        var corvette = scn.GetLSCard("corvette");
        var black2 = scn.GetDSCard("black2");
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, bwing, corvette, black2);
        scn.BoardAsPilot(bwing, ten);
        scn.BoardAsPilot(black2, scn.GetDSCard("bast"));
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(dantooine);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MIN_BATTLE_DESTINY_DRAWS, ten).isEmpty());
    }

    @Test
    public void TenNumbVNoBattleDestinyHelpWhilePilotingOtherStarship() {
        var scn = GetScenario();
        var ten = scn.GetLSCard("ten");
        var corvette = scn.GetLSCard("corvette");
        var black2 = scn.GetDSCard("black2");
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, corvette, black2);
        scn.BoardAsPilot(corvette, ten);
        scn.BoardAsPilot(black2, scn.GetDSCard("bast"));
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(dantooine);
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MIN_BATTLE_DESTINY_DRAWS, ten).isEmpty());
    }

    @Test
    public void TenNumbVDuringBattleReducesOpponentsStarfighterPowerByFourOncePerBattle() {
        var scn = GetScenario();
        var ten = scn.GetLSCard("ten");
        var bwing = scn.GetLSCard("bwing");
        var black2 = scn.GetDSCard("black2");
        var vader = scn.GetDSCard("vader");
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, bwing, black2);
        scn.BoardAsPilot(bwing, ten);
        scn.BoardAsPilot(black2, vader);
        int before = scn.GetPower(black2);
        assertTrue(before >= 4);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(dantooine);

        Vb81RestTestSupport.passUntil(scn, () -> scn.LSCardActionAvailable(ten, "Reduce power"));
        assertTrue(scn.LSCardActionAvailable(ten, "Reduce power"));
        scn.LSUseCardAction(ten, "Reduce power");
        if (scn.LSHasCardChoiceAvailable(black2)) {
            scn.LSChooseCard(black2);
        }
        assertEquals(before - 4, scn.GetPower(black2));
        Vb81RestTestSupport.passUntil(scn, () -> scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, ten, "Reduce power"));
    }

    @Test
    public void TenNumbVMayNotReducePowerWhileOutriderOnTable() {
        var scn = GetScenario();
        var ten = scn.GetLSCard("ten");
        var bwing = scn.GetLSCard("bwing");
        var outrider = scn.GetLSCard("outrider");
        var black2 = scn.GetDSCard("black2");
        var tibrin = scn.GetLSStartingLocation();
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, bwing, black2);
        scn.MoveCardsToLocation(tibrin, outrider);
        scn.BoardAsPilot(bwing, ten);
        scn.BoardAsPilot(black2, scn.GetDSCard("bast"));
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(dantooine);
        Vb81RestTestSupport.passUntil(scn, () -> scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, ten, "Reduce power"));
    }
}
