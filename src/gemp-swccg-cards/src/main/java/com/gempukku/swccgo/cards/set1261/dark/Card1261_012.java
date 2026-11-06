package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 Massassi Warrior. */
public class Card1261_012 extends AbstractSith {
    public Card1261_012() {
        super(Side.DARK, 2, 3, 3, 1, 4, "Massassi Warrior", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        addIcons(Icon.WARRIOR, Icon.SPECIAL_EDITION);
        setGameText("Deploys -1 to same site as a Dark Jedi. While with another Sith, draws one battle destiny if unable to otherwise and opponent's characters here are power -1. When lost from the same site as a Jedi, opponent loses 1 Force.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DeployCostToLocationModifier(self, -1,
                Filters.and(Filters.site, Filters.sameSiteAs(self, Filters.Dark_Jedi))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition withAnotherSith = new WithCondition(self, Filters.and(Filters.Sith, Filters.other(self)));
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, withAnotherSith, 1));
        modifiers.add(new PowerModifier(self,
                Filters.and(Filters.opponents(self), Filters.character, Filters.here(self)), withAnotherSith, -1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult,
            PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLostFromLocation(game, effectResult, self,
                Filters.and(Filters.site, Filters.sameSiteAs(self, Filters.Jedi)))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            action.appendEffect(new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
