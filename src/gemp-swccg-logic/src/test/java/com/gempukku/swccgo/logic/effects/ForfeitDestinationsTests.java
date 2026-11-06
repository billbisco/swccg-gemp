package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToLostPileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToUsedPileFromTableResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ForfeitDestinationsTests {
    private PhysicalCard card(String title) {
        PhysicalCard card = mock(PhysicalCard.class, RETURNS_DEEP_STUBS);
        when(card.getTitle()).thenReturn(title);
        when(card.getBlueprint().getTitle()).thenReturn(title);
        when(card.getBlueprint().getCardCategory()).thenReturn(CardCategory.CHARACTER);
        when(card.getZone()).thenReturn(Zone.AT_LOCATION);
        when(card.getOwner()).thenReturn("light");
        return card;
    }

    @Test
    void PerCardForfeitReplacementChangesOnlyItsDestinationAndTrigger() {
        PhysicalCard first = card("First");
        PhysicalCard second = card("Second");
        Action action = mock(Action.class);
        when(action.getPerformingPlayer()).thenReturn("light");
        ForfeitCardsFromTableSimultaneouslyEffect effect =
                new ForfeitCardsFromTableSimultaneouslyEffect(action, List.of(first, second));
        AboutToForfeitCardFromTableResult firstResult = new AboutToForfeitCardFromTableResult(action, first, effect);
        AboutToForfeitCardFromTableResult secondResult = new AboutToForfeitCardFromTableResult(action, second, effect);
        SwccgGame game = mock(SwccgGame.class, RETURNS_DEEP_STUBS);

        effect.setForfeitToUsedPile(first);

        assertFalse(firstResult.isToBeForfeitedToLostPile());
        assertTrue(secondResult.isToBeForfeitedToLostPile());
        assertFalse(TriggerConditions.isAboutToBeForfeitedToLostPile(game, firstResult, Filters.any));
        assertTrue(TriggerConditions.isAboutToBeForfeitedToLostPile(game, secondResult, Filters.any));
        effect.setForfeitToUsedPile();
        assertFalse(secondResult.isToBeForfeitedToLostPile());
    }

    @Test
    void SimultaneousForfeitsKeepSeparatePilesResultsAndFullForfeitCredit() {
        PhysicalCard first = card("First");
        PhysicalCard second = card("Second");
        SwccgGame game = mock(SwccgGame.class, RETURNS_DEEP_STUBS);
        GameState state = game.getGameState();
        BattleState battle = state.getBattleState();
        when(game.getDarkPlayer()).thenReturn("dark");
        when(game.getLightPlayer()).thenReturn("light");
        when(state.getCurrentPlayerId()).thenReturn("light");
        when(battle.getBattleDamageRemaining(game, "light")).thenReturn(8f);
        when(battle.getAttritionRemaining(game, "light")).thenReturn(8f);
        when(game.getModifiersQuerying().getForfeitWhenForfeiting(state, first)).thenReturn(3f);
        when(game.getModifiersQuerying().getForfeitWhenForfeiting(state, second)).thenReturn(5f);
        Action action = mock(Action.class);
        when(action.getPerformingPlayer()).thenReturn("light");
        ForfeitCardsFromTableSimultaneouslyEffect effect =
                new ForfeitCardsFromTableSimultaneouslyEffect(action, List.of(first, second));
        List<EffectResult> results = new ArrayList<>();
        ActionsEnvironment actions = game.getActionsEnvironment();
        doAnswer(call -> {
            EffectResult result = call.getArgument(0);
            results.add(result);
            if (result instanceof AboutToForfeitCardFromTableResult about && about.getCardToBeForfeited() == first)
                about.getForfeitCardEffect().setForfeitToUsedPile(first);
            return null;
        }).when(actions).emitEffectResult(any(EffectResult.class));
        doAnswer(call -> {
            run((SubAction) call.getArgument(0), game);
            return null;
        }).when(actions).addActionToStack(any(Action.class));

        effect.playEffect(game);

        verify(state).addCardToTopOfZone(first, Zone.USED_PILE, "light");
        verify(state).addCardToTopOfZone(second, Zone.LOST_PILE, "light");
        verify(state, never()).addCardToTopOfZone(second, Zone.USED_PILE, "light");
        verify(battle).increaseBattleDamageSatisfied("light", 3f);
        verify(battle).increaseBattleDamageSatisfied("light", 5f);
        verify(battle).increaseAttritionSatisfied("light", 3f);
        verify(battle).increaseAttritionSatisfied("light", 5f);
        assertTrue(results.stream().anyMatch(r -> r instanceof ForfeitedCardToUsedPileFromTableResult used && used.getCard() == first));
        assertTrue(results.stream().anyMatch(r -> r instanceof ForfeitedCardToLostPileFromTableResult lost && lost.getCard() == second));
        assertFalse(results.stream().anyMatch(r -> r instanceof ForfeitedCardToUsedPileFromTableResult used && used.getCard() == second));
    }

    private void run(SubAction action, SwccgGame game) {
        List<StandardEffect> pending;
        while (!(pending = action.drainPendingEffects()).isEmpty())
            for (StandardEffect effect : pending)
                effect.playEffect(game);
    }
}
