package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Alien
 * Title: Yarua (V)
 */
public class Card1116_021 extends AbstractAlien {
    public Card1116_021() {
        super(Side.LIGHT, 2, 5, 6, 2, 4, "Yarua", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setPolitics(2);
        setSpecies(Species.WOOKIEE);
        setGameText("Agenda: taxation. Deploys -2 to Coruscant or Kashyyyk. At end of your deploy phase, if with a Wookiee on Kashyyyk and you have 8 or more cards in hand, opponent must lose 1 Force. Your Wookiees on Kashyyyk move for free.");
        addIcons(Icon.CORUSCANT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.TAXATION));
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.Coruscant_site, Filters.Kashyyyk_site)));
        modifiers.add(new MovesForFreeModifier(self, Filters.and(Filters.your(self), Filters.Wookiee, Filters.at(Filters.Kashyyyk_location))));
        return modifiers;
    }
}
