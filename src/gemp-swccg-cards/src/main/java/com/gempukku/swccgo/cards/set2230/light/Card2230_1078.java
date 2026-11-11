package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_078;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Princess Leia Organa
 * PC-E1 version of 1230_78. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): original (2230_78) = the Virtual Block 3 release sheet (2009-12-07), card #79; PC-E1
 * (2230_1078) = the PC website card list (by 2012-07-21): the full stop after “escort” moves outside the closing
 * quote mark (“escort.” becomes “escort”.). The list's “*” is its way of writing the printed ✶ mark, not a change.
 * PC-E2 (2230_2078) = the PC card image (uploaded 2013-02-25; it may have been made earlier), the original words
 * again; PC-E3 = the Master List (2013-04-27), the original words again, the current card. Same play. Anything Goes
 * only (docs/legacy-versions.md).
 */
public class Card2230_1078 extends Card1230_078 {
    public Card2230_1078() {
        super();
        setGameText("* Deploys free only as an escorted captive of Jabba or a bounty hunter. Your Strangle destiny draws are +1. 'Escort alone' on Strangle is treated as 'escort'. When released, escort is lost and you may retrieve any three cards (except Strangle).");
        setLegacyVersion(new LegacyVersion("1230_78", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the PC card image (uploaded 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
