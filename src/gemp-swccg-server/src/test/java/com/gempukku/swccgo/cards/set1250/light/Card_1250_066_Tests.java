package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_066_Tests {
    private static final String CHANGE = "Activate 1 Force and shuffle Reserve Deck";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("odds", "1250_66"); put("luke", "1_19"); put("guard", "1_26");
        }}, new HashMap<>() {{
            put("emperor", "9_109"); put("lightning", "9_136"); put("vader", "1_168"); put("saber", "1_324");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalChangingOddsIsAUsedInterruptWithThePrintedIcons() {
        var s = scenario(); var bp = s.GetLSCard("odds").getBlueprint();
        assertEquals(Title.Changing_The_Odds, bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(CardSubtype.USED, bp.getCardSubtype()); assertEquals(5, bp.getDestiny(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.EPISODE_I, Icon.TATOOINE, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    private boolean offered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardPlayAvailable(s.GetLSCard("odds"), CHANGE);
    }

    private void passUntilOffer(VirtualTableScenario s) {
        for (int i = 0; i < 40 && !offered(s)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
    }

    private void checkActivationAndShuffle(VirtualTableScenario s) {
        var top = s.GetDSDestiny(6); var marker = s.GetDSDestiny(3);
        s.MoveCardsToTopOfDSReserveDeck(top, marker);
        assertEquals(top, s.GetTopOfDSReserveDeck());
        s.gameState().markCardsRevealedFromPile(List.of(marker));
        assertTrue(s.GetCurrentDecision().getText(), offered(s));
        int darkForce = s.GetDSForcePileCount(); int lightForce = s.GetLSForcePileCount();
        var odds = s.GetLSCard("odds");
        s.LSPlayCard(odds, CHANGE); s.PassAllResponses();
        assertEquals(darkForce + 1, s.GetDSForcePileCount()); assertTrue(s.GetDSForcePile().contains(top));
        assertEquals(lightForce, s.GetLSForcePileCount());
        assertFalse(s.gameState().isCardRevealedFromPile(marker));
        assertTrue(s.GetLSUsedPile().contains(odds));
    }

    @Test
    public void ActualOpposingInterruptDrawActivatesItsTopCardAndShufflesItsReserve() {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        var lightning = s.GetDSCard("lightning"); var target = s.GetLSCard("luke");
        s.MoveCardsToLocation(site, s.GetDSCard("emperor"), target);
        s.MoveCardsToHand(lightning, s.GetLSCard("odds")); s.SkipToDSTurn(Phase.CONTROL);
        s.DSPlayCard(lightning, "Make target lost");
        if (s.DSDecisionAvailable("Choose Emperor")) s.DSChooseCard(s.GetDSCard("emperor"));
        if (s.DSDecisionAvailable("Choose character")) s.DSChooseCard(target);
        passUntilOffer(s); checkActivationAndShuffle(s);
    }

    private VirtualTableScenario battle() {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        var vader = s.GetDSCard("vader");
        s.MoveCardsToLocation(site, vader, s.GetLSCard("luke"), s.GetLSCard("guard"));
        s.AttachCardsTo(vader, s.GetDSCard("saber"));
        s.MoveCardsToHand(s.GetLSCard("odds")); s.DSActivateForceCheat(6);
        s.SkipToDSTurn(Phase.BATTLE); s.DSInitiateBattle(site);
        return s;
    }

    @Test
    public void ActualWeaponDrawAlsoActivatesAndShufflesBeforeItsDestiny() {
        var s = battle(); var saber = s.GetDSCard("saber");
        assertTrue(s.DSCardActionAvailable(saber, "Fire")); s.DSUseCardAction(saber, "Fire");
        s.DSChooseCard(s.GetLSCard("guard")); passUntilOffer(s); checkActivationAndShuffle(s);
    }

    @Test
    public void OrdinaryBattleDestinyDoesNotOfferTheFirstMode() {
        var s = battle(); s.SkipToPowerSegment();
        assertTrue(s.DSDecisionAvailable("battle destiny")); s.DSChooseYes(); passUntilOffer(s);
        assertFalse(offered(s)); assertTrue(s.GetLSHand().contains(s.GetLSCard("odds")));
    }
}
