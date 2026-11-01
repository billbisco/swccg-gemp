package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: Ralltiir: Imperial Supply Route
 */
public class Card1113_024 extends AbstractSite {
    public Card1113_024() {
        super(Side.DARK, "Ralltiir: Imperial Supply Route", Title.Ralltiir, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("If you control, opponent's Imperial Supply Route game text is canceled.");
        setLocationDarkSideGameText("Place all devices (and Effects) on your characters just lost from table in owner's Used Pile.");
        setGameText("Light: If you control, opponent's Imperial Supply Route game text is canceled. Dark: Place all devices (and Effects) on your characters just lost from table in owner's Used Pile.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self,
                Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.title("Ralltiir: Imperial Supply Route")),
                new ControlsCondition(playerOnLightSideOfLocation, self)));
        return modifiers;
    }
}
