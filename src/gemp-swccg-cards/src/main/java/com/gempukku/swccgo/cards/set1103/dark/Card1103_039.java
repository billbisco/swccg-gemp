package com.gempukku.swccgo.cards.set1103.dark;

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
 * Set: Virtual Set 3: Revolution
 * Type: Weapon
 * Subtype: Automated
 * Title: Leia Seeker
 */
public class Card1103_039 extends AbstractSeeker {
    public Card1103_039() {
        super(Side.DARK, 3, "Leia Seeker", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Military version of a 'remote'. Programmed to stalk specific targets or secondary targets. Heat and light sensors track with fatal accuracy. Can stow away on starships.");
        setGameText("Use 1 Force to deploy to an unoccupied site. Deploys and moves like an Undercover spy. If present with [Permanent Weapon] Leia or a senator, this weapon and that character are lost ('all cards' situation).");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getEligibleSeekerTargetFilter(SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.and(Filters.Leia, Icon.PERMANENT_WEAPON), Filters.senator);
    }
}
