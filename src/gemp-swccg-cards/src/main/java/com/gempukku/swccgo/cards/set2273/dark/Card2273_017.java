package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.common.LegacyVersion;
import java.time.LocalDate;
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

/** Original Coruscant: Palpatine's Quarters (Virtual Block 7.3 sheet, 2011-06-03), a version of 1273_17: "Cyborg Commander" (Grievous, Hunter Of Jedi's printed title on the 7.1 sheet, that card only) is immune to attrition; replaced by the Virtual Cards Master List text (PC-E1, 2013-04-27: "Grievous", any Grievous). Anything Goes only (docs/legacy-versions.md). */
public class Card2273_017 extends AbstractSite {
    public Card2273_017() {
        super(Side.DARK, "Coruscant: Palpatine's Quarters", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLocationDarkSideGameText("While Insidious Prisoner on table, Cyborg Commander is immune to attrition.");
        setLocationLightSideGameText("Force drain -1 here. Jedi here may not have their power reduced.");
        setLegacyVersion(new LegacyVersion("1273_17", "",
                "Virtual Block 7.3 release sheet (2011-06-03)", LocalDate.of(2011, 6, 3), LocalDate.of(2013, 4, 27)));
        setGameText("Dark: While Insidious Prisoner on table, Cyborg Commander is immune to attrition. Light: Force drain -1 here. Jedi here may not have their power reduced.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EPISODE_I, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        // Cyborg Commander: Grievous, Hunter Of Jedi's printed title on the Virtual Block 7.1 sheet (that card only)
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.title("Grievous, Hunter Of Jedi"), new OnTableCondition(self, Filters.Insidious_Prisoner)));
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
