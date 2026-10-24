package com.gempukku.swccgo.cards.set1200.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1200_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("another", "1200_11");
                    put("unusual", "13_5");
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
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

    protected VirtualTableScenario GetBHBMScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("another", "1200_11");
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("db", "1_291");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.BHBMObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ThereIsAnotherStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("another").getBlueprint();

        assertEquals("There is Another", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals("Blank", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.REFLECTIONS_III);
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.LEGACY_BLOCK_D);
        }});
        assertEquals(ExpansionSet.VIRTUAL_SHIELDS, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ThereIsAnotherPlaysFromStartingEffectUnlessASkywalkerHasBeenDeployed() {
        var scn = GetScenario();
        var another = scn.GetLSCard("another");
        var unusual = scn.GetLSCard("unusual");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(unusual);
        scn.StackCardsOn(unusual, another);
        awaitLSControl(scn);

        assertTrue(scn.LSCardActionAvailable(unusual, "Defensive Shield"));
        scn.LSUseCardAction(unusual, "Defensive Shield");
        scn.LSChooseCard(another);
        scn.PassAllResponses();
        assertTrue(another.getZone().isInPlay());
    }

    @Test
    public void ThereIsAnotherCannotPlayAfterASkywalkerHasBeenDeployedThisGame() {
        var scn = GetScenario();
        var another = scn.GetLSCard("another");
        var unusual = scn.GetLSCard("unusual");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        var chasm = scn.GetLSStartingLocation();
        scn.MoveCardsToLSSideOfTable(unusual);
        scn.StackCardsOn(unusual, another);
        scn.MoveCardsToLSHand(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(1);
        assertTrue(scn.LSCardPlayAvailable(luke));
        scn.LSPlayCard(luke);
        scn.LSChooseCard(chasm);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.CONTROL);
        awaitLSControlWithoutSkippingTurn(scn);
        assertFalse(scn.LSCardActionAvailable(unusual, "Defensive Shield"));
    }

    @Test
    public void ThereIsAnotherMakesYourDestinyTreatLeiaAsTheTargetInsteadOfLuke() {
        var scn = GetBHBMScenario();
        var another = scn.GetLSCard("another");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");
        var destiny = scn.GetDSCard("destiny");
        var db = scn.GetDSCard("db");
        var chasm = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(another);
        assertTrue(scn.game().getModifiersQuerying().hasGameTextModification(scn.gameState(), destiny,
                ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE));

        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, vader);
        scn.MoveCardsToLocation(chasm, luke);
        scn.SkipToDSTurn();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSForceLossPayment());
        scn.LSPayRemainingForceLossFromReserveDeck();
    }

    @Test
    public void ThereIsAnotherYourDestinyDoesNotLoseForceWhenLeiaIsAtABattleground() {
        var scn = GetBHBMScenario();
        var another = scn.GetLSCard("another");
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");
        var db = scn.GetDSCard("db");
        var chasm = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(another);
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLocation(db, vader);
        scn.MoveCardsToLocation(chasm, leia);
        scn.SkipToDSTurn();
        scn.PassAllResponses();
        assertFalse(scn.AwaitingLSForceLossPayment());
    }

    @Test
    public void ThereIsAnotherOpponentMayActivateOneForceOnceDuringOwnersTurn() {
        var scn = GetScenario();
        var another = scn.GetLSCard("another");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(another);
        awaitLSControl(scn);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(1));
        scn.LSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSControlPhaseActions());

        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(another, "Activate 1 Force"));
        scn.DSUseCardAction(another, "Activate 1 Force");
        scn.PassCardAndForceUseResponses();
        assertEquals(forceBefore + 1, scn.GetDSForcePileCount());

        scn.SkipToLSTurn(Phase.CONTROL);
        awaitLSControlWithoutSkippingTurn(scn);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(1));
        scn.LSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSCardActionAvailable(another, "Activate 1 Force"));
    }

    @Test
    public void ThereIsAnotherOpponentMayNotActivateForceDuringDarkTurn() {
        var scn = GetScenario();
        var another = scn.GetLSCard("another");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(another);
        scn.SkipToDSTurn(Phase.CONTROL);
        for (int i = 0; i < 12 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSAnyDecisionsAvailable() && !scn.AwaitingDSControlPhaseActions()) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(1));
        assertFalse(scn.DSCardActionAvailable(another, "Activate 1 Force"));
    }

    private void awaitLSControl(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.CONTROL);
        awaitLSControlWithoutSkippingTurn(scn);
    }

    private void awaitLSControlWithoutSkippingTurn(VirtualTableScenario scn) {
        for (int i = 0; i < 12 && !scn.AwaitingLSControlPhaseActions(); i++) {
            if (scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
                scn.DSPass();
                continue;
            }
            if (scn.LSAnyDecisionsAvailable() && !scn.AwaitingLSControlPhaseActions()) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.AwaitingLSControlPhaseActions());
    }
}
