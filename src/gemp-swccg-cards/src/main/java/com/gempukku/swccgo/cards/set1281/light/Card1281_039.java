package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Location
 * Subtype: Site
 * Title: Nar Shaddaa: Scoundrel's Rest
 */
public class Card1281_039 extends AbstractSite {
    public Card1281_039() {
        super(Side.LIGHT, "Nar Shaddaa: Scoundrel's Rest", Title.Nar_Shaddaa, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLocationLightSideGameText("Characters (except scoundrels and bounty hunters) are deploy +1 to Nar Shaddaa.");
        setLocationDarkSideGameText("If you occupy, opponent's game text here is canceled.");
        setGameText("Light: Characters (except scoundrels and bounty hunters) are deploy +1 to Nar Shaddaa. Dark: If you occupy, opponent's game text here is canceled.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self,
                Filters.and(Filters.character, Filters.not(Filters.or(Filters.scoundrel, Filters.bounty_hunter))),
                1, Filters.partOfSystem(Title.Nar_Shaddaa)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, self,
                new OccupiesCondition(playerOnDarkSideOfLocation, self), opponent));
        return modifiers;
    }
}
