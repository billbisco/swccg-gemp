package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Title: Lightsaber Proficiency
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Lightsaber Proficiency]] (Decipher archive scan); the current text is [[Lightsaber Proficiency (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). As printed it says "any" character, so it may also deploy on an opponent's character; the PC errata limits it to your own. This state keeps "any".
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card1_054o extends Card1_054 {
    public Card1_054o() {
        super();
        setGameText("Deploy on any character with ability > 2 and a lightsaber. That character is power +3 in battles or may add 1 to Force drain where present. Effect is lost if character loses lightsaber.");
        setTextState(new TextState("1_54", "Printed",
                "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: "any character", so it may also deploy on an opponent's character. */
    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.character_with_a_lightsaber, Filters.abilityMoreThan(2));
    }
}
