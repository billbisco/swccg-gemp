package com.gempukku.swccgo.cards.set1115.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1115_024_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1240_102");
                    put("perimeter", "1115_24");
                    put("tat", "1_127");
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
    public void RebelCellPerimeterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("perimeter").getBlueprint();

        assertEquals("Rebel Cell - Perimeter", card.getTitle());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SPECIAL_EDITION);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
}});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelCellPerimeterDoesNotDeployWithoutHiddenLandingSite() {
        var scn = GetScenario();
        var perimeter = scn.GetLSCard("perimeter");

        scn.StartGame();
        scn.MoveCardsToLSHand(perimeter);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(perimeter));
    }

    @Test
    public void RebelCellPerimeterDeploysToSamePlanetAsHiddenLandingSite() {
        var scn = GetScenario();
        var hls = scn.GetLSCard("hls");
        var perimeter = scn.GetLSCard("perimeter");

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("tat"));
        scn.MoveCardsToLSHand(hls, perimeter);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(hls));
        scn.LSDeployLocation(hls);
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(Zone.LOCATIONS, hls.getZone());

        assertTrue(scn.LSDeployAvailable(perimeter));
        scn.LSDeployCard(perimeter);
        assertTrue(scn.LSHasCardChoiceAvailable(hls));
        scn.LSChooseCard(hls);
        assertTrue(scn.LSDecisionAvailable("On which side"));
        scn.LSChoose("Left");
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, perimeter.getZone());
        assertEquals("Tatooine", perimeter.getPartOfSystem());
    }

    @Test
    public void RebelCellPerimeterHothArtilleryIsPoweredForfeitPlus2AndFiresForFree() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("hls", "1240_102");
                    put("perimeter", "1115_24");
                    put("atgar", "3_72");
                    put("atgar2", "3_72");
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
        var hls = scn.GetLSCard("hls");
        var perimeter = scn.GetLSCard("perimeter");
        var atgar = scn.GetLSCard("atgar");
        var atgar2 = scn.GetLSCard("atgar2");
        var other = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(hls);
        scn.MoveLocationToTable(perimeter);
        scn.MoveCardsToLocation(perimeter, atgar);
        scn.MoveCardsToLocation(other, atgar2);

        assertTrue(scn.game().getModifiersQuerying().isPowered(scn.gameState(), atgar));
        assertFalse(scn.game().getModifiersQuerying().isPowered(scn.gameState(), atgar2));
        assertEquals(4, scn.GetForfeit(atgar));
        assertEquals(2, scn.GetForfeit(atgar2));
        assertEquals(0, scn.game().getModifiersQuerying().getFireWeaponCost(scn.gameState(), atgar, null, null, 2), scn.epsilon);
        assertEquals(2, scn.game().getModifiersQuerying().getFireWeaponCost(scn.gameState(), atgar2, null, null, 2), scn.epsilon);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), com.gempukku.swccgo.logic.modifiers.ModifierType.FORFEITED_TO_USED_PILE, atgar).isEmpty());
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), com.gempukku.swccgo.logic.modifiers.ModifierType.FORFEITED_TO_USED_PILE, atgar2).isEmpty());
    }
}
