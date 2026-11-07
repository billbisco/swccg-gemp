package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1250_070_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("prison", "1250_70"); put("trooper", "1_28"); put("leia", "1_17");
            put("palpatine", "12_28"); put("luke", "1_16"); put("revolution", "1_62");
        }}, new HashMap<>() {{
            put("cantina", "1_290"); put("otherPrison", "1_284"); put("vader", "1_168");
        }}, 20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private PhysicalCardImpl prisonOnTable(VirtualTableScenario scn) {
        var prison = scn.GetLSCard("prison");
        scn.MoveLocationToTable(prison);
        return prison;
    }

    @Test
    public void BlockadeFlagshipPrisonMetadataMatchesPrinted70Image() {
        var scn = scenario(); var bp = scn.GetLSCard("prison").getBlueprint();
        assertEquals("Blockade Flagship: Prison", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(Persona.BLOCKADE_FLAGSHIP, bp.getRelatedStarshipOrVehiclePersona());
        assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.LOCATION); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE); add(Icon.STARSHIP_SITE); add(Icon.MOBILE); add(Icon.SCOMP_LINK);
            add(Icon.ENDOR); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5); add(Icon.LIGHT_FORCE); add(Icon.DARK_FORCE);
        }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.PRISON); }});
        assertEquals(1, bp.getIconCount(Icon.STARSHIP_SITE));
        assertEquals(1, bp.getIconCount(Icon.LIGHT_FORCE)); assertEquals(1, bp.getIconCount(Icon.DARK_FORCE));
    }

    @Test
    public void BlockadeFlagshipPrisonReleasesImprisonedCaptiveWhileOccupied() { checkRelease(false); }
    @Test
    public void BlockadeFlagshipPrisonNeedsOccupancyRatherThanControl() { checkRelease(true); }

    private void checkRelease(boolean contested) {
        var scn = scenario(); scn.StartGame(); var prison = prisonOnTable(scn);
        var captive = scn.GetLSCard("leia");
        scn.MoveCardsToLocation(prison, scn.GetLSCard("trooper"));
        if (contested) scn.MoveCardsToLocation(prison, scn.GetDSCard("vader"));
        scn.gameState().imprisonCharacter(scn.game(), captive, prison);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(captive.isImprisoned());
        assertTrue(scn.LSCardActionAvailable(prison, "Release an imprisoned captive"));
        scn.LSUseCardAction(prison, "Release an imprisoned captive");
        if (scn.LSDecisionAvailable("Choose captive to release")) scn.LSChooseCard(captive);
        scn.PassAllResponses();
        assertFalse(captive.isCaptive()); assertEquals(prison, captive.getAtLocation());
    }

    @Test
    public void BlockadeFlagshipPrisonCannotReleaseWithoutFriendlyOccupant() {
        var scn = scenario(); scn.StartGame(); var prison = prisonOnTable(scn);
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("leia"), prison);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(prison, "Release an imprisoned captive"));
        assertTrue(scn.GetLSCard("leia").isImprisoned());
    }

    @Test
    public void BlockadeFlagshipPrisonCannotReleaseDuringMovePhase() {
        var scn = scenario(); scn.StartGame(); var prison = prisonOnTable(scn);
        scn.MoveCardsToLocation(prison, scn.GetLSCard("trooper"));
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("leia"), prison);
        scn.SkipToLSTurn(Phase.MOVE);
        assertFalse(scn.LSCardActionAvailable(prison, "Release an imprisoned captive"));
    }

    @Test
    public void BlockadeFlagshipPrisonCannotReleaseCaptiveAtAnotherPrison() {
        var scn = scenario(); scn.StartGame(); var prison = prisonOnTable(scn);
        var other = scn.GetDSCard("otherPrison"); scn.MoveLocationToTable(other);
        scn.MoveCardsToLocation(prison, scn.GetLSCard("trooper"));
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("leia"), other);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(prison, "Release an imprisoned captive"));
    }

    @Test
    public void BlockadeFlagshipPrisonImprisonedPalpatineReducesActualLightDrainElsewhere() { checkDrain(true, true, false, 1); }
    @Test
    public void BlockadeFlagshipPrisonUnimprisonedPalpatineDoesNotReduceDrains() { checkDrain(false, true, false, 2); }
    @Test
    public void BlockadeFlagshipPrisonPalpatineImprisonedElsewhereDoesNotReduceDrains() { checkDrain(true, false, false, 2); }
    @Test
    public void BlockadeFlagshipPrisonDoesNotReduceDarkForceDrains() { checkDrain(true, true, true, 2); }

    private void checkDrain(boolean imprisoned, boolean here, boolean dark, int expected) {
        var scn = scenario(); scn.StartGame(); var prison = prisonOnTable(scn);
        var cantina = scn.GetDSCard("cantina"); scn.MoveLocationToTable(cantina);
        if (imprisoned) {
            var where = prison;
            if (!here) { where = scn.GetDSCard("otherPrison"); scn.MoveLocationToTable(where); }
            scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("palpatine"), where);
        }
        else scn.MoveCardsToLocation(prison, scn.GetLSCard("palpatine"));
        scn.MoveCardsToLocation(cantina, dark ? scn.GetDSCard("vader") : scn.GetLSCard("trooper"));
        if (dark) {
            scn.SkipToDSTurn(Phase.CONTROL); scn.DSForceDrainAt(cantina); scn.LSPass();
        }
        else {
            scn.SkipToLSTurn(Phase.CONTROL); scn.LSForceDrainAt(cantina); scn.DSPass();
        }
        scn.PassForceDrainStartResponses();
        assertEquals(expected, scn.GetForceDrainTotal());
    }

    @Test
    public void BlockadeFlagshipPrisonReleasingPalpatineRestoresActualDrainAmount() {
        var scn = scenario(); scn.StartGame(); var prison = prisonOnTable(scn);
        var palpatine = scn.GetLSCard("palpatine"); var cantina = scn.GetDSCard("cantina");
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(prison, scn.GetLSCard("trooper"));
        scn.MoveCardsToLocation(cantina, scn.GetLSCard("leia"));
        scn.gameState().imprisonCharacter(scn.game(), palpatine, prison);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), cantina, scn.LS), scn.epsilon);
        scn.LSUseCardAction(prison, "Release an imprisoned captive");
        if (scn.LSDecisionAvailable("Choose captive to release")) scn.LSChooseCard(palpatine);
        scn.PassAllResponses(); assertFalse(palpatine.isImprisoned());
        assertEquals(2, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), cantina, scn.LS), scn.epsilon);
        scn.DSPass(); scn.LSForceDrainAt(cantina); scn.DSPass(); scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void BlockadeFlagshipPrisonCannotUseReleaseDuringDarkControl() {
        var scn = scenario(); scn.StartGame(); var prison = prisonOnTable(scn);
        scn.MoveCardsToLocation(prison, scn.GetLSCard("trooper"));
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("leia"), prison);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(prison, "Release an imprisoned captive"));
        assertTrue(scn.GetLSCard("leia").isImprisoned());
    }

    @Test
    public void BlockadeFlagshipPrisonIsNotAValidRevolutionTarget() {
        var scn = scenario(); scn.StartGame(); var prison = prisonOnTable(scn);
        var revolution = scn.GetLSCard("revolution"); scn.MoveCardsToHand(revolution);
        scn.LSActivateForceCheat(6); scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(revolution)); scn.LSDeployCard(revolution);
        assertFalse(scn.LSHasCardChoicesAvailable(prison));
        assertTrue(scn.LSHasCardChoicesAvailable(scn.GetDSStartingLocation()));
        scn.LSChooseCard(scn.GetDSStartingLocation()); scn.PassAllResponses();
        assertEquals(scn.GetDSStartingLocation(), revolution.getAttachedTo());
    }
}
