package com.gempukku.swccgo.cards.set1117.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Character
 * Subtype: Sith
 * Title: Galen, Secret Apprentice
 */
public class Card1117_013 extends AbstractSith {
    public Card1117_013() {
        super(Side.DARK, 1, 5, 6, 5, 7, "Galen, Secret Apprentice", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("");
        setGameText("Galen's forfeit may not be reduced. Power and defense value +2 while with Fallen Jedi. If defending a battle alone (or defending with Black Leader), may be targeted by Force Lightning. Immune to Clash Of Sabers and attrition < 4.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR);
        addPersona(Persona.GALEN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.Galen));
        modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.title("Fallen Jedi")), 2));
        modifiers.add(new DefenseValueModifier(self, new WithCondition(self, Filters.title("Fallen Jedi")), 2));
        modifiers.add(new MayBeTargetedByModifier(self, self,
                new AndCondition(new DefendingBattleCondition(self), new OrCondition(new AloneCondition(self), new WithCondition(self, Filters.or(Filters.Juno, Filters.title("Black Leader"))))),
                Title.Force_Lightning));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Clash_Of_Sabers));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
