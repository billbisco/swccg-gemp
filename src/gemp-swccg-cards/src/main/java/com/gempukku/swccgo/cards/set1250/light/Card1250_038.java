package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.List;

/** Legacy Block 5 Clone Trooper, final 38E. */
public class Card1250_038 extends AbstractRepublic {
    public Card1250_038() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Clone Trooper", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setArmor(4);
        setGameText("While with Palpatine, Heroic. Power and forfeit +1 on Coruscant. While [Virtual Block 5] They Will Be Lost And Confused on table, Palpatine is immune to attrition < 6 and your Force drains at same site are +1.");
        addIcons(Icon.A_NEW_HOPE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition onCoruscant = (state, modifiers) -> Filters.On_Coruscant.accepts(state, modifiers, self);
        Condition objectiveBack = new OnTableCondition(self,
                Filters.and(Filters.Objective, Icon.LEGACY_BLOCK_5, Filters.title("They Will Be Lost And Confused")));
        return List.of(
                new KeywordModifier(self, new WithCondition(self, Filters.Palpatine), Keyword.HEROIC),
                new PowerModifier(self, onCoruscant, 1),
                new ForfeitModifier(self, onCoruscant, 1),
                new ImmuneToAttritionLessThanModifier(self, Filters.Palpatine, objectiveBack, 6),
                new ForceDrainModifier(self, Filters.sameSite(self), objectiveBack, 1, self.getOwner()));
    }
}
