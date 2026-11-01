package com.gempukku.swccgo.cards.set1115.light;

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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Location
 * Subtype: Site
 * Title: Dantooine: Base - Operations Center
 */
public class Card1115_009 extends AbstractSite {
    public Card1115_009() {
        super(Side.LIGHT, "Dantooine: Base - Operations Center", Title.Dantooine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Your T-47s may move as a 'react' to Dantooine sites. Rebels are deploy -1 here (-2 if a leader). Immune to Revolution.");
        setLocationDarkSideGameText("Your characters are deploy +1 here. While More Dangerous Than You Realize on table, you must control three Dantooine locations to flip it back, and Dantooine system gains one [Dark Side Force].");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.SPECIAL_EDITION, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addImmuneToCardTitle(Title.Revolution);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character), 1, self));
        modifiers.add(new IconModifier(self, Filters.Dantooine_system, new OnTableCondition(self, Filters.More_Dangerous_Than_You_Realize), Icon.DARK_FORCE, 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.More_Dangerous_Than_You_Realize, ModifyGameTextType.LEGACY__MORE_DANGEROUS_THAN_YOU_REALIZE__REQUIRES_THREE_SITES_TO_FLIP_BACK));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move a T-47 as a react", playerOnLightSideOfLocation, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.T_47), Filters.Dantooine_site));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Rebel, Filters.not(Filters.leader)), -1, self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Rebel, Filters.leader), -2, self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }
}
