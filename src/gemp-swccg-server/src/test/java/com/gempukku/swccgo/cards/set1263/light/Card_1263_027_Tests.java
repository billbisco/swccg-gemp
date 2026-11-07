package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1263_027_Tests {
    private static final String BOOST = "Lose 1 Force to boost";
    private static final String EXCHANGE = "Exchange a character with the same persona in ";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("tactics", "1263_27"); put("tactics2", "1263_27"); put("luke", "1_19"); put("jedi", "9_24");
            put("commander", "3_3"); put("obi", "1_21"); put("han", "1_11"); put("saber", "9_90"); put("traffic", "1_66"); put("remote", "1_136");
        }}, new HashMap<>() {{ put("vader", "1_168"); }},
                60, 60, StartingSetup.LSStartingLocation("1_129"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario started() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("remote"));
        scn.MoveCardsToLSHand(scn.GetLSCard("tactics"), scn.GetLSCard("tactics2")); return scn;
    }
    @Test public void TacticsRetainsFourDestinyFromTheFullCard() {
        var scn = scenario(); var b = scn.GetLSCard("tactics").getBlueprint();
        assertEquals("Desperate Tactics", b.getTitle()); assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity()); assertEquals(CardSubtype.USED, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.INTERRUPT, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }
    private VirtualTableScenario battle(boolean opponentInitiates) {
        var scn = started(); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("jedi"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("jedi"), scn.GetLSCard("saber"));
        scn.MoveCardsToLocation(scn.GetLSCard("remote"), scn.GetLSCard("obi"));
        if (opponentInitiates) { scn.SkipToDSTurn(Phase.BATTLE); scn.DSInitiateBattle(site); }
        else { scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); }
        return scn;
    }
    private void boost(VirtualTableScenario scn, PhysicalCardImpl target) {
        int lost = scn.GetLSLostPileCount(); scn.LSPlayCard(scn.GetLSCard("tactics"), BOOST);
        assertTrue(scn.LSHasCardChoiceAvailable(target)); assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("obi")));
        scn.LSChooseCard(target); scn.PassAllResponses(); scn.LSChooseCard(scn.GetTopOfLSReserveDeck()); scn.PassAllResponses();
        assertEquals(lost+1, scn.GetLSLostPileCount()); assertInZone(Zone.USED_PILE, scn.GetLSCard("tactics"));
    }
    @Test public void TacticsCanTargetOpponentActuallyPaysOneAndRemovesAllImmunityThroughTurn() {
        var scn = battle(true); var vader = scn.GetDSCard("vader"); assertEquals(6, scn.GetPower(vader));
        assertTrue(Filters.hasAnyImmunityToAttrition.accepts(scn.game(), vader)); boost(scn, vader);
        assertEquals(8, scn.GetPower(vader)); assertFalse(Filters.hasAnyImmunityToAttrition.accepts(scn.game(), vader));
        assertTrue(Filters.hasAnyImmunityToAttrition.accepts(scn.game(), scn.GetLSCard("jedi")));
        scn.SkipToDamageSegment(false);
        scn.PassAllResponses();
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(8, scn.GetPower(vader)); assertFalse(Filters.hasAnyImmunityToAttrition.accepts(scn.game(), vader));
        scn.SkipToPhase(Phase.DRAW); scn.PassDrawActions(); scn.PassResponses("RECIRCULATED"); scn.PassAllResponses();
        assertEquals(scn.LS, scn.GetCurrentPlayer());
        assertEquals(6, scn.GetPower(vader)); assertTrue(Filters.hasAnyImmunityToAttrition.accepts(scn.game(), vader));
    }
    @Test public void TacticsCanAlsoBoostOwnCharacterAtBattle() {
        var scn = battle(true); var jedi = scn.GetLSCard("jedi"); int power = scn.GetPower(jedi); boost(scn, jedi);
        assertEquals(power+2, scn.GetPower(jedi)); assertFalse(Filters.hasAnyImmunityToAttrition.accepts(scn.game(), jedi));
        assertTrue(Filters.hasAnyImmunityToAttrition.accepts(scn.game(), scn.GetDSCard("vader")));
    }
    @Test public void TacticsCannotUseBoostWhenOwnerInitiatesBattle() {
        var scn = battle(false); assertNotNull(scn.gameState().getBattleState());
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(BOOST)));
    }
    @Test public void TacticsCannotUseBoostAfterBattleInitiationWindow() {
        var scn = battle(true); assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("tactics"), BOOST));
        scn.LSPass(); scn.PassAllResponses();
        assertNotNull(scn.gameState().getBattleState());
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains(BOOST)));
    }
    private VirtualTableScenario exchangeSetup(boolean lost) {
        var scn = started(); scn.MoveCardsToLSHand(scn.GetLSCard("luke")); scn.SkipToLSTurn(Phase.CONTROL);
        if (lost) {
            scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("jedi"), scn.GetLSCard("han"), scn.GetLSCard("traffic"));
            scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("commander"));
        } else {
            scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("jedi"), scn.GetLSCard("han"), scn.GetLSCard("traffic"));
            scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("commander"));
        }
        scn.LSPass(); scn.DSPass(); return scn;
    }
    private boolean selectable(VirtualTableScenario scn, String key) {
        int i = scn.LSGetBPChoices().indexOf(scn.GetLSCard(key).getBlueprintId(true));
        return i >= 0 && Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(i));
    }
    private void exchange(VirtualTableScenario scn, Zone pile) {
        scn.gameState().markCardsRevealedFromPile(Collections.singletonList(scn.GetLSCard("traffic")));
        assertTrue(scn.gameState().isCardRevealedFromPile(scn.GetLSCard("traffic")));
        scn.LSPlayCard(scn.GetLSCard("tactics"), EXCHANGE+pile.getHumanReadable()); scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose card from hand") && scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke"))) scn.LSChooseCard(scn.GetLSCard("luke"));
        scn.PassAllResponses();
        assertTrue(selectable(scn, "jedi")); assertFalse(selectable(scn, "han")); assertFalse(selectable(scn, "traffic"));
        scn.LSChooseCard(scn.GetLSCard("jedi")); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke"))) scn.LSChooseCard(scn.GetLSCard("luke"));
        scn.PassAllResponses(); assertInHand(scn.GetLSCard("jedi")); assertInZone(pile, scn.GetLSCard("luke"));
        assertInZone(pile, scn.GetLSCard("traffic")); assertFalse(scn.gameState().isCardRevealedFromPile(scn.GetLSCard("traffic")));
        assertInZone(Zone.USED_PILE, scn.GetLSCard("tactics"));
    }
    @Test public void TacticsExchangesSamePersonaFromReserveAndReshuffles() { exchange(exchangeSetup(false), Zone.RESERVE_DECK); }
    @Test public void TacticsExchangesSamePersonaFromLostPileAndReshuffles() { exchange(exchangeSetup(true), Zone.LOST_PILE); }
    @Test public void TacticsOneGameLimitSharedByBothPilesAndAnotherCopyNextTurn() {
        var scn = exchangeSetup(false); exchange(scn, Zone.RESERVE_DECK); scn.SkipToLSTurn(Phase.CONTROL);
        assertInHand(scn.GetLSCard("tactics2")); assertInHand(scn.GetLSCard("jedi")); assertInZone(Zone.LOST_PILE, scn.GetLSCard("commander"));
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("tactics2"), EXCHANGE+Zone.LOST_PILE.getHumanReadable()));
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("tactics2"), EXCHANGE+Zone.RESERVE_DECK.getHumanReadable()));
    }
    @Test public void TacticsCannotExchangeWithoutACharacterInHand() {
        var scn = started(); var tactics = scn.GetLSCard("tactics");
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSHand().stream().filter(c -> c != tactics).map(PhysicalCardImpl.class::cast).toArray(PhysicalCardImpl[]::new));
        scn.SkipToLSTurn(Phase.CONTROL); assertFalse(scn.LSCardPlayAvailable(tactics));
    }
}
