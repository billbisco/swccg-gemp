package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Starship
 * Subtype: Capital
 * Title: Katana
 */
public class Card1116_056 extends AbstractCapitalStarship {
    public Card1116_056() {
        super(Side.DARK, 3, 6, 5, 4, null, 3, 6, "Katana", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("May add 3 pilots, 6 passengers, and 4 TIEs. Your other Dreadnaughts here are power +1, forfeit +2, Heroic, and immune to attrition < 4. Permanent pilot provides ability of 3. Immune to attrition < 4.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.NAV_COMPUTER);
        addModelType(ModelType.DREADNAUGHT_CLASS_HEAVY_CRUISER);
        setPilotCapacity(3);
        setPassengerCapacity(6);
        setTIECapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(3) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        com.gempukku.swccgo.filters.Filter otherDreadnaughts = Filters.and(Filters.not(self), Filters.Dreadnaught, Filters.atSameLocation(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        modifiers.add(new PowerModifier(self, otherDreadnaughts, 1));
        modifiers.add(new ForfeitModifier(self, otherDreadnaughts, 2));
        modifiers.add(new KeywordModifier(self, otherDreadnaughts, Keyword.HEROIC));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, otherDreadnaughts, 4));
        return modifiers;
    }
}
