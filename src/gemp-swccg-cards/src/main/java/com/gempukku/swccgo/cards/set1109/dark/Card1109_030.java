package com.gempukku.swccgo.cards.set1109.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Effect
 * Title: Crossfire
 */
public class Card1109_030 extends AbstractNormalEffect {
    public Card1109_030() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Crossfire, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Scout walker pilots are trained to set up a deadly heavy fire zone. This tactic can be disrupted by enemy weapons fire.");
        setGameText("Deploy on table. Your AT-STs are immune to attrition < 3 (or their immunity to attrition is +2). Once per game, may lose 1 Force to take an Effect that deploys on a location (except Expand The Empire) into hand from Reserve Deck; reshuffle. May place this Effect out of play to add one battle destiny. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.AT_ST, Filters.not(Filters.alreadyHasImmunityToAttrition(self))), 3));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.your(self), Filters.AT_ST, Filters.alreadyHasImmunityToAttrition(self)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId searchId = GameTextActionId.OVS__CROSSFIRE__TAKE_EFFECT;
        if (GameConditions.isOncePerGame(game, self, searchId)
                && GameConditions.canSpot(game, self, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, searchId)) {
            final TopLevelGameTextAction search = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, searchId);
            search.setText("Take Effect into hand from Reserve Deck");
            search.appendUsage(
                    new OncePerGameEffect(search));
            search.appendCost(
                    new LoseForceEffect(search, playerId, 1, true));
            search.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(search, playerId,
                            Filters.and(Filters.Effect, Filters.not(Filters.title(Title.Expand_The_Empire)),
                                    Filters.or(Filters.gameTextContains("Deploy on a"), Filters.gameTextContains("Deploy on your"),
                                            Filters.gameTextContains("Deploy on opponent"))), true));
            actions.add(search);
        }

        if (GameConditions.isDuringBattle(game)) {
            final TopLevelGameTextAction destiny = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            destiny.setText("Place out of play to add one battle destiny");
            destiny.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(destiny, self));
            destiny.appendEffect(
                    new AddBattleDestinyEffect(destiny, 1));
            actions.add(destiny);
        }

        return actions;
    }
}
