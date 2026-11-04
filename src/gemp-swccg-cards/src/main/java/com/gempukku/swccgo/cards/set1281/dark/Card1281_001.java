package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeRetargetedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeUsedToSatisfyAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Republic
 * Title: Aurra Sing, Deadly Assassin
 */
public class Card1281_001 extends AbstractRepublic {
    public Card1281_001() {
        super(Side.DARK, 1, 4, 4, 4, 6, "Aurra Sing, Deadly Assassin", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("On her assignment to kill Sharad Hett, Aurra used her patience and cunning to help track down the Jedi Master.");
        setGameText("Once per game, may deploy a device on Aurra from Reserve Deck; reshuffle. While armed with a [Reflections III] rifle: it may not be retargeted or stolen, her total weapon destiny is +1, and characters she hits may not be used to satisfy attrition. Immune to attrition < 3 (< 4 while armed with a rifle).");
        addPersona(Persona.AURRA);
        addIcons(Icon.WARRIOR, Icon.EPISODE_I);
        addKeywords(Keyword.FEMALE);
    }

    private static Filter reflectionsIIIRifle() {
        return Filters.and(Icon.REFLECTIONS_III, Filters.rifle);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition armedWithRefIIIRifle = new ArmedWithCondition(self, reflectionsIIIRifle());
        Filter herRefIIIRifle = Filters.and(reflectionsIIIRifle(), Filters.attachedTo(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeRetargetedModifier(self, herRefIIIRifle, armedWithRefIIIRifle));
        modifiers.add(new MayNotBeStolenModifier(self, herRefIIIRifle, armedWithRefIIIRifle));
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self, new ConditionEvaluator(0, 1, armedWithRefIIIRifle), Filters.weapon));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 4, new ArmedWithCondition(self, Filters.rifle))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__AURRA_SING_DEADLY_ASSASSIN__DEPLOY_DEVICE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a device on Aurra from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.device, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.any, Filters.sameCardId(self))
                && GameConditions.isArmedWith(game, self, reflectionsIIIRifle())) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardHit) + " unable to satisfy attrition");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new MayNotBeUsedToSatisfyAttritionModifier(self, Filters.sameCardId(cardHit)),
                            GameUtils.getCardLink(cardHit) + " may not be used to satisfy attrition"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
