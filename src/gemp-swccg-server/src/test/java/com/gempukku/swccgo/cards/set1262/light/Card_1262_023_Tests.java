package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_023_Tests {
    private static final String USED = "Place a card from hand in Used Pile";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "thrown", "1262_23", "han", "108_1", "lando", "13_27", "luke", "1_19",
                "ordinary", "1_66", "spare", "9_47")),
                new HashMap<>(Map.of("ig88", "109_11", "vader", "1_168", "alter", "1_234", "fett", "13_59")),
                42, 42, StartingSetup.LSStartingLocation("1_136"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void thrownBackUsesTheFinalLegacyPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("thrown").getBlueprint();
        assertEquals("Thrown Back", b.getTitle()); assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.CORUSCANT, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario deploy() {
        var scn = scenario(); var effect = scn.GetLSCard("thrown");
        scn.MoveCardsToLSHand(effect);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(effect));
        scn.LSPlayCard(effect); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        assertTrue(scn.gameState().getStackedCards(effect).isEmpty());
        return scn;
    }

    private boolean mayPlace(VirtualTableScenario scn) {
        return scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(USED));
    }

    @Test public void thrownBackActuallyPlacesOneCardThenResetsOnTheNextTurn() {
        var scn = deploy(); var effect = scn.GetLSCard("thrown"); var card = scn.GetLSCard("ordinary");
        var other = scn.GetLSCard("spare");
        assertFalse(mayPlace(scn));
        scn.MoveCardsToLSHand(card, other);
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(mayPlace(scn));
        scn.LSUseCardAction(effect, USED);
        scn.LSChooseCard(card); scn.PassAllResponses();
        assertTrue(scn.GetLSUsedPile().contains(card));
        assertInHand(other);
        assertFalse(mayPlace(scn));
        scn.SkipToDSTurn(Phase.CONTROL);
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertTrue(mayPlace(scn));
        scn.LSUseCardAction(effect, USED);
        if (scn.LSHasCardChoiceAvailable(other)) scn.LSChooseCard(other);
        scn.PassAllResponses();
        assertTrue(scn.GetLSUsedPile().contains(other));
        assertFalse(mayPlace(scn));
    }

    private VirtualTableScenario battle(boolean effect, boolean maintenance) {
        var scn = scenario(); var site = scn.GetLSStartingLocation();
        if (effect) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("thrown"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard(maintenance ? "lando" : "han"));
        if (maintenance) scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), scn.GetDSCard("fett"));
        else scn.MoveCardsToLocation(site, scn.GetDSCard("ig88"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site); scn.PassAllResponses();
        return scn;
    }

    private int draws(VirtualTableScenario scn, String player) {
        return scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), player, false, false);
    }

    @Test public void permanentWeaponSourcesOnBothSidesCannotAddButOrdinaryDestinyRemains() {
        var baseline = battle(false, false);
        assertEquals(2, draws(baseline, baseline.LS));
        assertEquals(1, draws(baseline, baseline.DS)); // IG-88 has ability 3; his bonus supplies the draw.
        var restricted = battle(true, false);
        assertEquals(1, draws(restricted, restricted.LS));
        assertEquals(0, draws(restricted, restricted.DS));
    }

    @Test public void maintenanceSourcesCannotAddButAnOrdinaryVaderStillCan() {
        var baseline = battle(false, true);
        assertEquals(2, draws(baseline, baseline.LS)); assertEquals(4, draws(baseline, baseline.DS));
        var restricted = battle(true, true);
        assertEquals(1, draws(restricted, restricted.LS)); assertEquals(2, draws(restricted, restricted.DS));
    }

    @Test public void realAlterCanCancelAnotherEffectButCannotTargetThrownBack() {
        var scn = deploy(); var effect = scn.GetLSCard("thrown"); var ordinary = scn.GetLSCard("ordinary");
        scn.MoveCardsToLSSideOfTable(ordinary);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        var alter = scn.GetDSCard("alter");
        scn.MoveCardsToDSHand(alter); scn.SkipToDSTurn(Phase.CONTROL); scn.DSActivateForceCheat(5);
        assertTrue(scn.DSCardPlayAvailable(alter));
        scn.DSPlayCard(alter, "Cancel");
        assertFalse(scn.DSHasCardChoiceAvailable(effect));
        if (scn.DSHasCardChoiceAvailable(ordinary)) scn.DSChooseCard(ordinary);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        assertFalse(ordinary.getZone().isInPlay());
    }
}
