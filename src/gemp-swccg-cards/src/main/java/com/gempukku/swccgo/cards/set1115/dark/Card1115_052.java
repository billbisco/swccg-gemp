package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractStartingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.ThreeTimesPerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Effect
 * Subtype: Starting
 * Title: Knowledge And Defense (V)
 */
public class Card1115_052 extends AbstractStartingEffect {
    public Card1115_052() {
        super(Side.DARK, 4, "Knowledge And Defense", ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("'A Jedi uses the Force for knowledge and defense, never for attack.'");
        setGameText("Deploy on table with 12 cards from outside your deck face-down under here. Thrice per game, may play a Defensive Shield or deploy (at any time) [Virtual Set 11] Restricted Access from here (as if from hand). Players do not draw or activate using Thrown Back. Each time a player attempts to deploy or take a card for free (except their first time per game) they must first use 1 Force.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            action.appendEffect(new StackCardsFromOutsideDeckEffect(action, self.getOwner(), 12, 12, self, Filters.any));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANGER_FEAR_AGGRESSION__PLAY_CARD;
        Filter filter = Filters.or(Filters.Defensive_Shield, Filters.and(Filters.title("Restricted Access"), new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_11;
            }
        }));

        if (GameConditions.isThreeTimesPerGame(game, self, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, filter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Play a Defensive Shield or Restricted Access");
            action.appendUsage(new ThreeTimesPerGameEffect(action));
            action.appendTargeting(new ChooseStackedCardEffect(action, playerId, self, filter) {
                @Override
                protected void cardSelected(PhysicalCard selectedCard) {
                    action.appendEffect(new PlayStackedCardEffect(action, self, selectedCard));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
