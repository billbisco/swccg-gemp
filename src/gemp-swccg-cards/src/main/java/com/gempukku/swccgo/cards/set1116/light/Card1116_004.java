package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Rebel
 * Title: Senator Padme Amidala
 */
public class Card1116_004 extends AbstractRebel {
    public Card1116_004() {
        super(Side.LIGHT, 1, 3, 3, 4, 7, "Senator Padme Amidala", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setPolitics(4);
        setGameText("Agendas: justice, rebellion. Once per turn, if at Senate with Bail, may lose 1 Force to deploy a unique, non-[Maintenance] Rebel of ability < 4 from Lost Pile. If just lost from same site as Vader, place her out of play; opponent loses 4 Force.");
        addIcons(Icon.CORUSCANT, Icon.WARRIOR);
        addPersona(Persona.AMIDALA);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.JUSTICE));
        modifiers.add(new AgendaModifier(self, Agenda.REBELLION));
        return modifiers;
    }
}
