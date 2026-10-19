package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TreatsOccupyAsControlModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Character
 * Subtype: Alien
 * Title: Tarfful, Wookiee Insurgent
 */
public class Card1272_018 extends AbstractAlien {
    public Card1272_018() {
        super(Side.LIGHT, 3, 4, 6, 2, 5, "Tarfful, Wookiee Insurgent", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setLore("Leader");
        setGameText("Occupy on Forest Depths is treated as 'control.' Non-unique Wookiees here are Heroic. Once per game, when deployed, may 'roar' to take one card into hand from Used Pile reshuffle, and/or stack a Wookiee from hand on Grrrghrrrgh!.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_I);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.WOOKIEE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TreatsOccupyAsControlModifier(self, Filters.titleContains("Forest Depths"),
                new AtCondition(self, Filters.titleContains("Forest Depths"))));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.Wookiee, Filters.not(Filters.unique), Filters.here(self)), Keyword.HEROIC));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TARFFUL__ROAR;
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && (GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)
                    || (GameConditions.hasInHand(game, playerId, Filters.Wookiee)
                        && GameConditions.canSpot(game, self, Filters.title("Grrrghrrrgh!"))))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Roar");
            action.appendUsage(
                    new OncePerGameEffect(action));
            if (GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new YesNoDecision("Take one card into hand from Used Pile (reshuffle)?") {
                                    @Override
                                    protected void yes() {
                                        action.appendEffect(
                                                new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
                                    }
                                }));
            }
            if (GameConditions.hasInHand(game, playerId, Filters.Wookiee)
                    && GameConditions.canSpot(game, self, Filters.title("Grrrghrrrgh!"))) {
                final PhysicalCard grrrghrrrgh = Filters.findFirstActive(game, self, Filters.title("Grrrghrrrgh!"));
                if (grrrghrrrgh != null) {
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, playerId,
                                    new YesNoDecision("Stack a Wookiee from hand on Grrrghrrrgh!?") {
                                        @Override
                                        protected void yes() {
                                            action.appendEffect(
                                                    new StackCardFromHandEffect(action, playerId, grrrghrrrgh, false, Filters.Wookiee));
                                        }
                                    }));
                }
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
