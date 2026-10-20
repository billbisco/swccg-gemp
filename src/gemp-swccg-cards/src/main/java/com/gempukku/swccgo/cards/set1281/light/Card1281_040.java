package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationUsingLandspeedModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Location
 * Subtype: Site
 * Title: Nar Shaddaa: Undercity Street
 */
public class Card1281_040 extends AbstractSite {
    public Card1281_040() {
        super(Side.LIGHT, "Nar Shaddaa: Undercity Street", Title.Nar_Shaddaa, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLocationLightSideGameText("Scoundrels may move to or from here using landspeed for free.");
        setLocationDarkSideGameText("Force drain -1 here unless your assassin, bounty hunter, or thief present.");
        setGameText("Light: Scoundrels may move to or from here using landspeed for free. Dark: Force drain -1 here unless your assassin, bounty hunter, or thief present.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.UNDERGROUND);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationUsingLandspeedModifier(self, Filters.scoundrel, self));
        modifiers.add(new MovesFreeFromLocationUsingLandspeedModifier(self, Filters.scoundrel, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self,
                new UnlessCondition(new PresentCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                        Filters.or(Filters.assassin, Filters.bounty_hunter, Filters.thief)))),
                -1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
