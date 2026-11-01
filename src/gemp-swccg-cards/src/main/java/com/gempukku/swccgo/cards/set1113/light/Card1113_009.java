package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.LandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: Outpost
 */
public class Card1113_009 extends AbstractSite {
    public Card1113_009() {
        super(Side.LIGHT, "Outpost", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("May not deploy to Bespin, Coruscant, Hoth, Kessel or Tatooine. Non-Jedi scouts are landspeed +1 when moving from here.");
        setLocationDarkSideGameText("Unless opponent's matching operative is at same or adjacent site, those sites are immune to No Escape.");
        setGameText("Light: May not deploy to Bespin, Coruscant, Hoth, Kessel or Tatooine. Non-Jedi scouts are landspeed +1 when moving from here. Dark: Unless opponent's matching operative is at same or adjacent site, those sites are immune to No Escape.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Hoth, Title.Kessel, Title.Tatooine);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandspeedModifier(self, Filters.and(Filters.scout, Filters.not(Filters.Jedi), Filters.at(self)), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.or(self, Filters.adjacentSite(self)),
                new CantSpotCondition(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Keyword.OPERATIVE, Filters.at(Filters.or(self, Filters.adjacentSite(self))))),
                Title.No_Escape));
        return modifiers;
    }
}
