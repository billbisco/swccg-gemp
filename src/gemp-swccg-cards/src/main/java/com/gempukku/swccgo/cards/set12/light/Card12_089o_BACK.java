package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Coruscant
 * Type: Objective
 * Title: The Hyperdrive Generator's Gone / We'll Need A New One
 * Text state: o (as printed, August 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The 7 side of the printed state 12_89o (Card12_089o): flipping 12_89o loads 12_89o_BACK. Printed text from
 * wiki.swccg.com [[The Hyperdrive Generator's Gone / We'll Need A New One]] (Decipher archive scan): "Whenever you draw
 * battle destiny, may retrieve 1 Force". The Players Committee's errata (SWCCG 2023 Advanced Rulebook Appendix A) made
 * it "Whenever you complete a non-substituted battle destiny draw"; GEMP's current back (Card12_089_BACK) reads
 * "(unless canceled)". This state plays as the current back: the retrieval is offered when a drawn (not substituted)
 * battle destiny completes. Whether the printed text also retrieves for a battle destiny that is then canceled is
 * not decided here (that would need a ruling and a new trigger).
 */
public class Card12_089o_BACK extends Card12_089_BACK {
    public Card12_089o_BACK() {
        super();
        setGameText("While this side up, your unique (•) Republic characters are power +1 and forfeit +2. Aliens may not have their deploy cost modified and Imperials deploy +1 to Tatooine sites. Whenever you draw battle destiny, may retrieve 1 Force (Force retrieved in this way may be taken into hand.) While Queen's Royal Starship at a planet system, once during each of opponent's control phases may activate up to 2 Force. Once during each of your control phases, opponent loses 1 Force for each battleground site you occupy with a senator.");
        setTextState(new TextState("12_89_BACK", "Printed", "Coruscant printing (Decipher, August 2001)",
                LocalDate.of(2001, 8, 1), LocalDate.of(2023, 7, 30)));
    }
}
