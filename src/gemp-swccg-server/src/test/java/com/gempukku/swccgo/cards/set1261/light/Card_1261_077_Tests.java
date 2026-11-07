package com.gempukku.swccgo.cards.set1261.light;

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

public class Card_1261_077_Tests {
    protected String shieldId() { return "1261_77"; }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("shield", shieldId());
            put("otherPrinting", shieldId().equals("1261_77") ? "1261_76" : "1261_77");
            put("fear", "13_5");
            put("ounee", "112_4");
            put("ouneeShield", "13_37");
            put("trooper", "1_28");

            put("boshek", "1_4");
            put("luke", "1_19");
            put("ralltiir", "2_65");
            put("ownForest", "7_121");
            put("ownSwamp", "7_128");
        }}, new HashMap<>() {{
            put("forest", "7_284");
            put("swamp", "7_292");
            put("jungle", "7_285");

                        put("djas", "1_171");
            put("control", "4_139");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario ready(boolean shield) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveLocationToTable(scn.GetLSCard("ralltiir"));
        scn.MoveLocationToTable(scn.GetDSCard("forest"));
        if (shield)
            scn.MoveCardsToLSSideOfTable(scn.GetLSCard("shield"));
        scn.EnsureLSForcePile(20);
        scn.MoveCardsToLSHand(scn.GetLSCard("trooper"), scn.GetLSCard("luke"), scn.GetLSCard("boshek"));
        return scn;
    }

    @Test
    public void LetsKeepALittleOptimismHereCancelsTheOriginalOuneeTaEffectAlreadyOnTheTable() {
        var scn = ready(false);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("ounee"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("shield"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("ounee")));
    }

    @Test
    public void LetsKeepALittleOptimismHereCancelsAnOuneeTaEffectAsItIsPlayed() {
        var scn = ready(true);
        scn.MoveCardsToLSHand(scn.GetLSCard("ounee"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSPlayCard(scn.GetLSCard("ounee"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("ounee")));
    }

    @Test
    public void LetsKeepALittleOptimismHereAlsoCancelsTheOriginalOuneeTaDefensiveShield() {
        var scn = ready(false);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("ouneeShield"), scn.GetLSCard("shield"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("ouneeShield")));
    }

    @Test
    public void LetsKeepALittleOptimismHereConversionPrintingCountsTowardTheExistingOutsideDeckShieldCap() throws DeckInvalidException {
        var library = new SwccgCardBlueprintLibrary();
        assertEquals("1261_77", library.getBaseBlueprintId(shieldId()));
        var deck = new SwccgDeck("shield-cap");
        deck.addCard("13_5");
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
    public void LetsKeepALittleOptimismHerePrintingsKeepTheirOwnImageIdsButShareRulesAndUniqueness() {
        var scn = scenario();
        var shield = scn.GetLSCard("shield");
        var other = scn.GetLSCard("otherPrinting");
        assertEquals(shieldId(), shield.getBlueprintId(true));
        assertFalse(shield.getBlueprintId(true).equals(other.getBlueprintId(true)));
        assertEquals(shield.getBlueprint().getClass(), other.getBlueprint().getClass());
        assertEquals(shield.getBlueprint().getTitle(), other.getBlueprint().getTitle());
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSSideOfTable(shield, scn.GetLSCard("fear"));
        scn.StackCardsOn(scn.GetLSCard("fear"), other);
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("fear"), "Defensive Shield"));
        assertEquals(Zone.SIDE_OF_TABLE, shield.getZone());
        assertEquals(Zone.STACKED, other.getZone());
    }

    @Test
    public void LetsKeepALittleOptimismHereStatsIconsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("shield").getBlueprint();
        assertEquals("Let's Keep A Little Optimism Here", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
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
    public void LetsKeepALittleOptimismHereCanActuallyBePlayedFromTheStartingEffectsShieldStackForFree() {
        var scn = ready(false);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("fear"));
        scn.StackCardsOn(scn.GetLSCard("fear"), scn.GetLSCard("shield"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("fear"), "Defensive Shield");
        scn.LSChooseCard(scn.GetLSCard("shield"));
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetLSCard("shield").getZone());
        assertEquals(force, scn.GetLSForcePileCount());
    }

    private void deployAndCheckCost(VirtualTableScenario scn, String card, com.gempukku.swccgo.game.PhysicalCardImpl site, int cost) {
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard(card));
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, scn.GetLSCard(card).getAtLocation());
        assertEquals(force - cost, scn.GetLSForcePileCount());
    }

    @Test
    public void LetsKeepALittleOptimismHereReducesAnActualRebelDeploymentAtAnOpponentsRalltiirSite() {
        var scn = ready(true);
        deployAndCheckCost(scn, "trooper", scn.GetDSCard("forest"), 0);
    }

    @Test
    public void LetsKeepALittleOptimismHereReducesAnActualRebelDeploymentAtAnOpponentsRenegadeSite() {
        var scn = ready(true);
        scn.gameState().setRenegadePlanet("Tatooine");
        deployAndCheckCost(scn, "luke", scn.GetDSStartingLocation(), 1);
    }

    @Test
    public void LetsKeepALittleOptimismHereDoesNotReduceDeploymentAtAnUnrelatedSite() {
        var scn = ready(true);
        deployAndCheckCost(scn, "trooper", scn.GetDSStartingLocation(), 1);
    }

    @Test
    public void LetsKeepALittleOptimismHereDoesNotReduceAnAliensDeployment() {
        var scn = ready(true);
        deployAndCheckCost(scn, "boshek", scn.GetDSCard("forest"), 4);
    }

    @Test
    public void LetsKeepALittleOptimismHereDoesNotReduceDeploymentAtItsOwnersRalltiirSite() {
        var scn = ready(true);
        scn.MoveOutOfPlay(scn.GetDSCard("forest"));
        scn.MoveLocationToTable(scn.GetLSCard("ownForest"));
        deployAndCheckCost(scn, "trooper", scn.GetLSCard("ownForest"), 1);
    }

    private void nextLightControl(VirtualTableScenario scn) {
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.SkipToLSTurn(Phase.CONTROL);
    }

    @Test
    public void LetsKeepALittleOptimismHereBlocksAnOpponentsDrainModifierButRetainsItsOwnersBonus() {
        var scn = ready(true);
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetLSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("djas"));
        scn.ApplyAdHocModifier(new ForceDrainModifier(scn.GetLSCard("shield"), scn.GetDSCard("forest"), 2, scn.LS));
        scn.ApplyAdHocModifier(new ForceDrainModifier(scn.GetDSCard("djas"), scn.GetDSCard("forest"), -1, scn.LS));
        nextLightControl(scn);
        scn.LSForceDrainAt(scn.GetDSCard("forest"));
        scn.PassAllResponses();
        assertEquals(3, scn.GetForceDrainTotal());
    }

    @Test
    public void LetsKeepALittleOptimismHerePreventsOpponentFromCancelingTheProtectedDrain() {
        var scn = ready(true);
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetLSCard("trooper"));
        scn.MoveCardsToDSHand(scn.GetDSCard("control"));
        nextLightControl(scn);
        scn.LSForceDrainAt(scn.GetDSCard("forest"));
        org.junit.Assert.assertNotNull(scn.DSGetDecision());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("control")));
        scn.PassAllResponses();
        assertTrue(scn.IsActiveForceDrain());
    }

    @Test
    public void LetsKeepALittleOptimismHereAllowsOpponentToCancelADrainAtAnUnprotectedSite() {
        var scn = ready(true);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("trooper"));
        scn.MoveCardsToDSHand(scn.GetDSCard("control"));
        nextLightControl(scn);
        scn.LSForceDrainAt(scn.GetDSStartingLocation());
        scn.DSPlayCard(scn.GetDSCard("control"));
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
    }

    @Test
    public void LetsKeepALittleOptimismHereCancelsOnlyItsOwnersLocationTextOnOpponentsGenericSwampAndJungle() {
        var scn = ready(true);
        scn.MoveLocationToTable(scn.GetDSCard("swamp"));
        scn.MoveLocationToTable(scn.GetDSCard("jungle"));
        scn.SkipToPhase(Phase.DEPLOY);
        for (String key : new String[]{"swamp", "jungle"}) {
            assertTrue(scn.GetDSCard(key).isLocationGameTextCanceledForPlayer(scn.LS));
            assertFalse(scn.GetDSCard(key).isLocationGameTextCanceledForPlayer(scn.DS));
        }
    }

    @Test
    public void LetsKeepALittleOptimismHereActuallyRemovesTheOpponentsSwampBattleSurcharge() {
        var scn = ready(true);
        scn.MoveLocationToTable(scn.GetDSCard("swamp"));
        scn.MoveCardsToLocation(scn.GetDSCard("swamp"), scn.GetLSCard("trooper"), scn.GetDSCard("djas"));
        scn.SkipToPhase(Phase.BATTLE);
        int force = scn.GetLSForcePileCount();
        scn.LSInitiateBattle(scn.GetDSCard("swamp"));
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void LetsKeepALittleOptimismHereDoesNotCancelTextAtItsOwnersUniversalSwamp() {
        var scn = ready(true);
        scn.MoveLocationToTable(scn.GetLSCard("ownSwamp"));
        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.GetLSCard("ownSwamp").isLocationGameTextCanceledForPlayer(scn.LS));
    }

    private VirtualTableScenario forestBattle(boolean shield) {
        var scn = ready(shield);
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetLSCard("boshek"), scn.GetDSCard("djas"));
        scn.SkipToDSTurn(Phase.BATTLE);
        assertEquals(2, scn.GetPower(scn.GetLSCard("boshek")));
        scn.DSInitiateBattle(scn.GetDSCard("forest"));
        scn.PassAllResponses();
        scn.PrepareDSDestiny(5);
        scn.PrepareLSDestiny(5);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(scn.GetDSTotalPower(), scn.GetLSTotalPower());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.PassAllResponses();
        if (scn.AwaitingDSAttritionPayment()) {
            scn.DSPass(); // Forest's own Dark text protects the opposing alien.
            scn.PassAllResponses();
        }
        return scn;
    }

    @Test
    public void LetsKeepALittleOptimismHereProtectsAnAlienPresentAtOpponentsForestFromActualAttrition() {
        var scn = forestBattle(true);
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertEquals(0, scn.LSGetChoiceMin());
        scn.LSPass();
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, scn.GetLSCard("boshek").getZone());
    }

    @Test
    public void LetsKeepALittleOptimismHereWithoutTheShieldTheSameAlienMustActuallyBeForfeited() {
        var scn = forestBattle(false);
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertEquals(1, scn.LSGetChoiceMin());
        scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("boshek"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("boshek")));
    }
}
