package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * Set: Virtual Set 8
 * Type: Interrupt
 * Subtype: Starting
 * Title: Careful Planning (V)
 */
public class Card1108_005 extends AbstractStartingInterrupt {
    public Card1108_005() {
        super(Side.LIGHT, 5, Title.Careful_Planning, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Alliance troops on planet must plan ahead to achieve success in military operations.");
        setGameText("STARTING: If your starting location was a battleground site, [download] up to two related locations (one must be a system) with < 4 total [Light Side Force] and up to three Effects that are always [Immune to Alter]. Place this Interrupt in Lost Pile.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null
                && Filters.site.accepts(game, startingLocation)
                && startingLocation.isStartingLocationBattleground()) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy locations and Effects from Reserve Deck");
            action.allowResponses("Deploy up to two related locations and up to three Effects from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            Filter relatedSystem = Filters.and(
                                    Filters.system,
                                    Filters.relatedLocation(startingLocation),
                                    Filters.iconCountLessThan(Icon.LIGHT_FORCE, 4));
                            Filter relatedOtherLocation = Filters.and(
                                    Filters.location,
                                    Filters.relatedLocation(startingLocation),
                                    Filters.not(Filters.system),
                                    Filters.iconCountLessThan(Icon.LIGHT_FORCE, 4));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, relatedSystem, 0, 1, true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, relatedOtherLocation, 0, 1, true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 0, 3, true, false));
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}
