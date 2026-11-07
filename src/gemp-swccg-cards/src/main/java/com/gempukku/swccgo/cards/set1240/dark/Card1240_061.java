package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Starship
 * Subtype: Starfighter
 * Title: Raider Craft
 */
public class Card1240_061 extends AbstractStarfighter {
    public Card1240_061() {
        super(Side.DARK, 2, 3, 3, null, 3, 3, 4, "Raider Craft", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("Modified light freighter. Length 20 meters. Heavily armored for a landing craft. Has ion cannon mounts. Often carries speeder bikes for reconnaissance purposes.");
        setGameText("May add 2 alien pilots, 4 passengers, and 1 vehicle. Any Mistryl or smuggler is a matching pilot (while aboard, adds at least 1 to maneuver and draws one battle destiny if not able to otherwise).");
        addIcons(Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.SENTINEL_CLASS_LANDING_CRAFT);
        setPilotCapacity(2);
        setPassengerCapacity(4);
        setVehicleCapacity(1);
        setMatchingPilotFilter(matchingPilot());
    }

    private static Filter matchingPilot() {
        return Filters.or(Filters.Mistryl, Filters.smuggler);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        // Alien pilots, plus any matching pilot (Mistryl or smuggler).
        return Filters.and(Filters.pilot, Filters.or(Filters.alien, matchingPilot()));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition matchingPilotAboard = new HasPilotingCondition(self, matchingPilot());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, matchingPilotAboard, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, matchingPilotAboard, 1));
        return modifiers;
    }
}
