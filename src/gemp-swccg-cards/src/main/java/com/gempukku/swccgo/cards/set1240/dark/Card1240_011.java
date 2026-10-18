package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Character
 * Subtype: Imperial
 * Title: Myn Kyneugh
 */
public class Card1240_011 extends AbstractImperial {
    public Card1240_011() {
        super(Side.DARK, 4, 3, 4, 3, 5, "Myn Kyneugh", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Royal Guard leader. Remembers nothing of his past other than serving his Emperor. Early instructor of Kir Kanos and Carnor Jax.");
        setGameText("Heroic. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). Force Pike is a matching weapon for Myn, and, when using it, it fires for free and his total weapon destiny is +1. Immune to attrition < 4.");
        addIcons(Icon.WARRIOR, Icon.DEATH_STAR_II);
        addKeywords(Keyword.ROYAL_GUARD, Keyword.LEADER, Keyword.HEROIC);
        setMatchingWeaponFilter(Filters.Force_pike);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter forcePike = Filters.and(Filters.Force_pike, Filters.attachedTo(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        modifiers.add(new FiresForFreeModifier(self, forcePike));
        modifiers.add(new TotalWeaponDestinyModifier(self, forcePike, self, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
            if (startingEffect != null) {
                Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
                if (GameConditions.hasStackedCards(game, startingEffect, filter)) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Play a Defensive Shield");
                    action.appendTargeting(
                            new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    action.appendEffect(new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
