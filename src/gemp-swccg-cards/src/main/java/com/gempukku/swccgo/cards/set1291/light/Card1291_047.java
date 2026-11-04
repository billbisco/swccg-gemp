package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Alien
 * Title: Nien Nunb, Sullustan Smuggler
 */
public class Card1291_047 extends AbstractAlien {
    public Card1291_047() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Nien Nunb, Sullustan Smuggler", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Brilliant navigator. Former SoroSuub employee. Turned to pirating when that corporation backed the Empire. Tall for a Sullustan.");
        setGameText("Adds 2 to the power of anything he pilots. Once during your turn, may place a card from hand (except Obi-Wan) of the same card type as the top card of your Lost Pile out of play to retrieve 1 Force (may be taken into hand).");
        addPersona(Persona.NIEN_NUNB);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        setSpecies(Species.SULLUSTAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__NIEN_NUNB_SULLUSTAN_SMUGGLER__RETRIEVE_FORCE;
        PhysicalCard topOfLostPile = game.getGameState().getTopOfLostPile(playerId);

        // Check condition(s)
        if (topOfLostPile != null
                && GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            Filter sameCardTypeExceptObiWan = Filters.and(topOfLostPile.getBlueprint().getCardCategory(), Filters.not(Filters.ObiWan),
                    Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY));
            final Collection<PhysicalCard> mayBePlacedOutOfPlay = Filters.filter(game.getGameState().getHand(playerId), game, sameCardTypeExceptObiWan);
            if (!mayBePlacedOutOfPlay.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place card from hand out of play to retrieve 1 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new ChooseCardFromHandEffect(action, playerId, Filters.in(mayBePlacedOutOfPlay)) {
                            @Override
                            protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                action.setActionMsg("Place " + GameUtils.getCardLink(selectedCard) + " from hand out of play to retrieve 1 Force");
                                action.appendCost(
                                        new PlaceCardOutOfPlayFromOffTableEffect(action, selectedCard));
                                // Perform result(s)
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, 1) {
                                            @Override
                                            public boolean mayBeTakenIntoHand() {
                                                return true;
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
