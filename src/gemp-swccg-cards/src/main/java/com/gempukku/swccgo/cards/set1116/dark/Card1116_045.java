package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Alien
 * Title: Keder The Black (V)
 */
public class Card1116_045 extends AbstractAlien {
    public Card1116_045() {
        super(Side.DARK, 1, 3, 4, 3, 6, "Keder The Black", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While at Senate: Sidious is politics +3; 'senators' on Squabbling Delegates is treated as 'Coruscant guards;' and may lose 1 Force to place Keder and an opponent's [Coruscant] Political Effect (or [Coruscant] senator here) in owner's Lost Pile.");
        addIcons(Icon.CORUSCANT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.<Modifier>singletonList(new PoliticsModifier(self, Filters.Sidious, new AtCondition(self, Filters.titleContains("Senate")), 3));
    }
}
