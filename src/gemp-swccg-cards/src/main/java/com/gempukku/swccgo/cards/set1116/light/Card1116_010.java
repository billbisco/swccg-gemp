package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.MayMakeAdditionalMovesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Republic
 * Title: Supreme Chancellor Palpatine
 */
public class Card1116_010 extends AbstractRepublic {
    public Card1116_010() {
        super(Side.LIGHT, 0, 4, 2, 4, 6, "Supreme Chancellor Palpatine", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setPolitics(5);
        setGameText("Agendas: ambition, order. While on Coruscant, Ambush is a Used Interrupt and, each time you initiate a Force drain with a clone, may draw top card from Reserve Deck. During your turn, may make an additional regular move. Valorum is lost.");
        addIcons(Icon.CORUSCANT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.AMBITION));
        modifiers.add(new AgendaModifier(self, Agenda.ORDER));
        modifiers.add(new MayMakeAdditionalMovesModifier(self, self, new AtCondition(self, Filters.or(Filters.Coruscant_site, Filters.Coruscant_system)), 1));
        return modifiers;
    }
}
