package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_062;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda, Great Warrior
 *
 * PC-E1 version of 1250_55: the PC card image (yodagreatwarrior.gif, slip 56E), the same image as in
 * swccgvkit-legacy svn r1 (2009-12-09), so made by then; no firmer date is known, so it is placed at that proven
 * upper bound (Bill 2026-11-11 08:23) and may be older. It prints "when defending". Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2250_2055 extends Card601_062 {
    public Card2250_2055() {
        super();
        setGameText("Deploys only to battlegrounds. Power +3 when defending a battle. You may not deploy [Episode I] Jedi. While with a Wookiee, adds one battle destiny. Once per game, may lose 2 Force from Force Pile to cancel a non-[Immune to Sense] Interrupt. Immune to attrition.");
        setLegacyVersion(new LegacyVersion("1250_55", "PC-E1",
                "PC card image (slip 56E; in swccgvkit-legacy svn r1, 2009-12-09; may be older)", LocalDate.of(2009, 12, 9), LocalDate.of(2012, 7, 21)));
    }
}
