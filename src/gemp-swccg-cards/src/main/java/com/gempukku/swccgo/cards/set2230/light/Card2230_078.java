package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_078;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Princess Leia Organa
 * Original version of 1230_78: the text of the Virtual Block 3 release sheet (2009-12-07), card #79; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2230_1078) changes it: the full stop after
 * “escort” moves outside the closing quote mark (“escort.” becomes “escort”.). The list's “*” is its way of writing
 * the printed ✶ mark, not a change. The Master List of 2013-04-27 prints the original words again; it is listed as
 * its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2230_078 extends Card1230_078 {
    public Card2230_078() {
        super();
        setGameText("* Deploys free only as an escorted captive of Jabba or a bounty hunter. Your Strangle destiny draws are +1. 'Escort alone' on Strangle is treated as 'escort.' When released, escort is lost and you may retrieve any three cards (except Strangle).");
        setLegacyVersion(new LegacyVersion("1230_78", "",
                "Virtual Block 3 release sheet (2009-12-07), card #79; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
