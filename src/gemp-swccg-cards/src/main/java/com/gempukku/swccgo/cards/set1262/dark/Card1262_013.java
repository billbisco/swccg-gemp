package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.List;

/** Virtual Block 6.2: Cloud City: Dining Room (V). */
public class Card1262_013 extends AbstractSite {
    public Card1262_013() {
        super(Side.DARK, Title.Dining_Room, Title.Bespin, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Your characters and weapons may deploy here as a 'react.'");
        setLocationLightSideGameText("Your non-[Permanent weapon] blasters here fire for free.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6);
        addKeyword(Keyword.CLOUD_CITY_LOCATION);
        setAsLegacy(true);
    }
    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        return List.of(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy card as a 'react'",
                playerOnDarkSideOfLocation, Filters.or(Filters.character, Filters.weapon), self));
    }
    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        return List.of(new FiresForFreeModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.blaster, Filters.not(Icon.PERMANENT_WEAPON), Filters.atSameSite(self))));
    }
}
