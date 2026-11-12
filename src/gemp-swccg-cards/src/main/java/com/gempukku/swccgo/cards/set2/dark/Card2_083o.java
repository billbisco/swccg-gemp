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
 * Title: Captain Khurgee
 * Text state: o (as printed, 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Captain Khurgee]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), wiki [[Captain Khurgee (Errata)]], which added "Once during each of your control phases".
 * Printed "During your control phase, may" had no per-phase limit (Bill, 12 Nov 2026: nothing in the Premiere / A New
 * Hope rules limited it; the limit came only with the Glossary 2.0 errata), so this state may use the action any
 * number of times in each of your control phases, each use paying its costs. It overrides only that limit; all other
 * behaviour is the bare card's (Card2_083).
 * Each use still costs 2 Force.
 */
public class Card2_083o extends Card2_083 {
    public Card2_083o() {
        super();
        setGameText("During your control phase, you may use 2 Force to search through your Reserve Deck and take one Scanning Crew into your hand. Shuffle deck, cut and replace.");
        setTextState(new TextState("2_83", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
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
