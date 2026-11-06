package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeparturePilotSnapshotTests {
    private final SwccgGame game = mock(SwccgGame.class, RETURNS_DEEP_STUBS);
    private final Action action = mock(Action.class);
    private final PhysicalCard host = mock(PhysicalCard.class);

    private PhysicalCardImpl depart(boolean asPilot) {
        when(host.getCardsAttached()).thenReturn(new ArrayList<>());
        PhysicalCardImpl card = new PhysicalCardImpl();
        card.setZone(Zone.ATTACHED);
        card.attachTo(host, asPilot, !asPilot, false, false, false);
        when(game.getGameState().getAttachedCards(card)).thenReturn(List.of());
        card.updateRememberedInPlayCardInfo(game);
        card.attachTo(null, false, false, false, false, false);
        card.setZone(Zone.LOST_PILE);
        assertFalse(card.isPilotOf());
        return card;
    }

    private List<LostFromTableResult> results(PhysicalCard card) {
        return List.of(
                new LostCardFromTableResult(action, card, host, null, List.of()),
                new ForfeitedCardToLostPileFromTableResult(action, card, host, null, List.of()),
                new CancelCardOnTableResult(action, card, host));
    }

    @Test
    void LossForfeitAndCancelRetainFormerPilotAfterDetachment() {
        PhysicalCardImpl card = depart(true);
        List<LostFromTableResult> results = results(card);
        assertTrue(card.wasPreviouslyInPilotCapacitySlot());
        for (LostFromTableResult result : results) {
            assertTrue(result.wasInPilotCapacitySlot());
            assertSame(host, result.getFromAttachedTo());
        }
        card.updateRememberedInPlayCardInfo(game);
        assertFalse(card.wasPreviouslyInPilotCapacitySlot());
        for (LostFromTableResult result : results)
            assertTrue(result.wasInPilotCapacitySlot());
    }

    @Test
    void LossForfeitAndCancelDoNotTreatPassengersAsPilots() {
        for (LostFromTableResult result : results(depart(false)))
            assertFalse(result.wasInPilotCapacitySlot());
    }

    @Test
    void DepartureWithoutHostCannotReuseStalePilotStatus() {
        PhysicalCardImpl card = depart(true);
        assertFalse(new LostCardFromTableResult(action, card, null, null, List.of()).wasInPilotCapacitySlot());
        assertFalse(new ForfeitedCardToLostPileFromTableResult(action, card, null, null, List.of()).wasInPilotCapacitySlot());
        assertFalse(new CancelCardOnTableResult(action, card, null).wasInPilotCapacitySlot());
    }
}
