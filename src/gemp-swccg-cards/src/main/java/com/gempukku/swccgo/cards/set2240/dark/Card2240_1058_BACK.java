package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_087_BACK;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 * PC-E1 version of 1240_58 (back): the AR 2.0 Appendix A errata text (by Aug 2012). See {@link Card2240_1058}. Plays the same
 * as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_1058_BACK extends Card601_087_BACK {
    public Card2240_1058_BACK() {
        super();
        setGameText("While this side up, opponent's Force drain bonuses are canceled and your Force drains at battlegrounds may not be canceled. During your control phase, may retrieve 1 Force. During your control phase, may use 2 Force to ▲ any one card without ability. If targeting a vehicle or starship with Lightsaber Parry, Tarkin's Orders, or There Is No Conflict, that destiny draw is +2.\n" +
                "Flip this card if opponent has a unique (•) character of ability > 3 present at a battleground site or if neither Vader nor Galen is on table.");
        setLegacyVersion(new LegacyVersion("1240_58_BACK", "PC-E1",
                "AR 2.0 Appendix A (Virtual card errata, by 2012-08-27)", LocalDate.of(2012, 8, 27), LocalDate.of(2013, 4, 27)));
    }
}
