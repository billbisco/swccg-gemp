package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_1250_053_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("palpatine", "1250_53"); put("valorum", "12_30"); put("senate", "12_75");
            put("clone1", "1116_8"); put("clone2", "1116_8"); put("rebel", "1_28");
            put("ambush", "5_31"); put("chewie", "8_2"); put("corridor", "5_82");
            put("system", "12_73"); put("ship", "1_140");
        }}, new HashMap<>() {{ put("trooper", "1_194"); }}, 25, 25,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SupremeChancellorPalpatineMetadataMatchesFinalPrinted53Image() {
        var scn = scenario(); var bp = scn.GetLSCard("palpatine").getBlueprint();
        assertEquals("Supreme Chancellor Palpatine", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(0, bp.getDestiny(), scn.epsilon); assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon); assertEquals(4, bp.getAbility(), scn.epsilon);
        assertEquals(5, bp.getPolitics(), scn.epsilon); assertEquals(6, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.REPUBLIC); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{
            add(Icon.REPUBLIC); add(Icon.CORUSCANT); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.LEADER); }});
        scn.BlueprintPersonaCheck(bp, new ArrayList<>() {{ add(Persona.SIDIOUS); }});
    }

    @Test
    public void SupremeChancellorPalpatineHasExactlyAmbitionAndOrderAgendas() {
        var scn = scenario(); scn.StartGame(); var palpatine = scn.GetLSCard("palpatine");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), palpatine);
        assertEquals(List.of(Agenda.AMBITION, Agenda.ORDER), scn.game().getModifiersQuerying().getAgendas(scn.gameState(), palpatine));
    }

    @Test
    public void SupremeChancellorPalpatineDeploymentMakesExistingValorumLost() {
        var scn = scenario(); scn.StartGame(); var palpatine = scn.GetLSCard("palpatine"); var valorum = scn.GetLSCard("valorum");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), valorum); scn.MoveCardsToHand(palpatine);
        scn.LSActivateForceCheat(10); scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(palpatine); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertEquals(force - 4, scn.GetLSForcePileCount());
        assertTrue(scn.GetLSLostPile().contains(valorum)); assertEquals(scn.GetLSStartingLocation(), palpatine.getAtLocation());
    }

    @Test
    public void SupremeChancellorPalpatineAlsoLosesValorumDeployedAfterHim() {
        var scn = scenario(); scn.StartGame(); var valorum = scn.GetLSCard("valorum");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("palpatine")); scn.MoveCardsToHand(valorum);
        scn.LSActivateForceCheat(12); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(valorum); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(valorum));
    }

    @Test
    public void SupremeChancellorPalpatineCanMakeTwoRegularMovesAwayFromCoruscantButNotThree() {
        var scn = scenario(); scn.StartGame(); var palpatine = scn.GetLSCard("palpatine");
        var from = scn.GetLSStartingLocation(); var to = scn.GetLSCard("corridor");
        scn.MoveLocationToTable(to); scn.MoveCardsToLocation(from, palpatine);
        scn.LSActivateForceCheat(6); scn.SkipToLSTurn(Phase.MOVE);
        int force = scn.GetLSForcePileCount(); scn.LSMoveCard(palpatine, to); scn.PassAllResponses(); scn.DSPass();
        assertEquals(to, palpatine.getAtLocation());
        assertEquals(0, scn.game().getModifiersQuerying().getGrantedAdditionalMovesUsedThisTurn(palpatine));
        assertTrue(scn.game().getModifiersQuerying().hasUnusedGrantedAdditionalMoves(scn.gameState(), palpatine));
        scn.LSMoveCard(palpatine, from); scn.PassAllResponses(); scn.DSPass();
        assertEquals(from, palpatine.getAtLocation()); assertEquals(force - 2, scn.GetLSForcePileCount());
        assertFalse(scn.LSCardActionAvailable(palpatine, "Move"));
    }

    @Test
    public void SupremeChancellorPalpatineMakesActualAmbushUsedWhileOnCoruscant() { checkAmbush(true); }
    @Test
    public void SupremeChancellorPalpatineLeavesActualAmbushLostAwayFromCoruscant() { checkAmbush(false); }

    private void checkAmbush(boolean onCoruscant) {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate");
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(onCoruscant ? senate : scn.GetDSStartingLocation(), scn.GetLSCard("palpatine"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("chewie"), scn.GetDSCard("trooper"));
        var ambush = scn.GetLSCard("ambush"); scn.MoveCardsToHand(ambush); scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(onCoruscant ? CardSubtype.USED : CardSubtype.LOST,
                scn.game().getModifiersQuerying().getInterruptType(scn.gameState(), ambush));
        scn.LSPlayCard(ambush); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("trooper")));
        assertEquals(onCoruscant, scn.GetLSUsedPile().contains(ambush));
        assertEquals(!onCoruscant, scn.GetLSLostPile().contains(ambush));
    }

    @Test
    public void SupremeChancellorPalpatineAboveCoruscantIsNotOnThePlanet() {
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("system"); var ship = scn.GetLSCard("ship");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship); scn.BoardAsPassenger(ship, scn.GetLSCard("palpatine"));
        assertEquals(CardSubtype.LOST, scn.game().getModifiersQuerying().getInterruptType(scn.gameState(), scn.GetLSCard("ambush")));
    }

    @Test
    public void SupremeChancellorPalpatineDrawsForCloneDrainOnAnotherPlanet() { checkDraw(true, true, true); }
    @Test
    public void SupremeChancellorPalpatineMayDeclineCloneDrainDraw() { checkDraw(true, true, false); }
    @Test
    public void SupremeChancellorPalpatineDoesNotDrawWithoutClone() { checkDraw(true, false, false); }
    @Test
    public void SupremeChancellorPalpatineDoesNotDrawWhenAwayFromCoruscant() { checkDraw(false, true, false); }

    private boolean drawOffered(VirtualTableScenario scn) {
        return scn.GetADParam(scn.LS, "cardId") != null && scn.GetADParam(scn.LS, "actionText") != null
                && scn.LSCardActionAvailable(scn.GetLSCard("palpatine"), "Draw top card");
    }

    @Test
    public void SupremeChancellorPalpatineAboveCoruscantCannotDrawForCloneDrain() { checkDraw(2, true, false); }

    private void checkDraw(boolean onCoruscant, boolean clone, boolean accept) { checkDraw(onCoruscant ? 0 : 1, clone, accept); }

    private void checkDraw(int mode, boolean clone, boolean accept) {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        if (mode == 2) {
            scn.MoveLocationToTable(scn.GetLSCard("system"));
            scn.MoveCardsToLocation(scn.GetLSCard("system"), scn.GetLSCard("ship"));
            scn.BoardAsPassenger(scn.GetLSCard("ship"), scn.GetLSCard("palpatine"));
        }
        else scn.MoveCardsToLocation(mode == 0 ? senate : scn.GetLSStartingLocation(), scn.GetLSCard("palpatine"));
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard(clone ? "clone1" : "rebel"));
        if (!clone) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("clone1"));
        scn.SkipToLSTurn(Phase.CONTROL); var top = scn.GetTopOfLSReserveDeck(); int hand = scn.GetLSHandCount();
        scn.LSForceDrainAt(site); scn.DSPass();
        assertEquals(mode == 0 && clone, drawOffered(scn));
        if (accept) scn.LSUseCardAction(scn.GetLSCard("palpatine"), "Draw top card");
        scn.PassAllResponses();
        assertEquals(hand + (accept ? 1 : 0), scn.GetLSHandCount()); assertEquals(accept, scn.GetLSHand().contains(top));
        scn.PassForceDrainStartResponses(); scn.DSChooseCard(scn.GetTopOfDSForcePile()); scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
    }
    @Test
    public void SupremeChancellorPalpatineMayDrawForEachOfTwoDifferentCloneDrains() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, scn.GetLSCard("palpatine"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("clone1"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("clone2"));
        scn.SkipToLSTurn(Phase.CONTROL); int hand = scn.GetLSHandCount();
        for (int i = 0; i < 2; i++) {
            var top = scn.GetTopOfLSReserveDeck();
            scn.LSForceDrainAt(i == 0 ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation()); scn.DSPass();
            assertTrue(drawOffered(scn)); scn.LSUseCardAction(scn.GetLSCard("palpatine"), "Draw top card"); scn.PassAllResponses();
            assertTrue(scn.GetLSHand().contains(top));
            scn.PassForceDrainStartResponses(); scn.DSChooseCard(scn.GetTopOfDSForcePile()); scn.PassAllResponses();
            if (i == 0) scn.DSPass();
        }
        assertEquals(hand + 2, scn.GetLSHandCount());
    }

    @Test
    public void SupremeChancellorPalpatineDoesNotOfferDrawWithEmptyReserve() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, scn.GetLSCard("palpatine"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("clone1"));
        scn.SkipToLSTurn(Phase.CONTROL);
        for (var card : new ArrayList<>(scn.GetLSReserveDeck())) scn.MoveCardsToTopOfLSForcePile((PhysicalCardImpl) card);
        assertEquals(0, scn.GetLSReserveDeckCount()); int hand = scn.GetLSHandCount();
        scn.LSForceDrainAt(scn.GetDSStartingLocation()); scn.DSPass();
        assertFalse(drawOffered(scn)); scn.PassAllResponses();
        assertEquals(hand, scn.GetLSHandCount());
        scn.PassForceDrainStartResponses(); scn.DSChooseCard(scn.GetTopOfDSForcePile()); scn.PassAllResponses();
    }

}
