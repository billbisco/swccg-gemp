package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Character
 * Subtype: Republic
 * Title: Jedi Guardian
 */
public class Card1117_008 extends AbstractRepublic {
    public Card1117_008() {
        super(Side.LIGHT, 1, 5, 5, 6, 6, "Jedi Guardian", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("");
        setGameText("Deploys +3 unless your [Virtual Set 17] objective on table. Whenever this Jedi uses a lightsaber to hit a character, opponent loses 1 Force. Immune to attrition < 4.");
        addIcons(Icon.DAGOBAH, Icon.EPISODE_I, Icon.WARRIOR);
    }

    private com.gempukku.swccgo.filters.Filter yourObjective(final PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.Objective, new com.gempukku.swccgo.filters.Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_17;
            }
        });
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new NotCondition(new OnTableCondition(self, yourObjective(self))), 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.lightsaber, Filters.sameCardId(self))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            action.appendEffect(new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
