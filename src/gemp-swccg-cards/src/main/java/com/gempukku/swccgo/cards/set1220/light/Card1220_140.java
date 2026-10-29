package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Computer Interface
 */
public class Card1220_140 extends AbstractUsedInterrupt {
    public Card1220_140() {
        super(Side.LIGHT, 3, Title.Computer_Interface, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Lobot's direct link with the Cloud City central computer allowed him to efficiently manipulate the floating city's resources.");
        setGameText("Twice per turn, if your Lobot on Cloud City or you occupy three Cloud City sites with aliens, peek at the top three cards of your Reserve Deck. Take any of them into hand and replace the others in any order.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COMPUTER_INTERFACE_V__PEEK;
        if (GameConditions.isNumTimesPerTurn(game, self, playerId, 2, self.getCardId(), gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)
                && (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Lobot, Filters.on_Cloud_City))
                || GameConditions.occupiesWith(game, self, playerId, 3, Filters.Cloud_City_site, Filters.alien))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Peek at top 3 cards of Reserve Deck");
            action.appendUsage(
                    new NumTimesPerTurnEffect(action, 2));
            action.allowResponses("Peek at the top three cards of Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 3, 0, 3));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
