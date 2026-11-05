package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("operations", "1273_19");
                    put("office", "1273_18"); // Kessel: Spice Mines - Administrator's Office
                    put("trap", "2_124"); // Program Trap
                    put("trooper", "1_194");
                    put("kessel", "1_288"); // Kessel system
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
    public void SpiceMineOperationsStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("operations").getBlueprint();
        assertEquals("Spice Mine Operations", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.MISSION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.MISSION);
            add(Icon.SETUP);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private boolean deployAvailable(boolean officeOnTable) {
        var scn = GetScenario();
        var operations = scn.GetDSCard("operations");
        scn.StartGame();
        if (officeOnTable) {
            MissionTestSupport.ensurePlanetLayout(scn, "Kessel", 8);
            scn.MoveLocationToTable(scn.GetDSCard("office"));
        }
        scn.MoveCardsToDSHand(operations);
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn.DSDeployAvailable(operations);
    }

    @Test
    public void SpiceMineOperationsDeploysOnTableOnlyIfYourVirtualBlock7OfficeOnTable() {
        assertTrue(deployAvailable(true));
        assertFalse(deployAvailable(false));
    }

    @Test
    public void SpiceMineOperationsCancelsProgramTrap() {
        var scn = GetScenario();
        var trap = scn.GetDSCard("trap");
        scn.StartGame();
        scn.AttachCardsTo(scn.GetDSStartingLocation(), trap);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("operations"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(trap.getZone().isInPlay());
    }

    @Test
    public void SpiceMineOperationsUsesTwoForceToStackCardFromLostPile() {
        var scn = GetScenario();
        var operations = scn.GetDSCard("operations");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(operations);
        scn.MoveCardsToTopOfDSLostPile(trooper);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSUseCardAction(operations, "Stack a card from Lost Pile here");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.IsStackedOn(operations, trooper));
        assertTrue(scn.IsStackedOn(operations, trooper));
        assertEquals(forceBefore - 2, scn.GetDSForcePileCount());
    }

    @Test
    public void SpiceMineOperationsDeploysNonCharacterKesselCardFromReserveDeck() {
        var scn = GetScenario();
        var operations = scn.GetDSCard("operations");
        var office = scn.GetDSCard("office");
        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, "Kessel", 8);
        scn.MoveLocationToTable(scn.GetDSCard("kessel"));
        scn.MoveCardsToDSSideOfTable(operations);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(office);
        scn.DSUseCardAction(operations, "Deploy a non-character Kessel card");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> office.getZone() == Zone.LOCATIONS);
        assertEquals(Zone.LOCATIONS, office.getZone());
    }

    private VirtualTableScenario mine(int lightDestiny, int darkDestiny) {
        var scn = GetScenario();
        var operations = scn.GetDSCard("operations");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(operations);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.PrepareLSDestiny(lightDestiny);
        scn.PrepareDSDestiny(darkDestiny);
        scn.DSUseCardAction(operations, "'Mine'");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.AwaitingDSDeployPhaseActions());
        return scn;
    }

    @Test
    public void SpiceMineOperationsMineStacksOpponentsDestinyWhenPrintedDestinyMatches() {
        var scn = mine(4, 4);
        var operations = scn.GetDSCard("operations");
        assertEquals(1, scn.GetStackedCards(operations).size());
        assertEquals(Side.LIGHT, scn.GetStackedCards(operations).get(0).getBlueprint().getSide());
    }

    @Test
    public void SpiceMineOperationsMineStacksNothingWithoutMatch() {
        var scn = mine(4, 2);
        assertEquals(0, scn.GetStackedCards(scn.GetDSCard("operations")).size());
    }

    @Test
    public void SpiceMineOperationsPlacesSpiceInLostPileToDrawTwoIfYouControlExtractionFacility() {
        var scn = GetScenario();
        var operations = scn.GetDSCard("operations");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        var facility = LegacyVb73DarkTestSupport.putKesselSiteOnTable(scn, 7319, "Kessel: Spice Mines - Extraction Facility");
        scn.MoveCardsToDSSideOfTable(operations);
        scn.StackCardsOn(operations, trooper);
        scn.MoveCardsToLocation(facility, scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        int handBefore = scn.GetDSHandCount();
        assertTrue(scn.DSCardActionAvailable(operations, "Place a 'spice' card in Lost Pile"));
        scn.DSUseCardAction(operations, "Place a 'spice' card in Lost Pile");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.AwaitingDSDeployPhaseActions());
        assertInZone(Zone.LOST_PILE, trooper);
        assertEquals(handBefore + 2, scn.GetDSHandCount());
    }
}
