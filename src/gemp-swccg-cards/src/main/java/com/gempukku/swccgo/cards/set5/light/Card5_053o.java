package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;

import java.time.LocalDate;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Innocent Scoundrel
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Cloud City (Decipher, November 1997) card from wiki.swccg.com [[Innocent Scoundrel]] and its Decipher archive
 * scan (CC-L-innocentscoundrel-decipher-archive.gif), printed without the uniqueness dot. The Decipher Glossary
 * Supplement (29 January 2002, "Erratum: This card is now unique (•)") and the SWCCG 2023 Advanced Rulebook Appendix A
 * (p. 117) made it unique, which the bare card now is. Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text
 * readings" 6): the printed card is non-unique.
 * <p>
 * The printed USED text "If opponent just targeted one of your gamblers" plays as the bare "If your gambler was just
 * targeted by a weapon": the only targeting an opponent can be made to redo by selecting a new target is a weapon's.
 */
public class Card5_053o extends Card5_053 {
    public Card5_053o() {
        super();
        setUniqueness(Uniqueness.UNRESTRICTED);
        setGameText("USED: If opponent just targeted one of your gamblers, opponent must choose to select a new target or lose 2 Force. LOST: Cancel any Effect (except those Immune to Alter) deployed on Han or your Lando.");
        setTextState(new TextState("5_53", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2002, 1, 29)));
    }
}
