package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Imperial
 * Title: Sim Aloo
 */
public class Card1230_018 extends AbstractImperial {
    public Card1230_018() {
        super(Side.DARK, 1, 4, 3, 4, 6, "Sim Aloo", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setInfluence(3);
        setLore("Imperial council member. Senior political advisor to Emperor Palpatine. Never speaks. Like his master, shows remarkable patience and wisdom.");
        setGameText("Agendas: law, rule. While at Conference Room, adds one [Dark Side Force]. While with another Imperial Council Member, adds one battle destiny. Immune to attrition < 4.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.IMPERIAL_COUNCIL_MEMBER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.LAW, Agenda.RULE));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.Death_Star_Conference_Room, new AtCondition(self, Filters.Death_Star_Conference_Room), Icon.DARK_FORCE, 1));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Keyword.IMPERIAL_COUNCIL_MEMBER), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
