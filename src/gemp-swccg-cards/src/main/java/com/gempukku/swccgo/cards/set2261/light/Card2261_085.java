package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_085;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Return Of The Jedi
 * Original version of 1261_85: the Virtual Block 6.1 release sheet text ("At end of your turn, place this Effect in Lost
 * Pile"), printed without a uniqueness dot; also on the PC card image and the Master List (2013-04-27). Replaced on
 * 2014-04-16 (Competitive Balance Team errata, PC forum "Current Rulings/Errata": only if you control this site;
 * "NOTE: it is also unique now"). A version keeps the current card's uniqueness (docs/legacy-versions.md, "Same card"),
 * so this card is unique in GEMP like the current one. Anything Goes only.
 */
public class Card2261_085 extends Card1261_085 {
    public Card2261_085() {
        super();
        setGameText("Deploy on a site to cancel a Force drain there. Skywalkers are power +1 here. At end of your turn, place this Effect in Lost Pile. OR At any time, deploy on Sidious or [Virtual Block 3] Emperor. Opponent's Interrupts with “Emperor” in game text are canceled and Sidious may not place Jedi out of play. [Immune to Control]");
        setLegacyVersion(new LegacyVersion("1261_85", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #86 (printed without the uniqueness dot)", LocalDate.of(2009, 9, 4), LocalDate.of(2014, 4, 16)));
    }

    @Override
    protected boolean isLostAtEndOfTurn(SwccgGame game, PhysicalCard self, PhysicalCard site) {
        return true;
    }
}
