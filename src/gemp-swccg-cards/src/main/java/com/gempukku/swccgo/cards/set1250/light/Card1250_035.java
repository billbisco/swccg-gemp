package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Republic
 * Title: Clone Pilot
 */
public class Card1250_035 extends AbstractRepublic {
    public Card1250_035() {
        super(Side.LIGHT, 3, 2, 1, 2, 4, "Clone Pilot", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setArmor(3);
        setGameText("Adds 2 to power of anything he pilots. While piloting a [Republic] starship at a battleground, forfeit +1 and your Force drains are +1 here. Anything he pilots is maneuver +1 and immune to attrition < 3 (< 5 if all your pilot characters here are clones and/or Jedi).");
        addIcons(Icon.PILOT, Icon.EPISODE_I);
        addKeyword(Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition republicAtBattleground = new AndCondition(
                new PilotingCondition(self, Filters.and(Filters.starship, Icon.REPUBLIC)),
                new PilotingCondition(self, Filters.at(Filters.battleground)));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForfeitModifier(self, republicAtBattleground, 1));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), republicAtBattleground, 1, self.getOwner()));
        Condition onlyClonesAndJedi = new NotCondition(new OnTableCondition(self,
                Filters.and(Filters.your(self), Filters.character, Icon.PILOT, Filters.here(self),
                        Filters.not(Filters.or(Filters.clone, Filters.Jedi)))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.hasPiloting(self), null,
                new ConditionEvaluator(3, 5, onlyClonesAndJedi)));
        return modifiers;
    }
}
