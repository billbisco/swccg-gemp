package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_111_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("village", "1230_111");
                    put("endor", "8_068");
                    put("ewok", "8_011");
                    put("chirpa", "8_003");
                    put("luke", "1_19");
                    put("door", "8_69");
                    put("vehicle", "1_151");
                    put("ship", "8_79");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("vehicle", "8_172");
                    put("ship", "1_304");
                }},
                30, 30,
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
    public void EndorEwokVillageStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("village").getBlueprint();

        assertEquals(Title.Ewok_Village, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.ENDOR);
            add(Icon.LEGACY_BLOCK_3);
            add(Icon.PLANET);
            add(Icon.INTERIOR_SITE);
            add(Icon.EXTERIOR_SITE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EndorEwokVillageMakesNonUniqueEwoksHeroicAndGainsLightForceWhileOccupied() {
        var scn = GetScenario();
        var village = scn.GetLSCard("village");
        var endor = scn.GetLSCard("endor");
        var ewok = scn.GetLSCard("ewok");
        var chirpa = scn.GetLSCard("chirpa");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(endor);
        scn.MoveLocationToTable(village);
        assertEquals(1, scn.GetIconCount(village, Icon.LIGHT_FORCE));
        scn.MoveCardsToLocation(village, ewok, chirpa);
        assertTrue(scn.HasKeyword(ewok, Keyword.HEROIC));
        assertFalse(scn.HasKeyword(chirpa, Keyword.HEROIC));
        assertEquals(2, scn.GetIconCount(village, Icon.LIGHT_FORCE));
        scn.MoveCardsToLocation(village, luke);
        assertEquals(2, scn.GetIconCount(village, Icon.LIGHT_FORCE));
    }

    @Test
    public void EndorEwokVillageHeroicAppliesToNonUniqueEwoksAtOtherEndorSites() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("village"));
        scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLocation(scn.GetLSCard("door"), scn.GetLSCard("ewok"), scn.GetLSCard("chirpa"));
        assertTrue(scn.HasKeyword(scn.GetLSCard("ewok"), Keyword.HEROIC));
        assertFalse(scn.HasKeyword(scn.GetLSCard("chirpa"), Keyword.HEROIC));
        assertFalse(scn.HasKeyword(scn.GetLSCard("luke"), Keyword.HEROIC));
    }

    @Test
    public void EndorEwokVillageOccupationAddsIconEvenWhenContestedAndLosesItWhenUnoccupied() {
        var scn = GetScenario();
        scn.StartGame();
        var village = scn.GetLSCard("village");
        scn.MoveLocationToTable(village);
        scn.MoveCardsToLocation(village, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        assertEquals(2, scn.GetIconCount(village, Icon.LIGHT_FORCE));
        assertEquals(1, scn.GetIconCount(village, Icon.DARK_FORCE));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        assertEquals(1, scn.GetIconCount(village, Icon.LIGHT_FORCE));
    }

    @Test
    public void EndorEwokVillageDarkDrainIsTwoWithNoEwoksOnEndor() { CheckDarkDrain(0, 2); }

    @Test
    public void EndorEwokVillageDarkDrainIsOneWithEwokAtAnotherEndorSite() { CheckDarkDrain(1, 1); }

    @Test
    public void EndorEwokVillageEwokOnAnotherPlanetDoesNotPreventDrainBonus() { CheckDarkDrain(2, 2); }

    private void CheckDarkDrain(int ewokPlacement, int expected) {
        var scn = GetScenario();
        scn.StartGame();
        var village = scn.GetLSCard("village");
        scn.MoveLocationToTable(village);
        scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLocation(village, scn.GetDSCard("vader"));
        if (ewokPlacement != 0) {
            // Deployment is Endor-only; this reachable off-Endor fixture represents later transportation.
            scn.MoveCardsToLocation(ewokPlacement == 1 ? scn.GetLSCard("door") : scn.GetLSStartingLocation(), scn.GetLSCard("ewok"));
        }
        scn.SkipToDSTurn(Phase.CONTROL);
        int lost = scn.GetLSLostPileCount();
        scn.DSForceDrainAt(village);
        scn.PassAllResponses();
        assertEquals(expected, scn.GetForceDrainTotal());
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lost + expected, scn.GetLSLostPileCount());
    }

    @Test
    public void EndorEwokVillageProhibitsBothPlayersStarshipsAndVehiclesButNotCharacters() {
        var scn = GetScenario();
        scn.StartGame();
        var village = scn.GetLSCard("village");
        scn.MoveLocationToTable(village);
        scn.MoveLocationToTable(scn.GetLSCard("door"));
        for (var card : new com.gempukku.swccgo.game.PhysicalCard[] {
                scn.GetLSCard("ship"), scn.GetLSCard("vehicle"), scn.GetDSCard("ship"), scn.GetDSCard("vehicle")}) {
            assertTrue(scn.game().getModifiersQuerying().isProhibitedFromTarget(scn.gameState(), card, village));
            assertFalse(scn.game().getModifiersQuerying().isProhibitedFromTarget(scn.gameState(), card, scn.GetLSCard("door")));
        }
        assertFalse(scn.game().getModifiersQuerying().isProhibitedFromTarget(scn.gameState(), scn.GetLSCard("luke"), village));
    }

    @Test
    public void EndorEwokVillageVehicleCannotDeployHereButCanDeployAtAdjacentBackDoor() {
        var scn = GetScenario();
        scn.StartGame();
        var village = scn.GetLSCard("village");
        scn.MoveLocationToTable(village);
        scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLocation(village, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSCard("door"), scn.GetLSCard("chirpa"));
        scn.MoveCardsToLSHand(scn.GetLSCard("vehicle"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("vehicle"));
        assertFalse(scn.LSHasCardChoiceAvailable(village));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("door")));
        scn.LSChooseCard(scn.GetLSCard("door"));
        scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertAtLocation(scn.GetLSCard("door"), scn.GetLSCard("vehicle"));
    }
}
