package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractLocationDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Device
 * Title: Carbonite Chamber Console (V)
 */
public class Card1283_009 extends AbstractLocationDevice {
    public Card1283_009() {
        super(Side.DARK, 4, Title.Carbonite_Chamber_Console, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Most often used to freeze Tibanna gas for transport. Modified by Ugloste to work on humans. Intended to capture Luke Skywalker, the Emperor's prize.");
        setGameText("Deploy on Carbonite Chamber. Once per game may use 3 Force to take [Virtual Block 6][Jabba's Palace] Boba Fett into hand from Reserve Deck; reshuffle. While Jabba's Prize on Tatooine, you may add 1 to your destiny draws in battle anywhere, and once per game may take Audience Chamber into hand from Reserve Deck; reshuffle. Immune to Sabotage and Uutkik.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Carbonite_Chamber;
    }

    private static boolean jabbasPrizeOnTatooine(SwccgGame game, PhysicalCard self) {
        return GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Jabbas_Prize, Filters.on(Title.Tatooine)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        Condition jabbasPrizeOnTatooine = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return jabbasPrizeOnTatooine(gameState.getGame(), self);
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, Filters.owner(self.getOwner()),
                new AndCondition(jabbasPrizeOnTatooine, new DuringBattleCondition()), 1));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sabotage));
        modifiers.add(new ImmuneToTitleModifier(self, "Uutkik"));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CARBONITE_CHAMBER_CONSOLE_V__TAKE_BOBA_FETT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Boba Fett into hand from Reserve Deck");
            action.setActionMsg("Take a [Virtual Block 6][Jabba's Palace] Boba Fett into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Boba_Fett, Icon.LEGACY_BLOCK_6, Icon.JABBAS_PALACE), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__CARBONITE_CHAMBER_CONSOLE_V__TAKE_AUDIENCE_CHAMBER;

        // Check condition(s)
        if (jabbasPrizeOnTatooine(game, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Audience Chamber into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Audience_Chamber, true));
            actions.add(action);
        }
        return actions;
    }
}
