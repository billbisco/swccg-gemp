package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Droid
 * Title: B2 Super Battle Droid
 */
public class Card1291_001 extends AbstractDroid {
    public Card1291_001() {
        super(Side.DARK, 3, 3, 4, 4, "B2 Super Battle Droid", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setArmor(4);
        setGameText("Deploys +1 if In Complete Control on table. Draws one battle destiny if unable to otherwise. Adds 1 to attrition against opponent here (2 if with another [Separatist] card). May fire any [Episode I] weapon for free.");
        addIcons(Icon.WARRIOR, Icon.SEPARATIST, Icon.PRESENCE, Icon.EPISODE_I);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.title(Title.In_Complete_Control)), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new AttritionModifier(self, Filters.sameLocation(self),
                new ConditionEvaluator(1, 2, new WithCondition(self, Filters.and(Filters.other(self), Icon.SEPARATIST))), opponent));
        modifiers.add(new FireWeaponFiredByForFreeModifier(self, Icon.EPISODE_I));
        return modifiers;
    }
}
