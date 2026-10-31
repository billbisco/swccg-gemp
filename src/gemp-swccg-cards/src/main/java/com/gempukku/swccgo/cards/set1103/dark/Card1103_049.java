package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Imperial
 * Title: Trooper Davin Felth
 */
public class Card1103_049 extends AbstractImperial {
    public Card1103_049() {
        super(Side.DARK, 2, 1, 2, 2, 3, "Trooper Davin Felth", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Dispatched to Tatooine to apprehend renegade droids fleeing the Tantive IV. Suspected to have misgivings about Imperial methods. Allegedly shot his commander in the back.");
        setGameText("During your control phase, may take (or lose 1 Force to retrieve) one Local Trouble or Look Sir, Droids into hand from Reserve Deck; reshuffle. Opponent may not fire weapons during Local Trouble battles.");
        addIcons(Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.SANDTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition duringLocalTroubleBattle = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return gameState.isDuringBattle()
                        && gameState.getBattleState() != null
                        && gameState.getBattleState().isLocalTrouble();
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotFireWeaponsModifier(self, Filters.opponents(self), duringLocalTroubleBattle));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TROOPER_DAVIN_FELTH_V__TAKE_OR_RETRIEVE;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
                final TopLevelGameTextAction takeAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                takeAction.setText("Take card into hand from Reserve Deck");
                takeAction.setActionMsg("Take Local Trouble or Look Sir, Droids into hand from Reserve Deck");
                takeAction.appendUsage(
                        new OncePerPhaseEffect(takeAction));
                takeAction.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(takeAction, playerId, Filters.or(Filters.Local_Trouble, Filters.Look_Sir_Droids), true));
                actions.add(takeAction);
            }

            if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
                final TopLevelGameTextAction retrieveAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                retrieveAction.setText("Lose 1 Force to retrieve card");
                retrieveAction.setActionMsg("Lose 1 Force to retrieve Local Trouble or Look Sir, Droids into hand");
                retrieveAction.appendUsage(
                        new OncePerPhaseEffect(retrieveAction));
                retrieveAction.appendCost(
                        new LoseForceEffect(retrieveAction, playerId, 1));
                retrieveAction.appendEffect(
                        new RetrieveCardIntoHandEffect(retrieveAction, playerId, Filters.or(Filters.Local_Trouble, Filters.Look_Sir_Droids)));
                actions.add(retrieveAction);
            }
        }

        return actions;
    }
}
