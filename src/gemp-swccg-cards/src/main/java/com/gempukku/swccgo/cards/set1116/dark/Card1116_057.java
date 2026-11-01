package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Device
 * Title: Mandalorian Armor (V)
 */
public class Card1116_057 extends AbstractCharacterDevice {
    public Card1116_057() {
        super(Side.DARK, 3, "Mandalorian Armor", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Deploy on your alien of ability < 4. Character gains bounty hunter skill and immunity to attrition < 4. If character just hit, may place this device in Lost Pile (Used Pile if on The Mandalorian) to restore it to normal.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.alien, Filters.abilityLessThan(4));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.hasAttached(self), Keyword.BOUNTY_HUNTER));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.hasAttached(self), 4));
        return modifiers;
    }
}
