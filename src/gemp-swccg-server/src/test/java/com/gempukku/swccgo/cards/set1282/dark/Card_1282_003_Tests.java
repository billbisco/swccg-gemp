package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("landing", "1282_3");
                    put("iceplains", "3_148"); // Hoth: Ice Plains (5th Marker)
                    put("northridge", "3_149"); // Hoth: North Ridge (4th Marker)
                    put("perimeter", "3_144"); // Hoth: Defensive Perimeter (3rd Marker)
                    put("garrison", "104_7"); // Walker Garrison
                    put("blizzard1", "3_154"); // Blizzard 1 (AT-AT, no permanent pilot)
                    put("driver", "3_83"); // AT-AT Driver
                    put("blizzard2", "3_155"); // Blizzard 2 (AT-AT, permanent pilot)
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

    /** Stand-in for [Virtual Block 2] Imperial Control (flip side of 1220_72, not coded yet): matched by title + [Virtual Block 2] icon. */
    private static AbstractNormalEffect vb2ImperialControl() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Control, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V) {
        };
    }

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 20; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    @Test
    public void YouMayStartYourLandingStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("landing").getBlueprint();
        assertEquals(Title.You_May_Start_Your_Landing, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.TATOOINE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void YouMayStartYourLandingDeploysOnlyIfFifthMarkerOnTable() {
        var scn = GetScenario();
        var landing = scn.GetDSCard("landing");
        var iceplains = scn.GetDSCard("iceplains");
        scn.StartGame();
        scn.MoveCardsToHand(landing);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(6);
        assertFalse(scn.DSDeployAvailable(landing));

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveLocationToTable(iceplains);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(6);
        assertTrue(scn.DSDeployAvailable(landing));
        scn.DSDeployCard(landing);
        scn.PassAllResponses();
        assertTrue(scn.IsCardActive(landing));
    }

    private void twoMarkerSitesControlledWithATATs(VirtualTableScenario scn) {
        var landing = scn.GetDSCard("landing");
        var iceplains = scn.GetDSCard("iceplains");
        var northridge = scn.GetDSCard("northridge");
        scn.StartGame();
        scn.MoveLocationToTable(iceplains);
        scn.MoveLocationToTable(northridge);
        scn.MoveCardsToDSSideOfTable(landing);
    }

    /** Puts the AT-ATs on the marker sites during Light Side's turn, then goes to Dark Side's next control phase. */
    private void atatsArriveAndSkipToDSControl(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(scn.GetDSCard("iceplains"), scn.GetDSCard("blizzard1"));
        scn.BoardAsPilot(scn.GetDSCard("blizzard1"), scn.GetDSCard("driver"));
        scn.MoveCardsToLocation(scn.GetDSCard("northridge"), scn.GetDSCard("blizzard2"));
        scn.SkipToDSTurn(Phase.CONTROL);
    }

    @Test
    public void YouMayStartYourLandingForceLossIsLimitedToOneWithoutImperialControl() {
        var scn = GetScenario();
        var landing = scn.GetDSCard("landing");
        twoMarkerSitesControlledWithATATs(scn);
        atatsArriveAndSkipToDSControl(scn);

        assertTrue(scn.DSCardActionAvailable(landing, "Make opponent lose 1 Force"));
        int lsLifeForce = scn.GetLSLifeForceRemaining();
        scn.DSUseCardAction(landing, "Make opponent lose 1 Force");
        passUntil(scn, () -> scn.LSDecisionAvailable("Choose Force to lose") || scn.AwaitingLSForceLossPayment());
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
        }
        scn.PassAllResponses();
        assertEquals(lsLifeForce - 1, scn.GetLSLifeForceRemaining());
        passUntil(scn, scn::DSAnyDecisionsAvailable);
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(a -> a.startsWith("Make opponent lose")));
    }

    @Test
    public void YouMayStartYourLandingNonVirtualImperialOccupationDoesNotLiftTheLimit() {
        var scn = GetScenario();
        var landing = scn.GetDSCard("landing");
        twoMarkerSitesControlledWithATATs(scn);
        // Imperial Occupation (Special Edition 7_298, not [Virtual Block 2]); kept out of the deck so it is not a starting Objective
        MissionTestSupport.putOnTable(scn, 128232, scn.DS, VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("7_298"));
        atatsArriveAndSkipToDSControl(scn);
        assertTrue(scn.DSCardActionAvailable(landing, "Make opponent lose 1 Force"));
    }

    @Test
    public void YouMayStartYourLandingLosesOneForcePerMarkerSiteWithVirtualBlock2ImperialControl() {
        var scn = GetScenario();
        var landing = scn.GetDSCard("landing");
        twoMarkerSitesControlledWithATATs(scn);
        MissionTestSupport.putOnTable(scn, 128231, scn.DS, vb2ImperialControl());
        atatsArriveAndSkipToDSControl(scn);

        assertTrue(scn.DSCardActionAvailable(landing, "Make opponent lose 2 Force"));
        int lsLifeForce = scn.GetLSLifeForceRemaining();
        scn.DSUseCardAction(landing, "Make opponent lose 2 Force");
        passUntil(scn, () -> scn.AwaitingLSForceLossPayment());
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
        }
        scn.PassAllResponses();
        assertEquals(lsLifeForce - 2, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void YouMayStartYourLandingForceLossHappensAtEndOfControlPhaseIfNotUsed() {
        var scn = GetScenario();
        twoMarkerSitesControlledWithATATs(scn);
        atatsArriveAndSkipToDSControl(scn);
        int lsLifeForce = scn.GetLSLifeForceRemaining();
        scn.PassControlActions();
        passUntil(scn, () -> scn.AwaitingLSForceLossPayment() || scn.GetCurrentPhase() != Phase.CONTROL);
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
        }
        scn.PassAllResponses();
        assertEquals(lsLifeForce - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void YouMayStartYourLandingTakesWalkerGarrisonAndOrThirdMarkerOncePerGame() {
        var scn = GetScenario();
        var landing = scn.GetDSCard("landing");
        var garrison = scn.GetDSCard("garrison");
        var perimeter = scn.GetDSCard("perimeter");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(landing);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);
        scn.MoveCardsToTopOfDSReserveDeck(garrison, perimeter);

        assertTrue(scn.DSCardActionAvailable(landing, "Take cards into hand from Reserve Deck"));
        scn.DSUseCardAction(landing, "Take cards into hand from Reserve Deck");
        passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && scn.DSGetSelectableCount() > 0);
        assertTrue(scn.DSHasCardChoiceAvailable(garrison));
        assertTrue(scn.DSHasCardChoiceAvailable(perimeter));
        // "and/or": one card alone is a valid selection
        assertEquals(1, scn.DSGetChoiceMin());
        scn.DSChooseCard(garrison);
        // Re-submitting the same selection finishes with Walker Garrison alone
        scn.DSChooseCard(garrison);
        scn.PassAllResponses();

        assertInHand(garrison);
        passUntil(scn, scn::DSAnyDecisionsAvailable);
        assertFalse(scn.GetDSAvailableActions().contains("Take cards into hand from Reserve Deck"));
    }
}
