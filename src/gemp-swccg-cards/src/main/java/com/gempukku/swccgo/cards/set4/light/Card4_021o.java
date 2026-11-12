package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Do, Or Do Not
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Dagobah (Decipher, April 1997) card from wiki.swccg.com [[Do, Or Do Not]] and its Decipher archive scan
 * (Dagobah-L-doordonot-decipher-archive.gif): the same game text, printed without the uniqueness dot. The Players
 * Committee errata (SWCCG 2023 Advanced Rulebook Appendix A, p. 110: "This Effect is now unique (•)"; upper bound
 * 30 July 2023) made it unique, which the bare card has.
 * <p>
 * Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text readings" 6): the printed card is non-unique, and the
 * cumulative rule still applies: two copies of Do, Or Do Not do not make a player lose 2 Force twice for one successful
 * Sense or Alter destiny draw. A printed copy therefore makes the player lose Force only when it is the first-deployed
 * printed copy on table and no other card titled Do, Or Do Not is active. (Making Sense and Alter Lost Interrupts does
 * not stack either way.)
 */
public class Card4_021o extends Card4_021 {
    public Card4_021o() {
        super();
        setUniqueness(Uniqueness.UNRESTRICTED);
        setTextState(new TextState("4_21", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        for (PhysicalCard other : Filters.filterActive(game, self, Filters.and(Filters.title(Title.Do_Or_Do_Not), Filters.not(self)))) {
            if (!(other.getBlueprint() instanceof Card4_021o) || other.getCardId() < self.getCardId()) {
                // Cumulative rule: another Do, Or Do Not already makes the player lose 2 Force
                return null;
            }
        }
        return super.getGameTextRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }
}
