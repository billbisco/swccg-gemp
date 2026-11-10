package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_101;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Outpost
 * Original version of 1240_101: the Virtual Block 4 release sheet text (Dec 2009), "may ▼ a device (except a comlink) or Effect
 * that deploys on a character here", reworded in AR 2.0 Appendix A (by Aug 2012) and on the Virtual Cards Master List (Apr 2013). Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_101 extends Card1240_101 {
    public Card2240_101() {
        super();
        setLocationLightSideGameText("May not deploy to Bespin, Coruscant, Dantooine, Kessel or Tatooine. Once per game, may ▼ a device (except a comlink) or Effect that deploys on a character here.");
        setLocationDarkSideGameText("Unless opponent's matching operative at same or related site, those sites are immune to [Premium] No Escape.");
        setGameText("Light: May not deploy to Bespin, Coruscant, Dantooine, Kessel or Tatooine. Once per game, may ▼ a device (except a comlink) or Effect that deploys on a character here. Dark: Unless opponent's matching operative at same or related site, those sites are immune to [Premium] No Escape.");
        setLegacyVersion(new LegacyVersion("1240_101", "",
                "Virtual Block 4 release sheet (2009-12-07), card #100; superseded by the PC forum errata (2011-08-26)", LocalDate.of(2009, 12, 7), LocalDate.of(2011, 8, 26)));
    }
}
