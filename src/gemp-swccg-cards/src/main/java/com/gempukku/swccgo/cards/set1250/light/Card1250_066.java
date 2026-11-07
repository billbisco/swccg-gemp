package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToDrawDestinyCardResult;

import java.util.List;

/** Legacy Block 5 Changing The Odds (V), printing 66. */
public class Card1250_066 extends AbstractUsedInterrupt {
    public Card1250_066() {
        super(Side.LIGHT, 5, Title.Changing_The_Odds, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Qui-Gon used his Jedi abilities to insure that there was no 'chance' involved with the use of Watto's chance cube.");
        setGameText("If a player is about to draw destiny for an Epic Event, Interrupt, Objective, or weapon, that player must first activate 1 Force and shuffle their Reserve Deck. OR If both players just drew one battle destiny, cancel and redraw both destiny draws.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self) {
        if (!(result instanceof AboutToDrawDestinyCardResult about) || about.isSubstituteDestiny()) return null;

        var draw = game.getGameState().getTopDrawDestinyState();
        if (draw == null) return null;
        String drawingPlayer = draw.getDrawDestinyEffect().getPlayerDrawingDestiny();
        PhysicalCard source = about.getActionSource();
        boolean eligible = about.getDestinyType() == DestinyType.WEAPON_DESTINY
                || about.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY
                || (source != null && Filters.or(CardType.EPIC_EVENT, CardType.INTERRUPT,
                        CardType.OBJECTIVE, Filters.weapon).accepts(game, source));
        if (!eligible || !GameConditions.hasReserveDeck(game, drawingPlayer)) return null;

        PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Activate 1 Force and shuffle Reserve Deck");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new ActivateForceEffect(action, drawingPlayer, 1));
                action.appendEffect(new ShuffleReserveDeckEffect(action, drawingPlayer));
            }
        });
        return List.of(action);
    }
}
