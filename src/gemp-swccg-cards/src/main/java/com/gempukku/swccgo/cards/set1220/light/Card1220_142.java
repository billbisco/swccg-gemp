package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Dark Dissension
 */
public class Card1220_142 extends AbstractUsedOrLostInterrupt {
    public Card1220_142() {
        super(Side.LIGHT, 3, Title.Dark_Dissension, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("The huge Imperial officers corps, with its high rate of turnover, is ripe with competitiveness and ambition. Advancement comes at the expense of another's career.");
        setGameText("USED: Take Demotion into hand from Reserve Deck; reshuffle. OR For remainder of turn, cancel the game text of an Imperial (except a Dark Jedi) present with another Imperial. LOST: Cancel Imperial Command (unless limiting your battle destiny draws).");
        addIcons(Icon.HOTH);
    }

    private boolean isLimitingBattleDestinyDraws(Effect effect) {
        if (effect.getAction() == null || effect.getAction().getText() == null) {
            return false;
        }
        String text = effect.getAction().getText();
        return text.contains("Limit opponent") || text.contains("Prevent opponent from drawing more than one");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Imperial_Command))
                && !isLimitingBattleDestinyDraws(effect)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId takeId = GameTextActionId.LEGACY__DARK_DISSENSION_V__TAKE_DEMOTION;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId, CardSubtype.USED);
            action.setText("Take Demotion into hand from Reserve Deck");
            action.allowResponses("Take Demotion into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Demotion, true));
                        }
                    }
            );
            actions.add(action);
        }

        Filter imperialFilter = Filters.and(Filters.Imperial, Filters.not(Filters.Dark_Jedi), Filters.presentWith(self, Filters.Imperial));
        if (GameConditions.canTarget(game, self, imperialFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel an Imperial's game text");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Imperial", imperialFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text for remainder of turn",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
