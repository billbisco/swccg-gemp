package com.gempukku.swccgo.cards.set2210.dark;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.AbstractSeeker;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * Set: Virtual Block 1
 * Type: Weapon
 * Subtype: Automated
 * Title: Han Seeker
 * Original version of 1210_84 (Virtual Block 1 release sheet, Dec 2009): smuggler listed before Han; same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_084 extends AbstractSeeker {
    public Card2210_084() {
        super(Side.DARK, 3, "Han Seeker", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Military version of a 'remote'. Programmed to stalk specific targets or secondary targets. Heat and light sensors track with fatal accuracy. Can stow away on starships.");
        setGameText("Use 1 Force to deploy to an unoccupied site. Deploys and moves like an Undercover spy. If present with a smuggler of ability < 3 or [Permanent Weapon] Han, this weapon and that character are lost ('all cards' situation).");
        setLegacyVersion(new LegacyVersion("1210_84", "",
                "Virtual Block 1 release sheet (2009-12-07), card #84; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected Filter getEligibleSeekerTargetFilter(SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.and(Filters.Han, Icon.PERMANENT_WEAPON), Filters.and(Filters.smuggler, Filters.abilityLessThan(3)));
    }
}
