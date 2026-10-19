package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasStackedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CaptureWithImprisonmentEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptivesEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Mission
 * Title: Arena Execution
 */
public class Card1291_026 extends AbstractMission {
    public Card1291_026() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Arena Execution", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V, false);
        setGameText("Deploy on War Has Begun. [Virtual Block 2] Special Delivery is canceled. May deploy Arena Pillars from Reserve Deck; reshuffle. Petranaki Arena is a prison. If there are no captives at Petranaki Arena and opponent about to lose a non-droid character, may imprison it there. If opponent controls Petranaki Arena during their control phase, they may release any number of captives there. If an opponent's imprisoned character was just 'executed,' stack it here face down (Mission complete).\n{Reward}: Attrition against opponent is +1.\n{Turn the Tide}: If opponent controls Petranaki Arena during their control phase, place this Mission in Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.SEPARATIST);
    }

    private Filter vb2SpecialDelivery() {
        return Filters.and(Filters.Special_Delivery, Icon.LEGACY_BLOCK_2);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title("War Has Begun");
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.title("War Has Begun");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.Geonosis_Petranaki_Arena, Keyword.PRISON));
        modifiers.add(new AttritionModifier(self, new HasStackedCondition(self, Filters.any), 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, vb2SpecialDelivery())
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String opponent = game.getOpponent(self.getOwner());
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, vb2SpecialDelivery())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, vb2SpecialDelivery(), Title.Special_Delivery);
            actions.add(action);
        }
        // End of opponent's Control so they can take the printed release action first.
        if (TriggerConditions.isEndOfOpponentsPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.controls(game, opponent, Filters.Geonosis_Petranaki_Arena)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place in Used Pile");
            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
            actions.add(action);
        }
        if (TriggerConditions.justLostFromAttachedTo(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.Geonosis_Petranaki_Arena)) {
            PhysicalCard executed = ((LostFromTableResult) effectResult).getCard();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Stack executed captive (Mission complete)");
            action.appendEffect(new StackOneCardFromLostPileEffect(action, executed, self, true, false, true));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter toImprison = Filters.and(Filters.opponents(self), Filters.character, Filters.not(Filters.droid));
        if (TriggerConditions.isAboutToBeLost(game, effectResult, toImprison)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.at(Filters.Geonosis_Petranaki_Arena)))
                && GameConditions.canSpotLocation(game, Filters.Geonosis_Petranaki_Arena)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard character = result.getCardToBeLost();
            final PhysicalCard arena = Filters.findFirstFromTopLocationsOnTable(game, Filters.Geonosis_Petranaki_Arena);
            if (character != null && arena != null && result.getPreventableCardEffect() != null) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Imprison " + character.getTitle() + " at Petranaki Arena");
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(character);
                                action.appendEffect(new CaptureWithImprisonmentEffect(action, character, arena, character.isUndercover(), character.isMissing()));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ARENA_EXECUTION__DEPLOY_ARENA_PILLARS;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Arena Pillars from Reserve Deck");
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title("Arena Pillars"), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.controls(game, playerId, Filters.Geonosis_Petranaki_Arena)) {
            final Collection<PhysicalCard> captives = Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                    Filters.and(Filters.captive, Filters.at(Filters.Geonosis_Petranaki_Arena)));
            if (!captives.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
                action.setText("Release any number of captives at Petranaki Arena");
                action.appendEffect(
                        new ChooseCardsOnTableEffect(action, playerId, "Choose captives to release", 0, captives.size(), captives) {
                            @Override
                            protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                                if (!selectedCards.isEmpty()) {
                                    action.appendEffect(new ReleaseCaptivesEffect(action, selectedCards));
                                }
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
