package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_063;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: Daughter Of Skywalker (V)
 * Original version of 1261_63: the Virtual Block 6.1 release sheet text (Sep 2009), "May be deployed instead of Luke by Mind What You Have Learned" (PC website card list by 2012-07-21, AR 2.0 Appendix A and the Master List: "targeted"). GEMP's Mind What You Have Learned already lets it deploy instead of Luke, so play is the same as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_063 extends Card1261_063 {
    public Card2261_063() {
        setGameText("If in hand, may place her under Reserve Deck. May be deployed instead of Luke by Mind What You Have Learned (that card then targets Leia instead of Luke for remainder of game). May ▼ Reflection. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1261_63", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #65; superseded by the PC forum errata (2010-10-08)", LocalDate.of(2009, 9, 4), LocalDate.of(2010, 10, 8)));
    }
}
