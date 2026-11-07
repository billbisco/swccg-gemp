package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedInUsedPileByAnyPlayerModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedInUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersLogic;
import com.gempukku.swccgo.logic.timing.Action;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsedPilePlacementProhibitionTest {
    private final SwccgGame game = mock(SwccgGame.class);
    private final GameState state = mock(GameState.class);
    private final PhysicalCard source = mock(PhysicalCard.class);
    private final PhysicalCard card = mock(PhysicalCard.class);
    private final ModifiersQuerying querying = spy(new ModifiersLogic(game));

    UsedPilePlacementProhibitionTest() {
        when(game.getGameState()).thenReturn(state);
        when(game.getModifiersQuerying()).thenReturn(querying);
        when(source.getPermanentCardId()).thenReturn(1);
        when(source.getOwner()).thenReturn("owner");
        when(state.findCardByPermanentId(1)).thenReturn(source);
        when(card.getZone()).thenReturn(Zone.AT_LOCATION);
    }

    private void affectWith(Modifier... modifiers) {
        doReturn(List.of(modifiers)).when(querying).getModifiersAffectingCard(
                state, ModifierType.MAY_NOT_BE_PLACED_IN_USED_PILE, card);
    }

    private SubAction placement(String player) {
        Action parent = mock(Action.class);
        return new PlaceCardsInCardPileFromTableEffect(parent, player, List.of(card),
                Zone.USED_PILE, false, null).getSubAction(game);
    }

    @Test
    void existingModifierStillBlocksOnlyOpponent() {
        affectWith(new MayNotBePlacedInUsedPileModifier(source, Filters.any));
        assertFalse(querying.mayNotBePlacedInUsedPile(state, card, "owner"));
        assertTrue(querying.mayNotBePlacedInUsedPile(state, card, "opponent"));
        assertFalse(querying.mayNotBePlacedInUsedPile(state, card, null));
        assertNotNull(placement("owner").nextEffect(game));
        assertNull(placement("opponent").nextEffect(game));
        assertNotNull(placement(null).nextEffect(game));
    }

    @Test
    void allPlayerModifierStopsPlacementSchedulingForOwnerOpponentAndUnattributedAction() {
        affectWith(new MayNotBePlacedInUsedPileByAnyPlayerModifier(source, Filters.any));
        for (String player : new String[]{"owner", "opponent", null}) {
            assertTrue(querying.mayNotBePlacedInUsedPile(state, card, player));
            assertNull(placement(player).nextEffect(game), "No placement may be scheduled for " + player);
        }
    }

    @Test
    void unaffectedCardStillReachesPlacement() {
        affectWith();
        for (String player : new String[]{"owner", "opponent", null}) {
            assertFalse(querying.mayNotBePlacedInUsedPile(state, card, player));
            assertNotNull(placement(player).nextEffect(game));
        }
    }
}
