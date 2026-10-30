package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardsFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardsEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Bantha Herd
 */
public class Card1230_024 extends AbstractNormalEffect {
    public Card1230_024() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Bantha_Herd, Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("While banthas are found on many worlds, the largest herds are found on Tatooine. The Sand People of that planet learned to tame the beasts.");
        setGameText("Deploy on a Tatooine site. Once per turn, may place up to 3 cards from hand face down here. You may 'react' to battles initiated against your lone Tusken Raider by deploying (for free) any Tusken Raider(s) and/or Bantha(s) here. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Tatooine_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.or(Filters.Tusken_Raider, Filters.bantha), Filters.stackedOn(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BANTHA_HERD_V__STACK;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack cards from hand face down");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new StackCardsFromHandEffect(action, playerId, 1, 3, self, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        Filter tuskenOrBanthaStacked = Filters.and(Filters.or(Filters.Tusken_Raider, Filters.bantha), Filters.stackedOn(self));
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.here(self))
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Tusken_Raider, Filters.alone, Filters.here(self)))
                && GameConditions.hasStackedCards(game, self, tuskenOrBanthaStacked)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("React: deploy Tusken Raider or Bantha");
            action.appendEffect(
                    new ChooseStackedCardsEffect(action, playerId, self, 1, Integer.MAX_VALUE, tuskenOrBanthaStacked) {
                        @Override
                        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                            final ReactActionOption react = new ReactActionOption(self, true, 0, false, "Deploy as 'react'",
                                    Filters.in(selectedCards), Filters.here(self), null, true);
                            for (final PhysicalCard stacked : selectedCards) {
                                action.appendEffect(
                                        new PassthruEffect(action) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {
                                                PlayCardAction play = stacked.getBlueprint().getPlayCardAction(playerId, game, stacked, self, true, 0, null, null, null, react, null, false, 0, Filters.here(self), null);
                                                if (play != null) {
                                                    game.getActionsEnvironment().addActionToStack(play);
                                                }
                                            }
                                        }
                                );
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
