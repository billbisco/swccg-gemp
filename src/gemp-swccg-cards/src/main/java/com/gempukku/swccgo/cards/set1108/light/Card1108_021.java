package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.FourTimesPerGameEffect;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Effect
 * Title: Thrown Back (V)
 */
public class Card1108_021 extends AbstractNormalEffect {
    public Card1108_021() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Thrown_Back, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Being sent to dispose of two Jedi is the battle droid equivalent of a really bad day at the office.");
        setGameText("Deploy on table with 12 Defensive Shields from outside your deck face-down under here. Four times per game may play a card from here (as if from hand). Unless Yoda on Dagobah, each player's [upload] or [download] attempt (after the first in a turn) allows their opponent to draw top card from Reserve deck and/or activate 1 Force.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            // Printed is 12 shields; 0-12 so a play table without an outside-deck collection can still deploy.
            action.appendEffect(
                    new StackCardsFromOutsideDeckEffect(action, playerId, 0, 12, self, Filters.Defensive_Shield));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__THROWN_BACK__PLAY_CARD;
        Filter filter = Filters.playable(self);

        if (GameConditions.isFourTimesPerGame(game, self, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Play a card");
            action.appendUsage(
                    new FourTimesPerGameEffect(action));
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, self, filter) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(
                                    new PlayStackedCardEffect(action, self, selectedCard));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
