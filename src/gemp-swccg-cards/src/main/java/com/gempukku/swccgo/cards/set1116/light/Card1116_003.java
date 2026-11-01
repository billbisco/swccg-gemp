package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
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
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Rebel
 * Title: Senator Leia Organa
 */
public class Card1116_003 extends AbstractRebel {
    public Card1116_003() {
        super(Side.LIGHT, 2, 3, 3, 3, 5, "Senator Leia Organa", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setPolitics(1);
        setGameText("Agendas: justice, rebellion. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). While aboard Alderaan Consular Ship, it is immune to Lateral Damage and attrition. Attrition against opponent is +2 here.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addPersona(Persona.LEIA);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        AboardCondition aboardShip = new AboardCondition(self, Filters.title("Alderaan Consular Ship"));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.JUSTICE));
        modifiers.add(new AgendaModifier(self, Agenda.REBELLION));
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.title("Alderaan Consular Ship"), aboardShip));
        modifiers.add(new AttritionModifier(self, Filters.here(self), 2, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
