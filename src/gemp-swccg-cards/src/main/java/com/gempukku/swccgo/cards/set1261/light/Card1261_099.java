package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/** Protector (V), final Light master page 35. */
public class Card1261_099 extends AbstractUsedOrStartingInterrupt {
    private static final Filter FOREST_DEPTHS = Filters.title("Kashyyyk: Forest Depths");
    private static final Filter GRRR = Filters.title("Grrrghrrrgh!");

    public Card1261_099() {
        super(Side.LIGHT, 4, "Protector", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Chewie, this won't help me. Hey! Save your strength. There'll be another time. The princess... you have to take care of her.'");
        setGameText("USED: Raise your converted Kashyyyk system to the top. (Immune to Sense.) STARTING: If Kashyyyk was your starting location, deploy Forest Depths, Grrrghrrrgh!, and up to two Effects that are always immune to Alter from Reserve Deck; reshuffle. Place this Interrupt in hand.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        var kashyyyk = Filters.findFirstFromTopLocationsOnTable(game,
                Filters.and(Filters.Kashyyyk_system, Filters.canBeConvertedByRaisingLocationToTop(playerId)));
        if (kashyyyk == null) return null;
        PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
        action.setText("Raise your converted Kashyyyk system to the top");
        action.setImmuneTo(Title.Sense);
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, kashyyyk, true));
            }
        });
        return List.of(action);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(String playerId, SwccgGame game, PhysicalCard self) {
        var startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation == null || !Filters.Kashyyyk_system.accepts(game, startingLocation)) return null;
        PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.LEGACY__PROTECTOR_V__STARTING_DEPLOY, CardSubtype.STARTING);
        action.setText("Deploy Forest Depths, Grrrghrrrgh!, and up to two Effects");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, FOREST_DEPTHS, true, false));
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, GRRR, true, false));
                var effects = Filters.and(Filters.Effect, Filters.always_immune_to_Alter, Filters.not(GRRR));
                action.appendEffect(new DeployCardsFromReserveDeckEffect(action, effects, 0, 2, true, true));
                action.appendEffect(new TakeCardFromVoidIntoHandEffect(action, playerId, self));
            }
        });
        return action;
    }
}
