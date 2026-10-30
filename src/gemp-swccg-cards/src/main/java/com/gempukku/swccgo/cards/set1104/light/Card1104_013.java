package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
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
import com.gempukku.swccgo.logic.effects.FindMissingCharacterEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Nice Of You Guys To Drop By
 */
public class Card1104_013 extends AbstractUsedInterrupt {
    public Card1104_013() {
        super(Side.LIGHT, 5, Title.Nice_Of_You_Guys_To_Drop_By, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Echo Base, this is Rogue 2. I've found them. Repeat. I've found them.'");
        setGameText("Take Lost In The Wilderness or a Rogue speeder into hand from Reserve Deck; reshuffle. OR If you occupy a site, find one of your missing characters there. OR Cancel Stunning Leader. OR Cancel opponent's attempt to randomly remove one or more cards from your hand (except with Monnok).");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Stunning_Leader)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        final String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isAboutToPlaceRandomCardsFromOpponentsHandOnUsedPile(game, effect, opponent, Filters.not(Filters.Monnok))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel random removal from hand");
            action.allowResponses("Cancel opponent's attempt to randomly remove cards from hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            effect.cancel(self);
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId takeId = GameTextActionId.LEGACY__NICE_OF_YOU_GUYS_V__TAKE_CARD;
        Filter takeFilter = Filters.or(Filters.Lost_In_The_Wilderness, Filters.Rogue_1, Filters.Rogue_2, Filters.Rogue_3, Filters.Rogue_4, Filters.Rogue_T47);
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId);
            action.setText("Take card into hand from Reserve Deck");
            action.allowResponses("Take Lost In The Wilderness or a Rogue speeder into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, takeFilter, true));
                        }
                    }
            );
            actions.add(action);
        }

        Filter missingFilter = Filters.and(Filters.your(self), Filters.missing, Filters.character, Filters.at(Filters.and(Filters.site, Filters.occupies(playerId))));
        if (GameConditions.occupies(game, playerId, Filters.site)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_MISSING, missingFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Find missing character");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose missing character", SpotOverride.INCLUDE_MISSING, missingFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Find " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new FindMissingCharacterEffect(action, finalTarget, false));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canTargetToCancel(game, self, Filters.Stunning_Leader)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Stunning_Leader, Title.Stunning_Leader);
            actions.add(action);
        }
        return actions;
    }
}
