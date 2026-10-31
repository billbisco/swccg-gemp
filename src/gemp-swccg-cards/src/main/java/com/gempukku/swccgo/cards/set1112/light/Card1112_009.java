package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
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
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Location
 * Subtype: Site
 * Title: Endor: Ewok Village
 */
public class Card1112_009 extends AbstractSite {
    public Card1112_009() {
        super(Side.LIGHT, Title.Ewok_Village, Title.Endor, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Non-unique Ewoks are Heroic. While you occupy, gains one [Light Side] icon. No starships or vehicles here.");
        setLocationDarkSideGameText("No starships or vehicles here. If no Ewoks on Endor, Force drain +1 here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.PLANET, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.Ewok, Filters.not(Filters.unique)), Keyword.HEROIC));
        modifiers.add(new IconModifier(self, new OccupiesCondition(playerOnLightSideOfLocation, self), Icon.LIGHT_FORCE, 1));
        modifiers.add(new MayNotExistAtLocationModifier(self, Filters.or(Filters.starship, Filters.vehicle), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtLocationModifier(self, Filters.or(Filters.starship, Filters.vehicle), self));
        modifiers.add(new ForceDrainModifier(self, new CantSpotCondition(self, Filters.and(Filters.Ewok, Filters.on(Title.Endor))),
                1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
