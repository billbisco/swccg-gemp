package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_036;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Protocol Failure
 * Original version of 1261_36: the Virtual Block 6.1 release sheet text ("At end of your turn, place this Effect in Lost
 * Pile"), also on the PC card image and the Master List (2013-04-27). Replaced on 2014-04-16 (Competitive Balance Team
 * errata, PC forum "Current Rulings/Errata": only if you control this site). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_036 extends Card1261_036 {
    public Card2261_036() {
        super();
        setGameText("Deploy on a site to cancel a Force drain there. Vader is deploy -1 here. At end of your turn, place this Effect in Lost Pile. OR At any time, target a unique (•) [Episode I] droid. Opponent chooses: deploy on droid (its game text is canceled), or place this Effect in Lost Pile (you may ▲ any one card). [Immune to Control]");
        setLegacyVersion(new LegacyVersion("1261_36", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #37", LocalDate.of(2009, 9, 4), LocalDate.of(2014, 4, 16)));
    }

    @Override
    protected boolean isLostAtEndOfTurn(SwccgGame game, PhysicalCard self, PhysicalCard site) {
        return true;
    }
}
