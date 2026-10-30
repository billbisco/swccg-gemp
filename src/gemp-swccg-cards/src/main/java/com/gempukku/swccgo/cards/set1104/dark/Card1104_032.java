package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ReplaceAbility1PermanentPilotsModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Lennox
 */
public class Card1104_032 extends AbstractImperial {
    public Card1104_032() {
        super(Side.DARK, 2, 2, 2, 2, 5, Title.Captain_Lennox, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Captain of the Imperial Star Destroyer Tyrant. An able leader. Unlike most Imperial officers, he is dedicated to his ship and crew. Finds political maneuvering distasteful.");
        setGameText("Adds 3 to power of any Star Destroyer he pilots. Tyrant and •••Death Squadron Star Destroyers are deploy -3 and power +1 here, and their ability-1 permanent pilots are replaced by ability-2 permanent pilots.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.CAPTAIN, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Tyrant);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter tyrantAndDeathSquadronStarDestroyers = Filters.or(Filters.Tyrant, Filters.and(Filters.Star_Destroyer, Keyword.DEATH_SQUADRON));
        Filter thoseShipsHere = Filters.and(tyrantAndDeathSquadronStarDestroyers, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.Star_Destroyer));
        modifiers.add(new DeployCostToLocationModifier(self, tyrantAndDeathSquadronStarDestroyers, -3, Filters.here(self)));
        modifiers.add(new PowerModifier(self, thoseShipsHere, 1));
        modifiers.add(new ReplaceAbility1PermanentPilotsModifier(self, thoseShipsHere, 2));
        return modifiers;
    }
}
