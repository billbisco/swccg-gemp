package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Take Evasive Action
 */
public class Card1105_048 extends AbstractUsedOrLostInterrupt {
    public Card1105_048() {
        super(Side.DARK, 4, Title.Take_Evasive_Action, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Aoooga! Aoooga!");
        setGameText("USED: Cancel Fallen Portal, Fly Casual, or T-47 Battle Formation (unless canceling Walker Garrison). (Immune to Sense.) LOST: If opponent just drew lightsaber combat or weapon destiny, cancel and redraw that destiny.");
        addIcons(Icon.DAGOBAH);
    }

    private Filter cardsToCancel() {
        return Filters.or(Filters.Fallen_Portal, Filters.title(Title.Fly_Casual), Filters.title(Title.T_47_Battle_Formation));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        if (TriggerConditions.isPlayingCard(game, effect, cardsToCancel())
                && !TriggerConditions.isPlayingCardTargeting(game, effect, cardsToCancel(), TargetingReason.TO_BE_CANCELED, Filters.Walker_Garrison)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        Filter[] targets = new Filter[]{
                Filters.Fallen_Portal,
                Filters.title(Title.Fly_Casual),
                Filters.title(Title.T_47_Battle_Formation)
        };
        String[] titles = new String[]{
                Title.Fallen_Portal,
                Title.Fly_Casual,
                Title.T_47_Battle_Formation
        };
        for (int i = 0; i < targets.length; i++) {
            if (GameConditions.canTargetToCancel(game, self, targets[i])) {
                PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setImmuneTo(Title.Sense);
                CancelCardActionBuilder.buildCancelCardAction(action, targets[i], titles[i]);
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        final String opponent = game.getOpponent(playerId);
        if (opponentJustDrewLightsaberCombatOrWeaponDestiny(effectResult, opponent)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY, CardSubtype.LOST);
            action.setText("Cancel and redraw destiny");
            action.allowResponses("Cancel and redraw opponent's lightsaber combat or weapon destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    private boolean opponentJustDrewLightsaberCombatOrWeaponDestiny(EffectResult effectResult, String opponent) {
        if (effectResult.getType() != EffectResult.Type.DESTINY_DRAWN) {
            return false;
        }
        DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
        if (destinyDrawnResult.isCanceled() || !opponent.equals(effectResult.getPerformingPlayerId())) {
            return false;
        }
        DestinyType destinyType = destinyDrawnResult.getDestinyType();
        return destinyType == DestinyType.WEAPON_DESTINY
                || destinyType == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY
                || destinyType == DestinyType.LIGHTSABER_COMBAT_DESTINY;
    }
}
