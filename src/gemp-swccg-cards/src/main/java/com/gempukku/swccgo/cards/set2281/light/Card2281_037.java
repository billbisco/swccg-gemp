package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_037;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Luck
 * Original version of 1281_37: the PC card image text (Virtual Block 8.1, May 2012) without "(even if inactive)" (Bill: card image = original); the errata posted on the PC forum on 2012-07-13 (p739757) and the re-made sheet (2012-07-18) add it (PC-E1, 2281_1037); the card list repeats the original (PC-E2, 2281_2037); the Master List repeats PC-E1 (PC-E3, the current card). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_037 extends Card1281_037 {
    public Card2281_037() {
        super();
        setGameText("Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here. Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_37", "", "PC card image (Virtual Block 8.1, 2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 13)));
    }

    /** The PC card image text has no "(even if inactive)": only active [Scoundrel] Effects are stacked. */
    @Override
    protected boolean stackAtStartOfTurnEvenIfInactive() {
        return false;
    }
}
