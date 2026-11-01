package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DuelTotalModifier;
import com.gempukku.swccgo.logic.modifiers.LightsaberCombatTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Character
 * Subtype: Jedi Master
 * Title: Jedi Advisor
 */
public class Card1117_007 extends AbstractJediMaster {
    public Card1117_007() {
        super(Side.LIGHT, 2, 3, 4, 7, 6, "Jedi Advisor", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("Many of the Alliance's generals and commanders are from Alderaan. Not all of Alderaan's population are active members of the Rebellion, but most support the cause.");
        setGameText("Never deploys, except to Coruscant. While Yoda here, Force retrieval from your [Virtual Set 17] objective cannot be canceled (except by Secret Plans). Unless Inner Strength on table, your duel destiny and lightsaber combat totals are cumulatively +1.");
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_at_Coruscant;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition unlessInnerStrength = new NotCondition(new OnTableCondition(self, Filters.title(Title.Inner_Strength)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DuelTotalModifier(self, Filters.your(self), unlessInnerStrength, 1));
        modifiers.add(new LightsaberCombatTotalModifier(self, Filters.your(self), unlessInnerStrength, new com.gempukku.swccgo.logic.evaluators.ConstantEvaluator(1)));
        return modifiers;
    }
}
