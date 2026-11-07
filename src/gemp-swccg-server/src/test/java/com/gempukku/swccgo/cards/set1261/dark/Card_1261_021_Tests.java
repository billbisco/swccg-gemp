package com.gempukku.swccgo.cards.set1261.dark;

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
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.DefaultSwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1261_021_Tests {
    protected String shieldId() { return "1261_21"; }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("forest", "7_121");
            put("swamp", "7_128");
            put("jungle", "7_122");
            put("dagobah", "4_87");
            put("alderaan", "1_121");
            put("luke", "1_19");
            put("control", "4_48");
        }}, new HashMap<>() {{
            put("shield", shieldId());
            put("otherPrinting", shieldId().equals("1261_21") ? "1261_24" : "1261_21");
            put("fear", "13_69");
            put("trooper", "1_194");
            put("alien", "7_174");
            put("djas", "1_171");
            put("vader", "1_168");
            put("dantooine", "1_282");
            put("ownForest", "7_284");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario ready(boolean shield) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveLocationToTable(scn.GetDSCard("dantooine"));
        scn.MoveLocationToTable(scn.GetLSCard("forest"));
        if (shield)
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("shield"));
        scn.EnsureDSForcePile(20);
        scn.MoveCardsToDSHand(scn.GetDSCard("trooper"), scn.GetDSCard("vader"), scn.GetDSCard("alien"));
        return scn;
    }

    @Test
    public void LeaveThemToMeConversionPrintingCountsTowardTheExistingOutsideDeckShieldCap() throws DeckInvalidException {
        var library = new SwccgCardBlueprintLibrary();
        assertEquals("1261_21", library.getBaseBlueprintId(shieldId()));
        var deck = new SwccgDeck("shield-cap");
        deck.addCard("13_69");
        for (int i = 0; i < 10; i++)
            deck.addCardOutsideDeck(shieldId());
        DefaultSwccgFormat.validateOriginalStartingEffectOutsideDeckShieldCap(library, deck);
        deck.addCardOutsideDeck(shieldId());
        try {
            DefaultSwccgFormat.validateOriginalStartingEffectOutsideDeckShieldCap(library, deck);
            org.junit.Assert.fail("The eleventh physical shield must exceed the original Starting Effect cap");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage().contains("More defensive shields"));
        }
    }

    @Test
    public void LeaveThemToMePrintingsKeepTheirOwnImageIdsButShareRulesAndUniqueness() {
        var scn = scenario();
        var shield = scn.GetDSCard("shield");
        var other = scn.GetDSCard("otherPrinting");
        assertEquals(shieldId(), shield.getBlueprintId(true));
        assertFalse(shield.getBlueprintId(true).equals(other.getBlueprintId(true)));
        assertEquals(shield.getBlueprint().getClass(), other.getBlueprint().getClass());
        assertEquals(shield.getBlueprint().getTitle(), other.getBlueprint().getTitle());
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSSideOfTable(shield, scn.GetDSCard("fear"));
        scn.StackCardsOn(scn.GetDSCard("fear"), other);
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("fear"), "Defensive Shield"));
        assertEquals(Zone.SIDE_OF_TABLE, shield.getZone());
        assertEquals(Zone.STACKED, other.getZone());
    }

    @Test
    public void LeaveThemToMeStatsIconsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetDSCard("shield").getBlueprint();
        assertEquals("Leave Them To Me", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.DEFENSIVE_SHIELD); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD); add(Icon.REFLECTIONS_III); add(Icon.LEGACY_BLOCK_6);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
    }

    @Test
    public void LeaveThemToMeCanActuallyBePlayedFromTheStartingEffectsShieldStackForFree() {
        var scn = ready(false);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("fear"));
        scn.StackCardsOn(scn.GetDSCard("fear"), scn.GetDSCard("shield"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(scn.GetDSCard("fear"), "Defensive Shield");
        scn.DSChooseCard(scn.GetDSCard("shield"));
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("shield").getZone());
        assertEquals(force, scn.GetDSForcePileCount());
    }

    private void deployAndCheckCost(VirtualTableScenario scn, String card, com.gempukku.swccgo.game.PhysicalCardImpl site, int cost) {
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard(card));
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, scn.GetDSCard(card).getAtLocation());
        assertEquals(force - cost, scn.GetDSForcePileCount());
    }

    @Test
    public void LeaveThemToMeReducesAnActualImperialDeploymentAtAnOpponentsDantooineSite() {
        var scn = ready(true);
        deployAndCheckCost(scn, "trooper", scn.GetLSCard("forest"), 0);
    }

    @Test
    public void LeaveThemToMeReducesAnActualImperialDeploymentAtAnOpponentsSubjugatedSite() {
        var scn = ready(true);
        scn.gameState().setSubjugatedPlanet("Bespin");
        deployAndCheckCost(scn, "vader", scn.GetLSStartingLocation(), 4);
    }

    @Test
    public void LeaveThemToMeDoesNotReduceDeploymentAtAnUnrelatedSite() {
        var scn = ready(true);
        deployAndCheckCost(scn, "trooper", scn.GetLSStartingLocation(), 1);
    }

    @Test
    public void LeaveThemToMeDoesNotReduceAnAliensDeployment() {
        var scn = ready(true);
        deployAndCheckCost(scn, "alien", scn.GetLSCard("forest"), 1);
    }

    @Test
    public void LeaveThemToMeDoesNotReduceDeploymentAtItsOwnersDantooineSite() {
        var scn = ready(true);
        scn.MoveOutOfPlay(scn.GetLSCard("forest"));
        scn.MoveLocationToTable(scn.GetDSCard("ownForest"));
        deployAndCheckCost(scn, "trooper", scn.GetDSCard("ownForest"), 1);
    }

    private void nextDarkControl(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.CONTROL);
    }

    @Test
    public void LeaveThemToMeBlocksAnOpponentsDrainModifierButRetainsItsOwnersBonus() {
        var scn = ready(true);
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.ApplyAdHocModifier(new ForceDrainModifier(scn.GetDSCard("shield"), scn.GetLSCard("forest"), 2, scn.DS));
        scn.ApplyAdHocModifier(new ForceDrainModifier(scn.GetLSCard("luke"), scn.GetLSCard("forest"), -1, scn.DS));
        nextDarkControl(scn);
        scn.DSForceDrainAt(scn.GetLSCard("forest"));
        scn.PassAllResponses();
        assertEquals(3, scn.GetForceDrainTotal());
    }

    @Test
    public void LeaveThemToMePreventsOpponentFromCancelingTheProtectedDrain() {
        var scn = ready(true);
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLSHand(scn.GetLSCard("control"));
        nextDarkControl(scn);
        scn.DSForceDrainAt(scn.GetLSCard("forest"));
        org.junit.Assert.assertNotNull(scn.LSGetDecision());
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("control")));
        scn.PassAllResponses();
        assertTrue(scn.IsActiveForceDrain());
    }

    @Test
    public void LeaveThemToMeAllowsOpponentToCancelADrainAtAnUnprotectedSite() {
        var scn = ready(true);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("trooper"));
        scn.MoveCardsToLSHand(scn.GetLSCard("control"));
        nextDarkControl(scn);
        scn.DSForceDrainAt(scn.GetLSStartingLocation());
        scn.LSPlayCard(scn.GetLSCard("control"));
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
    }

    @Test
    public void LeaveThemToMeCancelsOnlyItsOwnersLocationTextOnOpponentsGenericSwampAndJungle() {
        var scn = ready(true);
        scn.MoveLocationToTable(scn.GetLSCard("swamp"));
        scn.MoveLocationToTable(scn.GetLSCard("jungle"));
        scn.SkipToPhase(Phase.DEPLOY);
        for (String key : new String[]{"swamp", "jungle"}) {
            assertTrue(scn.GetLSCard(key).isLocationGameTextCanceledForPlayer(scn.DS));
            assertFalse(scn.GetLSCard(key).isLocationGameTextCanceledForPlayer(scn.LS));
        }
    }

    @Test
    public void LeaveThemToMeActuallyRemovesTheOpponentsSwampBattleSurcharge() {
        var scn = ready(true);
        scn.MoveLocationToTable(scn.GetLSCard("swamp"));
        scn.MoveCardsToLocation(scn.GetLSCard("swamp"), scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.SkipToPhase(Phase.BATTLE);
        int force = scn.GetDSForcePileCount();
        scn.DSInitiateBattle(scn.GetLSCard("swamp"));
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void LeaveThemToMeDoesNotCancelTextAtANonGenericSwamp() {
        var scn = ready(true);
        scn.MoveLocationToTable(scn.GetLSCard("dagobah"));
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.GetLSCard("dagobah").isLocationGameTextCanceledForPlayer(scn.DS));
    }

    private VirtualTableScenario forestBattle(boolean shield) {
        var scn = ready(shield);
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetDSCard("djas"), scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE);
        assertEquals(2, scn.GetPower(scn.GetDSCard("djas")));
        scn.LSInitiateBattle(scn.GetLSCard("forest"));
        scn.PassAllResponses();
        scn.PrepareLSDestiny(3);
        scn.PrepareDSDestiny(4);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(scn.GetLSTotalPower(), scn.GetDSTotalPower());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.PassAllResponses();
        if (scn.AwaitingLSAttritionPayment()) {
            scn.LSPass(); // Forest's own Light text protects Luke.
            scn.PassAllResponses();
        }
        return scn;
    }

    @Test
    public void LeaveThemToMeProtectsAnAlienPresentAtOpponentsForestFromActualAttrition() {
        var scn = forestBattle(true);
        assertTrue(scn.AwaitingDSAttritionPayment());
        assertEquals(0, scn.DSGetChoiceMin());
        scn.DSPass();
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, scn.GetDSCard("djas").getZone());
    }

    @Test
    public void LeaveThemToMeWithoutTheShieldTheSameAlienMustActuallyBeForfeited() {
        var scn = forestBattle(false);
        assertTrue(scn.AwaitingDSAttritionPayment());
        assertEquals(1, scn.DSGetChoiceMin());
        scn.DSPayAttritionFromCardInPlay(scn.GetDSCard("djas"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("djas")));
    }
}
