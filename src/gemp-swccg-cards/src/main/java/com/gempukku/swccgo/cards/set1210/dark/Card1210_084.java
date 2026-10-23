package com.gempukku.swccgo.cards.set1210.dark;

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
 */
public class Card1210_084 extends AbstractSeeker {
    public Card1210_084() {
        super(Side.DARK, 3, "Han Seeker", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Military version of a 'remote'. Programmed to stalk specific targets or secondary targets. Heat and light sensors track with fatal accuracy. Can stow away on starships.");
        setGameText("Use 1 Force to deploy to an unoccupied site. Deploys and moves like an Undercover spy. If present with [Permanent Weapon] Han or a smuggler of ability < 3, this weapon and that character are lost ('all cards' situation).");
    }

    @Override
    protected Filter getEligibleSeekerTargetFilter(SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.and(Filters.Han, Icon.PERMANENT_WEAPON), Filters.and(Filters.smuggler, Filters.abilityLessThan(3)));
    }
}
