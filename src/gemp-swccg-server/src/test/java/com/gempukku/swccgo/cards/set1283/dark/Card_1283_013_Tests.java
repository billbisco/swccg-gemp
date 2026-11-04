package com.gempukku.swccgo.cards.set1283.dark;

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
import com.gempukku.swccgo.filters.DeviceThatDeploysOnStarshipFilter;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("lstatooine", "1_127"); // Tatooine system (Light)
                }},
                new HashMap<>() {{
                    put("camp", "1283_13");
                    put("dstatooine", "1_289"); // Tatooine system (Dark)
                    put("shields", "203_29"); // Deflector Shield Generators (V): deploys on a capital starship
                    put("restrainer", "1_205"); // device that deploys on a droid
                    put("vader", "1_168");
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
    public void TatooineImperialVanguardCampStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("camp").getBlueprint();
        assertEquals("Tatooine: Imperial Vanguard Camp", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TatooineImperialVanguardCampDeviceFilterMatchesDevicesThatDeployOnAStarship() {
        var scn = GetScenario();
        var shields = scn.GetDSCard("shields");
        var restrainer = scn.GetDSCard("restrainer");
        scn.StartGame();
        assertTrue(DeviceThatDeploysOnStarshipFilter.INSTANCE.accepts(scn.game(), shields));
        assertFalse(DeviceThatDeploysOnStarshipFilter.INSTANCE.accepts(scn.game(), restrainer));
    }

    @Test
    public void TatooineImperialVanguardCampOncePerGameUses1ForceToTakeAStarshipDeviceIntoHand() {
        var scn = GetScenario();
        var camp = scn.GetDSCard("camp");
        var shields = scn.GetDSCard("shields");
        var restrainer = scn.GetDSCard("restrainer");
        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(shields, restrainer);
        int forcePile = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(camp, "Take starship device into hand from Reserve Deck"));
        scn.DSUseCardAction(camp, "Take starship device into hand from Reserve Deck");
        scn.PassForceUseResponses();
        assertEquals(1, scn.DSGetSelectableCount());
        scn.DSChooseCard(shields);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, shields);
        assertTrue(scn.GetDSReserveDeck().contains(restrainer));
        assertEquals(forcePile - 1, scn.GetDSForcePileCount());
        assertFalse(scn.GetDSAvailableActions().contains("Take starship device into hand from Reserve Deck"));
    }

    @Test
    public void TatooineImperialVanguardCampOffersToDeployJirFromReserveDeck() {
        // Jir (Commander Daine Jir, Virtual Block 6.1) is not in GEMP yet: the action is offered, nothing else matches
        var scn = GetScenario();
        var camp = scn.GetDSCard("camp");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(vader);
        assertTrue(scn.DSCardActionAvailable(camp, "Deploy Jir from Reserve Deck"));
        scn.DSUseCardAction(camp, "Deploy Jir from Reserve Deck");
        assertEquals(0, scn.DSGetSelectableCount());
        scn.PassAllResponses();
        assertTrue(scn.GetDSReserveDeck().contains(vader));
    }

    @Test
    public void TatooineImperialVanguardCampLetsAPlayerWhoControlsItRaiseTheirConvertedTatooine() {
        var scn = GetScenario();
        var camp = scn.GetDSCard("camp");
        var lstatooine = scn.GetLSCard("lstatooine");
        var dstatooine = scn.GetDSCard("dstatooine");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(lstatooine);
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, luke);
        scn.MoveCardsToHand(dstatooine);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(dstatooine);
        scn.PassAllResponses();
        assertEquals(Zone.CONVERTED_LOCATIONS, lstatooine.getZone());
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(camp, "Raise converted Tatooine system"));
        scn.LSUseCardAction(camp, "Raise converted Tatooine system");
        scn.LSChooseCard(dstatooine);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, lstatooine.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, dstatooine.getZone());
    }

    @Test
    public void TatooineImperialVanguardCampDarkPlayerMayAlsoRaiseWhenControlling() {
        var scn = GetScenario();
        var camp = scn.GetDSCard("camp");
        var lstatooine = scn.GetLSCard("lstatooine");
        var dstatooine = scn.GetDSCard("dstatooine");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        scn.MoveLocationToTable(dstatooine);
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, vader);
        scn.MoveCardsToHand(lstatooine);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(lstatooine);
        scn.PassAllResponses();
        assertEquals(Zone.CONVERTED_LOCATIONS, dstatooine.getZone());
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(camp, "Raise converted Tatooine system"));
        scn.DSUseCardAction(camp, "Raise converted Tatooine system");
        scn.DSChooseCard(lstatooine);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, dstatooine.getZone());
    }

    @Test
    public void TatooineImperialVanguardCampMayNotRaiseWithoutControl() {
        var scn = GetScenario();
        var camp = scn.GetDSCard("camp");
        var lstatooine = scn.GetLSCard("lstatooine");
        var dstatooine = scn.GetDSCard("dstatooine");
        scn.StartGame();
        scn.MoveLocationToTable(lstatooine);
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToHand(dstatooine);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(dstatooine);
        scn.PassAllResponses();
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(camp, "Raise converted Tatooine system"));
    }
}
