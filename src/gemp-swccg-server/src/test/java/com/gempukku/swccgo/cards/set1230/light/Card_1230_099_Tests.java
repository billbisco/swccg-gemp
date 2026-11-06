package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1230_099_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("patience", "1230_99"); put("second", "1230_99"); put("luke", "1_19"); put("shmi", "11_12");
        }}, new HashMap<>() {{ put("vader", "1_168"); put("trooper", "1_194"); put("prison", "1_284"); }},
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void AJedisPatienceStatsIconsKeywordsAndHostLoreAreCorrect() {
        var s = scenario(); var c = s.GetLSCard("patience").getBlueprint();
        assertEquals("A Jedi's Patience", c.getTitle()); assertTrue(c.hasVirtualSuffix());
        assertEquals(Side.LIGHT, c.getSide()); assertEquals(Uniqueness.UNIQUE, c.getUniqueness());
        assertEquals(5, c.getDestiny(), s.epsilon); assertEquals(CardSubtype.USED_OR_LOST, c.getCardSubtype());
        assertEquals("Early on in his duel with Vader, Luke took his time in trying to sense his opponent's weaknesses.", c.getLore());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, c.getExpansionSet()); assertEquals(Rarity.V, c.getRarity());
        s.BlueprintCardTypeCheck(c, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        s.BlueprintIconCheck(c, new ArrayList<>() {{ add(Icon.INTERRUPT); add(Icon.TATOOINE); add(Icon.LEGACY_BLOCK_3); }});
        s.BlueprintKeywordCheck(c, new ArrayList<Keyword>());
    }

    private VirtualTableScenario ready(boolean captive, boolean frozen) {
        var s = scenario(); s.StartGame();
        s.MoveCardsToLSHand(s.GetLSCard("patience"), s.GetLSCard("second"));
        s.MoveOutOfPlay(s.GetLSCard("shmi"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"), s.GetDSCard("vader"));
        if (captive) s.CaptureCardWith(s.GetDSCard("vader"), s.GetLSCard("luke"));
        if (frozen) s.FreezeCard(s.GetLSCard("luke"));
        s.SkipToLSTurn(Phase.CONTROL);
        return s;
    }

    @Test
    public void AJedisPatienceUsedRequiresACaptive() {
        var s = ready(false, false);
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Activate 2 Force"));
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Initiate duel"));
    }

    @Test
    public void AJedisPatienceUsedActivatesExactlyTwoAndGoesToUsedPile() {
        var s = ready(true, false); int force = s.GetLSForcePileCount(); int reserve = s.GetLSReserveDeckCount();
        s.LSPlayCard(s.GetLSCard("patience"), "Activate 2 Force"); s.PassAllResponses();
        assertEquals(force + 2, s.GetLSForcePileCount()); assertEquals(reserve - 2, s.GetLSReserveDeckCount());
        assertInZone(Zone.USED_PILE, s.GetLSCard("patience"));
    }

    @Test
    public void AJedisPatienceFrozenCaptiveAllowsUsedButNotLost() {
        var s = ready(true, true);
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Activate 2 Force"));
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Initiate duel"));
    }

    private void duel(VirtualTableScenario s, int light1, int light2, int dark1, int dark2) {
        s.PrepareLSDestiny(light2); s.PrepareLSDestiny(light1);
        s.PrepareDSDestiny(dark2); s.PrepareDSDestiny(dark1);
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Initiate duel"));
        s.LSPlayCard(s.GetLSCard("patience"), "Initiate duel");
        for (int i = 0; i < 32 && s.GetLSCard("patience").getZone() != Zone.LOST_PILE; i++) {
            if (s.LSHasCardChoiceAvailable(s.GetLSCard("luke"))) s.LSChooseCard(s.GetLSCard("luke"));
            else if (s.LSHasCardChoiceAvailable(s.GetDSCard("vader"))) s.LSChooseCard(s.GetDSCard("vader"));
            else s.PassAllResponses();
        }
        assertInZone(Zone.LOST_PILE, s.GetLSCard("patience"));
        for (int value : new int[]{light1, light2}) assertInZone(Zone.USED_PILE, s.GetLSDestiny(value));
        for (int value : new int[]{dark1, dark2}) assertInZone(Zone.USED_PILE, s.GetDSDestiny(value));
    }

    @Test
    public void AJedisPatienceLightWinnerGainsImmunityAndDarkLoserCannotMove() {
        var s = ready(false, false); duel(s, 6, 5, 1, 2);
        assertTrue((s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), s.GetLSCard("luke")) == Float.MAX_VALUE));
        assertFalse((s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), s.GetDSCard("vader")) == Float.MAX_VALUE));
        assertTrue(s.game().getModifiersQuerying().mayNotMove(s.gameState(), s.GetDSCard("vader")));
        assertFalse(s.game().getModifiersQuerying().mayNotMove(s.gameState(), s.GetLSCard("luke")));
    }

    @Test
    public void AJedisPatienceDarkWinnerGainsImmunityAndLightLoserCannotMove() {
        var s = ready(false, false); duel(s, 1, 2, 6, 5);
        assertTrue((s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), s.GetDSCard("vader")) == Float.MAX_VALUE));
        assertTrue(s.game().getModifiersQuerying().mayNotMove(s.gameState(), s.GetLSCard("luke")));
    }

    @Test
    public void AJedisPatienceEqualDestinyPlusAbilityTotalsGiveNeitherRewardNorPenalty() {
        var s = ready(false, false); duel(s, 6, 1, 3, 2);
        assertFalse((s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), s.GetLSCard("luke")) == Float.MAX_VALUE));
        assertFalse((s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), s.GetDSCard("vader")) == Float.MAX_VALUE));
        assertFalse(s.game().getModifiersQuerying().mayNotMove(s.gameState(), s.GetLSCard("luke")));
        assertFalse(s.game().getModifiersQuerying().mayNotMove(s.gameState(), s.GetDSCard("vader")));
    }

    @Test
    public void AJedisPatienceNonFrozenEscortedSkywalkerCanDuel() {
        var s = ready(true, false); duel(s, 6, 5, 1, 2);
        assertTrue(s.GetLSCard("luke").isCaptive());
        assertTrue((s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), s.GetLSCard("luke")) == Float.MAX_VALUE));
    }

    @Test
    public void AJedisPatienceCannotDuelShmiEvenThoughSheIsASkywalker() {
        var s = scenario(); s.StartGame();
        s.MoveCardsToLSHand(s.GetLSCard("patience")); s.MoveOutOfPlay(s.GetLSCard("luke"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("shmi"), s.GetDSCard("vader"));
        s.CaptureCardWith(s.GetDSCard("vader"), s.GetLSCard("shmi")); s.SkipToLSTurn(Phase.CONTROL);
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Activate 2 Force"));
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Initiate duel"));
    }

    @Test
    public void AJedisPatienceDuelRequiresADarkJediPresentWithTheSkywalker() {
        for (boolean separated : new boolean[]{false, true}) {
            var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("patience"));
            s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"));
            if (separated) s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("vader"));
            else {
                s.MoveOutOfPlay(s.GetDSCard("vader"));
                s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetDSCard("trooper"));
            }
            s.SkipToLSTurn(Phase.CONTROL);
            assertFalse(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Initiate duel"));
        }
    }

    @Test
    public void AJedisPatienceImprisonedCaptiveAllowsUsedButIsNotPresentWithADarkJedi() {
        var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("patience"));
        s.MoveLocationToTable(s.GetDSCard("prison"));
        s.MoveCardsToLocation(s.GetDSCard("prison"), s.GetLSCard("luke"), s.GetDSCard("vader"));
        s.CaptureCardWith(s.GetDSCard("vader"), s.GetLSCard("luke"));
        s.gameState().imprisonCharacter(s.game(), s.GetLSCard("luke"), s.GetDSCard("prison"));
        s.SkipToLSTurn(Phase.CONTROL);
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Activate 2 Force"));
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("patience"), "Initiate duel"));
    }

    @Test
    public void AJedisPatienceLostIsOncePerGameAcrossCopiesButUsedRemainsAvailable() {
        var s = ready(true, false); duel(s, 6, 5, 1, 2);
        advanceToDarkControl(s); if (s.DSAnyDecisionsAvailable()) s.DSPass();
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("second"), "Initiate duel"));
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("second"), "Activate 2 Force"));
    }

    @Test
    public void AJedisPatienceUsedDoesNotConsumeTheOncePerGameDuel() {
        var s = ready(true, false);
        s.LSPlayCard(s.GetLSCard("second"), "Activate 2 Force"); s.PassAllResponses();
        advanceToDarkControl(s);
        if (s.DSAnyDecisionsAvailable()) s.DSPass();
        duel(s, 6, 5, 1, 2);
        assertTrue((s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), s.GetLSCard("luke")) == Float.MAX_VALUE));
    }

    @Test
    public void AJedisPatienceWinnerAndLoserModifiersExpireAtEndOfTurn() {
        var s = ready(false, false); duel(s, 6, 5, 1, 2);
        advanceToDarkControl(s);
        assertFalse((s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), s.GetLSCard("luke")) == Float.MAX_VALUE));
        assertFalse(s.game().getModifiersQuerying().mayNotMove(s.gameState(), s.GetDSCard("vader")));
    }

    private void advanceToDarkControl(VirtualTableScenario s) {
        for (int i = 0; i < 40 && s.gameState().getCurrentPlayerId().equals(s.LS); i++) {
            if (s.LSAnyDecisionsAvailable()) s.LSPass(); else s.DSPass();
        }
        s.SkipToPhase(Phase.CONTROL);
        assertEquals(s.DS, s.gameState().getCurrentPlayerId());
    }

    @Test
    public void AJedisPatienceDuelLoserMayStillInitiateAndParticipateInBattle() {
        var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("patience"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("luke"), s.GetDSCard("vader"));
        s.SkipToDSTurn(Phase.BATTLE); s.DSPass();
        duel(s, 6, 5, 1, 2);
        s.PassAllResponses(); if (s.LSAnyDecisionsAvailable()) s.LSPass();
        assertTrue(s.game().getModifiersQuerying().mayNotMove(s.gameState(), s.GetDSCard("vader")));
        assertTrue(s.DSCanInitiateBattle(s.GetLSStartingLocation()));
        s.DSInitiateBattle(s.GetLSStartingLocation());
        assertTrue(s.gameState().getBattleState().getCardsParticipating(s.DS).contains(s.GetDSCard("vader")));
    }
}
