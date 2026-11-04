package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawDestinyExceptBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Title: Occupied Territory
 */
public class Card1274_007 extends AbstractNormalEffect {
    public Card1274_007() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Occupied Territory", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setLore("'I advise everyone to leave before more Imperial troops arrive.'");
        setGameText("Deploy on a battleground. If you occupy, during battle here or at a related location, may place this Effect in Used Pile and choose: Draw up to two cards from Reserve Deck OR Prevent opponent from drawing any destinies during the power segment of that battle (except battle destiny).");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.location, Filters.battleground);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__OCCUPIED_TERRITORY__DRAW_OR_PREVENT_DESTINIES;
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        if (!GameConditions.isDuringBattleAt(game, Filters.sameOrRelatedLocation(self))
                || !GameConditions.occupies(game, playerId, Filters.sameLocation(self))) {
            return null;
        }
        final String opponent = game.getOpponent(playerId);

        for (int numCards = 2; numCards >= 1; numCards--) {
            if (GameConditions.hasReserveDeck(game, playerId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place in Used Pile to draw " + numCards + " card" + (numCards == 1 ? "" : "s") + " from Reserve Deck");
                action.appendCost(
                        new PlaceCardInUsedPileFromTableEffect(action, self));
                action.appendEffect(
                        new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, numCards));
                actions.add(action);
            }
        }

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
        action.setText("Place in Used Pile to prevent opponent's power segment destiny draws");
        action.setActionMsg("Prevent opponent from drawing any destinies during the power segment of this battle (except battle destiny)");
        action.appendCost(
                new PlaceCardInUsedPileFromTableEffect(action, self));
        action.appendEffect(
                new AddUntilEndOfBattleModifierEffect(action,
                        new MayNotDrawDestinyExceptBattleDestinyModifier(self, new Condition() {
                            @Override
                            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                                BattleState battleState = gameState.getBattleState();
                                return battleState != null && battleState.isReachedPowerSegment() && !battleState.isReachedDamageSegment();
                            }
                        }, opponent),
                        "Prevents opponent from drawing any destinies during the power segment of this battle (except battle destiny)"));
        actions.add(action);
        return actions;
    }
}
