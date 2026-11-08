package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_087;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Title;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 * PC-E1 version of 1240_58 (front): the AR 2.0 Appendix A errata text (by Aug 2012): opponent loses 2 Force when a character
 * hit by Vader's (or Galen's) Lightsaber leaves table on the same turn, and the back side also flips if neither Vader nor Galen
 * is on table. Galen's Fighter is the starship later renamed Rogue Shadow. Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_1058 extends Card601_087 {
    public Card2240_1058() {
        super();
        setGameText("Deploy Coruscant system, Imperial City, and A Sith's Plans. May also deploy If The Trace Was Correct.\n" +
                "For remainder of game, you may not deploy [Episode I] Dark Jedi. Opponent loses 2 Force when a character hit by Vader's (or Galen's) Lightsaber leaves table on the same turn.\n" +
                "While this side up, may ▲ Galen's Fighter. Galen's immunity to attrition is +2 for each Jedi present.\n" +
                "Flip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site.");
        setLegacyVersion(new LegacyVersion("1240_58", "PC-E1",
                "AR 2.0 Appendix A (Virtual card errata, by 2012-08-27)", LocalDate.of(2012, 8, 27), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected String optionalDeployTitle() {
        return Title.If_The_Trace_Was_Correct;
    }
}
