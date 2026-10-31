package com.gempukku.swccgo.cards.set1107.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Interrupt
 * Subtype: Used
 * Title: Those Rebels Won't Escape Us (V)
 */
public class Card1107_020 extends AbstractUsedInterrupt {
    public Card1107_020() {
        super(Side.DARK, 3, Title.Those_Rebels_Wont_Escape_Us, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I don't know how we're gonna get out of this one.'");
        setGameText("Use 1 Force to ▲ Stinger or Virago. OR Cancel Darklighter Spin. OR If opponent just initiated a battle at same system or sector as Stinger or Virago, opponent may not target those starfighters with weapons for remainder of turn.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Darklighter_Spin)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter stingerOrVirago = Filters.or(Filters.title("Stinger"), Filters.title("Virago"));

        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.sameSystemOrSectorAs(self, stingerOrVirago))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Protect Stinger and Virago from weapons");
            action.allowResponses("Prevent opponent from targeting Stinger or Virago with weapons for remainder of turn",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new MayNotBeTargetedByWeaponsModifier(self, Filters.or(Filters.title("Stinger"), Filters.title("Virago"))),
                                            "Stinger and Virago may not be targeted by weapons"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OVS__THOSE_REBELS__TAKE_STINGER_OR_VIRAGO;

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Stinger or Virago into hand");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Take Stinger or Virago into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.title("Stinger"), Filters.title("Virago")), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canTargetToCancel(game, self, Filters.Darklighter_Spin)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Darklighter_Spin, Title.Darklighter_Spin);
            actions.add(action);
        }
        return actions;
    }
}
