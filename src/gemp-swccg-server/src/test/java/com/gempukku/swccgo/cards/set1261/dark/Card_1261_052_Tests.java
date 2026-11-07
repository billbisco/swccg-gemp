package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_052_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("colo", "13_11"); put("obiwan", "1_21"); }},
                new HashMap<>() {{ put("plains", "1261_52"); put("mountains", "104_4"); put("walker", "3_157");
                    put("maintenanceWalker", "13_56"); put("scout", "3_156"); put("officer", "1_166"); put("trooper", "1_194");
                    put("storm", "3_104"); put("hoth", "3_143"); put("occupation", "7_237"); put("colo", "13_60"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void LegacyIcePlainsPrintedMetadataAndSeparateFifthMarkerMatchSource() {
        var scn = scenario(); var b = scn.GetDSCard("plains").getBlueprint();
        assertEquals(Title.Ice_Plains, b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(CardSubtype.SITE, b.getCardSubtype()); assertEquals(Title.Hoth, b.getSystemName());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.LOCATION); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.MARKER_5); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.DARK_FORCE); add(Icon.EXTERIOR_SITE); add(Icon.PLANET); add(Icon.HOTH); add(Icon.LEGACY_BLOCK_6); }});
        assertEquals(2, b.getIconCount(Icon.DARK_FORCE)); assertEquals(0, b.getIconCount(Icon.LIGHT_FORCE));
    }

    private VirtualTableScenario sites() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("plains"));
        scn.MoveLocationToTable(scn.GetDSCard("mountains")); return scn;
    }

    @Test public void LegacyIcePlainsActuallyTakesOnlyNonMaintenanceAtAtAndReshufflesOncePerGame() {
        var scn = sites(); var plains = scn.GetDSCard("plains"); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("walker"), scn.GetDSCard("maintenanceWalker"), scn.GetDSCard("scout"));
        var remaining = scn.GetDSReserveDeck().stream().filter(c -> c != scn.GetDSCard("walker")).findFirst().orElseThrow();
        scn.gameState().markCardsRevealedFromPile(java.util.Collections.singletonList(remaining));
        int force = scn.GetDSForcePileCount(); scn.DSUseCardAction(plains, "Take non-maintenance AT-AT into hand"); scn.PassAllResponses();
        assertTrue(selectable(scn, scn.GetDSCard("walker"))); assertFalse(selectable(scn, scn.GetDSCard("maintenanceWalker")));
        assertFalse(selectable(scn, scn.GetDSCard("scout"))); scn.DSChooseCard(scn.GetDSCard("walker")); scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetDSCard("walker").getZone()); assertEquals(force, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSReserveDeck().contains(remaining)); assertFalse(scn.gameState().isCardRevealedFromPile(remaining));
        if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(plains, "Take non-maintenance AT-AT into hand"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("walker"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(plains, "Take non-maintenance AT-AT into hand"));
    }

    private boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.DS, "blueprintId"); var ids = scn.GetADParam(scn.DS, "cardId");
        var flags = scn.GetADParam(scn.DS, "selectable"); int size = blueprints != null ? blueprints.length : ids.length;
        for (int i = 0; i < size; i++) {
            boolean same = blueprints != null ? blueprints[i].equals(card.getBlueprintId(true)) : ids[i].equals(String.valueOf(card.getCardId()));
            if (same) return flags == null || Boolean.parseBoolean(flags[i]);
        }
        return false;
    }

    private void move(boolean reverse) {
        var scn = sites(); var plains = scn.GetDSCard("plains"); var mountains = scn.GetDSCard("mountains");
        var from = reverse ? mountains : plains; var to = reverse ? plains : mountains; var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(from, trooper); scn.SkipToDSTurn(Phase.DEPLOY); scn.DrainDSForcePileTo(0); scn.SkipToPhase(Phase.MOVE);
        assertEquals(0, scn.GetDSForcePileCount());
        scn.DSUseCardAction(plains, reverse ? "Move from Mountains to here" : "Move from here to Mountains");
        if (scn.DSHasCardChoiceAvailable(from)) scn.DSChooseCard(from);
        if (scn.DSHasCardChoiceAvailable(to)) scn.DSChooseCard(to);
        if (scn.DSHasCardChoiceAvailable(trooper)) scn.DSChooseCard(trooper);
        scn.PassAllResponses(); assertEquals(to, trooper.getAtLocation()); assertEquals(0, scn.GetDSForcePileCount());
    }

    @Test public void LegacyIcePlainsActuallyMovesToMountainsForFree() { move(false); }
    @Test public void LegacyIcePlainsActuallyMovesFromMountainsForFree() { move(true); }

    @Test public void LegacyIcePlainsMovementRequiresOwnMovePhaseAndCorrectOrigin() {
        var scn = sites(); var plains = scn.GetDSCard("plains");
        scn.MoveCardsToLocation(plains, scn.GetDSCard("trooper")); scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(plains, "Move from here to Mountains"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper")); scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(plains, "Move from here to Mountains"));
        assertFalse(scn.DSCardActionAvailable(plains, "Move from Mountains to here"));
    }

    @Test public void LegacyIcePlainsAddsOneLightIconAtBothSitesOnlyWhileImperialControlFaceIsUp() {
        var scn = sites(); var plains = scn.GetDSCard("plains"); var mountains = scn.GetDSCard("mountains");
        assertEquals(0, scn.GetIconCount(plains, Icon.LIGHT_FORCE)); assertEquals(0, scn.GetIconCount(mountains, Icon.LIGHT_FORCE));
        var objective = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, "7_298");
        scn.gameState().addCardToZone(objective, Zone.HAND, scn.DS); scn.MoveCardsToDSSideOfTable(objective);
        // Isolate the actual registered front/back title reference, without executing the Objective's own flip actions.
        objective.setObjectiveDeploymentComplete(true);
        assertEquals(0, scn.GetIconCount(plains, Icon.LIGHT_FORCE));
        scn.gameState().flipCard(scn.game(), objective, true);
        assertEquals(1, scn.GetIconCount(plains, Icon.LIGHT_FORCE)); assertEquals(1, scn.GetIconCount(mountains, Icon.LIGHT_FORCE));
        assertEquals(2, scn.GetIconCount(plains, Icon.DARK_FORCE)); assertEquals(1, scn.GetIconCount(mountains, Icon.DARK_FORCE));
        assertEquals(1, scn.GetIconCount(scn.GetDSStartingLocation(), Icon.LIGHT_FORCE));
        scn.gameState().flipCard(scn.game(), objective, false);
        assertEquals(0, scn.GetIconCount(plains, Icon.LIGHT_FORCE)); assertEquals(0, scn.GetIconCount(mountains, Icon.LIGHT_FORCE));
    }

    @Test public void LegacyIcePlainsCancelsActualDarkColoDeploymentButNotLightPlayersColo() {
        var scn = sites(); scn.MoveCardsToDSHand(scn.GetDSCard("colo")); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("colo")); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("colo"));
        var own = sites(); own.MoveCardsToLSHand(own.GetLSCard("colo")); own.SkipToLSTurn(Phase.DEPLOY);
        own.LSDeployCard(own.GetLSCard("colo")); own.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, own.GetLSCard("colo").getZone());
    }

    @Test public void LegacyIcePlainsCancelsActualIceStormDeployment() {
        var scn = sites(); scn.MoveCardsToDSHand(scn.GetDSCard("storm")); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("storm"));
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("mountains"))) scn.DSChooseCard(scn.GetDSCard("mountains"));
        scn.PassAllResponses(); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("storm"));
    }

    private void existingEffect(String name) {
        var scn = sites(); var effect = scn.GetDSCard(name);
        if (name.equals("occupation")) scn.MoveLocationToTable(scn.GetDSCard("hoth"));
        scn.MoveCardsToDSHand(scn.GetDSCard("officer")); scn.SkipToDSTurn(Phase.DEPLOY);
        // Stage an existing effect after entering the phase, then cause a real table-change event.
        if (name.equals("storm")) scn.AttachCardsTo(scn.GetDSCard("mountains"), effect);
        else if (name.equals("occupation")) scn.AttachCardsTo(scn.GetDSCard("hoth"), effect);
        else scn.MoveCardsToDSSideOfTable(effect);
        scn.DSDeployCard(scn.GetDSCard("officer"));
        if (scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation())) scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(effect)) scn.DSChooseCard(effect);
        if (scn.LSHasCardChoiceAvailable(effect)) scn.LSChooseCard(effect);
        scn.PassAllResponses(); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, effect);
    }

    @Test public void LegacyIcePlainsCancelsExistingRebelBaseOccupation() { existingEffect("occupation"); }
    @Test public void LegacyIcePlainsCancelsExistingIceStorm() { existingEffect("storm"); }
    @Test public void LegacyIcePlainsCancelsExistingOpponentsColo() { existingEffect("colo"); }
}
