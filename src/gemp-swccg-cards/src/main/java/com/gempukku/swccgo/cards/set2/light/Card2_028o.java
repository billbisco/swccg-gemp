package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Remote
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Remote]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Remote (PC Errata)]] (Advanced Rulebook Appendix A; date unknown, upper bound March 2017), which reads "Once during each of your control phases".
 * Printed "During your control phase, may" had no per-phase limit (Bill, 12 Nov 2026: nothing in the Premiere / A New
 * Hope rules limited it; the limit came only with the Glossary 2.0 errata), so this state may use the action any
 * number of times in each of your control phases, each use paying its costs. It overrides only that limit; all other
 * behaviour is the bare card's (Card2_028).
 * Each use (either option) may be repeated in the same control phase; the seeker option still costs 2 Force each time.
 */
public class Card2_028o extends Card2_028 {
    public Card2_028o() {
        super();
        setGameText("Use 1 Force to deploy at any site. Moves like a character at normal use of the Force. During your control phase, may: Select one character present to be power or forfeit +1 for remainder of turn. OR Use 2 Force to cancel any seeker present.");
        setTextState(new TextState("2_28", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected boolean isControlPhaseUseAvailable(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return GameConditions.isDuringYourPhaseNoLimit(game, self, playerId, gameTextActionId, Phase.CONTROL);
    }

    @Override
    protected void appendControlPhaseUsageLimit(TopLevelGameTextAction action) {
        // Printed text: no "once during each of your control phases" limit.
    }
}
