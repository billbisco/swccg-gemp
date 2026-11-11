package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_101;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Solo Han (V)
 * Original version of 1261_101: the Virtual Block 6.1 release sheet text, "▼ (or retrieve) non-[Reflections III] Han"
 * and "adds one destiny to power (+1 for each character and vehicle present)". The PC card list (PC-E1, the current
 * card) rewrote it: "non-[Permanent Weapon] Han (except Falcon)" and "he is power +1 for each character and vehicle
 * present". This plays as GEMP's current card did before. Tests: Card_2261_101_Tests runs the shared solo-defense
 * tests on this version.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_101 extends Card1261_101 {
    public Card2261_101() {
        setGameText("Use 1 Force to ▼ (or retrieve) non-[Reflections III] Han. OR If a battle just initiated, and Han defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1261_101", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #102", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
