package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Republic
 * Title: Voolvif Monn
 */
public class Card1291_048 extends AbstractRepublic {
    public Card1291_048() {
        super(Side.LIGHT, 2, 6, 4, 6, 7, "Voolvif Monn", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Shistavanen.");
        setGameText("May deploy -2 to Geonosis or Muunilinst as a 'react.' During battle, may lose 2 Force (or use 3 Force) to cancel any just drawn weapon destiny targeting Voolvif. During battle, unless another Jedi here, Voolvif is power +2 and immune to attrition < 5.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.CLONE_ARMY);
        setSpecies(Species.SHISTAVANEN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.or(Filters.Geonosis_location, Filters.partOfSystem(Title.Muunilinst)), -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition duringBattleUnlessAnotherJediHere = new AndCondition(new DuringBattleCondition(),
                new NotCondition(new HereCondition(self, Filters.and(Filters.other(self), Filters.Jedi))));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, duringBattleUnlessAnotherJediHere, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, duringBattleUnlessAnotherJediHere, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__VOOLVIF_MONN__CANCEL_WEAPON_DESTINY;
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, self)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canCancelDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction loseForceAction = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            loseForceAction.setText("Lose 2 Force to cancel weapon destiny");
            // Pay cost(s)
            loseForceAction.appendCost(
                    new LoseForceEffect(loseForceAction, playerId, 2, true));
            // Perform result(s)
            loseForceAction.appendEffect(
                    new CancelDestinyEffect(loseForceAction));
            actions.add(loseForceAction);

            if (GameConditions.canUseForce(game, playerId, 3)) {
                final OptionalGameTextTriggerAction useForceAction = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                useForceAction.setText("Use 3 Force to cancel weapon destiny");
                // Pay cost(s)
                useForceAction.appendCost(
                        new UseForceEffect(useForceAction, playerId, 3));
                // Perform result(s)
                useForceAction.appendEffect(
                        new CancelDestinyEffect(useForceAction));
                actions.add(useForceAction);
            }
        }
        return actions.isEmpty() ? null : actions;
    }
}
