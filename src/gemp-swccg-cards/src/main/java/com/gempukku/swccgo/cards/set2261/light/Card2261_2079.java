package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_079;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Commando Training & •K'lor'slug
 *
 * PC-E2 version of 1261_79: the PC card image (commandotraining&klorslug.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2079 extends Card1261_079 {
    public Card2261_2079() {
        super();
        setGameText("Deploy on table. If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Rebels (except Jedi or [Permanent Weapon] characters) ignore location deployment restrictions (and \"power -1\") in their game text, apply their Endor deployment modifiers at any location, and are immune to Bad Feeling Have I. Your Interrupts that retrieve exactly 1 Force (except Corellian Retort) are not stacked on Do They Have A Code Clearance?. Cannot be canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_79", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
