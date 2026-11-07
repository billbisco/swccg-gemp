package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.DuelTotalModifier;
import com.gempukku.swccgo.logic.modifiers.LightsaberCombatTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 5 Jedi Advisor, printing 39. */
public class Card1250_039 extends AbstractJediMaster {
    public Card1250_039() {
        super(Side.LIGHT, 2, 3, 4, 7, 6, "Jedi Advisor", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLore("Female. Jedi Council Member.");
        setGameText("Never deploys, except to Coruscant. While Yoda here, Force retrieval from your [Virtual Block 5] objective cannot be canceled (except by Secret Plans). Unless Inner Strength on table, your duel destiny and lightsaber combat totals are cumulatively +1.");
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I);
        addKeywords(Keyword.FEMALE, Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.Deploys_at_Coruscant;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var unlessInnerStrength = new NotCondition(new OnTableCondition(self, Filters.title(Title.Inner_Strength)));
        return List.of(
                new DuelTotalModifier(self, Filters.your(self), unlessInnerStrength, 1) {
                    @Override public boolean isCumulative() { return true; }
                },
                new LightsaberCombatTotalModifier(self, Filters.your(self), unlessInnerStrength, new ConstantEvaluator(1)) {
                    @Override public boolean isCumulative() { return true; }
                });
    }
}
