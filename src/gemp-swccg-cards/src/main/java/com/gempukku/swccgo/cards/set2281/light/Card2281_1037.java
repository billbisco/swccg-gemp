package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_037;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Luck
 * PC-E1 version of 1281_37: the errata posted on the PC forum on 2012-07-13 (p739757), "(even if inactive)", printed by
 * the re-made 8.1 sheet (2012-07-18). The PC website card list then printed the original words again (PC-E2, 2281_2037);
 * the Master List (2013-04-27) prints these words again (PC-E3, the current card).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1037 extends Card1281_037 {
    public Card2281_1037() {
        super();
        setGameText("Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here (even if inactive). Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_37", "PC-E1",
                "PC forum errata post p739757 (2012-07-13); re-made Virtual Block 8.1 release sheet (2012-07-18)", LocalDate.of(2012, 7, 13), LocalDate.of(2012, 7, 18)));
    }

    /** "(even if inactive)": inactive [Scoundrel] Effects are stacked too. */
    @Override
    protected boolean stackAtStartOfTurnEvenIfInactive() {
        return true;
    }
}
