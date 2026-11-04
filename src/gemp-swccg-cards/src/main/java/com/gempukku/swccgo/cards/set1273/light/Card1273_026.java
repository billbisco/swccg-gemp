package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeUsedToSatisfyAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, Rebel Hero
 */
public class Card1273_026 extends AbstractRebel {
    public Card1273_026() {
        super(Side.LIGHT, 1, 4, 4, 4, 6, "Luke Skywalker, Rebel Hero", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLore("Rogue Squadron pilot.");
        setGameText("Adds 3 to the power of anything he pilots. When deployed, may deploy from Reserve Deck (or lose 1 Force to deploy from Lost Pile) a blaster on Luke; reshuffle. While armed with a blaster, it may not be stolen and characters hit by Luke may not be used to satisfy attrition. Immune to attrition < 4.");
        addPersona(Persona.LUKE);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ROGUE_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new MayNotBeStolenModifier(self, Filters.and(Filters.blaster, Filters.attachedTo(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LUKE_SKYWALKER_REBEL_HERO__DEPLOY_BLASTER;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final Filter blaster = Filters.blaster;
            final Filter onLuke = Filters.sameCardId(self);
            final boolean fromReserve = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId);
            final boolean fromLost = GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId);
            if (fromReserve || fromLost) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy a blaster on Luke");
                action.setActionMsg("Deploy a blaster on " + GameUtils.getCardLink(self));
                // Perform result(s)
                if (fromReserve && fromLost) {
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, playerId,
                                    new MultipleChoiceAwaitingDecision("Choose where to deploy a blaster from", new String[]{"Reserve Deck", "Lost Pile (lose 1 Force)"}) {
                                        @Override
                                        protected void validDecisionMade(int index, String result) {
                                            if (index == 0) {
                                                action.appendEffect(
                                                        new DeployCardToTargetFromReserveDeckEffect(action, blaster, onLuke, true));
                                            }
                                            else {
                                                action.appendEffect(
                                                        new LoseForceEffect(action, playerId, 1, true));
                                                action.appendEffect(
                                                        new DeployCardToTargetFromLostPileEffect(action, blaster, onLuke, true));
                                            }
                                        }
                                    }));
                }
                else if (fromReserve) {
                    action.appendEffect(
                            new DeployCardToTargetFromReserveDeckEffect(action, blaster, onLuke, true));
                }
                else {
                    action.appendEffect(
                            new LoseForceEffect(action, playerId, 1, true));
                    action.appendEffect(
                            new DeployCardToTargetFromLostPileEffect(action, blaster, onLuke, true));
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Characters hit by Luke (while he is armed with a blaster) may not be used to satisfy attrition
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.any, Filters.sameCardId(self))
                && GameConditions.isArmedWith(game, self, Filters.blaster)) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            Condition armedWithBlaster = new ArmedWithCondition(self, Filters.blaster);

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardHit) + " unable to satisfy attrition");
            action.setActionMsg(GameUtils.getCardLink(cardHit) + " may not be used to satisfy attrition");
            action.skipInitialMessageAndAnimation();
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotBeUsedToSatisfyAttritionModifier(self, Filters.sameCardId(cardHit), armedWithBlaster), null));
            return Collections.singletonList(action);
        }
        return null;
    }
}
