package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Title: Temporary Foothold
 */
public class Card1274_020 extends AbstractNormalEffect {
    public Card1274_020() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Temporary Foothold", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setLore("Echo Base heavy weapons units provided covering fire, allowing Alliance personnel to escape Hoth.");
        setGameText("Deploy on a battleground. While you occupy at least two battlegrounds, your Force drains here may not be canceled. During battle here or at a related location, may place this Effect in Used Pile to draw up to two cards from Reserve Deck.");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.SETUP);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.battleground;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self),
                new OccupiesCondition(playerId, 2, Filters.battleground), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TEMPORARY_FOOTHOLD__DRAW_CARDS;
        final int maxCardsToDraw = Math.min(2, game.getGameState().getReserveDeckSize(playerId));

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.or(Filters.here(self), Filters.relatedLocation(self)))
                && maxCardsToDraw > 0) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place in Used Pile to draw cards");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile to draw up to two cards from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new IntegerAwaitingDecision("Choose number of cards to draw", 0, maxCardsToDraw, maxCardsToDraw) {
                                @Override
                                public void decisionMade(int result) throws DecisionResultInvalidException {
                                    if (result > 0) {
                                        action.appendEffect(
                                                new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, result));
                                    }
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
