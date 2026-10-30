package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Character
 * Subtype: Droid
 * Title: C-3PO (See-Threepio)
 */
public class Card1102_003 extends AbstractDroid {
    public Card1102_003() {
        super(Side.LIGHT, 3, 3, 1, 4, "C-3PO (See-Threepio)", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Cybot Galactica 3PO human-cyborg relations droid. Fluent in over six million forms of communication. 112 years old. Has never been memorywiped... as far as he knows.");
        setGameText("During a battle at a site with R2-D2, may not be targeted by weapons, and may draw one destiny; subtract that amount from opponent's total power. Once per game, may place all your non-Immune to Alter Effects on table in Used Pile.");
        addPersona(Persona.C3PO);
        addModelType(ModelType.PROTOCOL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new AndCondition(new AtCondition(self, Filters.site), new WithCondition(self, Filters.R2D2), new com.gempukku.swccgo.logic.conditions.InBattleCondition(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId drawId = GameTextActionId.LEGACY__C3PO_V__DRAW_DESTINY;
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, drawId)
                && GameConditions.isInBattleAt(game, self, Filters.site)
                && GameConditions.isWith(game, self, Filters.R2D2)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, drawId);
            action.setText("Draw destiny to reduce opponent's total power");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny != null && totalDestiny > 0) {
                                action.appendEffect(
                                        new SubtractFromOpponentsTotalPowerEffect(action, totalDestiny));
                            }
                        }
                    });
            actions.add(action);
        }

        GameTextActionId placeId = GameTextActionId.LEGACY__C3PO_V__PLACE_EFFECTS;
        if (GameConditions.isOncePerGame(game, self, placeId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Effect, Filters.not(Filters.immune_to_Alter)))) {
            Collection<PhysicalCard> effects = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.Effect, Filters.not(Filters.immune_to_Alter)));
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, placeId);
            action.setText("Place your Effects in Used Pile");
            action.setActionMsg("Place all your non-Immune to Alter Effects on table in Used Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new PlaceCardsInUsedPileFromTableEffect(action, effects));
            actions.add(action);
        }
        return actions;
    }
}
