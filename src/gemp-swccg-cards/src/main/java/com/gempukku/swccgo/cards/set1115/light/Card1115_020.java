package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Alien
 * Title: Melas
 */
public class Card1115_020 extends AbstractAlien {
    public Card1115_020() {
        super(Side.LIGHT, 2, 3, 2, 4, 4, "Melas", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sarkan smuggler. Smokes an Essoomian gruu pipe to heighten awareness. Exiled from his home planet of Sarka for displaying curiosity in other aliens. Misses his homeworld.");
        setGameText("Adds 2 to power of anything he pilots. Once per turn, may 'smoke pipe' by placing a card from hand on your Used Pile; Melas is power and defense value +2, but cannot move this turn (if during your draw phase, may instead peek at the top two cards of any Reserve Deck.)");
        addIcons(Icon.PILOT, Icon.SPECIAL_EDITION);
        setSpecies(Species.SARKAN);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MELAS_V__SMOKE_PIPE;
        String opponent = game.getOpponent(playerId);

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Smoke pipe");
            action.setActionMsg("Smoke pipe (power and defense value +2 and may not move)");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new PutCardFromHandOnUsedPileEffect(action, playerId));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new PowerModifier(self, 2), "make Melas power +2 until end of turn"));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new DefenseValueModifier(self, self, 2), "make Melas defense value +2 until end of turn"));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new MayNotMoveModifier(self), "make Melas unable to move until end of turn"));
            actions.add(action);
        }

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DRAW)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top two cards of any Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(final SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            action.setActionMsg("Peek at top two cards of " + cardPileOwner + "'s Reserve Deck");
                            action.appendEffect(
                                    new PeekAtTopCardsOfReserveDeckEffect(action, playerId, cardPileOwner, 2));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
