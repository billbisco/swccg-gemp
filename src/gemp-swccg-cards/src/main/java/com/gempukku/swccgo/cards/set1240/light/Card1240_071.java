package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Droid
 * Title: Lars' Protocol Droid
 */
public class Card1240_071 extends AbstractDroid {
    public Card1240_071() {
        super(Side.LIGHT, 4, 2, 1, 3, "Lars' Protocol Droid", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        addModelType(ModelType.PROTOCOL);
        setGameText("'Device' on Harvest may be treated as '[A New Hope] or [Hoth] droid'. While at a farm: your characters present are forfeit +3; may raise this site (if converted) to the top; and, once per move phase, may put hand in Used Pile to place a 'crop' on Reserve Deck.");
        addIcons(Icon.HOTH);
    }

    /**
     * What "device" means on Harvest for the specified player: also an [A New Hope] or [Hoth] droid while that
     * player has Lars' Protocol Droid (V) on table.
     * @param game the game
     * @param playerId the player playing Harvest
     * @return the filter
     */
    public static Filter harvestDevice(SwccgGame game, String playerId) {
        if (Filters.canSpot(game, null, Filters.and(Filters.owner(playerId), Filters.title("Lars' Protocol Droid"), Icon.LEGACY_BLOCK_4))) {
            return Filters.or(Filters.device, Filters.and(Filters.droid, Filters.or(Icon.A_NEW_HOPE, Icon.HOTH)));
        }
        return Filters.device;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.presentWith(self)),
                new AtCondition(self, Filters.farm), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isAtLocation(game, self, Filters.and(Filters.farm, Filters.site, Filters.canBeConvertedByRaisingYourLocationToTop(playerId)))) {
            final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            if (location != null) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Raise converted site to the top");
                action.setActionMsg("Raise converted site to the top to convert " + GameUtils.getCardLink(location));
                action.addAnimationGroup(location);
                action.appendEffect(
                        new ConvertLocationByRaisingToTopEffect(action, location, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
