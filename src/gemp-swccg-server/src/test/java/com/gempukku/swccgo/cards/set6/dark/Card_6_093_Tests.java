package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Amanin: "If Amanin is alone and causes a non-selective creature to be lost, creature is placed out of play and you
 * may retrieve Force equal to its deploy cost." Covers the "Amanin used a weapon on it" path, which relies on the
 * engine's weapon-hit record (ModifiersLogic.wasHitOrMadeLostByWeapon / Filters.hitBy).
 */
public class Card_6_093_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("worrt", "6_48")),
                new HashMap<>(Map.of("amanin", "6_93", "stick", "1_315", "otherStick", "1_315")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("amanin"), scn.GetLSCard("worrt"));
        scn.AttachCardsTo(scn.GetDSCard("amanin"), scn.GetDSCard("stick"));
        scn.SkipToDSTurn(Phase.CONTROL);
        return scn;
    }

    private final StringBuilder trace = new StringBuilder();

    private void loseWorrt(VirtualTableScenario scn) {
        var amanin = scn.GetDSCard("amanin");
        trace.append("alone=").append(Filters.alone.accepts(scn.game(), amanin)).append(' ');
        scn.DSExecuteAdHocEffect(amanin, new LoseCardFromTableEffect(
                new TopLevelGameTextAction(amanin, scn.DS, amanin.getCardId()), scn.GetLSCard("worrt")));
        boolean answered = false;
        for (int i = 0; i < 40; i++) {
            if (!answered && scn.DSDecisionAvailable("Force?")) { scn.DSChooseYes(); answered = true; continue; }
            if (scn.DSDecisionAvailable("place out of play")) { scn.DSChooseCard(scn.GetLSCard("worrt")); continue; }
            var decision = scn.GetCurrentDecision();
            trace.append(decision == null ? "null" : decision.getText() + scn.GetDSAvailableActions() + scn.GetLSAvailableActions()).append(" | ");
            if (decision == null || !decision.getText().toLowerCase().contains("response")) break;
            scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
        }
    }

    private boolean inLostPile(PhysicalCard card) {
        return card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE;
    }

    @Test public void creatureHitByAmaninsWeaponIsPlacedOutOfPlay() {
        var scn = scenario();
        var worrt = scn.GetLSCard("worrt");
        scn.game().getModifiersQuerying().hitOrMadeLostByWeapon(worrt, scn.GetDSCard("stick"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1), scn.GetDSFiller(2), scn.GetDSFiller(3), scn.GetDSFiller(4), scn.GetDSFiller(5));
        int lost = scn.GetDSLostPile().size();
        int deployCost = (int) scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), worrt);
        assertEquals(2, deployCost);
        loseWorrt(scn);
        assertEquals(trace.toString(), Zone.OUT_OF_PLAY, worrt.getZone());
        assertEquals("retrieves Worrt's deploy cost", lost - deployCost, scn.GetDSLostPile().size());
    }

    @Test public void creatureHitOnlyBySomeoneElsesWeaponIsNotPlacedOutOfPlay() {
        var scn = scenario();
        var worrt = scn.GetLSCard("worrt");
        scn.game().getModifiersQuerying().hitOrMadeLostByWeapon(worrt, scn.GetDSCard("otherStick"));
        loseWorrt(scn);
        assertTrue(trace.toString(), inLostPile(worrt));
        assertNull(trace.toString(), scn.DSGetDecision() != null && scn.DSDecisionAvailable("place out of play") ? "offered" : null);
    }

    @Test public void creatureNotHitByAnyWeaponIsNotPlacedOutOfPlay() {
        var scn = scenario();
        var worrt = scn.GetLSCard("worrt");
        loseWorrt(scn);
        assertTrue(trace.toString(), inLostPile(worrt));
        assertNull(trace.toString(), scn.DSGetDecision() != null && scn.DSDecisionAvailable("place out of play") ? "offered" : null);
    }

    @Test public void hitRecordMatchesTheWeaponAndItsUserOnly() {
        var scn = scenario();
        var worrt = scn.GetLSCard("worrt");
        var query = scn.game().getModifiersQuerying();
        assertFalse(query.wasHitOrMadeLostByWeapon(worrt, Filters.any));
        query.hitOrMadeLostByWeapon(worrt, scn.GetDSCard("stick"));
        assertTrue(query.wasHitOrMadeLostByWeapon(worrt, Filters.sameCardId(scn.GetDSCard("stick"))));
        assertTrue("the weapon's user also counts", query.wasHitOrMadeLostByWeapon(worrt, Filters.sameCardId(scn.GetDSCard("amanin"))));
        assertFalse(query.wasHitOrMadeLostByWeapon(worrt, Filters.sameCardId(scn.GetDSCard("otherStick"))));
        assertTrue(Filters.hitBy(Filters.sameCardId(scn.GetDSCard("amanin"))).accepts(scn.game(), worrt));
        assertFalse(Filters.hitBy(Filters.sameCardId(scn.GetDSCard("otherStick"))).accepts(scn.game(), worrt));
    }
}
