package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Title: Ket Maliss
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Ket Maliss]] (Decipher archive scan); the current text is [[Ket Maliss (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). As printed it says "any" character, so it may also deploy on an opponent's character; the PC errata limits it to your own. This state keeps "any".
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card1_221o extends Card1_221 {
    public Card1_221o() {
        super();
        setGameText("Deploy on any non-warrior character (except droids) to give that character warrior skill. OR Deploy on any warrior. That character is power +1. (Immune to Alter.)");
        setTextState(new TextState("1_221", "Printed",
                "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: "any" non-warrior / warrior, so it may also deploy on an opponent's character. */
    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_1) {
            return Filters.and(Filters.character, Filters.not(Filters.or(Filters.warrior, Filters.droid)));
        }
        else if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_2) {
            return Filters.and(Filters.character, Filters.warrior);
        }
        return Filters.none;
    }
}
