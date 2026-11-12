package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Alien
 * Title: Thok &amp; Thug
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The printed text lets Dark Side use X Force (limit 2) to add twice X to Thok &amp; Thug's own power whenever
 * opponent just initiated a battle (anywhere). The Players Committee Rules Supplement (27 January 2003) clarified it to
 * a battle at the same site, adding to total power (current text, Card13_092).
 * Text from wiki.swccg.com [[Thok &amp; Thug]].
 */
public class Card13_092o extends Card13_092 {
    public Card13_092o() {
        super();
        setGameText("Deploys -2 to a Jabba's Palace site. If opponent just initiated battle, may use X Force (limit 2) to add twice X to power. Your alien leaders present may not be targeted by weapons. End of your turn: Use 2 Force to maintain OR Lose 1 Force to place in Used Pile OR Place out of play.");
        setTextState(new TextState("13_92", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2003, 1, 27)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, game.getOpponent(playerId))) {
            int maxForceToUse = Math.min(2, GameConditions.forceAvailableToUse(game, playerId));
            if (maxForceToUse > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Add to power");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxForceToUse, maxForceToUse) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, result));
                                        int amountToAddToPower = 2 * result;
                                        action.setActionMsg("Add " + amountToAddToPower + " to Thok & Thug's power");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyPowerUntilEndOfBattleEffect(action, self, amountToAddToPower));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
