package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_038_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")), new HashMap<>(Map.of(
                "escort", "1261_38", "objective", "10_29", "city", "7_277", "square", "7_278",
                "senate", "12_167", "jax", "1261_8", "guard", "9_119", "guard2", "9_119",
                "guard3", "9_119", "pike", "9_179")), 20, 20, StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedVirtualEffectMetadataAndAlterImmunity() {
        var scn = scenario();
        var bp = scn.GetDSCard("escort").getBlueprint();
        assertEquals("Royal Escort", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(4, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(CardSubtype.NORMAL, bp.getCardSubtype());
        assertEquals(PlayCardZoneOption.YOUR_SIDE_OF_TABLE, bp.getSinglePlayCardZoneOption());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertTrue(bp.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void cannotDeployWithYourActualReflectionsTwoObjectiveOnTable() {
        for (boolean onTable : List.of(false, true)) {
            var scn = scenario();
            var objective = scn.GetDSCard("objective");
            var escort = scn.GetDSCard("escort");
            scn.MoveCardsToDSHand(objective);
            scn.StartGame();
            if (onTable)
                scn.MoveCardsToDSSideOfTable(objective);
            scn.MoveCardsToDSHand(escort);
            scn.SkipToDSTurn(Phase.DEPLOY);
            scn.EnsureDSForcePile(6);
            assertEquals(!onTable, scn.DSDeployAvailable(escort));
        }
    }

    @Test(timeout = 10000)
    public void iconsAndAttritionUseDifferentCorrectSiteCriteria() {
        var scn = scenario();
        var city = scn.GetDSCard("city");
        var square = scn.GetDSCard("square");
        var senate = scn.GetDSCard("senate");
        scn.MoveCardsToDSHand(scn.GetDSCard("objective"));
        scn.StartGame();
        scn.MoveLocationToTable(city);
        scn.MoveLocationToTable(square);
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("escort"));
        var query = scn.game().getModifiersQuerying();
        assertEquals(1, query.getIconCount(scn.gameState(), city, Icon.LIGHT_FORCE), scn.epsilon);
        assertEquals(1, query.getIconCount(scn.gameState(), square, Icon.LIGHT_FORCE), scn.epsilon);
        assertEquals(1, query.getIconCount(scn.gameState(), senate, Icon.LIGHT_FORCE), scn.epsilon);
        scn.MoveCardsToLocation(city, scn.GetDSCard("jax"), scn.GetDSCard("guard"));
        scn.MoveCardsToLocation(square, scn.GetDSCard("guard2"), scn.GetDSCard("guard3"));
        var battle = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(battle, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(battle, scn.GetDSCard("guard3"));
        scn.DSStartBattleAndSkipToWeaponsSegment(battle);
        var modifiers = query.getModifiersAffectingCard(scn.gameState(), ModifierType.ATTRITION, battle);
        assertEquals(1, modifiers.stream().mapToDouble(m -> m.getAttritionModifier(scn.LS,
                scn.gameState(), query, battle)).sum(), scn.epsilon);
        assertEquals(0, modifiers.stream().mapToDouble(m -> m.getAttritionModifier(scn.DS,
                scn.gameState(), query, battle)).sum(), scn.epsilon);
    }

    @Test(timeout = 10000)
    public void paysOneForceAndActuallyDeploysForcePikeFromReserveDeck() {
        var scn = scenario();
        var escort = scn.GetDSCard("escort");
        var guard = scn.GetDSCard("guard");
        var pike = scn.GetDSCard("pike");
        scn.MoveCardsToDSHand(scn.GetDSCard("objective"));
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(escort);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), guard);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(6);
        scn.MoveCardsToTopOfDSReserveDeck(pike);
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(escort, "Use 1 Force to deploy a Force Pike");
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(pike))
            scn.DSChooseCard(pike);
        if (scn.DSHasCardChoiceAvailable(guard))
            scn.DSChooseCard(guard);
        scn.PassAllResponses();
        assertSame(guard, pike.getAttachedTo());
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }
}
