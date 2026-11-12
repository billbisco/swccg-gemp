package com.gempukku.swccgo.cards.set1.dark;

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
import java.util.List;

/**
 * Set: Premiere
 * Type: Weapon
 * Subtype: Character
 * Title: Dark Jedi Lightsaber
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Dark Jedi Lightsaber]] and its Decipher archive scan (Premiere-D-darkjedilightsaber-decipher-archive.gif); superseded by the Decipher
 * Glossary Version 2.0 errata (November 1998), which the bare card plays.
 * <p>
 * Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text readings" 4): "you may then use as follows: ... OR ..." lists
 * two separate abilities (add 1 to Force drain where present; fire), not one choice per turn, as the bare card plays
 * them. Printed weapon text (docs/legacy-versions.md "Printed weapons"): a character target is compared to its ability,
 * not its defense value (FireWeaponActionBuilder.characterDefenseValueIsAbility), and creatures cannot be targeted.
 * "Any warrior" deploys only on your own warrior (Decipher Glossary 2.0, "weapons — deploying and carrying").
 */
public class Card1_314o extends Card1_314 {
    public Card1_314o() {
        super();
        setGameText("Use X Force to deploy on any warrior where X = (7 - warrior's ability), and you may then use as follows: Add 1 to Force drain where present. OR Target a character by using X Force. Draw two destiny. Character hit if total destiny > ability.");
        setTextState(new TextState("1_314", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        final int permCardId = self.getPermanentCardId();
        Evaluator forceToUseEvaluator = new CalculateCardVariableEvaluator(self, Variable.X) {
            @Override
            protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                return 7 - modifiersQuerying.getAbility(gameState, self.getAttachedTo());
            }
        };
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter),
                        Math.max(0, (int) forceToUseEvaluator.evaluateExpression(game.getGameState(), game.getModifiersQuerying(), self)),
                        TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.characterDefenseValueIsAbility().buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }
}
