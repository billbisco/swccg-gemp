package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Rebel
 * Title: Bail Organa
 */
public class Card1116_001 extends AbstractRebel {
    public Card1116_001() {
        super(Side.LIGHT, 3, 2, 3, 3, 5, "Bail Organa", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setPolitics(2);
        setLore("Senator from Alderaan. A founder of the Rebel Alliance.");
        setGameText("Agenda: rebellion. Adds one battle destiny with Leia. Your [Coruscant] senators and [Coruscant] Political Effects are lost. While at Senate: unique Rebels of ability < 4 are power +2, and, if opponent just lost Force to a Force drain by a Rebel, may retrieve 1 Force.");
        addIcons(Icon.SPECIAL_EDITION);
        addPersona(Persona.BAIL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.REBELLION));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.Leia), 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.unique, Filters.Rebel, Filters.abilityLessThan(4)),
                new AtCondition(self, Filters.titleContains("Senate")), 2));
        return modifiers;
    }
}
