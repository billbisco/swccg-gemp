package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_2_089_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("future", "1220_138", "han", "1_11", "stackOne", "2_18", "stackTwo", "2_23", "beggar", "1_44")),
                new HashMap<>(Map.of("greedo", "2_89")),
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
    }

    /**
     * Greedo's "may use all Force Pile cards to cancel threat" is limited to the Force Pile (Bill 2026-11-07):
     * It Is The Future You See (V)'s "instead of using X Force, may turn X cards here face down" does not apply.
     */
    private void threatenAndCancel(boolean withEpicEvent) { threatenAndCancel(withEpicEvent, false); }

    private void threatenAndCancel(boolean withEpicEvent, boolean withBeggar) {
        var scn = scenario();
        var future = scn.GetLSCard("future"); var han = scn.GetLSCard("han"); var greedo = scn.GetDSCard("greedo");
        scn.StartGame();
        if (scn.LSDecisionAvailable("Choose starting interrupt")) scn.LSPass();
        if (scn.DSDecisionAvailable("Choose starting interrupt")) scn.DSPass();
        var site = scn.GetDSStartingLocation();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(site, han, greedo);
        if (withBeggar) scn.AttachCardsTo(site, scn.GetLSCard("beggar")); // exterior Tatooine site
        if (withEpicEvent) {
            scn.MoveCardsToLSSideOfTable(future); // Jedi mode: deployed on its owner's side
            scn.gameState().relocateCardAsStacked(scn.GetLSCard("stackOne"), future, false, false);
            scn.gameState().relocateCardAsStacked(scn.GetLSCard("stackTwo"), future, false, false);
        }
        scn.SkipToPhase(Phase.CONTROL);
        int force = scn.GetLSForcePileCount();
        assertTrue(force > 0);
        int dsForce = scn.GetDSForcePileCount();
        if (withBeggar) assertTrue("Beggar has opponent's Force to offer", dsForce > 0);
        if (withEpicEvent) {
            assertEquals(2, scn.game().getModifiersQuerying().getForceUseSubstitutesAvailable(scn.gameState(), scn.LS));
        }
        if (!scn.DSAnyDecisionsAvailable()) scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(greedo, "Threaten"));
        scn.DSUseCardAction(greedo, "Threaten");
        if (scn.DSHasCardChoiceAvailable(han)) scn.DSChooseCard(han);
        for (int i = 0; i < 6 && !scn.LSDecisionAvailable("cancel threat"); i++) scn.PlayerPass(scn.GetDecidingPlayer());
        assertTrue(scn.LSDecisionAvailable("cancel threat"));
        scn.LSChooseYes();
        for (int i = 0; i < 10 && scn.GetLSForcePileCount() > 0; i++) {
            assertFalse("no face-down offer", scn.LSDecisionAvailable("turn face down instead of using Force"));
            assertFalse("no opponent's Force", scn.LSDecisionAvailable("opponent's Force to use"));
            if (scn.LSDecisionAvailable("Force - Optional responses")) {
                assertFalse(scn.LSCardActionAvailable(future, "Turn cards face down instead of using Force"));
                assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("beggar"), "opponent's Force"));
            }
            if (scn.GetCurrentDecision() == null) break;
            if (scn.DSDecisionAvailable("activate")) { scn.DSChooseNo(); continue; }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertEquals("all Force Pile cards used", 0, scn.GetLSForcePileCount());
        assertEquals(force, scn.GetLSUsedPileCount());
        assertEquals("none of opponent's Force used", dsForce, scn.GetDSForcePileCount());
        assertEquals(0, scn.GetDSUsedPileCount());
        if (withEpicEvent) {
            for (var card : scn.gameState().getStackedCards(future)) assertEquals(Zone.STACKED, card.getZone());
        }
        assertTrue(scn.CardsAtLocation(site, han));
    }

    @Test public void threatCancelUsesAllForcePileCardsWithoutEpicEvent() { threatenAndCancel(false); }

    @Test public void threatCancelIgnoresFaceDownSubstitutionOfItIsTheFutureYouSee() { threatenAndCancel(true); }

    @Test public void threatCancelIgnoresBeggarsOpponentsForce() { threatenAndCancel(false, true); }

    @Test public void threatCancelIgnoresBeggarAndFaceDownSubstitutionTogether() { threatenAndCancel(true, true); }
}
