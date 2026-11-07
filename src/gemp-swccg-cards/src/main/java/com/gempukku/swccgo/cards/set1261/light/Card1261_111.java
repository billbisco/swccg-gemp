package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Hoth: Echo Command Center (War Room) (V), Virtual Block 6.1. */
public class Card1261_111 extends AbstractSite {
    public Card1261_111() {
        super(Side.LIGHT, Title.Echo_Command_Center, Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("If Local Uprising on table, may use 1 Force to take Rendezvous Point into hand from Reserve Deck; reshuffle. Your transports are hyperspeed +2. May not be converted.");
        setLocationDarkSideGameText("Unless you occupy this site (or three related locations), The First Transport Is Away! is immune to Our First Catch Of The Day.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.HOTH, Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.WAR_ROOM);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        return Arrays.asList(new HyperspeedModifier(self, Filters.and(Filters.your(playerId), Filters.transport), 2),
                new MayNotBeConvertedModifier(self));
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new ImmuneToTitleModifier(self, Filters.The_First_Transport_Is_Away,
                new NotCondition(new OrCondition(new OccupiesCondition(playerId, self),
                        new OccupiesCondition(playerId, 3, Filters.relatedLocation(self)))), Title.Our_First_Catch_Of_The_Day));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId actionId = GameTextActionId.LEGACY__ECHO_COMMAND_CENTER__TAKE_RENDEZVOUS_POINT;
        if (GameConditions.canSpot(game, self, Filters.title(Title.Local_Uprising))
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, actionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, actionId);
            action.setText("Take Rendezvous Point into hand");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            // No time frame is printed: the One Rule does not impose a usage limit.
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Rendezvous_Point), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
