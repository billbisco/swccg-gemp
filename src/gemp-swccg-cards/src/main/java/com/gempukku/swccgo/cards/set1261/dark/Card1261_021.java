package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledByModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedByModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Arrays;
import java.util.List;

/** Virtual Block 6.1, final official text. The conversion printing shares this implementation. */
public class Card1261_021 extends AbstractDefensiveShield {
    public Card1261_021() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Leave_Them_To_Me,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I will deal with them myself.'");
        setGameText("Plays on table. At opponent's Dantooine and Subjugated Planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent. Your game text at opponent's <>Swamp and <>Jungle is canceled. Your characters present at opponent's <>Forest are immune to attrition.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter protectedSites = Filters.and(Filters.opponents(self), Filters.site,
                Filters.or(Filters.Dantooine_site, Filters.Subjugated_planet_location));
        Filter swampOrJungle = Filters.and(Filters.opponents(self), Filters.generic_site,
                Filters.or(Filters.title("Swamp"), Filters.title("Jungle")));
        Filter forest = Filters.and(Filters.opponents(self), Filters.generic_site, Filters.title("Forest"));
        return Arrays.asList(
                new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Imperial), -2, protectedSites),
                new ForceDrainsMayNotBeModifiedByModifier(self, Filters.opponents(self), null, playerId, protectedSites),
                new ForceDrainsMayNotBeCanceledByModifier(self, Filters.opponents(self), playerId, protectedSites),
                new CancelsGameTextOnSideOfLocationModifier(self, swampOrJungle, playerId),
                new ImmuneToAttritionModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.presentAt(forest))));
    }
}
