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
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptivesEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

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
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Special_Delivery)
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
                && GameConditions.canTargetToCancel(game, self, Filters.Special_Delivery)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Special_Delivery, Title.Special_Delivery);
            actions.add(action);
        }
        if (TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.controls(game, opponent, Filters.Geonosis_Petranaki_Arena)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.controls(game, opponent, Filters.Geonosis_Petranaki_Arena)
                && GameConditions.canSpot(game, self, Filters.and(Filters.captive, Filters.at(Filters.Geonosis_Petranaki_Arena)))) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, opponent, gameTextSourceCardId);
            action.setText("Release captives at Petranaki Arena");
            action.appendEffect(new ReleaseCaptivesEffect(action, Filters.filterActive(game, self, Filters.and(Filters.captive, Filters.at(Filters.Geonosis_Petranaki_Arena)))));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ARENA_EXECUTION__DEPLOY_ARENA_PILLARS;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Arena Pillars from Reserve Deck");
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title("Arena Pillars"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
