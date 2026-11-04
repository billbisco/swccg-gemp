package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractNewRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: New Republic
 * Title: Anakin Solo
 */
public class Card1282_012 extends AbstractNewRepublic {
    public Card1282_012() {
        super(Side.LIGHT, 2, 3, 2, 4, 4, "Anakin Solo", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setLore("Padawan.");
        setGameText("Power +2 while armed with a lightsaber. May use any 'stolen' lightsaber. Once per turn, where present, may steal an opponent's just-lost lightsaber. Immune to attrition < 3 (< 5 while armed with a 'stolen' lightsaber).");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.PADAWAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new ArmedWithCondition(self, Filters.lightsaber), 2));
        modifiers.add(new MayUseWeaponModifier(self, Filters.and(Filters.stolen, Filters.lightsaber)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5,
                new ArmedWithCondition(self, Filters.and(Filters.stolen, Filters.lightsaber)))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            return null;
        }
        Filter opponentsLightsaberInLostPile = Filters.and(Filters.lightsaber, Filters.inLostPile(game.getOpponent(playerId)));
        Set<PhysicalCard> lightsabers = new LinkedHashSet<PhysicalCard>();

        // An opponent's lightsaber just lost from where Anakin is present
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.lightsaber), Filters.wherePresent(self))) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            lightsabers.addAll(Filters.filter(Collections.singletonList(justLostCard), game, self, TargetingReason.TO_BE_STOLEN, opponentsLightsaberInLostPile));
        }
        // A lightsaber lost along with an opponent's character where Anakin is present
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.wherePresent(self))) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            lightsabers.addAll(Filters.filter(justLostCard.getCardsPreviouslyAttached(), game, self, TargetingReason.TO_BE_STOLEN, opponentsLightsaberInLostPile));
        }
        if (lightsabers.isEmpty()) {
            return null;
        }

        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        for (PhysicalCard cardToSteal : lightsabers) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Steal " + GameUtils.getFullName(cardToSteal));
            action.setActionMsg("Steal " + GameUtils.getCardLink(cardToSteal));
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new StealCardAndAttachFromLostPileEffect(action, playerId, self, Filters.sameCardId(cardToSteal)));
            actions.add(action);
        }
        return actions;
    }
}
