package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Vehicle
 * Subtype: Transport
 * Title: Maul's Sith Speeder (V)
 */
public class Card1115_055 extends AbstractTransportVehicle {
    public Card1115_055() {
        super(Side.DARK, 5, 1, 1, null, 6, 6, 2, "Maul's Sith Speeder", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Attain speeds of up to 600 kph. 3 meters long. Extremely difficult to control at high speeds. Dengar rode a similar swoop when injured in a race with Han at Agrilat.");
        setGameText("May add Maul as driver. May deploy with Maul as a 'react'. During battle, Maul may disembark (place this vehicle in Used Pile) to cancel the immunity to attrition of a Jedi here. Immune to attrition.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.SWOOP);
        setDriverCapacity(1);
    }

    @Override
    protected Filter getGameTextValidDriverFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Maul;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Maul, Filters.aboard(self)))
                && GameConditions.canSpot(game, self, Filters.and(Filters.Jedi, Filters.here(self)))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Maul disembarks");
            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(new CancelImmunityToAttritionUntilEndOfBattleEffect(action,
                    Filters.and(Filters.Jedi, Filters.here(self)), "Cancel a Jedi's immunity to attrition"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
