package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_072_Tests {
    private static final String TAKE = "Take a card into hand from Used Pile";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("chamber", "1250_72"); put("senate", "12_75"); put("platform", "1113_3");
            put("depa", "12_4"); put("luke", "1_19"); put("sense", "1_109"); put("patience", "11_28");
        }}, new HashMap<>() {{
            put("senate", "12_167"); put("wipe", "1109_50");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalChamberHasThreeLightIconsAndNoDarkIcons() {
        var s = scenario(); var bp = s.GetLSCard("chamber").getBlueprint();
        assertEquals(Title.Jedi_Council_Chamber, bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Title.Coruscant, bp.getSystemName()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.LOCATION)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.INTERIOR_SITE, Icon.PLANET,
                Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertEquals(3, bp.getIconCount(Icon.LIGHT_FORCE)); assertEquals(0, bp.getIconCount(Icon.DARK_FORCE));
    }

    @Test
    public void OwnSenatePreventsChamberDeployment() {
        var s = scenario(); s.StartGame(); s.MoveLocationToTable(s.GetLSCard("senate"));
        s.MoveCardsToHand(s.GetLSCard("chamber")); s.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(s.AwaitingLSDeployPhaseActions());
        assertFalse(s.LSDeployAvailable(s.GetLSCard("chamber")));
    }

    @Test
    public void OpposingSenateDoesNotPreventChamberDeployment() {
        var s = scenario(); s.StartGame(); s.MoveLocationToTable(s.GetDSCard("senate"));
        var chamber = s.GetLSCard("chamber"); s.MoveCardsToHand(chamber); s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployLocation(chamber); s.PassAllResponses();
        assertEquals(Zone.LOCATIONS, chamber.getZone());
    }

    @Test
    public void SenateLandingPlatformDoesNotCountAsTheSenate() {
        var s = scenario(); s.StartGame(); s.MoveLocationToTable(s.GetLSCard("platform"));
        var chamber = s.GetLSCard("chamber"); s.MoveCardsToHand(chamber); s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployLocation(chamber); s.PassAllResponses();
        assertEquals(Zone.LOCATIONS, chamber.getZone());
    }

    private boolean takeOffered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("chamber"), TAKE);
    }

    private VirtualTableScenario deployToChamber(String character, boolean seedUsed) {
        var s = scenario(); s.StartGame(); var chamber = s.GetLSCard("chamber");
        var deploying = s.GetLSCard(character); s.MoveLocationToTable(chamber); s.MoveCardsToHand(deploying);
        s.SkipToLSTurn(Phase.DEPLOY);
        if (s.GetLSForcePileCount() < 7) s.LSActivateForceCheat(7 - s.GetLSForcePileCount());
        if (seedUsed) s.MoveCardsToTopOfLSUsedPile(s.GetLSCard("patience"), s.GetLSCard("sense"));
        s.LSDeployCard(deploying);
        if (s.LSDecisionAvailable("Choose a location to deploy")) s.LSChooseCard(chamber);
        for (int i = 0; i < 12 && !takeOffered(s)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertEquals(chamber, deploying.getAttachedTo());
        return s;
    }

    @Test
    public void ActualCouncilMemberDeploymentTakesAnyOneUsedCardAndReshuffles() {
        var s = deployToChamber("depa", true); var chosen = s.GetLSCard("sense"); var left = s.GetLSCard("patience");
        s.gameState().markCardsRevealedFromPile(List.of(left));
        assertTrue(takeOffered(s)); s.LSUseCardAction(s.GetLSCard("chamber"), TAKE); s.PassAllResponses();
        assertTrue(s.LSHasCardChoicesAvailable(chosen)); assertTrue(s.LSHasCardChoicesAvailable(left));
        int used = s.GetLSUsedPileCount(); s.LSChooseCard(chosen); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(chosen)); assertTrue(s.GetLSUsedPile().contains(left));
        assertEquals(used - 1, s.GetLSUsedPileCount());
        assertFalse(s.gameState().isCardRevealedFromPile(left)); assertFalse(takeOffered(s));
    }

    @Test
    public void NonMemberDeploymentDoesNotOfferUsedPileAction() {
        var s = deployToChamber("luke", true);
        assertFalse(takeOffered(s)); assertTrue(s.GetLSUsedPile().contains(s.GetLSCard("sense")));
    }

    @Test
    public void OriginalVirtualWipeIsNotSuspendedByThisPrinting() {
        var s = scenario(); s.StartGame(); s.MoveLocationToTable(s.GetLSCard("chamber"));
        var wipe = s.GetDSCard("wipe"); s.AttachCardsTo(s.GetDSStartingLocation(), wipe);
        s.SkipToPhase(Phase.CONTROL);
        assertFalse(wipe.isSuspended());
    }
}
