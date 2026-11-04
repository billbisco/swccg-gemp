package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.framework.VirtualTableScenario;

import java.util.function.BooleanSupplier;

/**
 * Small helpers shared by the Virtual Block 9.1 Light character tests: advance the game through response windows
 * (and "draw battle destiny?" prompts) until a specific decision or action shows up.
 */
final class Vb91LightTestSupport {
    private Vb91LightTestSupport() {
    }

    static boolean advanceUntil(VirtualTableScenario scn, BooleanSupplier condition) {
        for (int i = 0; i < 60; ++i) {
            if (condition.getAsBoolean())
                return true;
            var decision = scn.GetCurrentDecision();
            if (decision == null)
                return false;
            String player = scn.GetAwaitingDecision(scn.LS) != null ? scn.LS : scn.DS;
            if (scn.GetAwaitingDecision(player) == null)
                return condition.getAsBoolean();
            decision = scn.GetAwaitingDecision(player);
            String text = decision.getText().toLowerCase();
            if (text.contains("optional response") || text.contains("required response") || text.contains("or pass")) {
                scn.PlayerPass(player);
            } else if (text.contains("battle destiny") && scn.ChoiceAvailable(player, "Yes")) {
                scn.PlayerChooseYes(player);
            } else {
                return condition.getAsBoolean();
            }
        }
        return condition.getAsBoolean();
    }

    static void reach(VirtualTableScenario scn, BooleanSupplier condition) {
        if (!advanceUntil(scn, condition))
            throw new AssertionError("Condition not reached; " + decisions(scn));
    }

    static boolean lsAction(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        try {
            return scn.LSCardActionAvailable(card, text);
        } catch (RuntimeException e) {
            return false;
        }
    }

    static boolean dsAction(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (RuntimeException e) {
            return false;
        }
    }

    static boolean lsSelectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var ids = scn.GetADParamAsList(scn.LS, "blueprintId");
        var selectable = scn.GetADParamAsList(scn.LS, "selectable");
        for (int i = 0; i < ids.size() && i < selectable.size(); ++i) {
            if (ids.get(i).equals(card.getBlueprintId(true)) && "true".equals(selectable.get(i)))
                return true;
        }
        return false;
    }

    static int lsCardChoiceCount(VirtualTableScenario scn) {
        try {
            return scn.LSGetCardChoiceCount();
        } catch (RuntimeException e) {
            return -1;
        }
    }

    static String decisions(VirtualTableScenario scn) {
        var ls = scn.GetAwaitingDecision(scn.LS);
        var ds = scn.GetAwaitingDecision(scn.DS);
        return "LS=[" + (ls == null ? "<none>" : ls.getText()) + "] DS=[" + (ds == null ? "<none>" : ds.getText()) + "]";
    }
}
