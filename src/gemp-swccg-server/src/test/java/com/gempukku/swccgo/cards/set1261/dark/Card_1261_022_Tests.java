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
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1261_022_Tests {
    protected String shieldId() { return "1261_22"; }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("forest", "7_121");
            put("operative", "7_1");
            put("luke", "1_19");
            put("nudj", "4_5");
            put("control", "4_48");
        }}, new HashMap<>() {{
            put("shield", shieldId());
            put("fear", "13_69");
            put("operative", "7_174");
            put("vader", "1_168");
            put("dantooine", "1_282");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario ready(boolean occupy, boolean shield) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveLocationToTable(scn.GetDSCard("dantooine"));
        scn.MoveLocationToTable(scn.GetLSCard("forest"));
        scn.gameState().setSubjugatedPlanet(occupy ? "Dantooine" : "Hoth");
        if (shield)
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("shield"));
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetDSCard("operative"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("operative"));
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void nextDarkControl(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.CONTROL);
    }

    private void startDarkDrain(VirtualTableScenario scn) {
        nextDarkControl(scn);
        scn.DSForceDrainAt(scn.GetLSCard("forest"));
        scn.PassAllResponses();
    }

    @Test
    public void NoEscapeStatsIconsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetDSCard("shield").getBlueprint();
        assertEquals("No Escape", card.getTitle());
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
    public void NoEscapeCanActuallyBePlayedFromTheStartingEffectsShieldStackForFree() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
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

    @Test
    public void NoEscapeResetsBothPlayersOperativeForfeitWhileItsOwnerOccupiesTheSubjugatedPlanet() {
        var scn = ready(true, true);
        assertEquals(0, scn.GetForfeit(scn.GetDSCard("operative")));
        assertEquals(0, scn.GetForfeit(scn.GetLSCard("operative")));
        assertEquals(8, scn.GetDSCard("vader").getBlueprint().getForfeit(), scn.epsilon);
    }

    @Test
    public void NoEscapeDoesNotResetForfeitForOccupyingADifferentPlanet() {
        var scn = ready(false, true);
        assertEquals(2, scn.GetForfeit(scn.GetDSCard("operative")));
        assertEquals(3, scn.GetForfeit(scn.GetLSCard("operative")));
    }

    @Test
    public void NoEscapeForfeitReturnsWhenTheSubjugatedPlanetIsNoLongerOccupied() {
        var scn = ready(true, true);
        assertEquals(0, scn.GetForfeit(scn.GetLSCard("operative")));
        scn.MoveCardsToDSHand(scn.GetDSCard("operative"));
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(3, scn.GetForfeit(scn.GetLSCard("operative")));
    }

    @Test
    public void NoEscapeOperativeActuallyForfeitsForZeroDuringABattleElsewhere() {
        var scn = ready(true, true);
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("operative"), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(scn.GetLSCard("operative"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        scn.PrepareLSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.PassAllResponses();
        assertEquals(3, scn.GetUnpaidDSBattleDamage());
        scn.DSPayBattleDamageFromCardInPlay(scn.GetDSCard("operative"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("operative")));
        assertEquals(3, scn.GetUnpaidDSBattleDamage());
        scn.DSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses();
    }

    @Test
    public void NoEscapeRemovesTheOperativeBonusFromAnActualForceDrain() {
        var scn = ready(true, true);
        startDarkDrain(scn);
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void NoEscapeLeavesTheOperativeBonusWhenTheSubjugatedPlanetIsNotOccupied() {
        var scn = ready(false, true);
        startDarkDrain(scn);
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void NoEscapePreventsAnOpponentsReductionOfTheOwnersActualDrain() {
        var scn = ready(true, true);
        scn.MoveCardsToDSHand(scn.GetDSCard("operative"));
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.ApplyAdHocModifier(new ForceDrainModifier(scn.GetLSCard("luke"), scn.GetLSCard("forest"), -1, scn.DS));
        startDarkDrain(scn);
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void NoEscapeDoesNotProtectTheDrainFromCancellation() {
        var scn = ready(true, true);
        scn.MoveCardsToLSHand(scn.GetLSCard("control"));
        nextDarkControl(scn);
        scn.DSForceDrainAt(scn.GetLSCard("forest"));
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("control")));
        scn.LSPlayCard(scn.GetLSCard("control"));
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
    }

    private int activateWithNudj(boolean shield) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("dantooine"));
        scn.MoveLocationToTable(scn.GetLSCard("forest"));
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetLSCard("nudj"));
        if (shield)
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("shield"));
        assertEquals(shield ? 1 : 0, scn.game().getModifiersQuerying().getIconCount(scn.gameState(), scn.GetLSCard("forest"), Icon.DARK_FORCE));
        return scn.DSActivateMaxForceAndPass();
    }

    @Test
    public void NoEscapeRestoresTheForceIconAndActualActivationWithoutAnySubjugatedPlanetOccupation() {
        assertEquals(activateWithNudj(false) + 1, activateWithNudj(true));
    }

    @Test
    public void NoEscapePreservesNudjsPrintedFerocityWhileSuppressingItsLocationIconModifier() {
        var scn = ready(false, true);
        scn.MoveCardsToDSHand(scn.GetDSCard("operative"));
        scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetLSCard("nudj"));
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(1, scn.game().getModifiersQuerying().getIconCount(scn.gameState(), scn.GetLSCard("forest"), Icon.DARK_FORCE));
        assertEquals(1, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), scn.GetLSCard("nudj"), 5f), scn.epsilon);
    }
}
