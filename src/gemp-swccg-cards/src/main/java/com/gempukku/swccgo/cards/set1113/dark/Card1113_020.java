package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: Kessel: Spice Mines - Docking Bay
 */
public class Card1113_020 extends AbstractSite {
    public Card1113_020() {
        super(Side.DARK, "Kessel: Spice Mines - Docking Bay", Title.Kessel, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Your docking bay transit from here requires 3 Force (free if your smuggler present).");
        setLocationDarkSideGameText("Your docking bay transit from here requires 1 Force (free if your smuggler present).");
        setGameText("Light: Your docking bay transit from here requires 3 Force (free if your smuggler present). Dark: Your docking bay transit from here requires 1 Force (free if your smuggler present).");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.DOCKING_BAY);
    }

    private Condition yourSmugglerPresent(final PhysicalCard self, final String playerId) {
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return Filters.canSpot(gameState.getGame(), self, Filters.and(Filters.your(playerId), Keyword.SMUGGLER, Filters.present(self)));
            }
        };
    }

    private void addTransit(List<Modifier> modifiers, PhysicalCard self, String playerId, int cost) {
        Condition smuggler = yourSmugglerPresent(self, playerId);
        modifiers.add(new DockingBayTransitFromCostModifier(self, new NotCondition(smuggler), cost, playerId));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, smuggler, playerId));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        addTransit(modifiers, self, playerOnLightSideOfLocation, 3);
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        addTransit(modifiers, self, playerOnDarkSideOfLocation, 1);
        return modifiers;
    }
}
