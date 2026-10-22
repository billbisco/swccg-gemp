package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: System
 * Title: Muunilinst
 */
public class Card1291_020 extends AbstractSystem {
    public static final String SYSTEM_NAME = Title.Muunilinst;

    public Card1291_020() {
        super(Side.DARK, SYSTEM_NAME, 7, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationDarkSideGameText("If you control, attrition against opponent in battles on Muunilinst is +1.");
        setLocationLightSideGameText("If you control, attrition against opponent in battles on Muunilinst is +1.");
        setGameText("Dark: If you control, attrition against opponent in battles on Muunilinst is +1. Light: If you control, attrition against opponent in battles on Muunilinst is +1.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.PLANET, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.partOfSystem(SYSTEM_NAME),
                new ControlsCondition(playerOnDarkSideOfLocation, self), 1, opponent));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnLightSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.partOfSystem(SYSTEM_NAME),
                new ControlsCondition(playerOnLightSideOfLocation, self), 1, opponent));
        return modifiers;
    }
}
