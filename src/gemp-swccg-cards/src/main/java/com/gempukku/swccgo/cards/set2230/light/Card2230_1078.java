package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_078;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Princess Leia Organa
 * PC-E1 version of 1230_78: the PC website card list text (by 2012-07-21; Virtual Block 3 list and Virtual master
 * list to 2013-01-11). Against the original (2230_78): the full stop after “escort” moves outside the closing quote
 * mark (“escort.” becomes “escort”.). The list's “*” is its way of writing the printed ✶ mark, not a change. The
 * Master List of 2013-04-27 (PC-E2, the current card) prints the original words again (swccg-guild/work#236; Bill
 * 2026-11-11: every publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1078 extends Card1230_078 {
    public Card2230_1078() {
        super();
        setGameText("* Deploys free only as an escorted captive of Jabba or a bounty hunter. Your Strangle destiny draws are +1. 'Escort alone' on Strangle is treated as 'escort'. When released, escort is lost and you may retrieve any three cards (except Strangle).");
        setLegacyVersion(new LegacyVersion("1230_78", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
