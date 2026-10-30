package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Crossfire
 */
public class Card1230_026 extends AbstractNormalEffect {
    public Card1230_026() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Crossfire, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Scout walker pilots are trained to set up a deadly heavy fire zone. This tactic can be disrupted by enemy weapons fire.");
        setGameText("Deploy on table. Your AT-STs are immune to attrition < 4. Once per game, may use 3 Force to take an Effect that deploys on a location (except Expand The Empire) into hand from Reserve Deck; reshuffle. During battle, may place this Effect out of play to draw one battle destiny if unable to otherwise; your next battle destiny draw may not be canceled. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.AT_ST), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId searchId = GameTextActionId.LEGACY__CROSSFIRE_V__TAKE_EFFECT;
        if (GameConditions.isOncePerGame(game, self, searchId)
                && GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, searchId)) {
            final TopLevelGameTextAction search = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, searchId);
            search.setText("Take Effect into hand from Reserve Deck");
            search.appendUsage(
                    new OncePerGameEffect(search));
            search.appendCost(
                    new UseForceEffect(search, playerId, 3));
            search.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(search, playerId,
                            Filters.and(Filters.Effect, Filters.not(Filters.title(Title.Expand_The_Empire)),
                                    Filters.or(Filters.gameTextContains("Deploy on a"), Filters.gameTextContains("Deploy on your"),
                                            Filters.gameTextContains("Deploy on opponent"))), true));
            actions.add(search);
        }

        GameTextActionId destinyId = GameTextActionId.LEGACY__CROSSFIRE_V__DRAW_BATTLE_DESTINY;
        if (GameConditions.isDuringBattle(game)) {
            final TopLevelGameTextAction destiny = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, destinyId);
            destiny.setText("Place out of play to draw battle destiny");
            destiny.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(destiny, self));
            destiny.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(destiny,
                            new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.and(Filters.your(self), Filters.participatingInBattle), 1),
                            "Draws battle destiny if unable to otherwise"));
            destiny.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(destiny,
                            new MayNotCancelBattleDestinyModifier(self, playerId),
                            "Next battle destiny may not be canceled"));
            actions.add(destiny);
        }

        return actions;
    }
}
