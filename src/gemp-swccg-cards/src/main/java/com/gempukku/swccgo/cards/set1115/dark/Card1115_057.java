package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
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
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Starship
 * Subtype: Starfighter
 * Title: Raider Craft (V)
 */
public class Card1115_057 extends AbstractStarfighter {
    public Card1115_057() {
        super(Side.DARK, 2, 3, 3, null, 3, 3, 4, "Raider Craft", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Modified light freighter. Length 20 meters. Heavily armored for a landing craft. Has ion cannon mounts. Often carries speeder bikes for reconnaissance purposes.");
        setGameText("May add 2 alien pilots, 4 passengers, and 1 vehicle. Any Mistryl or smuggler is a matching pilot (while aboard, adds at least 1 to maneuver and draws one battle destiny if not able to otherwise).");
        addIcons(Icon.SPECIAL_EDITION, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelTypes(ModelType.SENTINEL_CLASS_LANDING_CRAFT, ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(4);
        setVehicleCapacity(1);
        setMatchingPilotFilter(Filters.or(Filters.Mistryl, Filters.smuggler));
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.alien, Filters.pilot);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        HasAboardCondition matchingAboard = new HasAboardCondition(self, Filters.or(Filters.Mistryl, Filters.smuggler));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, matchingAboard, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, matchingAboard, 1));
        return modifiers;
    }
}
