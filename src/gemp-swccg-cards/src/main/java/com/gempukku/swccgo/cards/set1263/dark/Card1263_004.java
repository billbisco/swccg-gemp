package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Subtype: Alien
 * Title: Grotto Werribee (V)
 */
public class Card1263_004 extends AbstractAlien {
    public Card1263_004() {
        super(Side.DARK, 1, 3, 2, 4, 4, "Grotto Werribee", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Once a junior traffic controller for an interstellar shipping corporation, Grotto brags that there is nothing about docking bay procedure that he doesn't know. Information broker.");
        setGameText("Adds 2 to power of anything he pilots. If opponent's [Virtual Block 6] card just added a destiny to total power or attrition here, draw two destiny, choose one, and subtract it from their attrition and total power. Your characters here are immune to Clash of Sabers and attrition <3.");
        addIcons(Icon.PILOT, Icon.EPISODE_I);
        addKeywords(Keyword.INFORMATION_BROKER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourCharactersHere = Filters.and(Filters.your(self), Filters.character, Filters.here(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToTitleModifier(self, yourCharactersHere, Title.Clash_Of_Sabers));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, yourCharactersHere, 3));
        return modifiers;
    }
}
