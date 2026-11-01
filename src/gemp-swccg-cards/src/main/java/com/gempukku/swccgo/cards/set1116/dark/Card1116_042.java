package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Republic
 * Title: Coruscant Guard
 */
public class Card1116_042 extends AbstractRepublic {
    public Card1116_042() {
        super(Side.DARK, 3, 3, 3, 2, 4, Title.Coruscant_Guard, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("Coruscant Guards are an elite force whose assignments include the protection of important political figures, as well as the policing of Coruscant's higher profile city districts.");
        setGameText("Heroic. Deploys -1 to Senate. While at Senate, Sidious is cumulatively Politics +2, and, during your control phase, may place this character in Used Pile to take [Virtual Block 5] Expand the Empire into hand from Reserve Deck; reshuffle, or take any one card into hand from Force Pile; reshuffle.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.CORUSCANT);
        addKeywords(Keyword.CORUSCANT_GUARD, Keyword.HEROIC);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Galactic_Senate));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        PoliticsModifier politics = new PoliticsModifier(self, Filters.Sidious, new AtCondition(self, Filters.Galactic_Senate), 2);
        politics.setCumulative(true);
        modifiers.add(politics);
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId expandId = GameTextActionId.LEGACY__CORUSCANT_GUARD_V__TAKE_EXPAND_THE_EMPIRE;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, expandId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.Galactic_Senate)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, expandId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, expandId);
            action.setText("Take Expand The Empire into hand");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                    Filters.and(Filters.Expand_The_Empire, new com.gempukku.swccgo.filters.Filter() {
                        @Override
                        public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                            return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_16;
                        }
                    }), true));
            actions.add(action);
        }

        GameTextActionId forcePileId = GameTextActionId.LEGACY__CORUSCANT_GUARD_V__TAKE_CARD_FROM_FORCE_PILE;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, forcePileId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.Galactic_Senate)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, forcePileId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, forcePileId);
            action.setText("Take a card from Force Pile");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
            actions.add(action);
        }
        return actions;
    }
}
