package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAttackModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Creature
 * Title: Thrawn's Pet
 */
public class Card1116_065 extends AbstractCreature {
    public Card1116_065() {
        super(Side.DARK, 4, 2, 3, 4, 0, "Thrawn's Pet", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Habitat: sites. Parasite: Imperials. While attached and present, at same site: all Force generation is canceled, Dark Jedi and Jedi are defense value -1 and their weapon destinies are -1, and no attacks.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        AttachedCondition attached = new AttachedCondition(self, Filters.Imperial);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.or(Filters.Dark_Jedi, Filters.Jedi), Filters.atSameSite(self)), attached, -1));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.attachedTo(Filters.and(Filters.or(Filters.Dark_Jedi, Filters.Jedi), Filters.atSameSite(self))), -1f, Filters.any));
        modifiers.add(new MayNotAttackModifier(self, attached, Filters.atSameSite(self)));
        return modifiers;
    }
}
