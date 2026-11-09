package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: 
 * Title: Rise Of The Dark Lord
 * Original version of 1271_19: the Virtual Block 7.1 release sheet (2010-06-18; copy re-made 2010-07-28) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "(deploys only at start of game)" for "(only at start of game)" and "from your Reserve Deck" for "from Reserve Deck"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_019 extends Card1271_019 {
    public Card2271_019() {
        super();
        setGameText("Deploy on table (only at start of game) with three different [Vader] Effects from Reserve Deck face down under here. Once per turn, may deploy a [Vader] Effect from here (never from elsewhere). Limit one [Vader] Effect on table and, when lost, stack it under here. You may cancel your [Vader] Effect at any time. Twisted And Evil: Dark Jedi Presence, Jedi Presence, Revenge Of The Sith and Sith Tests are canceled. When using The Empire's Back to deploy Vader, it is a Used Interrupt. If you just drew a [Vader] card for destiny, may take it into hand.");
        setLegacyVersion(new LegacyVersion("1271_19", "",
                "Virtual Block 7.1 release sheet (2010-06-18; copy re-made 2010-07-28)", LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21)));
    }
}
