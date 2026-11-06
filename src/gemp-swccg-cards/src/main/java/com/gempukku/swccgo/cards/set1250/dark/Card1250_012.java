package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromForcePileOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import java.util.Collections;
import java.util.List;

/** Virtual Block 5: Watto (V). */
public class Card1250_012 extends AbstractAlien {
    public Card1250_012() {
        super(Side.DARK, 3, 2, 3, 2, 4, "Watto", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Toydarian junk dealer. Skilled gambler. Won Shmi and Anakin in a Podrace bet with Gardulla the Hutt. Jedi mind tricks don't work on him, only money.");
        setGameText("Once per turn, if at Watto's Junkyard, may randomly reveal a card from Force Pile: if destiny odd, Force drain +1 here this turn (if even, draw a card from Reserve Deck); place revealed card in Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.TATOOINE, Icon.LEGACY_BLOCK_5);
        addKeyword(Keyword.GAMBLER);
        setSpecies(Species.TOYDARIAN);
        addPersona(Persona.WATTO);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        var actionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, actionId)
                && GameConditions.isAtLocation(game, self, Filters.Wattos_Junkyard)
                && GameConditions.hasForcePile(game, playerId)) {
            var action = new TopLevelGameTextAction(self, gameTextSourceCardId, actionId);
            action.setText("Reveal random Force Pile card");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new PassthruEffect(action) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                    var cards = GameUtils.getRandomCards(game, game.getGameState().getForcePile(playerId), 1);
                    if (cards.isEmpty()) return;
                    PhysicalCard card = cards.getFirst();
                    var location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
                    boolean odd = ((int) game.getModifiersQuerying().getDestiny(game.getGameState(), card)) % 2 != 0;
                    game.getGameState().markCardsRevealedFromPile(cards);
                    game.getGameState().sendMessage(playerId + " reveals " + GameUtils.getCardLink(card) + " at random from Force Pile");
                    action.appendEffect(new ShowCardOnScreenEffect(action, card));
                    if (odd)
                        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                new ForceDrainModifier(self, location, 1, playerId), "Force drain +1 at Watto's location"));
                    else
                        action.appendEffect(new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                    action.appendEffect(new PutCardFromForcePileOnTopOfCardPileEffect(action, playerId, card, Zone.USED_PILE, false));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
