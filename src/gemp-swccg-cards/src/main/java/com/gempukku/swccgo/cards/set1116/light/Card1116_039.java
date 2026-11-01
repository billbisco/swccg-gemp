package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Location
 * Subtype: Site
 * Title: Yavin 4: Jedi Academy
 */
public class Card1116_039 extends AbstractSite {
    public Card1116_039() {
        super(Side.LIGHT, "Yavin 4: Jedi Academy", Title.Yavin_4, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setLocationLightSideGameText("If a Dark Jedi on table, Force drain -1 here. Once per game, may deploy Jedi Luke here from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("While opponent's non-[Episode I] Jedi here, destiny draws for Sense and Alter may not be modified.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        return Collections.<Modifier>singletonList(new ForceDrainModifier(self, new OnTableCondition(self, Filters.Dark_Jedi), -1, playerOnLightSideOfLocation));
    }
}
