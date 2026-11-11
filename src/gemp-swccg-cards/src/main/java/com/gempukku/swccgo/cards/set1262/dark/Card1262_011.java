package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelDuelModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToCancelCardOnTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.2
 * Type: Effect
 * Title: So Be It, Jedi
 * Every publication is a version (Bill, 2026-11-11): the Virtual Cards Master List (2013-04-27, PC-E2) repeats the
 * release-sheet wording word for word (2262_11) and is the current text; the PC website card-list text
 * (PC-E1) is 2262_1011. Same play.
 */
public class Card1262_011 extends AbstractNormalEffect {
    public static final String SO_BE_IT_JEDI = "So Be It, Jedi";
    private static final String RISE_MY_FRIEND = "Rise, My Friend";

    public Card1262_011() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, SO_BE_IT_JEDI, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("'Now, young Skywalker ... you will die.'");
        setGameText("Deploy on table. When Luke deploys using your Objective, he deploys to Vader's site (if possible). Duels initiated with your Objective may not be canceled. Once per game, may ▲ Rise, My Friend. If this Effect canceled by opponent, stack it on Insignificant Rebellion.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final Filter yourObjective = Filters.and(Filters.your(self), Filters.Objective);
        Condition duelInitiatedWithYourObjective = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                DuelState duelState = gameState.getDuelState();
                return duelState != null && duelState.getCardInitiatedDuel() != null
                        && yourObjective.accepts(gameState, modifiersQuerying, duelState.getCardInitiatedDuel());
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, yourObjective, ModifyGameTextType.LEGACY__SO_BE_IT_JEDI__LUKE_DEPLOYS_TO_VADERS_SITE));
        modifiers.add(new MayNotCancelDuelModifier(self, Filters.any, duelInitiatedWithYourObjective));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SO_BE_IT_JEDI__TAKE_RISE_MY_FRIEND;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Rise, My Friend into hand");
            action.setActionMsg("Take Rise, My Friend into hand from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(RISE_MY_FRIEND), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        if (TriggerConditions.isAboutToBeCanceledFromTableBy(game, effectResult, opponent, self)) {
            final PhysicalCard insignificantRebellion = Filters.findFirstActive(game, self, Filters.Insignificant_Rebellion);
            if (insignificantRebellion != null) {
                final AboutToCancelCardOnTableResult result = (AboutToCancelCardOnTableResult) effectResult;
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack on Insignificant Rebellion");
                action.setActionMsg("Stack " + GameUtils.getCardLink(self) + " on " + GameUtils.getCardLink(insignificantRebellion));
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        result.getPreventableCardEffect().preventEffectOnCard(self);
                    }
                });
                action.appendEffect(new StackCardFromTableEffect(action, self, insignificantRebellion));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
