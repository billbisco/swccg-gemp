package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployOnlyUsingOwnForceToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Jawa Camp (V)
 */
public class Card1281_042 extends AbstractSite {
    public Card1281_042() {
        super(Side.LIGHT, Title.Jawa_Camp, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Your non-unique Jawas deploy for 1 Force from you only. Your deserts gain one [Light Side Force].");
        setLocationDarkSideGameText("If you control, opponent's Jawas and Sandcrawlers are power and forfeit -1.");
        setGameText("Light: Your non-unique Jawas deploy for 1 Force from you only. Your deserts gain one [Light Side Force]. Dark: If you control, opponent's Jawas and Sandcrawlers are power and forfeit -1.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Filter yourNonUniqueJawas = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.non_unique, Filters.Jawa);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetDeployCostModifier(self, yourNonUniqueJawas, 1));
        modifiers.add(new DeployOnlyUsingOwnForceToLocationModifier(self, yourNonUniqueJawas, Filters.location));
        modifiers.add(new IconModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.desert), Icon.LIGHT_FORCE));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter opponentsJawasAndSandcrawlers = Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.or(Filters.Jawa, Filters.sandcrawler));
        Condition youControl = new ControlsCondition(playerOnDarkSideOfLocation, self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, opponentsJawasAndSandcrawlers, youControl, -1));
        modifiers.add(new ForfeitModifier(self, opponentsJawasAndSandcrawlers, youControl, -1));
        return modifiers;
    }
}
