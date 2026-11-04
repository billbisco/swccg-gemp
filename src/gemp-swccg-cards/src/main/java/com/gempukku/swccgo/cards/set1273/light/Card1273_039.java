package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
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
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Lower Levels
 */
public class Card1273_039 extends AbstractSite {
    public Card1273_039() {
        super(Side.LIGHT, "Coruscant: Lower Levels", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLocationLightSideGameText("If your Insurgent present, Force drain +1 here.");
        setLocationDarkSideGameText("Characters of ability > 4 are defense value -1 here.");
        setGameText("Light: If your Insurgent present, Force drain +1 here. Dark: Characters of ability > 4 are defense value -1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new PresentCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Keyword.INSURGENT)), 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.character, Filters.abilityMoreThan(4), Filters.here(self)), -1));
        return modifiers;
    }
}
