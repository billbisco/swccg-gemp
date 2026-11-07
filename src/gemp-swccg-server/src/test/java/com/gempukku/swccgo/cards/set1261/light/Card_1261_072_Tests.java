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

public class Card_1261_072_Tests {
    protected String shieldId() { return "1261_72"; }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("shield", shieldId());
            put("otherPrinting", shieldId().equals("1261_72") ? "1261_74" : "1261_72");
            put("fear", "13_5");
            put("optimism", "1261_77");
            put("operative", "7_1");
            put("luke", "1_19");
            put("alderaan", "1_121");
        }}, new HashMap<>() {{
            put("forest", "7_284");
            put("operative", "7_174");
            put("djas", "1_171");
            put("sleen", "4_111");
            put("control", "4_139");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario ready(boolean occupy, boolean shield) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveLocationToTable(scn.GetLSCard("alderaan"));
        scn.MoveLocationToTable(scn.GetDSCard("forest"));
        scn.gameState().setRenegadePlanet(occupy ? "Alderaan" : "Hoth");
        if (shield)
            scn.MoveCardsToLSSideOfTable(scn.GetLSCard("shield"));
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetLSCard("operative"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("operative"));
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void nextLightControl(VirtualTableScenario scn) {
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.SkipToLSTurn(Phase.CONTROL);
    }

    private void startLightDrain(VirtualTableScenario scn) {
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetLSCard("luke"));
        nextLightControl(scn);
        scn.LSForceDrainAt(scn.GetDSCard("forest"));
        scn.PassAllResponses();
    }

    @Test
    public void OuneeTaStillPlaysWhenOptimismWouldOtherwiseCancelIt() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("fear"), scn.GetLSCard("optimism"));
        scn.StackCardsOn(scn.GetLSCard("fear"), scn.GetLSCard("shield"));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSUseCardAction(scn.GetLSCard("fear"), "Defensive Shield");
        scn.LSChooseCard(scn.GetLSCard("shield"));
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetLSCard("shield").getZone());
    }

    @Test
    public void OuneeTaCannotBeCanceledAndDoesNotProtectOtherCards() {
        var scn = ready(false, true);
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), scn.GetLSCard("shield")));
        assertFalse(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), scn.GetLSCard("luke")));
    }

    @Test
    public void OuneeTaConversionPrintingCountsTowardTheExistingOutsideDeckShieldCap() throws DeckInvalidException {
        var library = new SwccgCardBlueprintLibrary();
        assertEquals("1261_72", library.getBaseBlueprintId(shieldId()));
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
    public void OuneeTaPrintingsKeepTheirOwnImageIdsButShareRulesAndUniqueness() {
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
    public void OuneeTaStatsIconsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("shield").getBlueprint();
        assertEquals("Ounee Ta", card.getTitle());
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
    public void OuneeTaCanActuallyBePlayedFromTheStartingEffectsShieldStackForFree() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
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

    @Test
    public void OuneeTaResetsBothPlayersOperativeForfeitWhileItsOwnerOccupiesTheRenegadePlanet() {
        var scn = ready(true, true);
        assertEquals(0, scn.GetForfeit(scn.GetLSCard("operative")));
        assertEquals(0, scn.GetForfeit(scn.GetDSCard("operative")));
        assertEquals(7, scn.GetLSCard("luke").getBlueprint().getForfeit(), scn.epsilon);
    }

    @Test
    public void OuneeTaDoesNotResetForfeitForOccupyingADifferentPlanet() {
        var scn = ready(false, true);
        assertEquals(4, scn.GetForfeit(scn.GetLSCard("operative")));
        assertEquals(2, scn.GetForfeit(scn.GetDSCard("operative")));
    }

    @Test
    public void OuneeTaForfeitReturnsWhenTheRenegadePlanetIsNoLongerOccupied() {
        var scn = ready(true, true);
        assertEquals(0, scn.GetForfeit(scn.GetDSCard("operative")));
        scn.MoveCardsToLSHand(scn.GetLSCard("operative"));
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(2, scn.GetForfeit(scn.GetDSCard("operative")));
    }

    @Test
    public void OuneeTaOperativeActuallyForfeitsForZeroDuringABattleElsewhere() {
        var scn = ready(true, true);
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("operative"), scn.GetDSCard("djas"));
        scn.MoveCardsToDSHand(scn.GetDSCard("operative"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        scn.PrepareDSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.PassAllResponses();
        assertTrue(scn.GetUnpaidLSBattleDamage() > 0);
        int unpaid = scn.GetUnpaidLSBattleDamage();
        scn.LSPayBattleDamageFromCardInPlay(scn.GetLSCard("operative"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("operative")));
        assertEquals(unpaid, scn.GetUnpaidLSBattleDamage());
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses();
    }

    @Test
    public void OuneeTaRemovesTheOperativeBonusFromAnActualForceDrain() {
        var scn = ready(true, true);
        startLightDrain(scn);
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void OuneeTaLeavesTheOperativeBonusWhenTheRenegadePlanetIsNotOccupied() {
        var scn = ready(false, true);
        startLightDrain(scn);
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void OuneeTaPreventsAnOpponentsReductionOfTheOwnersActualDrain() {
        var scn = ready(true, true);
        scn.MoveCardsToLSHand(scn.GetLSCard("operative"));
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("djas"));
        scn.ApplyAdHocModifier(new ForceDrainModifier(scn.GetDSCard("djas"), scn.GetDSCard("forest"), -1, scn.LS));
        startLightDrain(scn);
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void OuneeTaDoesNotProtectTheDrainFromCancellation() {
        var scn = ready(true, true);
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("control"));
        nextLightControl(scn);
        scn.LSForceDrainAt(scn.GetDSCard("forest"));
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("control")));
        scn.DSPlayCard(scn.GetDSCard("control"));
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
    }

    private int activateWithSleen(boolean shield) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveLocationToTable(scn.GetLSCard("alderaan"));
        scn.MoveLocationToTable(scn.GetDSCard("forest"));
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetDSCard("sleen"));
        if (shield)
            scn.MoveCardsToLSSideOfTable(scn.GetLSCard("shield"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertEquals(shield ? 1 : 0, scn.game().getModifiersQuerying().getIconCount(scn.gameState(), scn.GetDSCard("forest"), Icon.LIGHT_FORCE));
        return scn.LSActivateMaxForceAndPass();
    }

    @Test
    public void OuneeTaRestoresTheForceIconAndActualActivationWithoutAnyRenegadePlanetOccupation() {
        assertEquals(activateWithSleen(false) + 1, activateWithSleen(true));
    }

    @Test
    public void OuneeTaPreservesSleensPrintedFerocityWhileSuppressingItsLocationIconModifier() {
        var scn = ready(false, true);
        scn.MoveCardsToLSHand(scn.GetLSCard("operative"));
        scn.MoveCardsToLocation(scn.GetDSCard("forest"), scn.GetDSCard("sleen"));
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(1, scn.game().getModifiersQuerying().getIconCount(scn.gameState(), scn.GetDSCard("forest"), Icon.LIGHT_FORCE));
        assertEquals(2, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), scn.GetDSCard("sleen"), 5f), scn.epsilon);
    }
}
