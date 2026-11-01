package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Republic
 * Title: Clone Trooper
 */
public class Card1116_009 extends AbstractRepublic {
    public Card1116_009() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Clone Trooper", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setArmor(4);
        setGameText("Heroic. Power and forfeit +1 on Coruscant (+2 if also at same site as a Jedi or leader). While [Virtual Set 16] They Will Be Lost And Confused on table, Palpatine is immune to attrition and your Force drains at this Clone Trooper's site are +1.");
        addKeywords(Keyword.HEROIC, Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        AndCondition onCoruscantWithLeader = new AndCondition(new AtCondition(self, Filters.Coruscant_site),
                new WithCondition(self, Filters.or(Filters.Jedi, Filters.leader)));
        Filter objective = Filters.and(Filters.title("They Will Be Lost And Confused"), new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_16;
            }
        });
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Coruscant_site), 1));
        modifiers.add(new ForfeitModifier(self, new AtCondition(self, Filters.Coruscant_site), 1));
        modifiers.add(new PowerModifier(self, onCoruscantWithLeader, 1));
        modifiers.add(new ForfeitModifier(self, onCoruscantWithLeader, 1));
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.titleContains("Palpatine"), new OnTableCondition(self, objective)));
        return modifiers;
    }
}
