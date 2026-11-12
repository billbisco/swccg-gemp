package com.gempukku.swccgo.cards.set2.dark;

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
 * Title: Magnetic Suction Tube
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Magnetic Suction Tube (Dark)]] (Decipher archive scan); superseded by the Players Committee errata, wiki [[Magnetic Suction Tube (Dark) (PC Errata)]] (Advanced Rulebook Appendix A; date unknown, upper bound March 2017), which reads "Once during each of your control phases".
 * Printed "During your control phase, may" had no per-phase limit (Bill, 12 Nov 2026: nothing in the Premiere / A New
 * Hope rules limited it; the limit came only with the Glossary 2.0 errata), so this state may use the action any
 * number of times in each of your control phases, each use paying its costs. It overrides only that limit; all other
 * behaviour is the bare card's (Card2_114).
 * 
 */
public class Card2_114o extends Card2_114 {
    public Card2_114o() {
        super();
        setGameText("Deploy on your Sandcrawler. During your control phase, may target one character present. Draw destiny. If destiny > character's ability, \"suck up\" character (relocate to related interior Sandcrawler site or owner's Used Pile).");
        setTextState(new TextState("2_114", "Printed",
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
