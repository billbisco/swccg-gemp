package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.LandsFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsLandedToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TakesOffFreeFromLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Location
 * Subtype: Site
 * Title: Rebel Cell - Hidden Landing Site
 */
public class Card1115_022 extends AbstractSite {
    public Card1115_022() {
        super(Side.LIGHT, "Rebel Cell - Hidden Landing Site", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setLocationLightSideGameText("Your starfighters may deploy here (for -1 Force), and take off and land here for free. Incom Corporation and Koensayr Manufacturing may deploy here.");
        setLocationDarkSideGameText("May not deploy to Bespin, Coruscant, Dagobah, Dantooine, Endor, Hoth, or Subjugated Planet. Rebel Cell sites are immune to Revolution and No Escape. Limit one copy on table.");
        setGameText("Light: Your starfighters may deploy here (for -1 Force), and take off and land here for free. Incom Corporation and Koensayr Manufacturing may deploy here. Dark: May not deploy to Bespin, Coruscant, Dagobah, Dantooine, Endor, Hoth, or Subjugated Planet. Rebel Cell sites are immune to Revolution and No Escape. Limit one copy on table.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.TATOOINE);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Dagobah, Title.Dantooine, Title.Endor, Title.Hoth);
    }

    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        if (super.mayNotBePartOfSystem(game, system)) {
            return true;
        }
        String subjugatedPlanet = game.getGameState().getSubjugatedPlanet();
        return subjugatedPlanet != null && subjugatedPlanet.equals(system);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsLandedToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter), self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter), -1, self, true));
        modifiers.add(new TakesOffFreeFromLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter), self));
        modifiers.add(new LandsFreeToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter), self));
        modifiers.add(new MayDeployToTargetModifier(self, Filters.or(Filters.title("Incom Corporation"), Filters.title("Koensayr Manufacturing")), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.sameTitle(self), playerOnDarkSideOfLocation));
        modifiers.add(new MayNotDeployModifier(self, Filters.sameTitle(self), opponent));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.titleContains("Rebel Cell"), Title.Revolution));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.titleContains("Rebel Cell"), Title.No_Escape));
        return modifiers;
    }
}
