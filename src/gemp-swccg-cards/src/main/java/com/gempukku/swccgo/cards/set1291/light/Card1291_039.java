package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
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
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayAddPilotAbilityWhenTargetedByTallonRollModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Republic
 * Title: Commander Adar Tallon
 */
public class Card1291_039 extends AbstractRepublic {
    private static final Filter UNIQUE_REPUBLIC_STARFIGHTER = Filters.and(Filters.unique, Icon.REPUBLIC, Filters.starfighter);

    public Card1291_039() {
        super(Side.LIGHT, 2, 3, 2, 3, 5, "Commander Adar Tallon", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Leader.");
        setGameText("Adds 2 to the power of anything he pilots. Adar is a matching pilot for any unique (•) [Republic] starfighter and, while piloting one, adds one battle destiny. Your [Republic] starfighters here are power and maneuver +1 and may add ability of pilot to total when targeted by Tallon Roll.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_I);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER);
        setMatchingStarshipFilter(UNIQUE_REPUBLIC_STARFIGHTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourRepublicStarfightersHere = Filters.and(Filters.your(self), Icon.REPUBLIC, Filters.starfighter, Filters.here(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new PilotingCondition(self, UNIQUE_REPUBLIC_STARFIGHTER), 1));
        modifiers.add(new PowerModifier(self, yourRepublicStarfightersHere, 1, true));
        modifiers.add(new ManeuverModifier(self, yourRepublicStarfightersHere, 1));
        modifiers.add(new MayAddPilotAbilityWhenTargetedByTallonRollModifier(self, yourRepublicStarfightersHere, null));
        return modifiers;
    }
}
