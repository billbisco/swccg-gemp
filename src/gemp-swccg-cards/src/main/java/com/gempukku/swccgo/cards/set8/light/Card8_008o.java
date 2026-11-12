package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Endor
 * Title: Daughter Of Skywalker
 * Text state: o (as printed, June 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Daughter Of Skywalker]] (Decipher archive scan); the current text is
 * [[Daughter Of Skywalker (PC Errata)]]. GEMP implements the Mind What You Have Learned clause as an action that makes Mind What You Have Learned / Save You It Can target Leia; the printed "deployed instead of" and the PC errata "targeted instead of" play the same there, so this state reuses the current card's game text code. The PC errata date is not known; supersededOn is an upper bound (Advanced
 * Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card8_008o extends Card8_008 {
    public Card8_008o() {
        super();
        setGameText("Deploys -3 on Tydirium, Endor or Dagobah. May be deployed instead of Luke by Mind What You Have Learned (that card then targets Leia instead of Luke for remainder of game). While at any exterior site, adds one Light side icon. Immune to attrition < 4.");
        setTextState(new TextState("8_8", "Printed",
                "Endor printing (Decipher, June 1998)",
                LocalDate.of(1998, 6, 1), LocalDate.of(2023, 7, 30)));
    }
}
