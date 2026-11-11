package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_037;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Scoundrel's Luck PC-E3: the Master List, with "(even if inactive)" (until the B41 rework this id was the original from the PC card image; the first printing is lost).
 * Anything Goes only (docs/legacy-versions.md; batch B41 rework, Bill's rulings of 2026-11-11: the PC card image and the
 * Master List are versions of their own, in date order).
 */
public class Card2281_037 extends Card1281_037 {
    public Card2281_037() {
        super();
        setGameText("Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here (even if inactive). Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_37", "PC-E3",
                "Players Committee Virtual Cards Master List (official text as of 2013-04-27)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }

    /** "(even if inactive)": inactive [Scoundrel] Effects are stacked too. */
    @Override
    protected boolean stackAtStartOfTurnEvenIfInactive() {
        return true;
    }
}
