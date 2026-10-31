package com.gempukku.swccgo.cards.set1110.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10: Fourth Anthology
 * Type: Character
 * Subtype: Imperial
 * Title: Veers
 */
public class Card1110_014 extends AbstractImperial {
    public Card1110_014() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Veers", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_10, Rarity.V);
        setVirtualSuffix(true);
        setLore("General of the AT-AT assault armor division sent by Darth Vader to crush the Rebellion on Hoth. Cold and ruthless.");
        setGameText("Adds 4 to power of anything he pilots. Deploys -2 to Hoth. Your combat vehicles are defense value +1 at same and related locations. While on Hoth, your AT-ATs and Imperials are deploy -1 and forfeit +1 at related locations.");
        addPersona(Persona.VEERS);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Hoth_location));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onHoth = new OnCondition(self, Title.Hoth);
        Filter atatsAndImperialsAtRelated = Filters.and(Filters.your(self), Filters.or(Filters.AT_AT, Filters.Imperial),
                Filters.at(Filters.relatedLocation(self)));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 4));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Filters.combat_vehicle, Filters.atSameOrRelatedLocation(self)), 1));
        modifiers.add(new DeployCostModifier(self, atatsAndImperialsAtRelated, onHoth, -1));
        modifiers.add(new ForfeitModifier(self, atatsAndImperialsAtRelated, onHoth, 1));
        return modifiers;
    }
}
