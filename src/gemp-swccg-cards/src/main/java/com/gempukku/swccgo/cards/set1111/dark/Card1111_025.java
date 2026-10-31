package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedInterruptEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Effect
 * Title: I've Lost Artoo!
 */
public class Card1111_025 extends AbstractNormalEffect {
    public Card1111_025() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Ive_Lost_Artoo, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("\"WHAAAAAAAAAOOOOW!\"");
        setGameText("Use 2 Force to deploy on table. Once per game, may use 1 Force to play an Interrupt (except Sense, Alter, or Control) from under your Starting Effect as if from hand (then place that card out of play). May place this Effect in Lost Pile to activate 1 Force.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId playInterruptId = GameTextActionId.OVS__IVE_LOST_ARTOO__PLAY_INTERRUPT_FROM_STARTING_EFFECT;
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        Filter interruptFilter = Filters.and(Filters.Interrupt, Filters.not(Filters.or(Filters.Sense, Filters.Alter, Filters.Control)), Filters.playable(self));
        if (startingEffect != null
                && GameConditions.isOncePerGame(game, self, playInterruptId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasStackedCards(game, startingEffect, interruptFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, playInterruptId);
            action.setText("Play Interrupt from Starting Effect");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, startingEffect, interruptFilter) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedInterrupt) {
                            action.setActionMsg("Play " + GameUtils.getCardLink(selectedInterrupt) + " from Starting Effect");
                            action.appendEffect(new PlayStackedInterruptEffect(action, selectedInterrupt, null, null, true));
                        }
                    });
            actions.add(action);
        }

        final TopLevelGameTextAction activate = new TopLevelGameTextAction(self, gameTextSourceCardId);
        activate.setText("Place in Lost Pile to activate 1 Force");
        activate.appendCost(new PlaceCardInLostPileFromTableEffect(activate, self));
        activate.appendEffect(new ActivateForceEffect(activate, playerId, 1));
        actions.add(activate);

        return actions;
    }
}
