package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.List;

/** Legacy Block 6.1 Lieutenant Cecius (V). */
public class Card1261_016 extends AbstractImperial {
    public Card1261_016() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Lieutenant Cecius", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Former member of the Imperial infantry. Specializes in boarding and taking control of enemy ships. Native of Vogel 7. Hasn't had a date in years.");
        setGameText("Opponent's characters are deploy +1 to same mobile site. At battlegrounds where you have a unique (•) and non-unique Imperial present (and no other Force drain bonus), your Force drains are +1. Tactical Support is a Used Interrupt.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourImperials = Filters.and(Filters.your(self), Filters.Imperial);
        Modifier forceDrainBonus = new ForceDrainModifier(self, Filters.and(Filters.battleground,
                Filters.wherePresent(self, Filters.and(yourImperials, Filters.unique)),
                Filters.wherePresent(self, Filters.and(yourImperials, Filters.non_unique))), 1, playerId);
        forceDrainBonus.setOnlyIfNoOtherBonus(true);
        return List.of(
                new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character), 1,
                        Filters.and(Filters.mobile_site, Filters.sameSite(self))),
                forceDrainBonus,
                new UsedInterruptModifier(self, Filters.Tactical_Support));
    }
}
