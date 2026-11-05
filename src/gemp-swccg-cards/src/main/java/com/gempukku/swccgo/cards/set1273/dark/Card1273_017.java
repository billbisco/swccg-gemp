package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Palpatine's Quarters
 */
public class Card1273_017 extends AbstractSite {
    public Card1273_017() {
        super(Side.DARK, "Coruscant: Palpatine's Quarters", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLocationDarkSideGameText("While Insidious Prisoner on table, Cyborg Commander is immune to attrition.");
        setLocationLightSideGameText("Force drain -1 here. Jedi here may not have their power reduced.");
        setGameText("Dark: While Insidious Prisoner on table, Cyborg Commander is immune to attrition. Light: Force drain -1 here. Jedi here may not have their power reduced.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EPISODE_I, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        // Cyborg Commander is Grievous's printed title on the Virtual Block 7 sheets
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.Grievous, new OnTableCondition(self, Filters.Insidious_Prisoner)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter jediHere = Filters.and(Filters.Jedi, Filters.here(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, self, -1, playerOnLightSideOfLocation));
        modifiers.add(new MayNotHavePowerReducedModifier(self, jediHere, playerOnLightSideOfLocation));
        modifiers.add(new MayNotHavePowerReducedModifier(self, jediHere, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}
