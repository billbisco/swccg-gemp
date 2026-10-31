package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AllAbilityOnTableProvidedByCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.TotalAbilityModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Effect
 * Title: Tusken Breath Mask (V)
 */
public class Card1108_022 extends AbstractNormalEffect {
    public Card1108_022() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Tusken_Breath_Mask, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sand People use a special mask to reclaim exhaled moisture in the hot, harsh environment of Tatooine. Protects by filtering blowing sand and dispersing excess heat.");
        setGameText("Deploy on an exterior Tatooine battleground site. X = the face-up destiny number of your Objective. Once per turn, if all your ability on table is provided by aliens, your total ability during battle at a site is +X. This Effect canceled if opponent controls this site.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.exterior_site, Filters.Tatooine_site, Filters.battleground);
    }

    private static int faceUpObjectiveDestiny(SwccgGame game, String playerId) {
        PhysicalCard objective = game.getGameState().getObjectivePlayed(playerId);
        if (objective == null) {
            return 0;
        }
        Float destiny = objective.getDestinyValueToUse();
        return destiny == null ? 0 : destiny.intValue();
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Condition allAliens = new AllAbilityOnTableProvidedByCondition(self, playerId, Filters.alien);
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleAt(game, Filters.site)
                && allAliens.isFulfilled(game.getGameState(), game.getModifiersQuerying())) {
            final int x = faceUpObjectiveDestiny(game, playerId);
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make total ability +" + x);
            action.setActionMsg("Your total ability during battle at a site is +" + x);
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new TotalAbilityModifier(self, Filters.battleLocation, x, playerId),
                            "Total ability +" + x));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, opponent, Filters.hasAttached(self))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
