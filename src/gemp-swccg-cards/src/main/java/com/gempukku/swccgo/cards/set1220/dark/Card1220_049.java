package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringDuelCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: The Quick And Easy Path
 */
public class Card1220_049 extends AbstractNormalEffect {
    public Card1220_049() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.The_Quick_And_Easy_Path, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setLore("'Ready are you? What know you of ready?'");
        setGameText("Deploy on table. Sith Fury and your non-epic duels are canceled. Once per turn, may use 1 Force to take a non-[Episode I] Interrupt (or Immediate Effect) with 'duel' in its game text into hand from Reserve Deck; reshuffle. If [Virtual Block 6] Epic Duel on table, Force retrieved (or lost) during a duel cannot be reduced or canceled. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, new AndCondition(new DuringDuelCondition(), new OnTableCondition(self, Filters.Epic_Duel)),
                ModifierFlag.FORCE_RETRIEVED_OR_LOST_DURING_DUEL_MAY_NOT_BE_REDUCED_OR_CANCELED));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__QUICK_AND_EASY_PATH_V__TAKE_DUEL_CARD;
        Filter takeFilter = Filters.and(Filters.or(Filters.Interrupt, Filters.Immediate_Effect),
                Filters.not(Icon.EPISODE_I), Filters.gameTextContains("duel"));
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Interrupt or Immediate Effect with 'duel' into hand");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, takeFilter, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Sith_Fury))
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
        String playerId = self.getOwner();

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.title(Title.Sith_Fury))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel Sith Fury");
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Sith_Fury), Title.Sith_Fury);
            actions.add(action);
        }

        if (TriggerConditions.nonEpicDuelInitiatedBy(game, effectResult, playerId)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel non-epic duel");
            action.appendEffect(
                    new CancelDuelEffect(action));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }
}
