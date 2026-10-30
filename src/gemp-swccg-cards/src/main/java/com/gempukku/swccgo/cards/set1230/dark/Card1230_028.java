package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AllAbilityOnTableProvidedByCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Empire's New Order
 */
public class Card1230_028 extends AbstractNormalEffect {
    public Card1230_028() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Empires_New_Order, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Palpatine's cruel vision of his Empire included the enslavement and subjugation of entire species.");
        setGameText("Deploy on table. Whenever an opponent's alien is just lost, they lose 1 Force. While all of your ability on table is provided by Imperials and Imperial starship pilots, your admirals, generals, moffs, and Emperor are deploy -1 and are forfeit +1. Aliens are deploy +1 and forfeit -1.");
        addIcons(Icon.ENDOR);
    }

    private Filter abilityProviders() {
        return Filters.or(Filters.Imperial, Filters.piloting(Filters.and(Filters.starship, Icon.IMPERIAL)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition allAbility = new AllAbilityOnTableProvidedByCondition(self, playerId, abilityProviders());
        Filter admiralsGeneralsMoffsEmperor = Filters.and(Filters.your(self), Filters.or(Keyword.ADMIRAL, Keyword.GENERAL, Keyword.MOFF, Filters.Emperor));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, admiralsGeneralsMoffsEmperor, allAbility, -1));
        modifiers.add(new ForfeitModifier(self, admiralsGeneralsMoffsEmperor, allAbility, 1));
        modifiers.add(new DeployCostModifier(self, Filters.alien, allAbility, 1));
        modifiers.add(new ForfeitModifier(self, Filters.alien, allAbility, -1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.opponents(self), Filters.alien))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
