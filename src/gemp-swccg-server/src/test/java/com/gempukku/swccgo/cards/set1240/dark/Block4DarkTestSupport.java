package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.AwaitingDecisionType;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

/**
 * Shared helpers for the Block 4 Dark legacy tests: steps the game forward by passing card-action decisions until a
 * condition holds.
 */
final class Block4DarkTestSupport {
    private Block4DarkTestSupport() {
    }

    /**
     * Passes card-action decisions (and declines optional yes/no prompts) until the condition holds.
     * @return true if the condition was reached
     */
    static boolean advanceUntil(VirtualTableScenario scn, BooleanSupplier condition, int maxSteps) {
        for (int i = 0; i < maxSteps; i++) {
            if (condition.getAsBoolean()) {
                return true;
            }
            String player = null;
            for (String p : new String[]{scn.DS, scn.LS}) {
                if (scn.GetAwaitingDecision(p) != null) {
                    player = p;
                    break;
                }
            }
            if (player == null) {
                return condition.getAsBoolean();
            }
            var decision = scn.GetAwaitingDecision(player);
            if (decision.getDecisionType() == AwaitingDecisionType.CARD_ACTION_CHOICE) {
                scn.PlayerPass(player);
            }
            else if (decision.getDecisionType() == AwaitingDecisionType.MULTIPLE_CHOICE
                    && decision.getDecisionParameters().get("results") != null
                    && Arrays.asList(decision.getDecisionParameters().get("results")).contains("No")) {
                scn.ChooseOption(player, "No");
            }
            else {
                return condition.getAsBoolean();
            }
        }
        return condition.getAsBoolean();
    }

    /**
     * True if the Dark Side player has a card-action decision that includes the given action on the card.
     */
    static boolean dsActionAvailable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        var d = scn.GetAwaitingDecision(scn.DS);
        return d != null && d.getDecisionType() == AwaitingDecisionType.CARD_ACTION_CHOICE && scn.DSCardActionAvailable(card, text);
    }

    /**
     * True if the card is offered as a selectable choice in the Dark Side player's current card-selection decision.
     */
    static boolean dsSelectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var d = scn.GetAwaitingDecision(scn.DS);
        if (d == null) return false;
        String[] ids = d.getDecisionParameters().get("cardId");
        String[] blueprints = d.getDecisionParameters().get("blueprintId");
        String[] selectable = d.getDecisionParameters().get("selectable");
        if (ids == null) return false;
        boolean found = false;
        for (int i = 0; i < ids.length; i++) {
            boolean matches = ids[i].equals(String.valueOf(card.getCardId()))
                    || (blueprints != null && blueprints[i].equals(card.getBlueprintId(true)));
            if (matches) {
                if (selectable == null || Boolean.parseBoolean(selectable[i])) return true;
                found = true;
            }
        }
        return false;
    }

    static String describe(VirtualTableScenario scn) {
        StringBuilder sb = new StringBuilder();
        for (String p : new String[]{scn.DS, scn.LS}) {
            var d = scn.GetAwaitingDecision(p);
            sb.append(p).append(": ").append(d == null ? "none" : "[" + d.getDecisionType() + "] " + d.getText()).append(" | ");
        }
        return sb.toString();
    }
}
