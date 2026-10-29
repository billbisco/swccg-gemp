package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Smuggler's Blues
 */
public class Card1220_134 extends AbstractNormalEffect {
    public Card1220_134() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Smugglers_Blues, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("It's the lure of easy credits. It's got a very strong appeal. Perhaps you'd understand better wearing my flight suit. It's the ultimate special modification, it's the smuggler's blues.");
        setGameText("Deploy on table. Opponent may not cancel or modify Force drains at any battleground where you have two musicians or two smugglers (or one of each). Once per turn, may take Han's Back, Han's Dice, Solo Han, Special Modifications, or Stay Sharp! into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter battlegroundWithTwoMusiciansOrSmugglers = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.and(Filters.battleground, Filters.location).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                SwccgGame swccgGame = gameState.getGame();
                int musicians = Filters.countActive(swccgGame, self, Filters.and(Filters.your(self), Filters.musician, Filters.at(physicalCard)));
                int smugglers = Filters.countActive(swccgGame, self, Filters.and(Filters.your(self), Filters.smuggler, Filters.at(physicalCard)));
                int either = Filters.countActive(swccgGame, self, Filters.and(Filters.your(self), Filters.or(Filters.musician, Filters.smuggler), Filters.at(physicalCard)));
                return either >= 2 && (musicians >= 2 || smugglers >= 2 || (musicians >= 1 && smugglers >= 1));
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, battlegroundWithTwoMusiciansOrSmugglers, opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, battlegroundWithTwoMusiciansOrSmugglers, opponent, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SMUGGLERS_BLUES_V__TAKE_CARD;
        Filter takeFilter = Filters.or(Filters.title(Title.Hans_Back), Filters.title(Title.Hans_Dice), Filters.title(Title.Solo_Han),
                Filters.title(Title.Special_Modifications), Filters.title(Title.Stay_Sharp));

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Han's Back, Han's Dice, Solo Han, Special Modifications, or Stay Sharp! into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, takeFilter, true));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
