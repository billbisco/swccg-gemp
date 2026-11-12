package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.evaluators.CalculateCardVariableEvaluator;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Weapon
 * Title: Bowcaster
 * Text state: o (as printed, July 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Bowcaster]] and its Decipher archive scan (ANH-L-bowcaster-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Printed: a character is hit if the destiny beats its ability; a creature is not hit but immediately lost (two fire actions: character / creature).
 */
public class Card2_077o extends Card2_077 {
    public Card2_077o() {
        super();
        setGameText("Use X Force to deploy on any warrior where X = (7 - warrior's power). X must be at least 1. May target using X Force. Draw destiny. Character hit if total destiny +1 > ability. Creature immediately lost if destiny +2 > defense value.");
        setTextState(new TextState("2_77", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        final int permCardId = self.getPermanentCardId();
        Evaluator forceToUseEvaluator = new CalculateCardVariableEvaluator(self, Variable.X) {
            @Override
            protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard self = gameState.findCardByPermanentId(permCardId);
                return Math.max(7 - modifiersQuerying.getPower(gameState, self.getAttachedTo()), 1);
            }
        };
        int x = Math.max(1, (int) forceToUseEvaluator.evaluateExpression(game.getGameState(), game.getModifiersQuerying(), self));
        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();
        // Printed: "Character hit if ... > ability."
        FireWeaponActionBuilder characterBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter), x, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (characterBuilder != null) {
            actions.add(characterBuilder.characterDefenseValueIsAbility().buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE));
        }
        // Printed: "Creature immediately lost if destiny +2 > defense value." (the +2 is the bare card's creature modifier)
        FireWeaponActionBuilder creatureBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.creature, x, TargetingReason.TO_BE_LOST).finishBuildPrep();
        if (creatureBuilder != null) {
            actions.add(creatureBuilder.buildFireWeaponLostAction(1, Statistic.DEFENSE_VALUE));
        }
        return actions.isEmpty() ? null : actions;
    }
}
