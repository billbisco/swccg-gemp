package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Hoth
 * Title: Probe Antennae
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Wording only: "Any Probe Droid" plays as "your Probe Droid" (a device deploys on your own cards unless it says otherwise), "glance" is "peek" and "once each turn during your control phase" is "once during each of your control phases"; Decipher Glossary 2.0 (November 1998) only reworded it.
 */
public class Card3_097o extends Card3_097 {
    public Card3_097o() {
        super();
        setGameText("Deploy on any Probe Droid. Adds 2 to X. OR Use 1 Force to deploy on one of your other droids. When at a site you control, once each turn during your control phase, you may glance at one card randomly selected from opponent's hand.");
        setTextState(new TextState("3_97", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
