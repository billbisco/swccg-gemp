package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.LegacyVb73Light;
import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DestinyDrawForActionSourceEvaluatorModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Main Power Plant
 */
public class Card1273_040 extends AbstractSite {
    public static final String TITLE = "Coruscant: Main Power Plant";

    public Card1273_040() {
        super(Side.LIGHT, TITLE, Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLocationLightSideGameText("Attempts to 'disable' the Planetary Shield are +2 for each Rogue Squadron pilot here.");
        setLocationDarkSideGameText("While Center Of Tyranny on table, you generate no Force here.");
        setGameText("Light: Attempts to 'disable' the Planetary Shield are +2 for each Rogue Squadron pilot here. Dark: While Center Of Tyranny on table, you generate no Force here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyDrawForActionSourceEvaluatorModifier(self, LegacyVb73Light.planetaryShield(), null,
                new MultiplyEvaluator(2, new HereEvaluator(self, Filters.Rogue_Squadron_pilot)), playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new GenerateNoForceModifier(self, self, new OnTableCondition(self, LegacyVb73Light.centerOfTyranny()), playerOnDarkSideOfLocation));
        return modifiers;
    }
}
