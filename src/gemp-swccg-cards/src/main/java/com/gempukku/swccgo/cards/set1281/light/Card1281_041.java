package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Location
 * Subtype: Site
 * Title: Nar Shaddaa: Undercity
 */
public class Card1281_041 extends AbstractSite {
    public Card1281_041() {
        super(Side.LIGHT, "Nar Shaddaa: Undercity", Title.Nar_Shaddaa, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLocationLightSideGameText("Scoundrel's Guild is immune to No Escape and your force generation there is +1.");
        setLocationDarkSideGameText("Bounty Hunters are power +1 while on Nar Shaddaa.");
        setGameText("Light: Scoundrel's Guild is immune to No Escape and your force generation there is +1. Dark: Bounty Hunters are power +1 while on Nar Shaddaa.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.UNDERGROUND);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // The site is titled "Spaceport Scoundrels Guild" (no apostrophe); the card text says "Scoundrel's Guild".
        Filter scoundrelsGuild = Filters.or(Filters.titleContains("Scoundrels Guild"), Filters.titleContains("Scoundrel's Guild"));
        modifiers.add(new ImmuneToTitleModifier(self, scoundrelsGuild, Title.No_Escape));
        modifiers.add(new ForceGenerationModifier(self, scoundrelsGuild, 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.bounty_hunter, Filters.on(Title.Nar_Shaddaa)), 1));
        return modifiers;
    }
}
