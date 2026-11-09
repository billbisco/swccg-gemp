package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: Death Star Gunner
 *
 * PC-E1 version of 1210_8: the errata of the PC news post "Errata to Death Star Gunner (V)/Rebel Tech (V)"
 * (2010-06-17, "Effective immediately"; also AR 2.0 Appendix A and the 2013 Master List): "... may place this character
 * in Used Pile to ▲ Superlaser." The PC website card list (by 2012-07-21, PC-E2, the current card) writes the ▲ out as
 * "take a Superlaser into hand from Reserve Deck; reshuffle", adding "a". Plays as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1008 extends Card1210_008 {
    public Card2210_1008() {
        setGameText("While on Death Star: cancels Force drain bonuses from Death Star sites' game text, your [A New Hope] Epic Event total is cumulatively +2, and during your deploy phase, may place this character in Used Pile to ▲ Superlaser.");
        setLegacyVersion(new LegacyVersion("1210_8", "PC-E1",
                "PC news post \"Errata to Death Star Gunner (V)/Rebel Tech (V)\" (2010-06-17); superseded by the PC card list text (by 2012-07-21)",
                LocalDate.of(2010, 6, 17), LocalDate.of(2012, 7, 21)));
    }
}
