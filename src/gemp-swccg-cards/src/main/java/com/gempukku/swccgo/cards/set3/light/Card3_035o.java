package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Evacuation Control
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "once per turn ... may fire during your move phase" is the current "once during each of your move phases", and the medium transport clause is the same; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_035o extends Card3_035 {
    public Card3_035o() {
        super();
        setGameText("Deploy on one of your war rooms. Once per turn, your Planet Defender Ion Cannon at same planet may fire during your move phase. Also, your Medium Transports at same planet are hyperspeed +2, are immune to attrition < 3 and may move for free.");
        setTextState(new TextState("3_35", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
