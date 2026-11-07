package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.List;

/** Legacy Block 6.2: Program Trap (V). */
public class Card1262_010 extends AbstractNormalEffect {
    public Card1262_010() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Program Trap", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial slicers imbed a secret command in a droid's primary performance banks. A predetermined trigger causes a power overload, destroying the droid and anything nearby.");
        setGameText("Deploy on opponent's capital or [Independent] starship. Starship is power -2, and its gametext (except related to capacity, immunity, and identity and ability of permanent pilots) is canceled.");
        addIcons(Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option,
                                                         boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.starship, Filters.or(Filters.capital_starship, Icon.INDEPENDENT));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new PowerModifier(self, Filters.hasAttached(self), -2));
    }
}
